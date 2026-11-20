/**
 * COSC 4400 - Project #8
 * Orders basic blocks into traces with each CJUMP followed by its false label.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Canon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import Temp.Label;

/** Appel-style trace scheduling for canonical basic blocks. */
public final class TraceSchedule {
    public final List<Tree.Stm> stms = new ArrayList<Tree.Stm>();
    private final Map<Label, List<Tree.Stm>> table = new HashMap<Label, List<Tree.Stm>>();
    private final Set<Label> marked = new HashSet<Label>();
    private final BasicBlocks basicBlocks;

    public TraceSchedule(BasicBlocks basicBlocks) {
        this.basicBlocks = basicBlocks;
        for (List<Tree.Stm> block : basicBlocks.blocks) {
            table.put(((Tree.LABEL)block.get(0)).label, block);
        }
        for (List<Tree.Stm> block : basicBlocks.blocks) {
            Label label = ((Tree.LABEL)block.get(0)).label;
            if (!marked.contains(label)) trace(block);
        }
        stms.add(new Tree.LABEL(basicBlocks.done));
    }

    private boolean available(Label label) {
        return table.containsKey(label) && !marked.contains(label);
    }

    private void appendBody(List<Tree.Stm> block) {
        for (int index = 0; index + 1 < block.size(); index++) {
            stms.add(block.get(index));
        }
    }

    private void trace(List<Tree.Stm> start) {
        List<Tree.Stm> block = start;
        while (block != null) {
            Label blockLabel = ((Tree.LABEL)block.get(0)).label;
            if (marked.contains(blockLabel)) return;
            marked.add(blockLabel);
            appendBody(block);

            Tree.Stm transfer = block.get(block.size() - 1);
            if (transfer instanceof Tree.JUMP) {
                Tree.JUMP jump = (Tree.JUMP)transfer;
                if (jump.targets.size() == 1 && available(jump.targets.getFirst())) {
                    block = table.get(jump.targets.getFirst());
                    continue;
                }
                stms.add(jump);
                return;
            }

            Tree.CJUMP jump = (Tree.CJUMP)transfer;
            if (available(jump.iffalse)) {
                stms.add(jump);
                block = table.get(jump.iffalse);
                continue;
            }
            if (available(jump.iftrue)) {
                stms.add(new Tree.CJUMP(Tree.CJUMP.notRel(jump.relop),
                        jump.left, jump.right, jump.iffalse, jump.iftrue));
                block = table.get(jump.iftrue);
                continue;
            }

            Label falseLabel = new Label();
            stms.add(new Tree.CJUMP(jump.relop, jump.left, jump.right,
                    jump.iftrue, falseLabel));
            stms.add(new Tree.LABEL(falseLabel));
            stms.add(new Tree.JUMP(jump.iffalse));
            return;
        }
    }
}
