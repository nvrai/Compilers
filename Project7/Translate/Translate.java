/**
 * COSC 4400 - Project #7
 * Translates the MiniJava abstract syntax tree into intermediate-representation trees.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Translate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Semant.ClassTable;
import Semant.ErrorReporter;
import Semant.TypeChecker;
import Symbol.Symbol;
import Symbol.Table;
import Temp.Label;
import Temp.Temp;
import Types.CLASS;
import Types.FIELD;
import Types.FUNCTION;
import Types.OBJECT;
import Types.RECORD;

/** Implements the AST-to-IR translation pass. */
public class Translate implements Absyn.TreeVisitor {
    private final Frame.Frame framePrototype;
    public Frame.Frame frame;

    private final LinkedList<Frag> fragments = new LinkedList<Frag>();
    private final Map<String, CLASS> classEnvironment = new HashMap<String, CLASS>();
    private final Map<String, Absyn.ClassDecl> classDeclarations =
            new HashMap<String, Absyn.ClassDecl>();
    private final Map<Symbol, Label> methodEnvironment = new HashMap<Symbol, Label>();
    private final Map<String, Label> vtableEnvironment = new HashMap<String, Label>();
    private final Map<String, Label> strings = new HashMap<String, Label>();

    private ClassTable classes;
    private CLASS currentClass;
    private Absyn.ClassDecl currentClassDeclaration;
    private Table<Frame.Access> variableEnvironment = new Table<Frame.Access>();
    private Frame.Access thisAccess;
    private Tree.Exp thisOverride;

    public Translate(Frame.Frame frame) {
        this.framePrototype = frame;
        this.frame = frame;
    }

    public List<Frag> results() {
        return fragments;
    }

    private static Tree.Exp CONST(int value) {
        return new Tree.CONST(value);
    }

    private static Tree.Exp NAME(Label label) {
        return new Tree.NAME(label);
    }

    private static Tree.Exp TEMP(Temp temp) {
        return new Tree.TEMP(temp);
    }

    private static Tree.Exp BINOP(int operator, Tree.Exp left, Tree.Exp right) {
        return new Tree.BINOP(operator, left, right);
    }

    private static Tree.Exp MEM(Tree.Exp expression) {
        return new Tree.MEM(expression);
    }

    private static Tree.Stm MOVE(Tree.Exp destination, Tree.Exp source) {
        return new Tree.MOVE(destination, source);
    }

    private static Tree.Stm EXP(Tree.Exp expression) {
        return expression == null ? null : new Tree.EXP(expression);
    }

    private static Tree.Stm seq(Tree.Stm left, Tree.Stm right) {
        if (left == null) return right;
        if (right == null) return left;
        return new Tree.SEQ(left, right);
    }

    private static Tree.Stm seq(Tree.Stm... statements) {
        Tree.Stm result = null;
        for (Tree.Stm statement : statements) result = seq(result, statement);
        return result;
    }

    private static Tree.Stm seq(List<Tree.Stm> statements) {
        Tree.Stm result = null;
        for (Tree.Stm statement : statements) result = seq(result, statement);
        return result;
    }

    private static Symbol methodKey(String owner, String method) {
        return Symbol.get(owner + "." + method);
    }

    private Label methodLabel(String owner, String method) {
        Symbol key = methodKey(owner, method);
        Label label = methodEnvironment.get(key);
        if (label == null) {
            label = new Label(owner + "$" + method);
            methodEnvironment.put(key, label);
        }
        return label;
    }

    private Label methodLabel(FUNCTION method) {
        if (method.self instanceof OBJECT) {
            return methodLabel(((OBJECT)method.self).myClass.name, method.name);
        }
        return new Label(method.name);
    }

    private Label vtableLabel(String className) {
        Label label = vtableEnvironment.get(className);
        if (label == null) {
            label = new Label(className + "$vtable");
            vtableEnvironment.put(className, label);
        }
        return label;
    }

    private Tree.Exp externalCall(String name, List<Tree.Exp> arguments) {
        return frame.externalCall(Symbol.get(name), arguments);
    }

    private Tree.Exp externalCall(String name, Tree.Exp... arguments) {
        ArrayList<Tree.Exp> values = new ArrayList<Tree.Exp>();
        for (Tree.Exp argument : arguments) values.add(argument);
        return externalCall(name, values);
    }

