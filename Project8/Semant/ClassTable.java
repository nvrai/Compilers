/**
 * COSC 4400 - Project 5
 * Builds and queries the class environment used by the MiniJava type checker.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Semant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import Types.RECORD;
import Types.Type;
import Types.VOID;

/** The global type environment constructed during Project 4. */
public final class ClassTable {
    private enum State { NEW, BUILDING, BUILT }

    private final Table<CLASS> classes = new Table<CLASS>();
    private final Map<CLASS, ClassDecl> declarationByClass =
            new IdentityHashMap<CLASS, ClassDecl>();
    private final Map<ClassDecl, CLASS> classByDeclaration =
            new IdentityHashMap<ClassDecl, CLASS>();
    private final Map<MethodDecl, FUNCTION> functionByDeclaration =
            new IdentityHashMap<MethodDecl, FUNCTION>();
    private final List<CLASS> programClasses = new ArrayList<CLASS>();
    private final Map<CLASS, State> states = new IdentityHashMap<CLASS, State>();
    private final Set<CLASS> populated = Collections.newSetFromMap(
            new IdentityHashMap<CLASS, Boolean>());
    private final Set<CLASS> cyclicClasses = Collections.newSetFromMap(
            new IdentityHashMap<CLASS, Boolean>());
    private final ErrorReporter errors;

    private final CLASS stringClass = new CLASS("String");
    private final CLASS threadClass = new CLASS("Thread");
    private boolean duplicateClasses;
    private boolean unresolvedParents;
    private boolean instancesAllowed = true;
    private boolean descriptorOutputAllowed = true;

    private ClassTable(ErrorReporter errors) {
        this.errors = errors;
        classes.put("String", stringClass);
        classes.put("Thread", threadClass);
        addThreadRunMethod();
    }

