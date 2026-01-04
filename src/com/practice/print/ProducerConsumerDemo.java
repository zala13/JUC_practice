package com.practice.print;

public class ProducerConsumerDemo {
    static class Product {
        private int productCount = 0;
        private static final int MAX_PRODUCT = 100;
        public synchronized void produce() throws InterruptedException {
            while (productCount >= MAX_PRODUCT) {
                System.out.println("产品数量达到上限（" + MAX_PRODUCT + "),生产者停止生产");
                wait();
            }
            productCount++;
            System.out.println(Thread.currentThread().getName() + "生产1个产品，库存为：" + productCount);
            notify();
            Thread.sleep(10);
        }

        public synchronized void consume() throws InterruptedException {
            while (productCount <= 0) {
                System.out.println("产品库存为空，消费者暂停消费");
                wait();
            }
            productCount--;
            System.out.println(Thread.currentThread().getName() + "消费1个产品库存为：" + productCount);
            notify();
            Thread.sleep(20);
        }
    }
    static class Producer extends Thread {
        private Product product;
        public Producer(Product product, String name) {
            super(name);
            this.product = product;
        }
        @Override
        public void run() {
            try {
                while (true) {
                    product.produce();
                }
            } catch (InterruptedException e) {
                System.out.println(getName() + "被中断，停止生产");
                Thread.currentThread().interrupt();
            }
        }
    }
    static class Consumer extends Thread {
        private Product product;
        public Consumer(Product product, String name) {
            super(name);
            this.product = product;
        }
        @Override
        public void run() {
            try {
                while (true) {
                    product.consume();
                }
            } catch (InterruptedException e) {
                System.out.println(getName() + "被中断，停止消费");
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        Product product = new Product();
        Producer producer = new Producer(product, "Producer-1");
        Consumer consumer = new Consumer(product, "Consumer-1");
        producer.start();
        consumer.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        producer.interrupt();
        consumer.interrupt();
        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("程序结束，最终库存：" + product.productCount);
    }

}
