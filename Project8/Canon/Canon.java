/**
 * COSC 4400 - Project #8
 * Canonicalizes IR by removing ESEQ/SEQ nodes and lifting nested calls.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Canon;

import java.util.LinkedList;
import java.util.List;
import Temp.Temp;

/** Appel-style canonicalization for the Tree intermediate representation. */
public final class Canon {
    private Canon() {
    }

    private static final class StmExps {
        final Tree.Stm stm;
        final LinkedList<Tree.Exp> exps;

        StmExps(Tree.Stm stm, LinkedList<Tree.Exp> exps) {
            this.stm = stm;
            this.exps = exps;
        }
    }

    private static Tree.Stm nop() {
        return new Tree.EXP(new Tree.CONST(0));
    }

    private static boolean isNop(Tree.Stm statement) {
        return statement instanceof Tree.EXP
                && ((Tree.EXP)statement).exp instanceof Tree.CONST;
    }

    private static Tree.Stm sequence(Tree.Stm left, Tree.Stm right) {
        if (isNop(left)) return right;
        if (isNop(right)) return left;
        return new Tree.SEQ(left, right);
    }

    private static boolean commutes(Tree.Stm statement, Tree.Exp expression) {
        return isNop(statement) || expression instanceof Tree.NAME
                || expression instanceof Tree.CONST;
    }

    private static Tree.Stm doStm(Tree.Stm statement) {
        if (statement instanceof Tree.SEQ) {
            Tree.SEQ sequence = (Tree.SEQ)statement;
            return Canon.sequence(doStm(sequence.left), doStm(sequence.right));
        }
        if (statement instanceof Tree.MOVE) {
            Tree.MOVE move = (Tree.MOVE)statement;
            if (move.dst instanceof Tree.ESEQ) {
                Tree.ESEQ destination = (Tree.ESEQ)move.dst;
                return doStm(new Tree.SEQ(destination.stm,
                        new Tree.MOVE(destination.exp, move.src)));
            }
            if (move.dst instanceof Tree.TEMP && move.src instanceof Tree.CALL) {
                Tree.CALL call = (Tree.CALL)move.src;
                StmExps reordered = reorder(call.kids());
                return sequence(reordered.stm,
                        new Tree.MOVE(move.dst, call.build(reordered.exps)));
            }
        }
        if (statement instanceof Tree.EXP
                && ((Tree.EXP)statement).exp instanceof Tree.CALL) {
            Tree.CALL call = (Tree.CALL)((Tree.EXP)statement).exp;
            StmExps reordered = reorder(call.kids());
            return sequence(reordered.stm,
                    new Tree.EXP(call.build(reordered.exps)));
        }
        StmExps reordered = reorder(statement.kids());
        return sequence(reordered.stm, statement.build(reordered.exps));
    }

    private static Tree.ESEQ doExp(Tree.Exp expression) {
        if (expression instanceof Tree.CALL) {
            Temp temporary = new Temp();
            return doExp(new Tree.ESEQ(
                    new Tree.MOVE(new Tree.TEMP(temporary), expression),
                    new Tree.TEMP(temporary)));
        }
        if (expression instanceof Tree.ESEQ) {
            Tree.ESEQ eseq = (Tree.ESEQ)expression;
            Tree.Stm statement = doStm(eseq.stm);
            Tree.ESEQ result = doExp(eseq.exp);
            return new Tree.ESEQ(sequence(statement, result.stm), result.exp);
        }
        StmExps reordered = reorder(expression.kids());
        return new Tree.ESEQ(reordered.stm, expression.build(reordered.exps));
    }

    private static StmExps reorder(List<Tree.Exp> expressions) {
        if (expressions == null || expressions.isEmpty()) {
            return new StmExps(nop(), new LinkedList<Tree.Exp>());
        }
        LinkedList<Tree.Exp> remaining = new LinkedList<Tree.Exp>(expressions);
        Tree.Exp first = remaining.removeFirst();
        if (first instanceof Tree.CALL) {
            Temp temporary = new Temp();
            Tree.Exp replacement = new Tree.ESEQ(
                    new Tree.MOVE(new Tree.TEMP(temporary), first),
                    new Tree.TEMP(temporary));
            remaining.addFirst(replacement);
            return reorder(remaining);
        }

        Tree.ESEQ head = doExp(first);
        StmExps tail = reorder(remaining);
        LinkedList<Tree.Exp> results = new LinkedList<Tree.Exp>();
        if (commutes(tail.stm, head.exp)) {
            results.add(head.exp);
            results.addAll(tail.exps);
            return new StmExps(sequence(head.stm, tail.stm), results);
        }

        Temp temporary = new Temp();
        results.add(new Tree.TEMP(temporary));
        results.addAll(tail.exps);
        Tree.Stm statements = sequence(head.stm,
                sequence(new Tree.MOVE(new Tree.TEMP(temporary), head.exp),
                        tail.stm));
        return new StmExps(statements, results);
    }

    private static void linear(Tree.Stm statement, List<Tree.Stm> result) {
        if (statement instanceof Tree.SEQ) {
            Tree.SEQ sequence = (Tree.SEQ)statement;
            linear(sequence.left, result);
            linear(sequence.right, result);
        } else {
            result.add(statement);
        }
    }

    /** Returns the canonical, linear statement list for a procedure body. */
    public static List<Tree.Stm> linearize(Tree.Stm statement) {
        LinkedList<Tree.Stm> result = new LinkedList<Tree.Stm>();
        if (statement != null) linear(doStm(statement), result);
        return result;
    }
}
