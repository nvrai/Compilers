/**
 * COSC 4400 - Project #7
 * Converts conditional expressions into value, statement, or branch IR forms.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Translate;

import Temp.Label;
import Temp.Temp;

/** Represents a conditional that can be used as an expression or statement. */
public class IfThenElseExp extends Exp {
    private final Exp condition;
    private final Exp trueExpression;
    private final Exp falseExpression;

    IfThenElseExp(Exp condition, Exp trueExpression, Exp falseExpression) {
        this.condition = condition;
        this.trueExpression = trueExpression;
        this.falseExpression = falseExpression;
    }

    private static Tree.Stm seq(Tree.Stm left, Tree.Stm right) {
        if (left == null) return right;
        if (right == null) return left;
        return new Tree.SEQ(left, right);
    }

    Tree.Stm unCx(Label trueLabel, Label falseLabel) {
        Label thenLabel = new Label();
        Label elseLabel = new Label();
        return seq(condition.unCx(thenLabel, elseLabel),
                seq(new Tree.LABEL(thenLabel),
                seq(trueExpression.unCx(trueLabel, falseLabel),
                seq(new Tree.LABEL(elseLabel),
                        falseExpression.unCx(trueLabel, falseLabel)))));
    }

    Tree.Exp unEx() {
        Temp result = new Temp();
        Label thenLabel = new Label();
        Label elseLabel = new Label();
        Label joinLabel = new Label();

        Tree.Stm statements = seq(condition.unCx(thenLabel, elseLabel),
                seq(new Tree.LABEL(thenLabel),
                seq(new Tree.MOVE(new Tree.TEMP(result), trueExpression.unEx()),
                seq(new Tree.JUMP(joinLabel),
                seq(new Tree.LABEL(elseLabel),
                seq(new Tree.MOVE(new Tree.TEMP(result), falseExpression.unEx()),
                seq(new Tree.JUMP(joinLabel), new Tree.LABEL(joinLabel))))))));
        return new Tree.ESEQ(statements, new Tree.TEMP(result));
    }

    Tree.Stm unNx() {
        Label thenLabel = new Label();
        Label elseLabel = new Label();
        Label joinLabel = new Label();
        return seq(condition.unCx(thenLabel, elseLabel),
                seq(new Tree.LABEL(thenLabel),
                seq(trueExpression.unNx(),
                seq(new Tree.JUMP(joinLabel),
                seq(new Tree.LABEL(elseLabel),
                seq(falseExpression.unNx(),
                seq(new Tree.JUMP(joinLabel), new Tree.LABEL(joinLabel))))))));
    }
}