    public static ClassTable build(Program program, ErrorReporter errors) {
        ClassTable table = new ClassTable(errors);
        table.collectClasses(program);
        if (table.duplicateClasses) return table;
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

    public boolean descriptorOutputAllowed() {
        return descriptorOutputAllowed;
    }

    /** Returns the descriptor associated with a source-level class declaration. */
    public CLASS descriptorFor(ClassDecl declaration) {
        return classByDeclaration.get(declaration);
    }

    /** Returns the descriptor associated with a source-level method declaration. */
    public FUNCTION descriptorFor(MethodDecl declaration) {
        return functionByDeclaration.get(declaration);
    }

    /** True when class names and inheritance are sound enough for expression checking. */
    public boolean canTypeCheck() {
        return !duplicateClasses && !unresolvedParents && cyclicClasses.isEmpty();
    }

    /** Resolves an AST type in the global class environment. */
    public Type typeOf(Absyn.Type type) {
        return resolveType(type);
    }

    /** Looks up an instance field, including inherited fields. */
    public FIELD lookupField(CLASS owner, String name) {
        FIELD instanceField = owner.instance.fields.get(name);
        if (instanceField != null) return instanceField;
        Set<CLASS> visited = Collections.newSetFromMap(new IdentityHashMap<CLASS, Boolean>());
        for (CLASS current = owner; current != null && visited.add(current);
                current = current.parent) {
            FIELD field = current.fields.get(name);
            if (field != null) return field;
        }
        return null;
    }

    /** Looks up an instance method, including inherited methods. */
    public FUNCTION lookupMethod(CLASS owner, String name) {
        FIELD field = lookupMethodField(owner, name);
        if (field != null && field.type instanceof FUNCTION) {
            return (FUNCTION) field.type;
        }
        return null;
    }

    /** Looks up a method field so calls can retain its dispatch-table index. */
    public FIELD lookupMethodField(CLASS owner, String name) {
        FIELD instanceMethod = owner.instance.methods.get(name);
        if (instanceMethod != null) return instanceMethod;
        return inheritedMethod(owner, name);
    }

    private void addThreadRunMethod() {
        FUNCTION run = new FUNCTION("run", threadClass.instance, new RECORD(), new VOID());
        threadClass.methods.put(run, "run");
        threadClass.instance.methods.put(run, "run");
    }

    private void collectClasses(Program program) {
        for (ClassDecl declaration : program.classes) {
            CLASS descriptor = new CLASS(declaration.name);
            programClasses.add(descriptor);
            declarationByClass.put(descriptor, declaration);
            classByDeclaration.put(declaration, descriptor);
            declaration.descriptor = descriptor;
            states.put(descriptor, State.NEW);

            if (classes.get(declaration.name) != null) {
                errors.report("duplicate class: " + declaration.name + ": line not available");
                duplicateClasses = true;
                descriptorOutputAllowed = false;
            } else {
                classes.put(declaration.name, descriptor);
            }
        }
    }

    private void resolveParents() {
        for (CLASS descriptor : programClasses) {
            ClassDecl declaration = declarationByClass.get(descriptor);
            if (declaration.parent == null) continue;

            CLASS parent = classes.get(declaration.parent);
            if (parent == null) {
                errors.report("cannot resolve parent class: " + declaration.parent
                        + ": line not available");
                unresolvedParents = true;
                instancesAllowed = false;
                descriptorOutputAllowed = false;
            } else {
                descriptor.parent = parent;
            }
        }
    }

    private void checkInheritance() {
        for (CLASS descriptor : programClasses) {
            if (hasCycle(descriptor)) {
                errors.report("cyclic inheritance involving " + descriptor.name
                        + ": line not available");
                cyclicClasses.add(descriptor);
                descriptorOutputAllowed = false;
            }
        }
    }

    private boolean hasCycle(CLASS start) {
        Set<CLASS> visited = Collections.newSetFromMap(new IdentityHashMap<CLASS, Boolean>());
        CLASS current = start.parent;
        while (current != null && declarationByClass.containsKey(current)) {
            if (current == start) return true;
            if (!visited.add(current)) return false;
            current = current.parent;
        }
        return false;
    }

    private void buildMembers() {
        for (CLASS descriptor : programClasses) buildClass(descriptor);
        if (instancesAllowed) {
            for (CLASS descriptor : programClasses) {
                if (canPopulate(descriptor)) populateInstance(descriptor);
            }
        }
    }

    private boolean canPopulate(CLASS descriptor) {
        Set<CLASS> visited = Collections.newSetFromMap(new IdentityHashMap<CLASS, Boolean>());
        CLASS current = descriptor;
        while (current != null && declarationByClass.containsKey(current)) {
            if (cyclicClasses.contains(current) || !visited.add(current)) return false;
            current = current.parent;
        }
        return true;
    }

    private void buildClass(CLASS descriptor) {
        State state = states.get(descriptor);
        if (state == State.BUILT || state == State.BUILDING) return;
        states.put(descriptor, State.BUILDING);

        if (descriptor.parent != null && declarationByClass.containsKey(descriptor.parent)) {
            buildClass(descriptor.parent);
        }

        ClassDecl declaration = declarationByClass.get(descriptor);
        addMethods(descriptor, declaration);
        addFields(descriptor, declaration);
        states.put(descriptor, State.BUILT);
    }

    private void addMethods(CLASS descriptor, ClassDecl declaration) {
        for (MethodDecl method : declaration.methods) {
            RECORD formals = new RECORD();
            for (Formal formal : method.params) {
                FIELD previous = formals.put(resolveType(formal.type), formal.name);
                if (previous != null) {
                    errors.report(formal.name + " is already defined in " + method.name,
                            formal);
                    instancesAllowed = false;
                }
            }

            Type result = method.returnType == null ? new VOID() : resolveType(method.returnType);
            FUNCTION function = new FUNCTION(method.name, descriptor.instance, formals, result);
            functionByDeclaration.put(method, function);

            FIELD inherited = inheritedMethod(descriptor.parent, method.name);
            if (inherited != null && inherited.type instanceof FUNCTION
                    && !function.coerceTo(inherited.type)) {
                errors.report("incompatible method override: " + method.name
                        + " in class " + descriptor.name + ": line not available");
                descriptorOutputAllowed = false;
            }

            FIELD previous = descriptor.methods.put(function, method.name);
            if (previous != null) {
                errors.report(method.name + " is already defined in " + descriptor.name,
                        method);
                instancesAllowed = false;
            }
            method.descriptor = function;
        }
    }

    private void addFields(CLASS descriptor, ClassDecl declaration) {
        for (VarDecl field : declaration.fields) {
            Type fieldType = resolveType(field.type);
            FIELD previous = descriptor.fields.put(fieldType, field.name);
            if (previous != null) {
                errors.report(field.name + " is already defined in " + descriptor.name,
                        field);
                instancesAllowed = false;
            }
            field.semanticType = fieldType;
        }
    }

    private FIELD inheritedMethod(CLASS parent, String name) {
        Set<CLASS> visited = Collections.newSetFromMap(new IdentityHashMap<CLASS, Boolean>());
        while (parent != null && visited.add(parent)) {
            FIELD method = parent.methods.get(name);
            if (method != null) return method;
            parent = parent.parent;
        }
        return null;
    }

    private void populateInstance(CLASS descriptor) {
        if (populated.contains(descriptor)) return;
        if (descriptor.parent != null && declarationByClass.containsKey(descriptor.parent)) {
            populateInstance(descriptor.parent);
        }
        createInstanceMembers(descriptor);
        populated.add(descriptor);
    }

    private void createInstanceMembers(CLASS descriptor) {
        RECORD fields = new RECORD();
        RECORD methods = new RECORD();

        if (descriptor.parent != null) {
            copy(descriptor.parent.instance.fields, fields, null);
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
            CLASS descriptor = classes.get(name);
            if (descriptor == null) {
                errors.report("cannot resolve class " + name, type);
                instancesAllowed = false;
                return new VOID();
            }
            return descriptor.instance;
        }
        throw new IllegalArgumentException("Unknown AST type " + type.getClass().getName());
    }

}
