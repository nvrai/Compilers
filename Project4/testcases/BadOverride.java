/**
 * COSC 4400 - Project #4
 * Provides a MiniJava test case for the class descriptor pass.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
class BadOverride {
    public static void main(String[] args) {
    }
}

class Parent {
    public int value(int amount) {
        return amount;
    }
}

class Child extends Parent {
    public boolean value(int amount) {
        return true;
    }
}
