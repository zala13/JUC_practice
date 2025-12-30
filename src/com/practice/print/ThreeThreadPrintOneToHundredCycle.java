package com.practice.print;

public class ThreeThreadPrintOneToHundredCycle {
    private static int currentNumber = 1;
    private static int currentThread = 1;
    private Object lock = new Object();
    public static void main(String[] args) {
        ThreeThreadPrintOneToHundredCycle printer = new ThreeThreadPrintOneToHundredCycle();
        Thread firstPrinter = new Thread(() -> printer.printNumber(1), "1");
        firstPrinter.start();
        Thread secondPrinter = new Thread(() -> printer.printNumber(2), "2");
        secondPrinter.start();
        Thread thridPrinter = new Thread(() -> printer.printNumber(3), "3");
        thridPrinter.start();
    }

    private void printNumber(int number) {
        while (currentNumber <= 100) {
            synchronized (lock) {
                while (number != currentThread) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (currentNumber <= 100) {
                    System.out.println("Thread " + number + " print: " + currentNumber);
                    currentNumber++;
                    currentThread++;
                    if (currentThread == 4) {
                        currentThread = 1;
                    }
                    lock.notifyAll();
                }
            }
        }
    }
}
