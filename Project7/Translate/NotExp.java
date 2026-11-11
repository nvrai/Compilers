/**
 * COSC 4400 - Project #7
 * Represents logical negation in value, statement, and conditional IR forms.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Translate;

import Temp.Label;

/** A Boolean negation whose branch form reverses its destination labels. */
final class NotExp extends Exp {
    private final Exp expression;

    NotExp(Exp expression) {
        this.expression = expression;
    }

    Tree.Exp unEx() {
        return new Tree.BINOP(Tree.BINOP.BITXOR,
                expression.unEx(), new Tree.CONST(1));
    }

    Tree.Stm unNx() {
        return expression.unNx();
    }

    Tree.Stm unCx(Label trueLabel, Label falseLabel) {
        return expression.unCx(falseLabel, trueLabel);
    }
}
