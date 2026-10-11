/**
 * COSC 4400 - Project #4
 * Implements a scoped symbol table for semantic analysis.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Symbol;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/** A symbol table whose bindings can be removed one scope at a time. */
public final class Table<T> {
    private static final class Binding<T> {
        final String name;
        final T previous;
        final boolean hadPrevious;

        Binding(String name, T previous, boolean hadPrevious) {
            this.name = name;
            this.previous = previous;
            this.hadPrevious = hadPrevious;
        }
    }

    private final Map<String, T> values = new HashMap<String, T>();
    private final Deque<Binding<T>> changes = new ArrayDeque<Binding<T>>();
    private final Deque<Integer> scopes = new ArrayDeque<Integer>();

    public Table() {
        beginScope();
    }

    /** Adds or replaces the binding for {@code name} in the current scope. */
    public T put(String name, T value) {
        boolean hadPrevious = values.containsKey(name);
        T previous = values.put(name, value);
        changes.push(new Binding<T>(name, previous, hadPrevious));
        return previous;
    }

    /** Returns the current binding, or null when the name is not defined. */
    public T get(String name) {
        return values.get(name);
    }

    /** Starts a new nested scope. */
    public void beginScope() {
        scopes.push(Integer.valueOf(changes.size()));
    }

    /** Removes every binding added since the matching beginScope call. */
    public void endScope() {
        if (scopes.isEmpty()) {
            throw new IllegalStateException("No scope to end");
        }

        int boundary = scopes.pop().intValue();
        while (changes.size() > boundary) {
            Binding<T> binding = changes.pop();
            if (binding.hadPrevious) {
                values.put(binding.name, binding.previous);
            } else {
                values.remove(binding.name);
            }
        }
    }
}
