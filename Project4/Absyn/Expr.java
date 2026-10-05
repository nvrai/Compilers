/**
 * COSC 4400 - Project #3
 * Provides the base class shared by all expressions.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Absyn;

/**
 * Expression abstract class.
 */

public abstract class Expr extends Absyn
{
    /** Visitor pattern dispatch. */
    public abstract void accept(Visitor v);
}