    private Tree.Exp checkedPointer(Tree.Exp expression) {
        Temp pointer = new Temp();
        Label valid = new Label();
        Label invalid = new Label();
        Tree.Stm check = seq(
                MOVE(TEMP(pointer), expression),
                new Tree.CJUMP(Tree.CJUMP.NE, TEMP(pointer), CONST(0), valid, invalid),
                new Tree.LABEL(invalid),
                EXP(new Tree.CALL(NAME(frame.badPtr()), new ArrayList<Tree.Exp>())),
                new Tree.JUMP(valid),
                new Tree.LABEL(valid));
        return new Tree.ESEQ(check, TEMP(pointer));
    }

    private Tree.Exp arrayElement(Tree.Exp arrayExpression, Tree.Exp indexExpression) {
        Temp array = new Temp();
        Temp index = new Temp();
        Label pointerOkay = new Label();
        Label lowerOkay = new Label();
        Label indexOkay = new Label();
        Label badPointer = new Label();
        Label badIndex = new Label();

        Tree.Exp length = MEM(BINOP(Tree.BINOP.MINUS, TEMP(array), CONST(frame.wordSize())));
        Tree.Stm checks = seq(
                MOVE(TEMP(array), arrayExpression),
                MOVE(TEMP(index), indexExpression),
                new Tree.CJUMP(Tree.CJUMP.NE, TEMP(array), CONST(0), pointerOkay, badPointer),
                new Tree.LABEL(badPointer),
                EXP(new Tree.CALL(NAME(frame.badPtr()), new ArrayList<Tree.Exp>())),
                new Tree.JUMP(pointerOkay),
                new Tree.LABEL(pointerOkay),
                new Tree.CJUMP(Tree.CJUMP.GE, TEMP(index), CONST(0), lowerOkay, badIndex),
                new Tree.LABEL(lowerOkay),
                new Tree.CJUMP(Tree.CJUMP.LT, TEMP(index), length, indexOkay, badIndex),
                new Tree.LABEL(badIndex),
                EXP(new Tree.CALL(NAME(frame.badSub()), new ArrayList<Tree.Exp>())),
                new Tree.JUMP(indexOkay),
                new Tree.LABEL(indexOkay));
        Tree.Exp address = BINOP(Tree.BINOP.PLUS, TEMP(array),
                BINOP(Tree.BINOP.MUL, TEMP(index), CONST(frame.wordSize())));
        return new Tree.ESEQ(checks, MEM(address));
    }

    private List<Tree.Exp> translateArguments(List<Absyn.Expr> arguments) {
        ArrayList<Tree.Exp> translated = new ArrayList<Tree.Exp>();
        for (Absyn.Expr argument : arguments) translated.add(argument.accept(this).unEx());
        return translated;
    }

    private void normalizeMethodLayout(CLASS descriptor, Set<CLASS> completed) {
        if (descriptor == null || completed.contains(descriptor)) return;
        normalizeMethodLayout(descriptor.parent, completed);

        RECORD layout = new RECORD();
        Set<String> inheritedNames = new HashSet<String>();
        if (descriptor.parent != null) {
            for (FIELD inherited : descriptor.parent.instance.methods) {
                FIELD replacement = descriptor.methods.get(inherited.name);
                layout.put(replacement == null ? inherited.type : replacement.type,
                        inherited.name);
                inheritedNames.add(inherited.name);
            }
        }
        for (FIELD declared : descriptor.methods) {
            if (!inheritedNames.contains(declared.name)) {
                layout.put(declared.type, declared.name);
            }
        }
        descriptor.instance.methods = layout;
        completed.add(descriptor);
    }

    private void prepareEnvironments(Absyn.Program program) {
        ErrorReporter errors = new ErrorReporter();
        classes = ClassTable.build(program, errors);

        classEnvironment.clear();
        CLASS stringClass = classes.get("String");
        CLASS threadClass = classes.get("Thread");
        if (stringClass != null) classEnvironment.put(stringClass.name, stringClass);
        if (threadClass != null) classEnvironment.put(threadClass.name, threadClass);
        for (Absyn.ClassDecl declaration : program.classes) {
            CLASS descriptor = classes.descriptorFor(declaration);
            if (descriptor != null) {
                classEnvironment.put(descriptor.name, descriptor);
                classDeclarations.put(descriptor.name, declaration);
            }
        }

        Set<CLASS> completed = new HashSet<CLASS>();
        for (CLASS descriptor : classEnvironment.values()) {
            normalizeMethodLayout(descriptor, completed);
        }

        if (classes.canTypeCheck()) {
            new TypeChecker(classes, errors).check(program);
        }

        for (Absyn.ClassDecl declaration : program.classes) {
            vtableLabel(declaration.name);
            for (Absyn.MethodDecl method : declaration.methods) {
                Label label;
                if (method.returnType == null && "main".equals(method.name)
                        && !(method instanceof Absyn.VoidDecl)) {
                    label = new Label("main");
                } else {
                    label = new Label(declaration.name + "$" + method.name);
                }
                methodEnvironment.put(methodKey(declaration.name, method.name), label);
            }
        }
    }

