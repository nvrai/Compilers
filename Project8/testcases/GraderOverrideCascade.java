/**
 * COSC 4400 - Project 5
 * Verifies that a class-table error suppresses cascading method-body errors.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
class GraderOverrideCascade {
    public static void main(String[] args) {
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
