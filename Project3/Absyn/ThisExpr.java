/**
 * COSC 4400 - Project #3
 * Represents the this expression.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Absyn;
public class ThisExpr extends Expr { public void accept(Visitor v){v.visit(this);} }
