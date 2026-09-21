class Main {
  public static void main(String[] args) {
    int x = 1 + 2 * 3;
    int[] a = new int[10];
    x = a[0] + Xinu.readint();
    if (x > 2 && true) Xinu.printint(x); else { Xinu.println("small"); }
    while (x != 0) x = x - 1;
  }
}
class Counter extends Base {
  int value;
  public synchronized int add(int n, boolean ok) {
    Counter other = new Counter();
    value = this.value + n;
    return other.get(value);
  }
}
class Worker extends Thread {
  int[] data;
  public void run() {
    data = new int[4][];
    Xinu.println("done");
  }
}
