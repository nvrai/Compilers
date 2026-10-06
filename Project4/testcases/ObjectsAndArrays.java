/**
 * COSC 4400 - Project #4
 * Provides a MiniJava test case for the class descriptor pass.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
class ObjectsAndArrays {
  public static void main(String[] args) {
    Box b = new Box();
    int[][] values = new int[5][];
    values[0] = new int[2];
    b.value = values[0][1];
    Xinu.printint(b.get());
  }
}
class Box {
  int value;
  public int get() {
    return this.value;
  }
}
