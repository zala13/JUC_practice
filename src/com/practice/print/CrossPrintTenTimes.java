package com.practice.print;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 交替打印abcd和1234各10次
 * 实现a1b2c3d4a1b2c3d4...的输出
 */
public class CrossPrintTenTimes {
    public static void main(String[] args) {
        LockAndNotify();
        System.out.println();
        LockAndSignal();
    }

    private static final Lock reentLock = new ReentrantLock();
    private static final Condition printNumberCondition = reentLock.newCondition();
    private static final Condition printLetterCondition = reentLock.newCondition();
    private static boolean isNumberTurn = false;
    private static void LockAndSignal() {
        Thread printNumberThread = new Thread(() -> {
            reentLock.lock();
            try {
                for (int i = 0; i < 10; i++) {
                    for (char c : "1234".toCharArray()) {
                        while (!isNumberTurn) {
                            printNumberCondition.await();
                        }
                        System.out.print(c);
                        printLetterCondition.signal();
                        isNumberTurn = false;
                    }
                }
            } catch (InterruptedException e){
                e.printStackTrace();
            } finally {
                reentLock.unlock();
            }


        });

        Thread printLetterThread = new Thread(() -> {
            reentLock.lock();
            try {
                for (int i = 0; i < 10; i++) {
                    for (char c : "abcd".toCharArray()) {
                        while (isNumberTurn) {
                            printLetterCondition.await();
                        }
                        System.out.print(c);
                        printNumberCondition.signal();
                        isNumberTurn = true;
                    }
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            } finally {
                reentLock.unlock();
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

    final static Object lock = new Object();
    static boolean isLetterTurn = true;
    private static void LockAndNotify() {
        Thread printNumberThread = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                for (char c : "1234".toCharArray()) {
                    synchronized (lock) {
                        while (isLetterTurn) {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        System.out.print(c);
                        isLetterTurn = true;
                        lock.notifyAll();
                    }
                }
            }
        });

        Thread printLetterThread = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                for (char c : "abcd".toCharArray()) {
                    synchronized (lock) {
                        while (!isLetterTurn) {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        System.out.print(c);
                        isLetterTurn = false;
                        lock.notifyAll();
                    }
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
