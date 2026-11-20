/**
 * COSC 4400 - Project #8
 * Represents a register-to-register assembly move.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Assem;

import java.util.Collections;
import java.util.List;
import Temp.Temp;

/** A copy instruction kept distinct for later register coalescing. */
public final class MOVE extends Instr {
    public final Temp dst;
    public final Temp src;

    public MOVE(String assem, Temp dst, Temp src) {
        super(assem);
        this.dst = dst;
        this.src = src;
    }

    public List<Temp> use() { return Collections.singletonList(src); }
    public List<Temp> def() { return Collections.singletonList(dst); }
    public Targets jumps() { return null; }
}
