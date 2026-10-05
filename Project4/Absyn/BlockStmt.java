/**
 * COSC 4400 - Project #3
 * Represents a block containing a list of statements.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Absyn;
import java.util.LinkedList;
public class BlockStmt extends Stmt { public LinkedList<Stmt> stmts; public BlockStmt(LinkedList<Stmt> stmts){this.stmts=stmts;} public void accept(Visitor v){v.visit(this);} }
