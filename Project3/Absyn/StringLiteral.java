/**
 * COSC 4400 - Project #3
 * Stores a simple string literal.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
/* Copyright (C) 2009, Marquette University.  All rights reserved. */
package Absyn;

/**
 * String Literals.
 */

public class StringLiteral extends Expr
{
    public String value;

    public StringLiteral(String value)
    {
		this.value = value;
    }

    /** Visitor pattern dispatch. */
    public void accept(Visitor v) {v.visit(this); }
}
