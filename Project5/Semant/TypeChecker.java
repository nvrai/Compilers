/**
 * COSC 4400 - Project 5
 * Type checks MiniJava statements and expressions using scoped local and class environments.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Semant;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import Absyn.AddExpr;
import Absyn.AndExpr;
import Absyn.ArrayExpr;
import Absyn.AssignStmt;
import Absyn.BinOpExpr;
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
import Absyn.TrueExpr;
import Absyn.VarDecl;
import Absyn.Visitable;
import Absyn.WhileStmt;
import Absyn.XinuCallExpr;
import Absyn.XinuCallStmt;
import Symbol.Table;
import Types.ARRAY;
import Types.BOOLEAN;
import Types.CLASS;
import Types.FIELD;
import Types.FUNCTION;
import Types.INT;
import Types.NIL;
import Types.OBJECT;
import Types.RECORD;
import Types.STRING;
import Types.Type;
import Types.VOID;

/** Performs the statement and expression pass after the class table is built. */
public final class TypeChecker {
    private static final Type INT_TYPE = new INT();
    private static final Type BOOLEAN_TYPE = new BOOLEAN();
    private static final Type STRING_TYPE = new STRING();
    private static final Type NIL_TYPE = new NIL();
    private static final Type VOID_TYPE = new VOID();

    private final ClassTable classes;
    private final ErrorReporter errors;
    private CLASS currentClass;
    private String currentMethod;
    private Table<Type> variables;

    public TypeChecker(ClassTable classes, ErrorReporter errors) {
        this.classes = classes;
        this.errors = errors;
    }

    /** Checks all field initializers, method bodies, and return expressions. */
    public void check(Program program) {
        for (ClassDecl declaration : program.classes) {
            currentClass = classes.descriptorFor(declaration);
            if (currentClass == null) continue;

            variables = new Table<Type>();
            currentMethod = declaration.name;
            for (VarDecl field : declaration.fields) {
                if (field.init != null) {
                    Type required = field.semanticType;
                    require(required, typeOf(field.init), field.init);
                }
            }

            for (MethodDecl method : declaration.methods) check(method);
        }
    }

    private void check(MethodDecl method) {
        FUNCTION function = classes.descriptorFor(method);
        if (function == null) return;

        currentMethod = method.name;
        variables = new Table<Type>();

        Iterator<FIELD> formalTypes = function.formals.iterator();
        for (Formal formal : method.params) {
            Type type = formalTypes.hasNext()
                    ? formalTypes.next().type : classes.typeOf(formal.type);
            variables.put(formal.name, type);
        }

        Set<String> localNames = new HashSet<String>();
        for (VarDecl local : method.locals) {
            Type type = classes.typeOf(local.type);
            if (!localNames.add(local.name)) {
                errors.report(local.name + " is already defined in " + currentMethod,
                        local);
            }
            local.semanticType = type;
            variables.put(local.name, type);
        }
	//
	for (VarDecl local : method.locals) {
		if (local.init != null) {
			require(local.semanticType, typeOf(local.init), local.init);
		}
	}

        for (Stmt statement : method.stmts) check(statement);

        if (method.returnType != null && method.returnVal != null) {
            require(function.result, typeOf(method.returnVal), method.returnVal);
        }
    }

    private void check(Stmt statement) {
        if (statement instanceof AssignStmt) {
            AssignStmt assign = (AssignStmt) statement;
            require(typeOf(assign.lhs), typeOf(assign.rhs), assign);
        } else if (statement instanceof BlockStmt) {
            for (Stmt child : ((BlockStmt) statement).stmts) check(child);
        } else if (statement instanceof IfStmt) {
            IfStmt conditional = (IfStmt) statement;
            require(BOOLEAN_TYPE, typeOf(conditional.test), conditional);
            check(conditional.thenStm);
            if (conditional.elseStm != null) check(conditional.elseStm);
        } else if (statement instanceof WhileStmt) {
            WhileStmt loop = (WhileStmt) statement;
            require(BOOLEAN_TYPE, typeOf(loop.test), loop);
            check(loop.body);
        } else if (statement instanceof XinuCallStmt) {
            XinuCallStmt call = (XinuCallStmt) statement;
            checkXinuCall(call.method, call.args, call);
        } else {
            throw new IllegalArgumentException(
                    "Unknown statement " + statement.getClass().getName());
        }
    }

