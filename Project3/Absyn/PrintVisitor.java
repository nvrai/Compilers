/**
 * COSC 4400 - Project #3
 * Prints the completed AST in the required format.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Absyn;
import java.io.PrintWriter;

public class PrintVisitor implements Visitor {
    private final PrintWriter out;
    public int indentCount = 0;
    public PrintVisitor(PrintWriter out) { this.out = out; }
    public PrintVisitor() { this(new PrintWriter(System.out)); }
    private void indent() { out.print('\n'); for (int i=0;i<indentCount;i++) out.print(' '); }
    private void begin(String name) { indent(); out.print(name+"("); indentCount++; }
    private void end() { indentCount--; out.print(")"); }

    public void visit(Program ast) { out.print("Program("); indentCount++; visit(ast.classes); indentCount--; out.println(")"); out.flush(); }
    @SuppressWarnings("rawtypes")
    public void visit(java.util.AbstractList list) {
        if (list == null) { indent(); out.print("null"); return; }
        indent(); out.print("AbstractList("); indentCount++;
        for (Object item:list) { if(item==null){indent();out.print("null");} else ((Visitable)item).accept(this); }
        out.print(")"); indentCount--;
    }
    public void visit(ClassDecl ast) { begin("ClassDecl"); out.print(ast.name+" "+ast.parent); visit(ast.fields); visit(ast.methods); end(); }
    public void visit(ThreadDecl ast) { begin("ThreadDecl"); out.print(ast.name); visit(ast.fields); visit(ast.methods); end(); }
    public void visit(MethodDecl ast) {
        begin("MethodDecl");
        if(ast.returnType==null) out.print("public_static_void"); else ast.returnType.accept(this);
        if(ast.synced) out.print(" synchronized"); out.print(" "+ast.name);
        visit(ast.params); visit(ast.locals); visit(ast.stmts);
        if(ast.returnVal==null){indent();out.print("null");} else ast.returnVal.accept(this); end();
    }
    public void visit(VoidDecl ast) { begin("VoidDecl"); out.print(ast.name); visit(ast.locals); visit(ast.stmts); end(); }
    public void visit(Formal ast) { indent(); out.print("Formal("); ast.type.accept(this); out.print(" "+ast.name+")"); }
    public void visit(ArrayType ast) { out.print("ArrayType("); ast.base.accept(this); out.print(")"); }
    public void visit(IdentifierType ast) { out.print("IdentifierType("+ast.id+")"); }
    public void visit(BooleanType ast) { out.print("BooleanType"); }
    public void visit(IntegerType ast) { out.print("IntegerType"); }
    public void visit(VarDecl ast) { indent(); out.print("VarDecl("); ast.type.accept(this); out.print(" "+ast.name); if(ast.init==null)out.print(" null");else ast.init.accept(this); out.print(")"); }
    public void visit(XinuCallStmt ast) { begin("XinuCallStmt"); out.print(ast.method); visit(ast.args); end(); }
    public void visit(AssignStmt ast) { begin("AssignStmt"); ast.lhs.accept(this); ast.rhs.accept(this); end(); }
    public void visit(BlockStmt ast) { begin("BlockStmt"); visit(ast.stmts); end(); }
    public void visit(IfStmt ast) { begin("IfStmt"); ast.test.accept(this); ast.thenStm.accept(this); if(ast.elseStm==null){indent();out.print("null");}else ast.elseStm.accept(this); end(); }
    public void visit(WhileStmt ast) { begin("WhileStmt"); ast.test.accept(this); ast.body.accept(this); end(); }

    private void binary(String name, BinOpExpr ast) { begin(name); ast.e1.accept(this); ast.e2.accept(this); end(); }
    public void visit(AddExpr ast){binary("AddExpr",ast);} public void visit(AndExpr ast){binary("AndExpr",ast);}
    public void visit(DivExpr ast){binary("DivExpr",ast);} public void visit(EqualExpr ast){binary("EqualExpr",ast);}
    public void visit(GreaterExpr ast){binary("GreaterExpr",ast);} public void visit(LesserExpr ast){binary("LesserExpr",ast);}
    public void visit(MulExpr ast){binary("MulExpr",ast);} public void visit(NotEqExpr ast){binary("NotEqExpr",ast);}
    public void visit(OrExpr ast){binary("OrExpr",ast);} public void visit(SubExpr ast){binary("SubExpr",ast);}
    public void visit(ArrayExpr ast) { begin("ArrayExpr"); ast.target.accept(this); ast.index.accept(this); end(); }
    public void visit(CallExpr ast) { begin("CallExpr"); ast.target.accept(this); indent(); out.print(ast.method); visit(ast.args); end(); }
    public void visit(FieldExpr ast) { begin("FieldExpr"); ast.target.accept(this); indent(); out.print(ast.field); end(); }
    public void visit(IdentifierExpr ast) { indent(); out.print("IdentifierExpr("+ast.id+")"); }
    public void visit(IntegerLiteral ast) { indent(); out.print("IntegerLiteral("+ast.value+")"); }
    public void visit(StringLiteral ast) { indent(); out.print("StringLiteral("+ast.value+")"); }
    public void visit(XinuCallExpr ast) { begin("XinuCallExpr"); out.print(ast.method); visit(ast.args); end(); }
    public void visit(NewArrayExpr ast) { begin("NewArrayExpr"); ast.type.accept(this); visit(ast.dimensions); end(); }
    public void visit(NewObjectExpr ast) { begin("NewObjectExpr"); ast.type.accept(this); end(); }
    private void unary(String name, Expr e) { begin(name); e.accept(this); end(); }
    public void visit(NegExpr ast){unary("NegExpr",ast.e1);} public void visit(NotExpr ast){unary("NotExpr",ast.e1);}
    public void visit(FalseExpr ast){indent();out.print("FalseExpr");} public void visit(NullExpr ast){indent();out.print("NullExpr");}
    public void visit(ThisExpr ast){indent();out.print("ThisExpr");} public void visit(TrueExpr ast){indent();out.print("TrueExpr");}
}
