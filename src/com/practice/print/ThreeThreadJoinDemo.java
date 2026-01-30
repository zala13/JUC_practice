package com.practice.print;

import java.util.concurrent.CountDownLatch;

public class ThreeThreadJoinDemo {

    public static void main(String[] args) {
        CountDownLatchExample();
    }
    private final static Object lock = new Object();
    private static void CountDownLatchExample() {
        CountDownLatch t3ToT4Latch = new CountDownLatch(1);
        CountDownLatch t4ToT5Latch = new CountDownLatch(1);

        Thread t3 = new Thread(() -> {
            System.out.println("T3 start execute");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("T3 end execute");
            t3ToT4Latch.countDown();
        });
        Thread t4 = new Thread(() -> {
            System.out.println("T4 start execute");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("T4 end execute");
            t4ToT5Latch.countDown();
        });
        Thread t5 = new Thread(() -> {
            System.out.println("T5 start execute");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("T5 end execute");
        });

        try {
            t3.join();
            t4.join();
            t5.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void JoinExample() {
        Thread t1 = new Thread(new Task("T1"), "T1");
        Thread t2 = new Thread(new Task("T2"), "T2");
        Thread t3 = new Thread(new Task("T3"), "T3");

        try {
            t1.start();
            t1.join();

            t2.start();
            t2.join();

            t3.start();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("JoinExample: All tasks completed");
    }

    static class Task implements Runnable {
        private String name;

        Task(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            System.out.println(name + " start execute");
            try {
                Thread.sleep((long) (Math.random() * 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(name + " end execute");
        }
    }

}
