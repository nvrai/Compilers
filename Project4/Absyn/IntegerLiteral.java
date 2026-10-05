/**
 * COSC 4400 - Project #3
 * Stores a decimal integer literal.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Absyn;

/**
 * Integer Literals.
 */

public class IntegerLiteral extends Expr
{
    public int value;

    public IntegerLiteral(int value)
    {
		this.value = value;
    }

    public IntegerLiteral(Integer value)
    {
		this.value = value.intValue();
    }

    /** Visitor pattern dispatch. */
    public void accept(Visitor v) {v.visit(this); }
}
