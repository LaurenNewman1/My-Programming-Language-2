package edu.ufl.cise.plpfa22;

public class prog implements Runnable {
    int a;
    String b;
    boolean c;

    public prog() {
        super();
    }

    class p implements Runnable {

        class q implements Runnable {

            @Override
            public void run() {
                a = 42;
                b = "hello";
                c = true;
                System.out.println(a);
                System.out.println(b);
                System.out.println(c);
            }
        }

        @Override
        public void run() {
            new q().run();
        }
    }

    public static void main(String[] args) {
        new prog().run();
    }

    @Override
    public void run() {
        new p().run();
    }

}