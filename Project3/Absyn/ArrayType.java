/**
 * COSC 4400 - Project #3
 * Represents an array type in the AST.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Absyn;

/**
 * Array type.
 */

public class ArrayType extends Type
{
    public Type base;
    public ArrayType(Type base)
    {
		this.base = base;
    }

    /** Visitor pattern dispatch. */
    public void accept(Visitor v) {v.visit(this); }
}