    public Exp visit(Absyn.Program program) {
        fragments.clear();
        prepareEnvironments(program);
        for (Absyn.ClassDecl declaration : program.classes) declaration.accept(this);
        return new Nx(null);
    }

    public Exp visit(Absyn.ClassDecl declaration) {
        CLASS previousClass = currentClass;
        Absyn.ClassDecl previousDeclaration = currentClassDeclaration;
        currentClass = classEnvironment.get(declaration.name);
        currentClassDeclaration = declaration;

        ArrayList<Label> methodLabels = new ArrayList<Label>();
        if (currentClass != null) {
            for (FIELD methodField : currentClass.instance.methods) {
                if (methodField.type instanceof FUNCTION) {
                    methodLabels.add(methodLabel((FUNCTION)methodField.type));
                }
            }
        }
        fragments.add(new DataFrag(framePrototype.record(
                vtableLabel(declaration.name), methodLabels)));

        for (Absyn.MethodDecl method : declaration.methods) method.accept(this);
        currentClass = previousClass;
        currentClassDeclaration = previousDeclaration;
        return new Nx(null);
    }

    public Exp visit(Absyn.ThreadDecl declaration) {
        return visit((Absyn.ClassDecl)declaration);
    }

    private boolean isStaticMain(Absyn.MethodDecl method) {
        return method.returnType == null && "main".equals(method.name)
                && !(method instanceof Absyn.VoidDecl);
    }

    public Exp visit(Absyn.MethodDecl method) {
        Frame.Frame previousFrame = frame;
        Table<Frame.Access> previousVariables = variableEnvironment;
        Frame.Access previousThis = thisAccess;
        Tree.Exp previousOverride = thisOverride;

        Label label = methodLabel(currentClassDeclaration.name, method.name);
        frame = framePrototype.newFrame(Symbol.get(label.toString()));
        variableEnvironment = new Table<Frame.Access>();
        thisAccess = null;
        thisOverride = null;

        if (!isStaticMain(method)) {
            thisAccess = frame.allocFormal();
            variableEnvironment.put("this", thisAccess);
        }
        for (Absyn.Formal formal : method.params) formal.accept(this);

        LinkedList<Tree.Stm> statements = new LinkedList<Tree.Stm>();
        if (method.synced) {
            statements.add(EXP(externalCall("lock", currentThis())));
        }
        for (Absyn.VarDecl local : method.locals) {
            Tree.Stm initializer = local.accept(this).unNx();
            if (initializer != null) statements.add(initializer);
        }
        for (Absyn.Stmt statement : method.stmts) {
            Tree.Stm translated = statement.accept(this).unNx();
            if (translated != null) statements.add(translated);
        }
        if (method.returnVal != null) {
            statements.add(MOVE(TEMP(frame.RV()), method.returnVal.accept(this).unEx()));
        }
        if (method.synced) {
            statements.add(EXP(externalCall("unlock", currentThis())));
        }

        frame.procEntryExit1(statements);
        Tree.Stm body = seq(statements);
        fragments.add(new ProcFrag(body, frame));
        frame = previousFrame;
        variableEnvironment = previousVariables;
        thisAccess = previousThis;
        thisOverride = previousOverride;
        return new Nx(body);
    }

    public Exp visit(Absyn.VoidDecl method) {
        return visit((Absyn.MethodDecl)method);
    }

    public Exp visit(Absyn.Formal formal) {
        Frame.Access access = frame.allocFormal();
        variableEnvironment.put(formal.name, access);
        return new Nx(null);
    }

    public Exp visit(Absyn.VarDecl variable) {
        Frame.Access access = frame.allocLocal();
        variableEnvironment.put(variable.name, access);
        if (variable.init == null) return new Nx(null);
        return new Nx(MOVE(access.exp(frame), variable.init.accept(this).unEx()));
    }

