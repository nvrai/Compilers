/**
 * COSC 4400 - Project 5
 * Covers name shadowing and diagnostic regressions found by the grading tests.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
class GraderRegressions {
    public static void main(String[] args) {
        boolean args;
        if (args) {
        }
    }
}

class DuplicateLocal {
    public int test() {
        int value;
        int value;
        return 0;
    }
}

class MissingField {
    public int test() {
        MissingField target = new MissingField();
        return target.value;
    }
}

class OverrideParent {
    public int convert(int value) {
        return value;
    }
}

class OverrideChild extends OverrideParent {
    public int convert(OverrideChild value) {
        return value;
    }
}