    private Type typeOf(Expr expression) {
        if (expression instanceof IntegerLiteral) return INT_TYPE;
        if (expression instanceof StringLiteral) return STRING_TYPE;
        if (expression instanceof TrueExpr || expression instanceof FalseExpr) {
            return BOOLEAN_TYPE;
        }
        if (expression instanceof NullExpr) return NIL_TYPE;
        if (expression instanceof ThisExpr) return currentClass.instance;

        if (expression instanceof IdentifierExpr) {
            String name = ((IdentifierExpr) expression).id;
            Type local = variables == null ? null : variables.get(name);
            if (local != null) return local;
            FIELD field = classes.lookupField(currentClass, name);
            if (field != null) return field.type;
            errors.report("cannot resolve symbol " + name, expression);
            return VOID_TYPE;
        }

        if (expression instanceof ArrayExpr) {
            ArrayExpr access = (ArrayExpr) expression;
            Type target = typeOf(access.target);
            Type index = typeOf(access.index);
            require(INT_TYPE, index, access);
            if (!(target instanceof ARRAY)) {
                errors.report("incompatible types: array required, but " + target + " found",
                        access);
                return VOID_TYPE;
            }
            return ((ARRAY) target).element;
        }

        if (expression instanceof FieldExpr) {
            FieldExpr access = (FieldExpr) expression;
            Type target = typeOf(access.target);
            if (target instanceof ARRAY && "length".equals(access.field)) {
                access.index = Integer.valueOf(-1);
                return INT_TYPE;
            }
            OBJECT object = asObject(target, access);
            if (object == null) return VOID_TYPE;
            FIELD field = classes.lookupField(object.myClass, access.field);
            if (field == null) {
                errors.report("cannot resolve field " + access.field, access);
                return VOID_TYPE;
            }
            access.index = Integer.valueOf(field.index);
            return field.type;
        }

        if (expression instanceof CallExpr) {
            CallExpr call = (CallExpr) expression;
            Type target = typeOf(call.target);
            OBJECT object = asObject(target, call);
            if (object == null) {
                checkExpressions(call.args);
                return VOID_TYPE;
            }
            FIELD methodField = classes.lookupMethodField(object.myClass, call.method);
            if (methodField == null || !(methodField.type instanceof FUNCTION)) {
                checkExpressions(call.args);
                errors.report("cannot resolve method " + call.method, call);
                return VOID_TYPE;
            }
            FUNCTION method = (FUNCTION) methodField.type;
            if (method.formals.size() == call.args.size()) {
                call.index = Integer.valueOf(methodField.index);
            }
            checkArguments(method.formals, call.args, call);
            return method.result;
        }

        if (expression instanceof NewArrayExpr) {
            NewArrayExpr allocation = (NewArrayExpr) expression;
            Type result = classes.typeOf(allocation.type);
            for (Expr dimension : allocation.dimensions) {
                if (dimension != null) require(INT_TYPE, typeOf(dimension), allocation);
                result = new ARRAY(result);
            }
            return result;
        }

        if (expression instanceof NewObjectExpr) {
            Type type = classes.typeOf(((NewObjectExpr) expression).type);
            if (type instanceof OBJECT) return type;
            errors.report("target not object, type " + type, expression);
            return VOID_TYPE;
        }

        if (expression instanceof XinuCallExpr) {
            XinuCallExpr call = (XinuCallExpr) expression;
            return checkXinuCall(call.method, call.args, call);
        }

        if (expression instanceof NegExpr) {
            return unary("-", typeOf(((NegExpr) expression).e1),
                    INT_TYPE, INT_TYPE, expression);
        }
        if (expression instanceof NotExpr) {
            return unary("!", typeOf(((NotExpr) expression).e1),
                    BOOLEAN_TYPE, BOOLEAN_TYPE, expression);
        }

        if (expression instanceof AddExpr) return arithmetic((BinOpExpr) expression, "+");
        if (expression instanceof SubExpr) return arithmetic((BinOpExpr) expression, "-");
        if (expression instanceof MulExpr) return arithmetic((BinOpExpr) expression, "*");
        if (expression instanceof DivExpr) return arithmetic((BinOpExpr) expression, "/");
        if (expression instanceof GreaterExpr) return comparison((BinOpExpr) expression, ">");
        if (expression instanceof LesserExpr) return comparison((BinOpExpr) expression, "<");
        if (expression instanceof AndExpr) return logical((BinOpExpr) expression, "&&");
        if (expression instanceof OrExpr) return logical((BinOpExpr) expression, "||");
        if (expression instanceof EqualExpr) return equality((BinOpExpr) expression, "==");
        if (expression instanceof NotEqExpr) return equality((BinOpExpr) expression, "!=");

        throw new IllegalArgumentException(
                "Unknown expression " + expression.getClass().getName());
    }

