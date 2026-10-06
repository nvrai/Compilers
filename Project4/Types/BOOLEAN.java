/**
 * COSC 4400 - Project #4
 * Provides a type descriptor used during MiniJava semantic analysis.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Types;

/**
 * Basetype "boolean".
 */

public class BOOLEAN extends Type
{
    public BOOLEAN()
    {}

    public String toString()
    { return "boolean"; }

    public boolean coerceTo(Type t)
    {   return (t instanceof BOOLEAN);   }

    /** Visitor pattern dispatch. */
    public void accept(Visitor v)
    {   v.visit(this);   }
}
