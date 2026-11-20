/**
 * COSC 4400 - Project #8
 * Defines the visitor interface used to tile canonical IR trees into instructions.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Tree;

import Temp.Temp;

/** Visitor whose expression methods return the temporary holding each result. */
public interface CodeVisitor {
    void visit(SEQ node);
    void visit(LABEL node);
    void visit(JUMP node);
    void visit(CJUMP node);
    void visit(MOVE node);
    void visit(EXP node);
    Temp visit(BINOP node);
    Temp visit(MEM node);
    Temp visit(TEMP node);
    Temp visit(ESEQ node);
    Temp visit(NAME node);
    Temp visit(CONST node);
    Temp visit(CALL node);
}
