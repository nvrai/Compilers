/**
 * COSC 4400 - Project #4
 * Reads an abstract syntax tree and builds its MiniJava class descriptors.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Semant;

import java.io.BufferedInputStream;
import java.io.PrintWriter;

import Absyn.Program;
import Parse.ParseException;
import Parse.ReadAbsyn;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        PrintWriter out = new PrintWriter(System.out);
        PrintWriter errorOut = new PrintWriter(System.err);
        try {
            new ReadAbsyn(new BufferedInputStream(System.in));
            Program program = ReadAbsyn.Goal();
            ErrorReporter errors = new ErrorReporter();
            ClassTable classes = ClassTable.build(program, errors);
            errors.printTo(errorOut);
            Types.PrintVisitor printer = new Types.PrintVisitor(out);
            for (Types.CLASS descriptor : classes.programClasses()) {
                descriptor.accept(printer);
                out.println();
            }
            out.flush();
        } catch (ParseException error) {
            out.println(error.toString());
            out.flush();
            System.exit(1);
        } catch (IllegalArgumentException error) {
            out.println(error.getMessage());
            out.flush();
            System.exit(1);
        }
    }
}
