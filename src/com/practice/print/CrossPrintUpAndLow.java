package com.practice.print;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 两个线程交替打印出a1b2c3...z26
 */
public class CrossPrintUpAndLow {

    public static void main(String[] args) {
        LockAndConditionExample();
        System.out.println();
        LockAndNotifyExample();
    }

    private static final Lock reentLock = new ReentrantLock();
    private static final Condition printUpperCondition = reentLock.newCondition();
    private static final Condition printLowerCondition = reentLock.newCondition();
    private static boolean isPrintUpperTurn = true;
    private static void LockAndConditionExample() {
        Thread printLowerThread = new Thread(() -> {
            reentLock.lock();
            try {
                for (char c = 'a'; c <= 'z'; c++) {
                    while (isPrintUpperTurn) {
                        printLowerCondition.await();
                    }
                    System.out.print(c);
                    printUpperCondition.signal();
                    isPrintUpperTurn = true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                reentLock.unlock();
            }
        });

        Thread printUpperThread = new Thread(() -> {
            reentLock.lock();
            try {
                for (char c = 'A'; c <= 'Z'; c++) {
                    while (!isPrintUpperTurn) {
                        printUpperCondition.await();
                    }
                    System.out.print(c);
                    printLowerCondition.signal();
                    isPrintUpperTurn = false;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                reentLock.unlock();
            }
        });

        printLowerThread.start();
        printUpperThread.start();
        try {
            printLowerThread.join();
            printUpperThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static final Object lock = new Object();
    private static void LockAndNotifyExample() {
        Thread printLowerThread = new Thread(() -> {
            for (char c = 'a'; c <= 'z'; c++) {
                synchronized (lock) {
                    try {
                        while (isPrintUpperTurn) {
                            lock.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.print(c);
                    isPrintUpperTurn = true;
                    lock.notifyAll();
                }
            }
        });

        Thread printUpperThread = new Thread(() -> {
            for (char c = 'A'; c <= 'Z'; c++) {
                synchronized (lock) {
                    try {
                        while (!isPrintUpperTurn) {
                            lock.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.print(c);
                    isPrintUpperTurn = false;
                    lock.notifyAll();
                }
            }
        });

        printLowerThread.start();
        printUpperThread.start();
        try {
            printLowerThread.join();
            printUpperThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
