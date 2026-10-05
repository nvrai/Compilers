/**
 * COSC 4400 - Project #3
 * Represents an indexed array expression in the AST.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Absyn;
public class ArrayExpr extends AssignableExpr {
    public Expr target, index; public ArrayExpr(Expr target, Expr index) { this.target=target; this.index=index; }
    public void accept(Visitor v) { v.visit(this); }
}
