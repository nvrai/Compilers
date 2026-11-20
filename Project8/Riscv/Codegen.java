/**
 * COSC 4400 - Project #8
 * Tiles canonical IR trees into RISC-V instructions with abstract temporaries.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Riscv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import Temp.Label;
import Temp.Temp;

/** Instruction-selection visitor for the RISC-V target. */
public final class Codegen implements Tree.CodeVisitor {
    private final RiscvFrame frame;
    private final List<Assem.Instr> instructions = new ArrayList<Assem.Instr>();

    public Codegen(RiscvFrame frame) {
        this.frame = frame;
    }

    private static List<Temp> temps(Temp... values) {
        if (values.length == 0) return Collections.emptyList();
        return Arrays.asList(values);
    }

    private static List<Label> labels(Label... values) {
        if (values.length == 0) return Collections.emptyList();
        return Arrays.asList(values);
    }

    private void emit(Assem.Instr instruction) {
        instructions.add(instruction);
    }

    /** Selects instructions for an already canonical and trace-scheduled body. */
    public List<Assem.Instr> codegen(List<Tree.Stm> statements) {
        instructions.clear();
        for (Tree.Stm statement : statements) statement.accept(this);
        return new ArrayList<Assem.Instr>(instructions);
    }

    public void visit(Tree.SEQ node) {
        node.left.accept(this);
        node.right.accept(this);
    }

    public void visit(Tree.LABEL node) {
        emit(new Assem.LABEL(node.label + ":", node.label));
    }

    public void visit(Tree.JUMP node) {
        if (node.exp instanceof Tree.NAME) {
            emit(new Assem.OPER("\tj `j0", temps(), temps(),
                    new ArrayList<Label>(node.targets)));
            return;
        }
        Temp target = node.exp.accept(this);
        emit(new Assem.OPER("\tjalr `s0", temps(), temps(target),
                new ArrayList<Label>(node.targets)));
    }

    public void visit(Tree.CJUMP node) {
        Temp left = node.left.accept(this);
        Temp right = node.right.accept(this);
        String mnemonic;
        switch (node.relop) {
        case Tree.CJUMP.EQ: mnemonic = "beq"; break;
        case Tree.CJUMP.NE: mnemonic = "bne"; break;
        case Tree.CJUMP.LT: mnemonic = "blt"; break;
        case Tree.CJUMP.GT: mnemonic = "bgt"; break;
        case Tree.CJUMP.LE: mnemonic = "ble"; break;
        case Tree.CJUMP.GE: mnemonic = "bge"; break;
        case Tree.CJUMP.ULT: mnemonic = "bltu"; break;
        case Tree.CJUMP.ULE: mnemonic = "bleu"; break;
        case Tree.CJUMP.UGT: mnemonic = "bgtu"; break;
        case Tree.CJUMP.UGE: mnemonic = "bgeu"; break;
        default: throw new IllegalArgumentException("unknown relation " + node.relop);
        }
        emit(new Assem.OPER("\t" + mnemonic + " `s0, `s1, `j0",
                temps(), temps(left, right), labels(node.iftrue, node.iffalse)));
    }

    private static boolean immediate(int value) {
        return value >= -2048 && value <= 2047;
    }

    private static boolean powerOfTwo(int value) {
        return value > 0 && (value & (value - 1)) == 0;
    }

    private static Tree.CONST rightConstant(Tree.BINOP expression) {
        return expression.right instanceof Tree.CONST
                ? (Tree.CONST)expression.right : null;
    }

    private static Tree.CONST leftConstant(Tree.BINOP expression) {
        return expression.left instanceof Tree.CONST
                ? (Tree.CONST)expression.left : null;
    }

    private Temp loadAddress(Tree.Exp address, int[] offset) {
        if (address instanceof Tree.BINOP
                && ((Tree.BINOP)address).binop == Tree.BINOP.PLUS) {
            Tree.BINOP addition = (Tree.BINOP)address;
            Tree.CONST right = rightConstant(addition);
            if (right != null && immediate(right.value)) {
                offset[0] = right.value;
                return addition.left.accept(this);
            }
            Tree.CONST left = leftConstant(addition);
            if (left != null && immediate(left.value)) {
                offset[0] = left.value;
                return addition.right.accept(this);
            }
        }
        offset[0] = 0;
        return address.accept(this);
    }

    public void visit(Tree.MOVE node) {
        if (node.dst instanceof Tree.MEM) {
            Temp source = node.src.accept(this);
            int[] offset = new int[1];
            Temp base = loadAddress(((Tree.MEM)node.dst).exp, offset);
            emit(new Assem.OPER("\tsw `s0, " + offset[0] + "(`s1)",
                    temps(), temps(source, base)));
            return;
        }
        if (!(node.dst instanceof Tree.TEMP)) {
            throw new IllegalStateException("canonical MOVE destination is not TEMP or MEM");
        }
        Temp destination = ((Tree.TEMP)node.dst).temp;
        Temp source = node.src.accept(this);
        emit(new Assem.MOVE("\tmv `d0, `s0", destination, source));
    }

    public void visit(Tree.EXP node) {
        node.exp.accept(this);
    }

