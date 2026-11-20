/**
 * COSC 4400 - Project #8
 * Represents a general assembly operation with def, use, and jump metadata.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Assem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import Temp.Label;
import Temp.Temp;

/** A non-copy assembly operation. */
public final class OPER extends Instr {
    private final List<Temp> dst;
    private final List<Temp> src;
    private final Targets jump;

    public OPER(String assem, List<Temp> dst, List<Temp> src) {
        this(assem, dst, src, null);
    }

    public OPER(String assem, List<Temp> dst, List<Temp> src, List<Label> jumps) {
        super(assem);
        this.dst = immutable(dst);
        this.src = immutable(src);
        this.jump = jumps == null ? null : new Targets(jumps);
    }

    private static <T> List<T> immutable(List<T> values) {
        if (values == null || values.isEmpty()) return Collections.emptyList();
        return Collections.unmodifiableList(new ArrayList<T>(values));
    }

    public List<Temp> use() { return src; }
    public List<Temp> def() { return dst; }
    public Targets jumps() { return jump; }
}
