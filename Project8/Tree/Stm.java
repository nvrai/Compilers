/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Tree;
import Temp.Temp;
import java.util.LinkedList;

/**
 * An abstract class that represents a statment in the Tree language.
 */

abstract public class Stm implements Hospitable
{
    public abstract void accept(IntVisitor v, int d);
    public abstract void accept(CodeVisitor visitor);
    public abstract LinkedList<Exp> kids();
    public abstract Stm build(LinkedList<Exp> kids);
}
/**
 * COSC 4400 - Project #8
 * Defines IR node behavior used by canonicalization and instruction selection.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
