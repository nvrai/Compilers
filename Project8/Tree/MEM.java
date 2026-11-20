/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Tree;
import Temp.Temp;
import Temp.Label;
import java.util.LinkedList;

/**
 * Contents of a word of memory starting at address e.
 */

public class MEM extends Exp 
{
    public Exp exp;
    public MEM(Exp e) { exp=e; }
    public void accept(IntVisitor v, int d) { v.visit(this, d); }
    public Temp accept(CodeVisitor visitor) { return visitor.visit(this); }
    public LinkedList<Exp> kids() {
        LinkedList<Exp> result = new LinkedList<Exp>();
        result.add(exp);
        return result;
    }
    public Exp build(LinkedList<Exp> kids) { return new MEM(kids.getFirst()); }
}
/**
 * COSC 4400 - Project #8
 * Defines IR node behavior used by canonicalization and instruction selection.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
