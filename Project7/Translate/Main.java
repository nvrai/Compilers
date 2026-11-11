/**
 * COSC 4400 - Project #7
 * Reads MiniJava source or a checked AST, translates it to IR, and prints its fragments.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Translate;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;

import Absyn.Program;
import Parse.MiniJavaParser;
import Parse.ParseException;
import Parse.ReadTypes;
import Riscv.RiscvFrame;

/** Command-line entry point for the translation phase. */
public final class Main {
    private Main() {
    }

    private static String readInput() throws IOException {
        Reader reader = new InputStreamReader(System.in);
        StringBuilder input = new StringBuilder();
        char[] buffer = new char[4096];
        int count;
        while ((count = reader.read(buffer)) >= 0) {
            input.append(buffer, 0, count);
        }
        return input.toString();
    }

    private static Program parse(String input) throws ParseException {
        String trimmed = input.trim();
        if (trimmed.startsWith("Program(")) {
            new ReadTypes(new StringReader(input));
            return ReadTypes.Goal();
        }

        new MiniJavaParser(new StringReader(input));
        return MiniJavaParser.Goal();
    }

    public static void main(String[] arguments) {
        Program program;
        try {
            program = parse(readInput());
        } catch (IOException error) {
            System.err.println(error.toString());
            System.exit(1);
            return;
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
