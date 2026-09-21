/**
 * COSC 4400 - Project #3
 * Represents field selection on an object.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Absyn;
public class FieldExpr extends AssignableExpr {
    public Expr target; public String field; public FieldExpr(Expr target, String field) { this.target=target; this.field=field; }
    public void accept(Visitor v) { v.visit(this); }
}
