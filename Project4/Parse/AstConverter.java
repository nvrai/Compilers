/**
 * COSC 4400 - Project #4
 * Converts the textual AST representation into Absyn objects.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Parse;

import java.util.LinkedList;

import Absyn.AddExpr;
import Absyn.AndExpr;
import Absyn.ArrayExpr;
import Absyn.AssignStmt;
import Absyn.BlockStmt;
import Absyn.CallExpr;
import Absyn.ClassDecl;
import Absyn.DivExpr;
import Absyn.EqualExpr;
import Absyn.Expr;
import Absyn.FalseExpr;
import Absyn.FieldExpr;
import Absyn.Formal;
import Absyn.GreaterExpr;
import Absyn.IdentifierExpr;
import Absyn.IfStmt;
import Absyn.IntegerLiteral;
import Absyn.LesserExpr;
import Absyn.MethodDecl;
import Absyn.MulExpr;
import Absyn.NegExpr;
import Absyn.NewArrayExpr;
import Absyn.NewObjectExpr;
import Absyn.NotEqExpr;
import Absyn.NotExpr;
import Absyn.NullExpr;
import Absyn.OrExpr;
import Absyn.Program;
import Absyn.Stmt;
import Absyn.StringLiteral;
import Absyn.SubExpr;
import Absyn.ThisExpr;
import Absyn.ThreadDecl;
import Absyn.TrueExpr;
import Absyn.VarDecl;
import Absyn.VoidDecl;
import Absyn.WhileStmt;
import Absyn.XinuCallExpr;
import Absyn.XinuCallStmt;

public final class AstConverter {
    private AstConverter() { }

    public static Program program(AstNode node) {
        require(node, "Program");
        return new Program(classList(child(node, 0)));
    }

    private static LinkedList<ClassDecl> classList(AstNode node) {
        require(node, "AbstractList");
        LinkedList<ClassDecl> result = new LinkedList<ClassDecl>();
        for (Object item : node.items) {
            AstNode value = node(item);
            if ("ThreadDecl".equals(value.name)) {
                result.add(threadDecl(value));
            } else {
                result.add(classDecl(value));
            }
        }
        return result;
    }

    private static ClassDecl classDecl(AstNode node) {
        require(node, "ClassDecl");
        String parent = atom(node, 1);
        return new ClassDecl(atom(node, 0), "null".equals(parent) ? null : parent,
                varList(child(node, 2)), methodList(child(node, 3)));
    }

    private static ThreadDecl threadDecl(AstNode node) {
        require(node, "ThreadDecl");
        return new ThreadDecl(atom(node, 0), varList(child(node, 1)),
                methodList(child(node, 2)));
    }

    private static LinkedList<MethodDecl> methodList(AstNode node) {
        require(node, "AbstractList");
        LinkedList<MethodDecl> result = new LinkedList<MethodDecl>();
        for (Object item : node.items) {
            AstNode value = node(item);
            result.add("VoidDecl".equals(value.name) ? voidDecl(value) : methodDecl(value));
        }
        return result;
    }

    private static MethodDecl methodDecl(AstNode node) {
        require(node, "MethodDecl");
        int index = 0;
        Absyn.Type result = null;
        Object resultValue = node.items.get(index++);
        if (!(resultValue instanceof String && "public_static_void".equals(resultValue))) {
            result = type(resultValue);
        }

        boolean synced = false;
        if (index < node.items.size() && "synchronized".equals(asAtom(node.items.get(index)))) {
            synced = true;
            index++;
        }

        String name = atom(node, index++);
        LinkedList<Formal> params = formalList(child(node, index++));
        LinkedList<VarDecl> locals = varList(child(node, index++));
        LinkedList<Stmt> statements = stmtList(child(node, index++));
        Expr returnValue = nullableExpr(node.items.get(index));
        return new MethodDecl(result, synced, name, params, locals, statements, returnValue);
    }

    private static VoidDecl voidDecl(AstNode node) {
        require(node, "VoidDecl");
        return new VoidDecl(atom(node, 0), varList(child(node, 1)), stmtList(child(node, 2)));
    }

    private static LinkedList<Formal> formalList(AstNode node) {
        require(node, "AbstractList");
        LinkedList<Formal> result = new LinkedList<Formal>();
        for (Object item : node.items) {
            AstNode formal = node(item);
            require(formal, "Formal");
            result.add(new Formal(type(formal.items.get(0)), atom(formal, 1)));
        }
        return result;
    }

    private static LinkedList<VarDecl> varList(AstNode node) {
        require(node, "AbstractList");
        LinkedList<VarDecl> result = new LinkedList<VarDecl>();
        for (Object item : node.items) {
            AstNode variable = node(item);
            require(variable, "VarDecl");
            result.add(new VarDecl(type(variable.items.get(0)), atom(variable, 1),
                    nullableExpr(variable.items.get(2))));
        }
        return result;
    }

    private static Absyn.Type type(Object value) {
        if (value instanceof String) {
            if ("IntegerType".equals(value)) return new Absyn.IntegerType();
            if ("BooleanType".equals(value)) return new Absyn.BooleanType();
            throw malformed("Unknown type " + value);
        }
        AstNode node = node(value);
        if ("IdentifierType".equals(node.name)) return new Absyn.IdentifierType(atom(node, 0));
        if ("ArrayType".equals(node.name)) return new Absyn.ArrayType(type(node.items.get(0)));
        throw malformed("Unknown type " + node.name);
    }

    private static LinkedList<Stmt> stmtList(AstNode node) {
        require(node, "AbstractList");
        LinkedList<Stmt> result = new LinkedList<Stmt>();
        for (Object item : node.items) result.add(stmt(node(item)));
        return result;
    }

    private static Stmt stmt(AstNode node) {
        if ("AssignStmt".equals(node.name)) {
            return new AssignStmt((Absyn.AssignableExpr) expr(node.items.get(0)), expr(node.items.get(1)));
        }
        if ("BlockStmt".equals(node.name)) return new BlockStmt(stmtList(child(node, 0)));
        if ("IfStmt".equals(node.name)) {
            return new IfStmt(expr(node.items.get(0)), stmt(child(node, 1)), nullableStmt(node.items.get(2)));
        }
        if ("WhileStmt".equals(node.name)) {
            return new WhileStmt(expr(node.items.get(0)), stmt(child(node, 1)));
        }
        if ("XinuCallStmt".equals(node.name)) {
            return new XinuCallStmt(atom(node, 0), exprList(child(node, 1)));
        }
        throw malformed("Unknown statement " + node.name);
    }

    private static Expr expr(AstNode node) {
        if ("AddExpr".equals(node.name)) return new AddExpr(expr(node.items.get(0)), expr(node.items.get(1)));
        if ("AndExpr".equals(node.name)) return new AndExpr(expr(node.items.get(0)), expr(node.items.get(1)));
        if ("DivExpr".equals(node.name)) return new DivExpr(expr(node.items.get(0)), expr(node.items.get(1)));
        if ("EqualExpr".equals(node.name)) return new EqualExpr(expr(node.items.get(0)), expr(node.items.get(1)));
        if ("GreaterExpr".equals(node.name)) return new GreaterExpr(expr(node.items.get(0)), expr(node.items.get(1)));
        if ("LesserExpr".equals(node.name)) return new LesserExpr(expr(node.items.get(0)), expr(node.items.get(1)));
        if ("MulExpr".equals(node.name)) return new MulExpr(expr(node.items.get(0)), expr(node.items.get(1)));
        if ("NotEqExpr".equals(node.name)) return new NotEqExpr(expr(node.items.get(0)), expr(node.items.get(1)));
        if ("OrExpr".equals(node.name)) return new OrExpr(expr(node.items.get(0)), expr(node.items.get(1)));
        if ("SubExpr".equals(node.name)) return new SubExpr(expr(node.items.get(0)), expr(node.items.get(1)));
        if ("ArrayExpr".equals(node.name)) return new ArrayExpr(expr(node.items.get(0)), expr(node.items.get(1)));
        if ("CallExpr".equals(node.name)) {
            return new CallExpr(expr(node.items.get(0)), atom(node, 1), exprList(child(node, 2)));
        }
        if ("FieldExpr".equals(node.name)) return new FieldExpr(expr(node.items.get(0)), atom(node, 1));
        if ("IdentifierExpr".equals(node.name)) return new IdentifierExpr(atom(node, 0));
        if ("IntegerLiteral".equals(node.name)) return new IntegerLiteral(Integer.valueOf(atom(node, 0)));
        if ("StringLiteral".equals(node.name)) return new StringLiteral(atom(node, 0));
        if ("XinuCallExpr".equals(node.name)) {
            return new XinuCallExpr(atom(node, 0), exprList(child(node, 1)));
        }
        if ("NewArrayExpr".equals(node.name)) {
            return new NewArrayExpr(type(node.items.get(0)), nullableExprList(child(node, 1)));
        }
        if ("NewObjectExpr".equals(node.name)) return new NewObjectExpr(type(node.items.get(0)));
        if ("NegExpr".equals(node.name)) return new NegExpr(expr(node.items.get(0)));
        if ("NotExpr".equals(node.name)) return new NotExpr(expr(node.items.get(0)));
        if ("FalseExpr".equals(node.name)) return new FalseExpr();
        if ("NullExpr".equals(node.name)) return new NullExpr();
        if ("ThisExpr".equals(node.name)) return new ThisExpr();
        if ("TrueExpr".equals(node.name)) return new TrueExpr();
        throw malformed("Unknown expression " + node.name);
    }

    private static Expr expr(Object value) {
        if (value instanceof String) {
            if ("FalseExpr".equals(value)) return new FalseExpr();
            if ("NullExpr".equals(value)) return new NullExpr();
            if ("ThisExpr".equals(value)) return new ThisExpr();
            if ("TrueExpr".equals(value)) return new TrueExpr();
            throw malformed("Unknown expression " + value);
        }
        return expr(node(value));
    }

    private static LinkedList<Expr> exprList(AstNode node) {
        require(node, "AbstractList");
        LinkedList<Expr> result = new LinkedList<Expr>();
        for (Object item : node.items) result.add(expr(item));
        return result;
    }

    private static LinkedList<Expr> nullableExprList(AstNode node) {
        require(node, "AbstractList");
        LinkedList<Expr> result = new LinkedList<Expr>();
        for (Object item : node.items) result.add(nullableExpr(item));
        return result;
    }

    private static Expr nullableExpr(Object value) {
        if (value instanceof String && "null".equals(value)) return null;
        return expr(value);
    }

    private static Stmt nullableStmt(Object value) {
        if (value instanceof String && "null".equals(value)) return null;
        return stmt(node(value));
    }

    private static AstNode child(AstNode node, int index) {
        return node(node.items.get(index));
    }

    private static AstNode node(Object value) {
        if (!(value instanceof AstNode)) throw malformed("Expected AST node");
        return (AstNode) value;
    }

    private static String atom(AstNode node, int index) {
        return asAtom(node.items.get(index));
    }

    private static String asAtom(Object value) {
        if (!(value instanceof String)) return null;
        return (String) value;
    }

    private static void require(AstNode node, String name) {
        if (!name.equals(node.name)) throw malformed("Expected " + name + ", found " + node.name);
    }

    private static IllegalArgumentException malformed(String message) {
        return new IllegalArgumentException("Malformed abstract syntax tree: " + message);
    }
}
