/**
 * COSC 4400 - Project #4
 * Prints class and member type descriptors in the reference format.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Types;

import java.io.PrintWriter;

public final class PrintVisitor implements Visitor {
    private final PrintWriter out;
    private int indentCount;

    public PrintVisitor(PrintWriter out) {
        this.out = out;
    }

    private void indent() {
        out.print('\n');
        for (int i = 0; i < indentCount; i++) out.print(' ');
    }

    private void begin(String name) {
        indent();
        out.print(name);
        out.print('(');
        indentCount++;
    }

    private void end() {
        indentCount--;
        out.print(')');
    }

    public void visit(CLASS type) {
        begin("CLASS");
        out.print(type.name);
        indent();
        out.print(type.parent == null ? "null" : type.parent.name);
        type.methods.accept(this);
        type.fields.accept(this);
        printObject(type.instance, true);
        end();
    }

    public void visit(OBJECT type) {
        printObject(type, false);
    }

    private void printObject(OBJECT type, boolean includeMembers) {
        begin("OBJECT");
        out.print(type.myClass.name);
        if (includeMembers) {
            type.methods.accept(this);
            type.fields.accept(this);
        }
        end();
    }

    public void visit(RECORD type) {
        begin("RECORD");
        for (FIELD field : type) field.accept(this);
        end();
    }

    public void visit(FIELD type) {
        begin("FIELD");
        out.print(type.index);
        out.print(' ');
        out.print(type.name);
        type.type.accept(this);
        end();
    }

    public void visit(FUNCTION type) {
        begin("FUNCTION");
        out.print(type.name);
        type.self.accept(this);
        type.formals.accept(this);
        type.result.accept(this);
        end();
    }

    public void visit(ARRAY type) {
        begin("ARRAY");
        type.element.accept(this);
        end();
    }

    public void visit(BOOLEAN type) { atom("BOOLEAN"); }
    public void visit(INT type) { atom("INT"); }
    public void visit(NIL type) { atom("NIL"); }
    public void visit(STRING type) { atom("STRING"); }
    public void visit(VOID type) { atom("VOID"); }

    private void atom(String name) {
        indent();
        out.print(name);
    }
}
