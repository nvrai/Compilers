/**
 * COSC 4400 - Project #7
 * Represents translated expressions that execute only for side effects.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Translate;
import Temp.Label;

/**
 * Stands for "no result", represented as a Tree statement.
 */

public class Nx extends Exp {
    Tree.Stm stm;
    Nx(Tree.Stm s) { stm = s; }

    Tree.Exp unEx() {
        throw new IllegalStateException("statement used as an expression");
    }

    Tree.Stm unNx() { return stm; }

    Tree.Stm unCx(Label t, Label f) {
        throw new IllegalStateException("statement used as a condition");
    }
}
