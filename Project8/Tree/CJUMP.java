/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Tree;
import Temp.Temp;
import Temp.Label;
import java.util.LinkedList;

/**
 * Implements a conditional jump.
 */

public class CJUMP extends Stm 
{
    public int relop;
    public Exp left, right;
    public Label iftrue, iffalse;
    public CJUMP(int rel, Exp l, Exp r, Label t, Label f) 
	{
		relop=rel; left=l; right=r; iftrue=t; iffalse=f;
    }

    public final static int EQ=0, NE=1, LT=2, GT=3, LE=4, GE=5,
		ULT=6, ULE=7, UGT=8, UGE=9;

    public void accept(IntVisitor v, int d) { v.visit(this, d); }
    public void accept(CodeVisitor visitor) { visitor.visit(this); }
    public LinkedList<Exp> kids() {
        LinkedList<Exp> result = new LinkedList<Exp>();
        result.add(left);
        result.add(right);
        return result;
    }
    public Stm build(LinkedList<Exp> kids) {
        return new CJUMP(relop, kids.get(0), kids.get(1), iftrue, iffalse);
    }
    public static int notRel(int relation) {
        switch (relation) {
        case EQ: return NE;
        case NE: return EQ;
        case LT: return GE;
        case GE: return LT;
        case GT: return LE;
        case LE: return GT;
        case ULT: return UGE;
        case UGE: return ULT;
        case UGT: return ULE;
        case ULE: return UGT;
        default: throw new IllegalArgumentException("unknown relation " + relation);
        }
    }
}
/**
 * COSC 4400 - Project #8
 * Defines IR node behavior used by canonicalization and instruction selection.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
