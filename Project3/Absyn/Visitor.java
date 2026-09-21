/**
 * COSC 4400 - Project #3
 * Declares visit methods for every AST node type.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Absyn;

public interface Visitor {
    void visit(java.util.AbstractList<Visitable> list);
    void visit(Program ast); void visit(ClassDecl ast); void visit(ThreadDecl ast);
    void visit(MethodDecl ast); void visit(VoidDecl ast); void visit(Formal ast);
    void visit(ArrayType ast); void visit(IdentifierType ast);
    void visit(BooleanType ast); void visit(IntegerType ast); void visit(VarDecl ast);
    void visit(XinuCallStmt ast); void visit(AssignStmt ast); void visit(BlockStmt ast);
    void visit(IfStmt ast); void visit(WhileStmt ast);
    void visit(AddExpr ast); void visit(AndExpr ast); void visit(DivExpr ast);
    void visit(EqualExpr ast); void visit(GreaterExpr ast); void visit(LesserExpr ast);
    void visit(MulExpr ast); void visit(NotEqExpr ast); void visit(OrExpr ast);
    void visit(SubExpr ast); void visit(ArrayExpr ast); void visit(CallExpr ast);
    void visit(FieldExpr ast); void visit(IdentifierExpr ast); void visit(IntegerLiteral ast);
    void visit(StringLiteral ast); void visit(XinuCallExpr ast); void visit(NewArrayExpr ast);
    void visit(NewObjectExpr ast); void visit(NegExpr ast); void visit(NotExpr ast);
    void visit(FalseExpr ast); void visit(NullExpr ast); void visit(ThisExpr ast);
    void visit(TrueExpr ast);
}
