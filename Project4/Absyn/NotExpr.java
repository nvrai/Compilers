/**
 * COSC 4400 - Project #3
 * Represents logical negation.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Absyn;
public class NotExpr extends Expr { public Expr e1; public NotExpr(Expr e1){this.e1=e1;} public void accept(Visitor v){v.visit(this);} }
