/**
 * COSC 4400 - Project #4
 * Stores one node while reading a textual abstract syntax tree.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Parse;

import java.util.ArrayList;
import java.util.List;

public final class AstNode {
    public final String name;
    public final List<Object> items = new ArrayList<Object>();

    public AstNode(String name) {
        this.name = name;
    }

    public void add(Object item) {
        items.add(item);
    }
}
