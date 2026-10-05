/**
 * COSC 4400 - Project #3
 * Stores the two operands shared by binary expressions.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Absyn;
public abstract class BinOpExpr extends Expr {
    public Expr e1, e2;
    public BinOpExpr(Expr e1, Expr e2) { this.e1=e1; this.e2=e2; }
}
