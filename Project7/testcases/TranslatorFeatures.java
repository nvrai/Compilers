/**
 * COSC 4400 - Project #7
 * Exercises IR translation for inheritance, dispatch, arrays, control flow, and threads.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
class Main {
  public static void main(String[] args) {
    Calculator calculator = new Calculator();
    int[] values = new int[3];
    int result = calculator.sum(1, 2, 3, 4, 5, 6, 7, 8, 9);
    boolean valid = result > 0 && !(values.length == 0);
    values[1] = result;
    if (valid || false) {
      Xinu.printint(values[1]);
    } else {
      Xinu.print("invalid");
    }
    Xinu.println();
    Xinu.threadCreate(new Worker());
  }
}

class Parent {
  int base;

  public int adjust(int amount) {
    base = amount + 1;
    return base;
  }
}

class Calculator extends Parent {
  int value;

  public synchronized int sum(int a, int b, int c, int d, int e,
      int f, int g, int h, int i) {
    int total = a + b + c + d + e + f + g + h + i;
    value = total;
    return this.adjust(value);
  }
}

class Worker extends Thread {
  int counter;

  public void run() {
    counter = 0;
    while (counter < 2) {
      counter = counter + 1;
      Xinu.yield();
    }
    Xinu.sleep(1);
  }
}
