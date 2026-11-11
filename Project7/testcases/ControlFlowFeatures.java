/**
 * COSC 4400 - Project #7
 * Exercises default local initialization and short-circuit conditional translation.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
class Main {
  public static void main(String[] args) {
    Logic logic = new Logic();
    Xinu.printint(logic.run());
  }
}

class Logic {
  public int run() {
    boolean left;
    boolean right;
    int result;
    left = true;
    right = false;
    if (!(left && right) || (left && !right)) {
      result = 1;
    } else {
      result = 0;
    }
    return result;
  }
}
