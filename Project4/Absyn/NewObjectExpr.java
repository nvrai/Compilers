/**
 * COSC 4400 - Project #3
 * Represents object construction with new.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Absyn;
public class NewObjectExpr extends Expr {
    public Type type; public NewObjectExpr(Type type) { this.type=type; }
    public void accept(Visitor v) { v.visit(this); }
}
