/**
 * COSC 4400 - Project #4
 * Collects semantic errors in the order in which they are discovered.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Semant;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ErrorReporter {
    private final List<String> errors = new ArrayList<String>();

    public void report(String message) {
        errors.add("ERROR " + message);
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
