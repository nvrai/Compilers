/**
 * COSC 4400 - Project #3
 * Represents an assignment statement in the AST.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Absyn;
public class AssignStmt extends Stmt {
    public AssignableExpr lhs; public Expr rhs; public AssignStmt(AssignableExpr lhs, Expr rhs){this.lhs=lhs;this.rhs=rhs;}
    public void accept(Visitor v){v.visit(this);}
}
