/**
 * COSC 4400 - Project #8
 * Translates MiniJava through IR canonicalization and RISC-V instruction selection.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Assem;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import Absyn.Program;
import Parse.MiniJavaParser;
import Parse.ParseException;
import Parse.ReadTypes;
import Riscv.Codegen;
import Riscv.RiscvFrame;
import Translate.DataFrag;
import Translate.Frag;
import Translate.ProcFrag;
import Translate.Translate;

/** Command-line entry point for instruction selection. */
public final class Main {
    private Main() {
    }

    private static String readInput() throws IOException {
        Reader reader = new InputStreamReader(System.in);
        StringBuilder input = new StringBuilder();
        char[] buffer = new char[4096];
        int count;
        while ((count = reader.read(buffer)) >= 0) input.append(buffer, 0, count);
        return input.toString();
    }

    private static Program parse(String input) throws ParseException {
        if (input.trim().startsWith("Program(")) {
            new ReadTypes(new StringReader(input));
            return ReadTypes.Goal();
        }
        new MiniJavaParser(new StringReader(input));
        return MiniJavaParser.Goal();
    }

    private static void emitProcedure(PrintWriter writer, ProcFrag procedure) {
        List<Tree.Stm> linear = Canon.Canon.linearize(procedure.body);
        Canon.BasicBlocks blocks = new Canon.BasicBlocks(linear);
        List<Tree.Stm> traced = new Canon.TraceSchedule(blocks).stms;
        RiscvFrame frame = (RiscvFrame)procedure.frame;
        List<Instr> instructions = new Codegen(frame).codegen(traced);
        frame.procEntryExit2(instructions);

        writer.println("\t.text");
        writer.println("\t.align 2");
        writer.println(frame.name + ":");
        for (Instr instruction : instructions) {
            String formatted = instruction.format(frame);
            if (!formatted.isEmpty()) writer.println(formatted);
        }
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
            if (fragment instanceof DataFrag) writer.println(((DataFrag)fragment).data);
            else emitProcedure(writer, (ProcFrag)fragment);
        }
        writer.flush();
    }
}
