/**
 * COSC 4400 - Project 5
 * Exercises valid inheritance, arrays, calls, assignments, and Xinu operations.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
class Main {
  public static void main(String[] args) {
    Parent item = new Child();
    int[] values = new int[4];
    int count = values.length;
    boolean ready = count > 0 && true;
    values[0] = item.identity(count);
    if (ready) Xinu.printint(values[0]); else { Xinu.print("empty"); Xinu.println(); }
  }
}

class Parent {
  int value;

  public int identity(int input) {
    value = input;
    return value;
  }
}

class Child extends Parent {
  public int identity(int input) {
    return input;
  }
}

class Worker extends Thread {
  int[][] grid;

  public void run() {
    grid = new int[2][];
    Xinu.print("worker");
  }
}
