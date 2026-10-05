/**
 * COSC 4400 - Project #3
 * Represents a logical OR expression in the AST.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Absyn;
public class OrExpr extends BinOpExpr { public OrExpr(Expr e1, Expr e2){super(e1,e2);} public void accept(Visitor v){v.visit(this);} }
