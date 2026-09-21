/**
 * COSC 4400 - Project #3
 * Represents a Xinu call used as a statement.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
/* Copyright (C) 2013, Marquette University.  All rights reserved. */
package Absyn;
import java.util.LinkedList;

/**
 * Xinu Statements.
 */

public class XinuCallStmt extends Stmt
{
    public String method;
    public LinkedList<Expr> args;
    public XinuCallStmt(String method, LinkedList<Expr> args)
    {
		this.method = method;
		this.args   = args;
    }

    /** Visitor pattern dispatch. */
    public void accept(Visitor v) {v.visit(this); }
}
