/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Tree;
import Temp.Temp;
import java.util.LinkedList;

/**
 * A temporary t.
 */

public class TEMP extends Exp 
{
    public Temp temp;
    public TEMP(Temp t) 
	{ 
		temp=t; 
		//System.out.println("\n Call to TEMP : "+t.toString());
	}
    public void accept(IntVisitor v, int d) { v.visit(this, d); }
    public Temp accept(CodeVisitor visitor) { return visitor.visit(this); }
    public LinkedList<Exp> kids() { return new LinkedList<Exp>(); }
    public Exp build(LinkedList<Exp> kids) { return this; }
}
/**
 * COSC 4400 - Project #8
 * Defines IR node behavior used by canonicalization and instruction selection.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
