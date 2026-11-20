/**
 * COSC 4400 - Project #8
 * Exercises arithmetic, memory, branches, loops, calls, and argument moves.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
class Main {
  public static void main(String[] args) {
    Selector selector = new Selector();
    Xinu.printint(selector.run(10, 3));
  }
}

class Selector {
  public int run(int left, int right) {
    int[] values = new int[4];
    int result;
    int index;
    result = (left + right) * (left - right) / (right + 1);
    values[2] = result;
    if (result > 0 && left != right) {
      result = values[2] + 8;
    } else {
      result = 0 - result;
    }
    index = 0;
    while (index < 2) {
      result = result + index;
      index = index + 1;
    }
    return result;
  }
}
