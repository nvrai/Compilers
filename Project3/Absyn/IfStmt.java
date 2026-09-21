/**
 * COSC 4400 - Project #3
 * Represents an if statement with an optional else branch.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Absyn;
public class IfStmt extends Stmt {
    public Expr test; public Stmt thenStm, elseStm; public IfStmt(Expr test, Stmt thenStm, Stmt elseStm){this.test=test;this.thenStm=thenStm;this.elseStm=elseStm;}
    public void accept(Visitor v){v.visit(this);}
}
