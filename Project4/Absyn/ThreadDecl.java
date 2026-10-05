/**
 * COSC 4400 - Project #3
 * Stores a Concurrent MiniJava thread declaration.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Absyn;
import java.util.LinkedList;
public class ThreadDecl extends ClassDecl {
    public ThreadDecl(String name, LinkedList<VarDecl> fields, LinkedList<MethodDecl> methods) { super(name, "Thread", fields, methods); }
    public void accept(Visitor v) { v.visit(this); }
}
