package com.practice.print;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 两个线程交替打印
 * 线程A打印数字，线程B打印字母
 * 结果：12A34B56C78D...5152Z
 */
public class CrossPrint {

    public static void main(String[] args) {
        waitAndNotifyExample();
        System.out.println();
        reentrantLockAndConditionExample();
    }

    private static final Lock reentLock = new ReentrantLock();
    private static final Condition printNumberCondition = reentLock.newCondition();
    private static final Condition printLetterCondition = reentLock.newCondition();
    private static boolean isPrintNumberTurn = true;
    public static void reentrantLockAndConditionExample() {
        Thread printNumberThread1 = new Thread(() -> {
            reentLock.lock();
            try {
                for (int i = 1; i <= 52; i+= 2) {
                    while (!isPrintNumberTurn) {
                        printNumberCondition.await();
                    }
                    System.out.print(i);
                    System.out.print(i + 1);
                    isPrintNumberTurn = false;
                    printLetterCondition.signal();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                reentLock.unlock();
            }
        });

        Thread printLetterThread1 = new Thread(() -> {
            reentLock.lock();
            try {
                for (char c = 'a'; c <= 'z'; c++) {
                    while (isPrintNumberTurn) {
                        printLetterCondition.await();
                    }
                    System.out.print(c);
                    isPrintNumberTurn = true;
                    printNumberCondition.signal();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                reentLock.unlock();
            }
        });

        printNumberThread1.start();
        printLetterThread1.start();
        try {
            printNumberThread1.join();
            printLetterThread1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static final Object lock = new Object();
    private static boolean printNumber = true;
    private static void waitAndNotifyExample() {
        Thread printNumberThread = new Thread(() -> {
            for (int i = 1; i <= 52; i+= 2) {
                synchronized(lock) {
                    while (!printNumber) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.print(i);
                    System.out.print(i + 1);
                    printNumber = false;
                    lock.notifyAll();
                }
            }
        });

        Thread printLetterThread = new Thread(() -> {
            for (char c = 'a'; c <= 'z'; c++) {
                synchronized(lock) {
                    while (printNumber) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.print(c);
                    printNumber = true;
                    lock.notifyAll();
                }
            }
        });

        printNumberThread.start();
        printLetterThread.start();
        try {
            printNumberThread.join();
            printLetterThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
