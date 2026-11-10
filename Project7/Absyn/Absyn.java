/**
 * COSC 4400 - Project #7
 * Provides the base class shared by all AST nodes.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Absyn;

/**
 * Parent class of all abstract syntax tree nodes.
 */

public abstract class Absyn implements Visitable
{
    /** Visitor pattern dispatch. */
    public abstract void accept(Visitor v);

    /** Translation visitor dispatch. */
    public Translate.Exp accept(TreeVisitor v)
    {
        if (this instanceof ThreadDecl) return v.visit((ThreadDecl)this);
        if (this instanceof ClassDecl) return v.visit((ClassDecl)this);
        if (this instanceof VoidDecl) return v.visit((VoidDecl)this);
        if (this instanceof MethodDecl) return v.visit((MethodDecl)this);
        if (this instanceof Program) return v.visit((Program)this);
        if (this instanceof Formal) return v.visit((Formal)this);
        if (this instanceof VarDecl) return v.visit((VarDecl)this);
        if (this instanceof ArrayType) return v.visit((ArrayType)this);
        if (this instanceof BooleanType) return v.visit((BooleanType)this);
        if (this instanceof IdentifierType) return v.visit((IdentifierType)this);
        if (this instanceof IntegerType) return v.visit((IntegerType)this);
        if (this instanceof AssignStmt) return v.visit((AssignStmt)this);
        if (this instanceof BlockStmt) return v.visit((BlockStmt)this);
        if (this instanceof IfStmt) return v.visit((IfStmt)this);
        if (this instanceof WhileStmt) return v.visit((WhileStmt)this);
        if (this instanceof XinuCallStmt) return v.visit((XinuCallStmt)this);
        if (this instanceof AddExpr) return v.visit((AddExpr)this);
        if (this instanceof AndExpr) return v.visit((AndExpr)this);
        if (this instanceof ArrayExpr) return v.visit((ArrayExpr)this);
        if (this instanceof CallExpr) return v.visit((CallExpr)this);
        if (this instanceof DivExpr) return v.visit((DivExpr)this);
        if (this instanceof EqualExpr) return v.visit((EqualExpr)this);
        if (this instanceof FalseExpr) return v.visit((FalseExpr)this);
        if (this instanceof FieldExpr) return v.visit((FieldExpr)this);
        if (this instanceof GreaterExpr) return v.visit((GreaterExpr)this);
        if (this instanceof IdentifierExpr) return v.visit((IdentifierExpr)this);
        if (this instanceof IntegerLiteral) return v.visit((IntegerLiteral)this);
        if (this instanceof LesserExpr) return v.visit((LesserExpr)this);
        if (this instanceof MulExpr) return v.visit((MulExpr)this);
        if (this instanceof NegExpr) return v.visit((NegExpr)this);
        if (this instanceof NewArrayExpr) return v.visit((NewArrayExpr)this);
        if (this instanceof NewObjectExpr) return v.visit((NewObjectExpr)this);
        if (this instanceof NotEqExpr) return v.visit((NotEqExpr)this);
        if (this instanceof NotExpr) return v.visit((NotExpr)this);
        if (this instanceof NullExpr) return v.visit((NullExpr)this);
        if (this instanceof OrExpr) return v.visit((OrExpr)this);
        if (this instanceof StringLiteral) return v.visit((StringLiteral)this);
        if (this instanceof SubExpr) return v.visit((SubExpr)this);
        if (this instanceof ThisExpr) return v.visit((ThisExpr)this);
        if (this instanceof TrueExpr) return v.visit((TrueExpr)this);
        if (this instanceof XinuCallExpr) return v.visit((XinuCallExpr)this);
        throw new IllegalArgumentException("No translation visitor for "
                + getClass().getName());
    }
}
