/**
 * COSC 4400 - Project #3
 * Stores a formal method parameter and its type.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Absyn;

/**
 * Declarations for formal method parameters.
 */

public class Formal extends Absyn
{
    public Type type;
    public String name;
    public Formal(Type type, String name)
    {
		this.type = type;
		this.name = name;
    }

    /** Visitor pattern dispatch. */
    public void accept(Visitor v) {v.visit(this); }
}
