/**
 * COSC 4400 - Project #7
 * Reads a checked MiniJava AST, translates it to IR, and prints its fragments.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Translate;

import java.io.BufferedInputStream;
import java.io.PrintWriter;

import Absyn.Program;
import Parse.ParseException;
import Parse.ReadTypes;
import Riscv.RiscvFrame;

/** Command-line entry point for the translation phase. */
public final class Main {
    private Main() {
    }

    public static void main(String[] arguments) {
        Program program;
        try {
            new ReadTypes(new BufferedInputStream(System.in));
            program = ReadTypes.Goal();
        } catch (ParseException error) {
            System.err.println(error.toString());
            System.exit(1);
            return;
        }

        Translate translator = new Translate(new RiscvFrame());
        program.accept(translator);

        PrintWriter writer = new PrintWriter(System.out);
        for (Frag fragment : translator.results()) {
            if (fragment instanceof DataFrag) {
                writer.println("DataFrag(");
                writer.println(fragment);
                writer.println(")");
            } else {
                ProcFrag procedure = (ProcFrag)fragment;
                writer.println("ProcFrag(");
                procedure.frame.printFrame(writer);
                if (procedure.body != null) {
                    new Tree.Print(writer, procedure.body);
                    writer.println();
                }
                writer.println(")");
            }
        }
        writer.flush();
    }
}
