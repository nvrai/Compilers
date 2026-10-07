/**
 * COSC 4400 - Project #4
 * Tests inherited field hiding in class descriptors.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
class FieldOverride {
    public static void main(String[] args) {
    }
}

class FieldParent {
    int first;
    int second;
}

class FieldChild extends FieldParent {
    int first;
}
