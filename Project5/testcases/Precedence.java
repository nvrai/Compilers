/**
 * COSC 4400 - Project #4
 * Provides a MiniJava test case for the class descriptor pass.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
class Precedence {
  public static void main(String[] args) {
    int result = 1 + 2 * 3 - 8 / 4;
    boolean test = 1 + 2 * 3 < 8 && true || false;
    Xinu.printint(result);
    Xinu.println("precedence complete");
  }
}
