/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Tree;
import Temp.Temp;
import Temp.Label;
import java.util.LinkedList;

/**
 * Implements a move instruction.
 */

public class MOVE extends Stm 
{
    public Exp dst, src;
    public MOVE(Exp d, Exp s) { dst=d; src=s; }
    public void accept(IntVisitor v, int d) { v.visit(this, d); }
    public void accept(CodeVisitor visitor) { visitor.visit(this); }
    public LinkedList<Exp> kids() {
        LinkedList<Exp> result = new LinkedList<Exp>();
        if (dst instanceof MEM) result.add(((MEM)dst).exp);
        result.add(src);
        return result;
    }
    public Stm build(LinkedList<Exp> kids) {
        if (dst instanceof MEM) return new MOVE(new MEM(kids.get(0)), kids.get(1));
        return new MOVE(dst, kids.getFirst());
    }
}
/**
 * COSC 4400 - Project #8
 * Defines IR node behavior used by canonicalization and instruction selection.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
