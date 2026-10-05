/**
 * COSC 4400 - Project #3
 * Represents an identifier used as an expression.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Absyn;
public class IdentifierExpr extends AssignableExpr {
    public String id; public IdentifierExpr(String id) { this.id=id; }
    public void accept(Visitor v) { v.visit(this); }
}
