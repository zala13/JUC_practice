package com.practice.print;

public class ThreeThreadPrintOneTwoThreeCycle {
    private int maxCount = 10;
    private int currentThread = 1;
    private final Object lock = new Object();
    public static void main(String[] args) {
        ThreeThreadPrintOneTwoThreeCycle printer = new ThreeThreadPrintOneTwoThreeCycle();
        Thread firstPrinter = new Thread(() -> printer.printNumber(1, 0));
        firstPrinter.start();
        Thread secondPrinter = new Thread(() -> printer.printNumber(2, 0));
        secondPrinter.start();
        Thread thirdPrinter = new Thread(() -> printer.printNumber(3, 0));
        thirdPrinter.start();
    }

    private void printNumber(int numberNeedToPrint, int count) {
        while (count < maxCount) {
            synchronized (lock) {
                while (currentThread != numberNeedToPrint) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Thread " + currentThread + " print: " + numberNeedToPrint);
                count++;
                currentThread++;
                if (currentThread == 4) {
                    currentThread = 1;
                }
                lock.notifyAll();
            }
        }
    }
}
