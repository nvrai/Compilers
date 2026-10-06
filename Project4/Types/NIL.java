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
 * Base type for "null".
 */

public class NIL extends Type
{
    public NIL()
    {}

    public String toString()
    { return "NIL"; }

    public boolean coerceTo(Type t)
    {   return ( (t instanceof NIL) 
		 || (t instanceof ARRAY)
		 || (t instanceof OBJECT) );   
    }

    /** Visitor pattern dispatch. */
    public void accept(Visitor v)
    {   v.visit(this);   }
}
