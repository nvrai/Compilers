/**
 * COSC 4400 - Project #7
 * Represents a variable stored at an offset in a RISC-V stack frame.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Riscv;

/**
 * InFrame represents formal variables or local variables that may be stored
 *  in the activation record ("frame").
 * See MCIJ2e Section 6.2.
 */
public class InFrame extends Frame.Access {
    int offset;

    public InFrame(int o) {
	offset = o;
    }

    public Tree.Exp exp(Tree.Exp fp) {
	return new Tree.MEM(new Tree.BINOP(Tree.BINOP.PLUS, fp, new Tree.CONST(offset)));
    }

    public Tree.Exp exp(Frame.Frame f) {
	return new Tree.MEM(new Tree.BINOP(Tree.BINOP.PLUS, new Tree.TEMP(f.FP()),
					   new Tree.CONST(offset)));
    }

    public String toString() {
	return Integer.toString(offset);
    }
}
