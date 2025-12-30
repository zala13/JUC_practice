package com.practice.print;

public class TwoThreadPrintOneToHundredCycle {
    private int currentNumber = 1;
    private final Object lock = new Object();

    public static void main(String[] args) {
        TwoThreadPrintOneToHundredCycle printer = new TwoThreadPrintOneToHundredCycle();
        Thread oddPrinter = new Thread(() -> printer.printNumbers(true));
        oddPrinter.start();
        Thread evenPrinter = new Thread(() -> printer.printNumbers(false));
        evenPrinter.start();
    }

    private void printNumbers(boolean isOdd) {
        while (currentNumber <= 100) {
            synchronized (lock) {
                while ((isOdd && currentNumber % 2 == 0) || !isOdd && currentNumber % 2 != 0) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (currentNumber <= 100) {
                    System.out.println("Thread " + (isOdd ? "Odd " : "Even") + " print: " + currentNumber);
                    currentNumber++;
                    lock.notifyAll();
                }
            }
        }
    }
}
