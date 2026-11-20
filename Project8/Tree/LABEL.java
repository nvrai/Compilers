/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Tree;
import Temp.Temp;
import Temp.Label;
import java.util.LinkedList;

/**
 * Define constant value as current code address.
 */

public class LABEL extends Stm 
{ 
    public Label label;
    public LABEL(Label l) { label=l; }
    public LinkedList<Exp> kids() { return new LinkedList<Exp>(); }
    public void accept(IntVisitor v, int d) { v.visit(this, d); }
    public void accept(CodeVisitor visitor) { visitor.visit(this); }
    public Stm build(LinkedList<Exp> kids) { return this; }
}
/**
 * COSC 4400 - Project #8
 * Defines IR node behavior used by canonicalization and instruction selection.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
