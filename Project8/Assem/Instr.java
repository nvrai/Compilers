/**
 * COSC 4400 - Project #8
 * Defines a target instruction and formats its temporary and label operands.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Assem;

import java.util.Collections;
import java.util.List;
import Temp.Label;
import Temp.Temp;
import Temp.TempMap;

/** Base class for assembly instructions emitted by instruction selection. */
public abstract class Instr {
    public final String assem;

    protected Instr(String assem) {
        this.assem = assem;
    }

    public abstract List<Temp> use();
    public abstract List<Temp> def();
    public abstract Targets jumps();

    private static <T> void appendValues(StringBuilder result, String name,
            List<T> values) {
        if (values == null || values.isEmpty()) return;
        result.append(' ').append(name).append('(');
        for (T value : values) result.append(value).append(' ');
        result.append(')');
    }

    /** Prints the instruction and its data-flow metadata for the next phase. */
    public String toString() {
        StringBuilder result = new StringBuilder(getClass().getSimpleName());
        result.append("(\"").append(assem).append('"');
        appendValues(result, "defs", def());
        appendValues(result, "uses", use());
        Targets targets = jumps();
        if (targets != null) appendValues(result, "jumps", targets.labels);
        return result.append(')').toString();
    }

    private static int operand(String template, int[] position) {
        int value = 0;
        int index = position[0];
        if (index >= template.length() || !Character.isDigit(template.charAt(index))) {
            throw new IllegalArgumentException("missing assembly operand index");
        }
        while (index < template.length() && Character.isDigit(template.charAt(index))) {
            value = value * 10 + Character.digit(template.charAt(index), 10);
            index++;
        }
        position[0] = index;
        return value;
    }

    /** Expands `s, `d, and `j placeholders using the supplied temporary map. */
    public String format(TempMap map) {
        List<Temp> sources = use() == null ? Collections.<Temp>emptyList() : use();
        List<Temp> destinations = def() == null
                ? Collections.<Temp>emptyList() : def();
        Targets targets = jumps();
        List<Label> labels = targets == null
                ? Collections.<Label>emptyList() : targets.labels;
        StringBuilder result = new StringBuilder();
        for (int index = 0; index < assem.length();) {
            char character = assem.charAt(index++);
            if (character != '`') {
                result.append(character);
                continue;
            }
            if (index >= assem.length()) throw new IllegalArgumentException("bad assembly format");
            char kind = assem.charAt(index++);
            if (kind == '`') {
                result.append('`');
                continue;
            }
            int[] position = { index };
            int operand = operand(assem, position);
            index = position[0];
            if (kind == 's') result.append(map.tempMap(sources.get(operand)));
            else if (kind == 'd') result.append(map.tempMap(destinations.get(operand)));
            else if (kind == 'j') result.append(labels.get(operand));
            else throw new IllegalArgumentException("bad assembly operand kind " + kind);
        }
        return result.toString();
    }
}
