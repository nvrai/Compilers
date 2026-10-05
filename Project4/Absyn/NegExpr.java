/**
 * COSC 4400 - Project #3
 * Represents unary numeric negation.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Absyn;
public class NegExpr extends Expr { public Expr e1; public NegExpr(Expr e1){this.e1=e1;} public void accept(Visitor v){v.visit(this);} }
