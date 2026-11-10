/**
 * COSC 4400 - Project 5
 * Provides a MiniJava test case for the class descriptor pass.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
class ValidDescriptors {
    public static void main(String[] args) {
        Xinu.print("descriptors built");
        Xinu.println();
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
        Xinu.print("running");
        Xinu.println();
    }
}
