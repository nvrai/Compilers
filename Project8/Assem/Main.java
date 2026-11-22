/**
 * COSC 4400 - Project #8
 * Translates MiniJava through IR canonicalization and RISC-V instruction selection.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Assem;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import Absyn.Program;
import Parse.MiniJavaParser;
import Parse.ParseException;
import Parse.ReadTypes;
import Riscv.Codegen;
import Riscv.RiscvFrame;
import Translate.DataFrag;
import Translate.Frag;
import Translate.ProcFrag;
import Translate.Translate;

/** Command-line entry point for instruction selection. */
public final class Main {
    private static final Pattern DATA_LABEL =
            Pattern.compile("(?m)^(L[0-9]+)(?=:)");

    private Main() {
    }

    /** Matches the label interning performed by the reference IR reader. */
    private static final class LabelNormalizer {
        private final Map<String, String> names = new HashMap<String, String>();
        private final Set<Temp.Label> visited = Collections.newSetFromMap(
                new IdentityHashMap<Temp.Label, Boolean>());
        private int next;

        private String name(String original) {
            if (!original.matches("L[0-9]+")) return original;
            String normalized = names.get(original);
            if (normalized == null) {
                normalized = "L" + next++;
                names.put(original, normalized);
            }
            return normalized;
        }

        void data(DataFrag fragment) {
            Matcher matcher = DATA_LABEL.matcher(fragment.data);
            StringBuffer normalized = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(normalized,
                        Matcher.quoteReplacement(name(matcher.group(1))));
            }
            matcher.appendTail(normalized);
            fragment.data = normalized.toString();
        }

        private void label(Temp.Label label) {
            if (!visited.add(label)) return;
            label.rename(name(label.toString()));
        }

        private void statement(Tree.Stm statement) {
            if (statement instanceof Tree.SEQ) {
                Tree.SEQ sequence = (Tree.SEQ)statement;
                statement(sequence.left);
                statement(sequence.right);
            } else if (statement instanceof Tree.LABEL) {
                label(((Tree.LABEL)statement).label);
            } else if (statement instanceof Tree.JUMP) {
                Tree.JUMP jump = (Tree.JUMP)statement;
                expression(jump.exp);
                for (Temp.Label target : jump.targets) label(target);
            } else if (statement instanceof Tree.CJUMP) {
                Tree.CJUMP jump = (Tree.CJUMP)statement;
                expression(jump.left);
                expression(jump.right);
                label(jump.iftrue);
                label(jump.iffalse);
            } else if (statement instanceof Tree.MOVE) {
                Tree.MOVE move = (Tree.MOVE)statement;
                expression(move.dst);
                expression(move.src);
            } else if (statement instanceof Tree.EXP) {
                expression(((Tree.EXP)statement).exp);
            }
        }

        private void expression(Tree.Exp expression) {
            if (expression instanceof Tree.BINOP) {
                Tree.BINOP operation = (Tree.BINOP)expression;
                expression(operation.left);
                expression(operation.right);
            } else if (expression instanceof Tree.MEM) {
                expression(((Tree.MEM)expression).exp);
            } else if (expression instanceof Tree.ESEQ) {
                Tree.ESEQ eseq = (Tree.ESEQ)expression;
                statement(eseq.stm);
                expression(eseq.exp);
            } else if (expression instanceof Tree.NAME) {
                label(((Tree.NAME)expression).label);
            } else if (expression instanceof Tree.CALL) {
                Tree.CALL call = (Tree.CALL)expression;
                expression(call.func);
                for (Tree.Exp argument : call.args) expression(argument);
            }
        }

        void procedure(ProcFrag fragment) {
            statement(fragment.body);
        }

        void finish() {
            Temp.Label.setCount(next);
        }
    }

    private static void normalizeLabels(List<Frag> fragments) {
        LabelNormalizer normalizer = new LabelNormalizer();
        for (Frag fragment : fragments) {
            if (fragment instanceof DataFrag) normalizer.data((DataFrag)fragment);
            else normalizer.procedure((ProcFrag)fragment);
        }
        normalizer.finish();
    }

    private static String readInput() throws IOException {
        Reader reader = new InputStreamReader(System.in);
        StringBuilder input = new StringBuilder();
        char[] buffer = new char[4096];
        int count;
        while ((count = reader.read(buffer)) >= 0) input.append(buffer, 0, count);
        return input.toString();
    }

    private static Program parse(String input) throws ParseException {
        if (input.trim().startsWith("Program(")) {
            new ReadTypes(new StringReader(input));
            return ReadTypes.Goal();
        }
        new MiniJavaParser(new StringReader(input));
        return MiniJavaParser.Goal();
    }

    private static void emitProcedure(PrintWriter writer, ProcFrag procedure) {
        List<Tree.Stm> linear = Canon.Canon.linearize(procedure.body);
        Canon.BasicBlocks blocks = new Canon.BasicBlocks(linear);
        List<Tree.Stm> traced = new Canon.TraceSchedule(blocks).stms;
        if (blocks.blocks.size() == 1 && traced.size() >= 2
                && traced.get(traced.size() - 2) instanceof Tree.JUMP) {
            Tree.JUMP jump = (Tree.JUMP)traced.get(traced.size() - 2);
            if (jump.targets.size() == 1
                    && jump.targets.getFirst().toString().equals(
                            blocks.done.toString())) {
                traced.remove(traced.size() - 2);
            }
        }
        RiscvFrame frame = (RiscvFrame)procedure.frame;
        frame.procEntryExit1(traced);
        List<Instr> instructions = new Codegen(frame).codegen(traced);
        frame.procEntryExit2(instructions);

        writer.println("ProcFrag(");
        frame.printFrame(writer);
        for (Instr instruction : instructions) writer.println("  " + instruction);
        writer.println(")");
    }

    public static void main(String[] arguments) {
        Program program;
        try {
            program = parse(readInput());
        } catch (IOException error) {
            System.err.println(error.toString());
            System.exit(1);
            return;
        } catch (ParseException error) {
            System.err.println(error.toString());
            System.exit(1);
            return;
        }

        Translate translator = new Translate(new RiscvFrame());
        program.accept(translator);
        List<Frag> fragments = translator.results();
        normalizeLabels(fragments);
        PrintWriter writer = new PrintWriter(System.out);
        for (Frag fragment : fragments) {
            if (fragment instanceof DataFrag) {
                writer.println("DataFrag(");
                writer.println(fragment);
                writer.println(")");
            }
            else emitProcedure(writer, (ProcFrag)fragment);
        }
        writer.flush();
    }
}
