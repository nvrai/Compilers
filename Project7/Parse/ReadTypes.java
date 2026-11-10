/**
 * COSC 4400 - Project #7
 * Provides the grader-facing entry point for reading checked abstract syntax trees.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Parse;

import java.io.InputStream;
import java.io.Reader;

import Absyn.Program;

/** Delegates to the checked-AST parser generated for the previous phase. */
public final class ReadTypes {
    public ReadTypes(InputStream input) {
        new ReadAbsyn(input);
    }

    public ReadTypes(Reader input) {
        new ReadAbsyn(input);
    }

    public static Program Goal() throws ParseException {
        return ReadAbsyn.Goal();
    }
}