    public Temp visit(Tree.BINOP node) {
        Temp result = new Temp();
        Tree.CONST right = rightConstant(node);
        Tree.CONST left = leftConstant(node);

        if (node.binop == Tree.BINOP.MUL) {
            Tree.CONST factor = right != null ? right : left;
            Tree.Exp operand = right != null ? node.left : node.right;
            if (factor != null && powerOfTwo(factor.value)) {
                Temp source = operand.accept(this);
                emit(new Assem.OPER("\tsll `d0, `s0, "
                        + Integer.numberOfTrailingZeros(factor.value),
                        temps(result), temps(source)));
                return result;
            }
        }
        if (node.binop == Tree.BINOP.DIV && right != null
                && powerOfTwo(right.value)) {
            Temp source = node.left.accept(this);
            emit(new Assem.OPER("\tsra `d0, `s0, "
                    + Integer.numberOfTrailingZeros(right.value),
                    temps(result), temps(source)));
            return result;
        }

        boolean commutative = node.binop == Tree.BINOP.PLUS
                || node.binop == Tree.BINOP.AND || node.binop == Tree.BINOP.OR
                || node.binop == Tree.BINOP.BITAND || node.binop == Tree.BINOP.BITOR
                || node.binop == Tree.BINOP.BITXOR;
        Tree.CONST constant = right;
        Tree.Exp operand = node.left;
        if (constant == null && commutative && left != null) {
            constant = left;
            operand = node.right;
        }

        if (constant != null && immediate(constant.value)) {
            String mnemonic = null;
            int value = constant.value;
            switch (node.binop) {
            case Tree.BINOP.PLUS: mnemonic = "addi"; break;
            case Tree.BINOP.MINUS:
                if (right != null && immediate(-value)) {
                    mnemonic = "addi";
                    value = -value;
                }
                break;
            case Tree.BINOP.AND:
            case Tree.BINOP.BITAND: mnemonic = "andi"; break;
            case Tree.BINOP.OR:
            case Tree.BINOP.BITOR: mnemonic = "ori"; break;
            case Tree.BINOP.BITXOR: mnemonic = "xori"; break;
            case Tree.BINOP.LSHIFT: mnemonic = "sll"; break;
            case Tree.BINOP.RSHIFT: mnemonic = "srl"; break;
            case Tree.BINOP.ARSHIFT: mnemonic = "sra"; break;
            default: break;
            }
            if (mnemonic != null) {
                Temp source = operand.accept(this);
                emit(new Assem.OPER("\t" + mnemonic + " `d0, `s0, " + value,
                        temps(result), temps(source)));
                return result;
            }
        }

        Temp first = node.left.accept(this);
        Temp second = node.right.accept(this);
        String mnemonic;
        switch (node.binop) {
        case Tree.BINOP.PLUS: mnemonic = "add"; break;
        case Tree.BINOP.MINUS: mnemonic = "sub"; break;
        case Tree.BINOP.MUL: mnemonic = "mul"; break;
        case Tree.BINOP.DIV: mnemonic = "div"; break;
        case Tree.BINOP.AND:
        case Tree.BINOP.BITAND: mnemonic = "and"; break;
        case Tree.BINOP.OR:
        case Tree.BINOP.BITOR: mnemonic = "or"; break;
        case Tree.BINOP.LSHIFT: mnemonic = "sll"; break;
        case Tree.BINOP.RSHIFT: mnemonic = "srl"; break;
        case Tree.BINOP.ARSHIFT: mnemonic = "sra"; break;
        case Tree.BINOP.BITXOR: mnemonic = "xor"; break;
        default: throw new IllegalArgumentException("unknown binary operation " + node.binop);
        }
        emit(new Assem.OPER("\t" + mnemonic + " `d0, `s0, `s1",
                temps(result), temps(first, second)));
        return result;
    }

    public Temp visit(Tree.MEM node) {
        Temp result = new Temp();
        int[] offset = new int[1];
        Temp base = loadAddress(node.exp, offset);
        emit(new Assem.OPER("\tlw `d0, " + offset[0] + "(`s0)",
                temps(result), temps(base)));
        return result;
    }

    public Temp visit(Tree.TEMP node) {
        return node.temp;
    }

    public Temp visit(Tree.ESEQ node) {
        throw new IllegalStateException("ESEQ reached instruction selection");
    }

    public Temp visit(Tree.NAME node) {
        Temp result = new Temp();
        emit(new Assem.OPER("\tla `d0, " + node.label,
                temps(result), temps()));
        return result;
    }

    public Temp visit(Tree.CONST node) {
        if (node.value == 0) return RiscvFrame.ZERO;
        Temp result = new Temp();
        emit(new Assem.OPER("\tli `d0, " + node.value,
                temps(result), temps()));
        return result;
    }

    public Temp visit(Tree.CALL node) {
        Temp function = null;
        Label direct = null;
        if (node.func instanceof Tree.NAME) direct = ((Tree.NAME)node.func).label;
        else function = node.func.accept(this);

        ArrayList<Temp> arguments = new ArrayList<Temp>();
        for (Tree.Exp argument : node.args) arguments.add(argument.accept(this));
        ArrayList<Temp> callUses = new ArrayList<Temp>();
        for (int index = 0; index < arguments.size(); index++) {
            Temp argument = arguments.get(index);
            if (index < RiscvFrame.argRegs.length) {
                Temp register = RiscvFrame.argRegs[index];
                emit(new Assem.MOVE("\tmv `d0, `s0", register, argument));
                callUses.add(register);
            } else {
                int offset = (index - RiscvFrame.argRegs.length) * frame.wordSize();
                emit(new Assem.OPER("\tsw `s0, " + offset + "(`s1)",
                        temps(), temps(argument, RiscvFrame.SP)));
                frame.maxArgOffset = Math.max(frame.maxArgOffset,
                        offset + frame.wordSize());
                if (!callUses.contains(RiscvFrame.SP)) callUses.add(RiscvFrame.SP);
            }
        }

        if (direct != null) {
            emit(new Assem.OPER("\tcall " + direct,
                    Arrays.asList(RiscvFrame.calldefs), callUses));
        } else {
            callUses.add(function);
            emit(new Assem.OPER("\tjalr `s" + (callUses.size() - 1),
                    Arrays.asList(RiscvFrame.calldefs), callUses));
        }
        return frame.RV();
    }
}