    public Exp visit(Absyn.BooleanType type) {
        return new Nx(null);
    }

    public Exp visit(Absyn.IntegerType type) {
        return new Nx(null);
    }

    public Exp visit(Absyn.ArrayType type) {
        return new Nx(null);
    }

    public Exp visit(Absyn.IdentifierType type) {
        return new Nx(null);
    }

    public Exp visit(Absyn.AssignStmt statement) {
        Tree.Exp destination = statement.lhs.accept(this).unEx();
        Tree.Exp source = statement.rhs.accept(this).unEx();
        return new Nx(MOVE(destination, source));
    }

    public Exp visit(Absyn.BlockStmt block) {
        Tree.Stm statements = null;
        for (Absyn.Stmt statement : block.stmts) {
            statements = seq(statements, statement.accept(this).unNx());
        }
        return new Nx(statements);
    }

    public Exp visit(Absyn.IfStmt statement) {
        Label thenLabel = new Label();
        Label elseLabel = statement.elseStm == null ? null : new Label();
        Label doneLabel = new Label();
        Label falseLabel = elseLabel == null ? doneLabel : elseLabel;

        Tree.Stm translated = seq(
                statement.test.accept(this).unCx(thenLabel, falseLabel),
                new Tree.LABEL(thenLabel),
                statement.thenStm.accept(this).unNx(),
                new Tree.JUMP(doneLabel));
        if (statement.elseStm != null) {
            translated = seq(translated, new Tree.LABEL(elseLabel),
                    statement.elseStm.accept(this).unNx(), new Tree.JUMP(doneLabel));
        }
        translated = seq(translated, new Tree.LABEL(doneLabel));
        return new Nx(translated);
    }

    public Exp visit(Absyn.WhileStmt statement) {
        Label testLabel = new Label();
        Label bodyLabel = new Label();
        Label doneLabel = new Label();
        return new Nx(seq(
                new Tree.LABEL(testLabel),
                statement.test.accept(this).unCx(bodyLabel, doneLabel),
                new Tree.LABEL(bodyLabel),
                statement.body.accept(this).unNx(),
                new Tree.JUMP(testLabel),
                new Tree.LABEL(doneLabel)));
    }

    private Exp arithmetic(int operator, Absyn.BinOpExpr expression) {
        return new Ex(BINOP(operator, expression.e1.accept(this).unEx(),
                expression.e2.accept(this).unEx()));
    }

    public Exp visit(Absyn.AddExpr expression) {
        return arithmetic(Tree.BINOP.PLUS, expression);
    }

    public Exp visit(Absyn.DivExpr expression) {
        return arithmetic(Tree.BINOP.DIV, expression);
    }

    public Exp visit(Absyn.MulExpr expression) {
        return arithmetic(Tree.BINOP.MUL, expression);
    }

    public Exp visit(Absyn.SubExpr expression) {
        return arithmetic(Tree.BINOP.MINUS, expression);
    }

    public Exp visit(Absyn.NegExpr expression) {
        return new Ex(BINOP(Tree.BINOP.MINUS, CONST(0),
                expression.e1.accept(this).unEx()));
    }

    public Exp visit(Absyn.AndExpr expression) {
        return new IfThenElseExp(expression.e1.accept(this),
                expression.e2.accept(this), new Ex(CONST(0)));
    }

    public Exp visit(Absyn.OrExpr expression) {
        return new IfThenElseExp(expression.e1.accept(this), new Ex(CONST(1)),
                expression.e2.accept(this));
    }

    private Exp relation(int operator, Absyn.BinOpExpr expression) {
        return new RelCx(operator, expression.e1.accept(this).unEx(),
                expression.e2.accept(this).unEx());
    }

    public Exp visit(Absyn.GreaterExpr expression) {
        return relation(Tree.CJUMP.GT, expression);
    }

    public Exp visit(Absyn.LesserExpr expression) {
        return relation(Tree.CJUMP.LT, expression);
    }

    public Exp visit(Absyn.EqualExpr expression) {
        return relation(Tree.CJUMP.EQ, expression);
    }

    public Exp visit(Absyn.NotEqExpr expression) {
        return relation(Tree.CJUMP.NE, expression);
    }

    public Exp visit(Absyn.NotExpr expression) {
        return new IfThenElseExp(expression.e1.accept(this),
                new Ex(CONST(0)), new Ex(CONST(1)));
    }

