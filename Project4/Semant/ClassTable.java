/**
 * COSC 4400 - Project #4
 * Builds class, field, and method descriptors for a MiniJava program.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Semant;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import Absyn.ClassDecl;
import Absyn.Formal;
import Absyn.MethodDecl;
import Absyn.Program;
import Absyn.VarDecl;
import Symbol.Table;
import Types.ARRAY;
import Types.BOOLEAN;
import Types.CLASS;
import Types.FIELD;
import Types.FUNCTION;
import Types.INT;
import Types.OBJECT;
import Types.RECORD;
import Types.STRING;
import Types.Type;
import Types.VOID;

/** The global type environment constructed during Project 4. */
public final class ClassTable {
    private enum State { NEW, BUILDING, BUILT }

    private final Table<CLASS> classes = new Table<CLASS>();
    private final Map<String, ClassDecl> declarations = new LinkedHashMap<String, ClassDecl>();
    private final List<CLASS> programClasses = new ArrayList<CLASS>();
    private final Map<CLASS, State> states = new IdentityHashMap<CLASS, State>();
    private final ErrorReporter errors;

    private final CLASS stringClass = new CLASS("String");
    private final CLASS threadClass = new CLASS("Thread");

    private ClassTable(ErrorReporter errors) {
        this.errors = errors;
        classes.put("String", stringClass);
        classes.put("Thread", threadClass);
        addThreadRunMethod();
    }

    public static ClassTable build(Program program, ErrorReporter errors) {
        ClassTable table = new ClassTable(errors);
        table.collectClasses(program);
        table.resolveParents();
        table.checkInheritance();
        table.buildMembers();
        return table;
    }

    public CLASS get(String name) {
        return classes.get(name);
    }

    public List<CLASS> programClasses() {
        return new ArrayList<CLASS>(programClasses);
    }

    private void addThreadRunMethod() {
        FUNCTION run = new FUNCTION("run", threadClass.instance, new RECORD(), new VOID());
        threadClass.methods.put(run, "run");
        threadClass.instance.methods.put(run, "run");
    }

    private void collectClasses(Program program) {
        for (ClassDecl declaration : program.classes) {
            if (classes.get(declaration.name) != null) {
                errors.report("duplicate class");
                continue;
            }
            CLASS descriptor = new CLASS(declaration.name);
            classes.put(declaration.name, descriptor);
            declarations.put(declaration.name, declaration);
            programClasses.add(descriptor);
            states.put(descriptor, State.NEW);
        }
    }

    private void resolveParents() {
        for (CLASS descriptor : programClasses) {
            ClassDecl declaration = declarations.get(descriptor.name);
            if (declaration.parent == null) continue;

            CLASS parent = classes.get(declaration.parent);
            if (parent == null) {
                errors.report("cannot resolve parent class " + declaration.parent);
            } else {
                descriptor.parent = parent;
            }
        }
    }

    private void checkInheritance() {
        Map<CLASS, Integer> colors = new IdentityHashMap<CLASS, Integer>();
        for (CLASS descriptor : programClasses) {
            if (!colors.containsKey(descriptor)) visitParent(descriptor, colors);
        }
    }

    private void visitParent(CLASS descriptor, Map<CLASS, Integer> colors) {
        colors.put(descriptor, Integer.valueOf(1));
        CLASS parent = descriptor.parent;
        if (parent != null && declarations.containsKey(parent.name)) {
            Integer color = colors.get(parent);
            if (color == null) {
                visitParent(parent, colors);
            } else if (color.intValue() == 1) {
                errors.report("cyclic inheritance involving " + parent.name);
                descriptor.parent = null;
            }
        }
        colors.put(descriptor, Integer.valueOf(2));
    }

    private void buildMembers() {
        for (CLASS descriptor : programClasses) buildClass(descriptor);
    }

    private void buildClass(CLASS descriptor) {
        State state = states.get(descriptor);
        if (state == State.BUILT) return;
        if (state == State.BUILDING) return;
        states.put(descriptor, State.BUILDING);

        if (descriptor.parent != null && declarations.containsKey(descriptor.parent.name)) {
            buildClass(descriptor.parent);
        }

        ClassDecl declaration = declarations.get(descriptor.name);
        addFields(descriptor, declaration);
        addMethods(descriptor, declaration);
        createInstanceMembers(descriptor);
        states.put(descriptor, State.BUILT);
    }

    private void addFields(CLASS descriptor, ClassDecl declaration) {
        for (VarDecl field : declaration.fields) {
            if (descriptor.fields.get(field.name) != null) {
                errors.report(field.name + " is already defined in " + descriptor.name);
                continue;
            }
            descriptor.fields.put(resolveType(field.type), field.name);
        }
    }

    private void addMethods(CLASS descriptor, ClassDecl declaration) {
        for (MethodDecl method : declaration.methods) {
            if (descriptor.methods.get(method.name) != null) {
                errors.report(method.name + " is already defined in " + descriptor.name);
                continue;
            }

            RECORD formals = new RECORD();
            for (Formal formal : method.params) {
                if (formals.get(formal.name) != null) {
                    errors.report(formal.name + " is already defined in " + method.name);
                    continue;
                }
                formals.put(resolveType(formal.type), formal.name);
            }

            Type result = method.returnType == null ? new VOID() : resolveType(method.returnType);
            FUNCTION function = new FUNCTION(method.name, descriptor.instance, formals, result);

            FIELD inherited = inheritedMethod(descriptor.parent, method.name);
            if (inherited != null && inherited.type instanceof FUNCTION
                    && !function.coerceTo(inherited.type)) {
                errors.report("incompatible method override: " + method.name
                        + " in class " + descriptor.name);
            }
            descriptor.methods.put(function, method.name);
        }
    }

    private FIELD inheritedMethod(CLASS parent, String name) {
        while (parent != null) {
            FIELD method = parent.methods.get(name);
            if (method != null) return method;
            parent = parent.parent;
        }
        return null;
    }

    private void createInstanceMembers(CLASS descriptor) {
        RECORD fields = new RECORD();
        RECORD methods = new RECORD();

        if (descriptor.parent != null) {
            copy(descriptor.parent.instance.fields, fields, descriptor.fields);
            copy(descriptor.parent.instance.methods, methods, descriptor.methods);
        }
        copy(descriptor.fields, fields, null);
        copy(descriptor.methods, methods, null);

        descriptor.instance.fields = fields;
        descriptor.instance.methods = methods;
    }

    private static void copy(RECORD source, RECORD target, RECORD replacements) {
        for (FIELD field : source) {
            if (replacements == null || replacements.get(field.name) == null) {
                target.put(field.type, field.name);
            }
        }
    }

    private Type resolveType(Absyn.Type type) {
        if (type instanceof Absyn.IntegerType) return new INT();
        if (type instanceof Absyn.BooleanType) return new BOOLEAN();
        if (type instanceof Absyn.ArrayType) {
            return new ARRAY(resolveType(((Absyn.ArrayType) type).base));
        }
        if (type instanceof Absyn.IdentifierType) {
            String name = ((Absyn.IdentifierType) type).id;
            if ("String".equals(name)) return new STRING();
            CLASS descriptor = classes.get(name);
            if (descriptor == null) {
                errors.report("cannot resolve class " + name);
                return new OBJECT(new CLASS(name));
            }
            return descriptor.instance;
        }
        throw new IllegalArgumentException("Unknown AST type " + type.getClass().getName());
    }
}
