/**
 * COSC 4400 - Project #8
 * Represents an assembly-language label definition.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Assem;

import java.util.Collections;
import java.util.List;
import Temp.Label;
import Temp.Temp;

/** An instruction that defines a code label. */
public final class LABEL extends Instr {
    public final Label label;

    public LABEL(String assem, Label label) {
        super(assem);
        this.label = label;
    }

    public List<Temp> use() { return Collections.emptyList(); }
    public List<Temp> def() { return Collections.emptyList(); }
    public Targets jumps() { return null; }
}
