/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Tree;
import Temp.Temp;
import Temp.Label;
import java.util.List;
import java.util.LinkedList;

/**
 * Implements a procedure call, application of a function to an
 * argument list.
 */

public class CALL extends Exp 
{
    public Exp func;
    public List<Exp> args;
    public CALL(Exp f, List<Exp> a) { func=f; args=a; }
    public void accept(IntVisitor v, int d) { v.visit(this, d); }
    public Temp accept(CodeVisitor visitor) { return visitor.visit(this); }
    public LinkedList<Exp> kids() {
        LinkedList<Exp> result = new LinkedList<Exp>();
        result.add(func);
        result.addAll(args);
        return result;
    }
    public Exp build(LinkedList<Exp> kids) {
        LinkedList<Exp> arguments = new LinkedList<Exp>(kids);
        Exp function = arguments.removeFirst();
        return new CALL(function, arguments);
    }
}
/**
 * COSC 4400 - Project #8
 * Defines IR node behavior used by canonicalization and instruction selection.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
