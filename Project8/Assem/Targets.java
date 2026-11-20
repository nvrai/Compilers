/**
 * COSC 4400 - Project #8
 * Stores the possible control-flow targets of an assembly instruction.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Assem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import Temp.Label;

/** Immutable jump-target metadata. */
public final class Targets {
    public final List<Label> labels;

    public Targets(List<Label> labels) {
        this.labels = Collections.unmodifiableList(new ArrayList<Label>(labels));
    }
}
