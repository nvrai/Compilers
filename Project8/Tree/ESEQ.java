/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Tree;
import Temp.Temp;
import Temp.Label;
import java.util.LinkedList;

/** 
 * Implements an expression sequence, statment is evaluated
 * for side effects and the expression is evaluated as result.
 */

public class ESEQ extends Exp 
{
    public Stm stm;
    public Exp exp;
    public ESEQ(Stm s, Exp e) { stm=s; exp=e; }

    public void accept(IntVisitor v, int d) { v.visit(this, d); }
    public Temp accept(CodeVisitor visitor) { return visitor.visit(this); }
    public LinkedList<Exp> kids() {
        throw new IllegalStateException("ESEQ must be removed before instruction selection");
    }
    public Exp build(LinkedList<Exp> kids) {
        throw new IllegalStateException("ESEQ must be removed before instruction selection");
    }
}
/**
 * COSC 4400 - Project #8
 * Defines IR node behavior used by canonicalization and instruction selection.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
