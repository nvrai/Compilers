/**
 * COSC 4400 - Project 5
 * Exercises each major expression and statement type error handled by the checker.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
class Main {
  public static void main(String[] args) {
    Sample sample = new Sample();
    int number = true;
    boolean flag = 1;
    int[] values = new int[false];
    number = flag + 1;
    missing = number;
    if (number) number = 1;
    number = number[0];
    number = number.value;
    number = sample.identity();
    number = sample.identity(flag);
    flag = values == number;
    Xinu.unknown(number);
  }
}

class Sample {
  public int identity(int value) {
    boolean result = value;
    return result;
  }
}
