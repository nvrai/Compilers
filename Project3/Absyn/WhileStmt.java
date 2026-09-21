/**
 * COSC 4400 - Project #3
 * Represents a while-loop statement.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Absyn;
public class WhileStmt extends Stmt { public Expr test; public Stmt body; public WhileStmt(Expr test, Stmt body){this.test=test;this.body=body;} public void accept(Visitor v){v.visit(this);} }
