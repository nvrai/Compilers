/**
 * COSC 4400 - Project #3
 * Stores a void method declared by a thread class.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Absyn;
import java.util.LinkedList;
public class VoidDecl extends MethodDecl {
    public VoidDecl(String name, LinkedList<VarDecl> locals, LinkedList<Stmt> stmts) {
        super(null, false, name, new LinkedList<Formal>(), locals, stmts, null);
    }
    public void accept(Visitor v) { v.visit(this); }
}
