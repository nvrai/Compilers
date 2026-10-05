/**
 * COSC 4400 - Project #3
 * Represents an addition expression in the AST.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Absyn;
public class AddExpr extends BinOpExpr { public AddExpr(Expr e1, Expr e2){super(e1,e2);} public void accept(Visitor v){v.visit(this);} }
