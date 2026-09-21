/**
 * COSC 4400 - Project #3
 * Represents array allocation and its dimensions.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Absyn;
import java.util.LinkedList;
public class NewArrayExpr extends Expr {
    public Type type; public LinkedList<Expr> dimensions;
    public NewArrayExpr(Type type, LinkedList<Expr> dimensions) { this.type=type; this.dimensions=dimensions; }
    public void accept(Visitor v) { v.visit(this); }
}
