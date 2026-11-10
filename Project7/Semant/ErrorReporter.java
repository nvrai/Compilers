/**
 * COSC 4400 - Project 5
 * Collects type-checking errors in the order in which they are discovered.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Semant;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Absyn.Visitable;

public final class ErrorReporter {
    private final List<String> errors = new ArrayList<String>();

    public void report(String message) {
        errors.add("ERROR " + message);
    }

    /** Reports an error followed by the reference checker's indented AST context. */
    public void report(String message, Visitable node) {
        StringWriter context = new StringWriter();
        PrintWriter contextOut = new PrintWriter(context);
        Absyn.PrintVisitor printer = new Absyn.PrintVisitor(contextOut);
        printer.indentCount = 10;
        node.accept(printer);
        contextOut.flush();
        errors.add("ERROR " + message + ": " + context.toString());
    }

    public boolean anyErrors() {
        return !errors.isEmpty();
    }

    public List<String> errors() {
        return Collections.unmodifiableList(errors);
    }

    public void printTo(PrintWriter out) {
        for (String error : errors) out.println(error);
        out.flush();
    }
}
