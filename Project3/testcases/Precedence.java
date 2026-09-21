class Precedence {
  public static void main(String[] args) {
    int result = 1 + 2 * 3 - 8 / 4;
    boolean test = 1 + 2 * 3 < 8 && true || false;
    Xinu.printint(result);
    Xinu.println("precedence complete");
  }
}
