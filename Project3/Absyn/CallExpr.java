/**
 * COSC 4400 - Project #3
 * Represents a method-call expression in the AST.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Absyn;
import java.util.LinkedList;
public class CallExpr extends Expr {
    public Expr target; public String method; public LinkedList<Expr> args;
    public CallExpr(Expr target, String method, LinkedList<Expr> args) { this.target=target; this.method=method; this.args=args; }
    public void accept(Visitor v) { v.visit(this); }
}
