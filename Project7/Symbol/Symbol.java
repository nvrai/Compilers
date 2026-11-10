/**
 * COSC 4400 - Project #7
 * Provides interned symbolic names used by frames, labels, and translation.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Symbol;

import java.util.HashMap;
import java.util.Map;

/** A unique symbolic representation of a string. */
public final class Symbol {
    private static final Map<String, Symbol> symbols = new HashMap<String, Symbol>();

    private final String name;

    private Symbol(String name) {
        this.name = name;
    }

    /** Returns the unique symbol associated with {@code name}. */
    public static synchronized Symbol get(String name) {
        Symbol symbol = symbols.get(name);
        if (symbol == null) {
            symbol = new Symbol(name);
            symbols.put(name, symbol);
        }
        return symbol;
    }

    @Override
    public String toString() {
        return name;
    }
}
