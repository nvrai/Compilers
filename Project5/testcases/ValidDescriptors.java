/**
 * COSC 4400 - Project #4
 * Provides a MiniJava test case for the class descriptor pass.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
class ValidDescriptors {
    public static void main(String[] args) {
        Xinu.println("descriptors built");
    }
}

class Parent {
    int count;

    public int value(int amount) {
        return amount;
    }
}

class Child extends Parent {
    boolean ready;

    public int value(int amount) {
        return amount;
    }
}

class Worker extends Thread {
    public void run() {
        Xinu.println("running");
    }
}