    private Tree.Exp allocateArray(List<Temp> dimensions, int depth) {
        Temp array = new Temp();
        Temp length = dimensions.get(depth);
        Tree.Stm statements = MOVE(TEMP(array),
                externalCall("new", TEMP(length), TEMP(length)));

        if (depth + 1 < dimensions.size() && dimensions.get(depth + 1) != null) {
            Temp index = new Temp();
            Label test = new Label();
            Label body = new Label();
            Label done = new Label();
            Tree.Exp element = MEM(BINOP(Tree.BINOP.PLUS, TEMP(array),
                    BINOP(Tree.BINOP.MUL, TEMP(index), CONST(frame.wordSize()))));
            statements = seq(statements,
                    MOVE(TEMP(index), CONST(0)),
                    new Tree.LABEL(test),
                    new Tree.CJUMP(Tree.CJUMP.LT, TEMP(index), TEMP(length), body, done),
                    new Tree.LABEL(body),
                    MOVE(element, allocateArray(dimensions, depth + 1)),
                    MOVE(TEMP(index), BINOP(Tree.BINOP.PLUS, TEMP(index), CONST(1))),
                    new Tree.JUMP(test),
                    new Tree.LABEL(done));
        }
        return new Tree.ESEQ(statements, TEMP(array));
    }

    public Exp visit(Absyn.NewArrayExpr expression) {
        ArrayList<Temp> dimensions = new ArrayList<Temp>();
        Tree.Stm evaluateDimensions = null;
        for (Absyn.Expr dimension : expression.dimensions) {
            if (dimension == null) {
                dimensions.add(null);
                continue;
            }
            Temp length = new Temp();
            Label okay = new Label();
            Label invalid = new Label();
            dimensions.add(length);
            evaluateDimensions = seq(evaluateDimensions,
                    MOVE(TEMP(length), dimension.accept(this).unEx()),
                    new Tree.CJUMP(Tree.CJUMP.GE, TEMP(length), CONST(0), okay, invalid),
                    new Tree.LABEL(invalid),
                    EXP(new Tree.CALL(NAME(frame.badSub()), new ArrayList<Tree.Exp>())),
                    MOVE(TEMP(length), CONST(0)),
                    new Tree.JUMP(okay),
                    new Tree.LABEL(okay));
        }
        if (dimensions.isEmpty() || dimensions.get(0) == null) return new Ex(CONST(0));
        return new Ex(new Tree.ESEQ(evaluateDimensions, allocateArray(dimensions, 0)));
    }

    public Exp visit(Absyn.XinuCallStmt statement) {
        return new Nx(EXP(externalCall(statement.method,
                translateArguments(statement.args))));
    }

    public Exp visit(Absyn.XinuCallExpr expression) {
        return new Ex(externalCall(expression.method,
                translateArguments(expression.args)));
    }

    public Exp visit(Absyn.CallExpr expression) {
        Temp receiver = new Temp();
        Label valid = new Label();
        Label invalid = new Label();
        Tree.Stm check = seq(
                MOVE(TEMP(receiver), expression.target.accept(this).unEx()),
                new Tree.CJUMP(Tree.CJUMP.NE, TEMP(receiver), CONST(0), valid, invalid),
                new Tree.LABEL(invalid),
                EXP(new Tree.CALL(NAME(frame.badPtr()), new ArrayList<Tree.Exp>())),
                new Tree.JUMP(valid),
                new Tree.LABEL(valid));

        ArrayList<Tree.Exp> arguments = new ArrayList<Tree.Exp>();
        arguments.add(TEMP(receiver));
        arguments.addAll(translateArguments(expression.args));

        int index = expression.index == null ? 0 : expression.index.intValue();
        Tree.Exp vtable = MEM(BINOP(Tree.BINOP.MINUS, TEMP(receiver),
                CONST(frame.wordSize())));
        Tree.Exp function = MEM(BINOP(Tree.BINOP.PLUS, vtable,
                CONST(index * frame.wordSize())));
        return new Ex(new Tree.ESEQ(check, new Tree.CALL(function, arguments)));
    }

    public Exp visit(Absyn.FalseExpr expression) {
        return new Ex(CONST(0));
    }

    public Exp visit(Absyn.IntegerLiteral expression) {
        return new Ex(CONST(expression.value));
    }

    public Exp visit(Absyn.NullExpr expression) {
        return new Ex(CONST(0));
    }

