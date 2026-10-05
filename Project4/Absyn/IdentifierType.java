/**
 * COSC 4400 - Project #3
 * Represents a class name used as a type.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Absyn;

/**
 * Class types.
 */

public class IdentifierType extends Type
{
    public String id;
    public IdentifierType(String id)
    {
		this.id = id;
    }

    /** Visitor pattern dispatch. */
    public void accept(Visitor v) {v.visit(this); }
}
