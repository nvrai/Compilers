/**
 * COSC 4400 - Project 5
 * Provides the semantic-analysis entry point expected by the grading harness.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Semant;

import Absyn.Program;

/** Delegates a complete program type-checking pass to {@link TypeChecker}. */
public final class TypeCheckVisitor {
    private final TypeChecker checker;

    public TypeCheckVisitor(ClassTable classes, ErrorReporter errors) {
        checker = new TypeChecker(classes, errors);
    }

    public void check(Program program) {
        checker.check(program);
    }
}
