/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Tree;
import java.util.LinkedList;
import Temp.Temp;

/**
 * Abstract class that represents an expression in the Tree language.
 */

public abstract class Exp implements Hospitable
{
    public abstract void accept(IntVisitor v, int d);
    public abstract Temp accept(CodeVisitor visitor);
    public abstract LinkedList<Exp> kids();
    public abstract Exp build(LinkedList<Exp> kids);
}
/**
 * COSC 4400 - Project #8
 * Defines IR node behavior used by canonicalization and instruction selection.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