    private List<Absyn.ClassDecl> initializationOrder(CLASS descriptor) {
        LinkedList<Absyn.ClassDecl> declarations = new LinkedList<Absyn.ClassDecl>();
        for (CLASS current = descriptor; current != null; current = current.parent) {
            Absyn.ClassDecl declaration = classDeclarations.get(current.name);
            if (declaration != null) declarations.addFirst(declaration);
        }
        return declarations;
    }

    public Exp visit(Absyn.NewObjectExpr expression) {
        if (!(expression.type instanceof Absyn.IdentifierType)) return new Ex(CONST(0));
        String className = ((Absyn.IdentifierType)expression.type).id;
        CLASS descriptor = classEnvironment.get(className);
        if (descriptor == null) return new Ex(CONST(0));

        Temp object = new Temp();
        Tree.Stm statements = MOVE(TEMP(object), externalCall("new",
                CONST(descriptor.instance.fields.size()), NAME(vtableLabel(className))));

        CLASS savedClass = currentClass;
        Absyn.ClassDecl savedDeclaration = currentClassDeclaration;
        Table<Frame.Access> savedVariables = variableEnvironment;
        Tree.Exp savedOverride = thisOverride;
        thisOverride = TEMP(object);
        variableEnvironment = new Table<Frame.Access>();

        for (Absyn.ClassDecl declaration : initializationOrder(descriptor)) {
            currentClassDeclaration = declaration;
            currentClass = classEnvironment.get(declaration.name);
            for (Absyn.VarDecl field : declaration.fields) {
                if (field.init == null) continue;
                FIELD fieldDescriptor = descriptor.instance.fields.get(field.name);
                if (fieldDescriptor == null) continue;
                Tree.Exp location = MEM(BINOP(Tree.BINOP.PLUS, TEMP(object),
                        CONST(fieldDescriptor.index * frame.wordSize())));
                statements = seq(statements,
                        MOVE(location, field.init.accept(this).unEx()));
            }
        }

        currentClass = savedClass;
        currentClassDeclaration = savedDeclaration;
        variableEnvironment = savedVariables;
        thisOverride = savedOverride;
        return new Ex(new Tree.ESEQ(statements, TEMP(object)));
    }

    public Exp visit(Absyn.StringLiteral expression) {
        String value = expression.value;
        if (value.length() >= 2 && value.charAt(0) == '"'
                && value.charAt(value.length() - 1) == '"') {
            value = value.substring(1, value.length() - 1);
        }
        value = value.intern();
        Label label = strings.get(value);
        if (label == null) {
            label = new Label();
            strings.put(value, label);
            fragments.add(new DataFrag(framePrototype.string(label, value)));
        }
        return new Ex(NAME(label));
    }

    private Tree.Exp currentThis() {
        if (thisOverride != null) return thisOverride;
        if (thisAccess == null) return CONST(0);
        return thisAccess.exp(frame);
    }

    public Exp visit(Absyn.ThisExpr expression) {
        return new Ex(currentThis());
    }

    public Exp visit(Absyn.TrueExpr expression) {
        return new Ex(CONST(1));
    }

    public Exp visit(Absyn.FieldExpr expression) {
        Tree.Exp target = checkedPointer(expression.target.accept(this).unEx());
        int index = expression.index == null ? 0 : expression.index.intValue();
        if (index < 0 && "length".equals(expression.field)) {
            return new Ex(MEM(BINOP(Tree.BINOP.MINUS, target,
                    CONST(frame.wordSize()))));
        }
        return new Ex(MEM(BINOP(Tree.BINOP.PLUS, target,
                CONST(index * frame.wordSize()))));
    }

    public int getindex(String variableName) {
        if (currentClass == null) return -1;
        FIELD field = currentClass.instance.fields.get(variableName);
        return field == null ? -1 : field.index;
    }

    public Exp visitThis(String variableName) {
        int index = getindex(variableName);
        if (index < 0) throw new IllegalArgumentException("Unknown field " + variableName);
        return new Ex(MEM(BINOP(Tree.BINOP.PLUS, checkedPointer(currentThis()),
                CONST(index * frame.wordSize()))));
    }

    public Exp visit(Absyn.IdentifierExpr expression) {
        Frame.Access access = variableEnvironment.get(expression.id);
        if (access != null) return new Ex(access.exp(frame));
        return visitThis(expression.id);
    }

    public Exp visit(Absyn.ArrayExpr expression) {
        return new Ex(arrayElement(expression.target.accept(this).unEx(),
                expression.index.accept(this).unEx()));
    }
}
