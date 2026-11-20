/**
 * COSC 4400 - Project #8
 * Partitions canonical IR statements into single-entry, single-exit basic blocks.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Canon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import Temp.Label;

/** A collection of basic blocks and the synthetic procedure-done label. */
public final class BasicBlocks {
    public final List<List<Tree.Stm>> blocks = new ArrayList<List<Tree.Stm>>();
    public final Label done = new Label();

    public BasicBlocks(List<Tree.Stm> statements) {
        build(statements == null ? Collections.<Tree.Stm>emptyList() : statements);
    }

    private static boolean transfer(Tree.Stm statement) {
        return statement instanceof Tree.JUMP || statement instanceof Tree.CJUMP;
    }

    private void build(List<Tree.Stm> statements) {
        int index = 0;
        while (index < statements.size()) {
            ArrayList<Tree.Stm> block = new ArrayList<Tree.Stm>();
            Tree.Stm first = statements.get(index);
            if (first instanceof Tree.LABEL) {
                block.add(first);
                index++;
            } else {
                block.add(new Tree.LABEL(new Label()));
            }

            while (index < statements.size()) {
                Tree.Stm statement = statements.get(index);
                if (statement instanceof Tree.LABEL) {
                    block.add(new Tree.JUMP(((Tree.LABEL)statement).label));
                    break;
                }
                block.add(statement);
                index++;
                if (transfer(statement)) break;
            }

            if (!transfer(block.get(block.size() - 1))) {
                block.add(new Tree.JUMP(done));
            }
            blocks.add(block);
        }

        if (blocks.isEmpty()) {
            ArrayList<Tree.Stm> block = new ArrayList<Tree.Stm>();
            block.add(new Tree.LABEL(new Label()));
            block.add(new Tree.JUMP(done));
            blocks.add(block);
        }
    }
}
