/**
 * COSC 4400 - Project #3
 * Provides the base class shared by all types.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Absyn;

/**
 * Type abstract class.
 */

public abstract class Type extends Absyn
{
    /** Visitor pattern dispatch. */
    public abstract void accept(Visitor v);
}
