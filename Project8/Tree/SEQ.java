/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Tree;
import Temp.Temp;
import Temp.Label;
import java.util.LinkedList;

/**
 * Sequence: statement 1 followed by statment 2.
 */

public class SEQ extends Stm 
{
    public Stm left, right;
    public SEQ(Stm l, Stm r) { left=l; right=r; }
    public void accept(IntVisitor v, int d) { v.visit(this, d); }
    public void accept(CodeVisitor visitor) { visitor.visit(this); }
    public LinkedList<Exp> kids() {
        throw new IllegalStateException("SEQ must be removed before instruction selection");
    }
    public Stm build(LinkedList<Exp> kids) {
        throw new IllegalStateException("SEQ must be removed before instruction selection");
    }
}
/**
 * COSC 4400 - Project #8
 * Defines IR node behavior used by canonicalization and instruction selection.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
