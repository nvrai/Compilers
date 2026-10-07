/**
 * COSC 4400 - Project #4
 * Tests an incompatible method argument in an override.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
class MethodOverrideArgError {
    public static void main(String[] args) {
    }
}

class ArgumentParent {
    public int value(ArgumentParent item) {
        return 0;
    }
}

class ArgumentChild extends ArgumentParent {
    public int value(ArgumentChild item) {
        return 0;
    }
}
