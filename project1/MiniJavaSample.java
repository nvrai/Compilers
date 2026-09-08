class MiniJavaSample {
    public static void main(String[] args) {
        System.out.println(new Sum().run(5));
    }
}

class Sum extends Base {
    public int run(int count) {
        int[] values;
        int i;
        int total;
        boolean active;
        values = new int[count];
        i = 0;
        total = 0;
        active = true;
        while (i < values.length && active) {
            values[i] = i + 1;
            total = total + values[i];
            i = i + 1;
        }
        if (!(total == 0)) {
            Xinu.print("sum: ");
            Xinu.printint(total);
        } else {
            Xinu.println("empty");
        }
        return total;
    }
}

class Base {
    public int read() {
        return Xinu.readint();
    }
}