    private Type arithmetic(BinOpExpr expression, String operator) {
        Type left = typeOf(expression.e1);
        Type right = typeOf(expression.e2);
        return binary(operator, left, right, INT_TYPE, INT_TYPE, expression);
    }

    private Type comparison(BinOpExpr expression, String operator) {
        Type left = typeOf(expression.e1);
        Type right = typeOf(expression.e2);
        return binary(operator, left, right, INT_TYPE, BOOLEAN_TYPE, expression);
    }

    private Type logical(BinOpExpr expression, String operator) {
        Type left = typeOf(expression.e1);
        Type right = typeOf(expression.e2);
        return binary(operator, left, right, BOOLEAN_TYPE, BOOLEAN_TYPE, expression);
    }

    private Type equality(BinOpExpr expression, String operator) {
        Type left = typeOf(expression.e1);
        Type right = typeOf(expression.e2);
        if (!left.coerceTo(right) && !right.coerceTo(left)) {
            operatorError(operator, left, right, expression);
        }
        return BOOLEAN_TYPE;
    }

    private Type binary(String operator, Type left, Type right,
            Type operandType, Type resultType, Visitable context) {
        if (!left.coerceTo(operandType) || !right.coerceTo(operandType)) {
            operatorError(operator, left, right, context);
        }
        return resultType;
    }

    private Type unary(String operator, Type operand, Type required, Type result,
            Visitable context) {
        if (!operand.coerceTo(required)) {
            errors.report("operator " + operator + " cannot be applied to " + operand,
                    context);
        }
        return result;
    }

    private void operatorError(String operator, Type left, Type right, Visitable context) {
        errors.report("operator " + operator + " cannot be applied to "
                + left + ", " + right, context);
    }

    private Type checkXinuCall(String method, LinkedList<Expr> arguments,
            Visitable context) {
        RECORD formals = new RECORD();
        Type result;
        if ("print".equals(method)) {
            CLASS stringClass = classes.get("String");
            formals.put(stringClass.instance, "value");
            result = VOID_TYPE;
        } else if ("println".equals(method)) {
            result = VOID_TYPE;
        } else if ("printint".equals(method)) {
            formals.put(INT_TYPE, "value");
            result = VOID_TYPE;
        } else if ("readint".equals(method)) {
            result = INT_TYPE;
        } else {
            checkExpressions(arguments);
            errors.report("cannot resolve method " + method, context);
            return VOID_TYPE;
        }
        checkArguments(formals, arguments, context);
        return result;
    }

    private void checkArguments(RECORD formals, LinkedList<Expr> arguments,
            Visitable context) {
        if (formals.size() != arguments.size()) {
            errors.report("mismatch in number of arguments", context);
        }

        Iterator<FIELD> expected = formals.iterator();
        Iterator<Expr> actual = arguments.iterator();
        while (expected.hasNext() && actual.hasNext()) {
            Expr expression = actual.next();
            require(expected.next().type, typeOf(expression), expression);
        }
        while (actual.hasNext()) typeOf(actual.next());
    }

    private void checkExpressions(LinkedList<Expr> expressions) {
        for (Expr expression : expressions) typeOf(expression);
    }

    private OBJECT asObject(Type type, Visitable context) {
        if (type instanceof OBJECT) return (OBJECT) type;
        errors.report("target not object, type " + type, context);
        return null;
    }

    private void require(Type required, Type actual, Visitable context) {
        if (!actual.coerceTo(required)) {
            errors.report("incompatible types: " + required
                    + " required, but " + actual + " found", context);
        }
    }
}
