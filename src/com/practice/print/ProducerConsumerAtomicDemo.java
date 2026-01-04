import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumerAtomicDemo {
    // 共享的产品仓库
    static class Warehouse {
        // 用AtomicInteger保证产品数量操作的原子性
        private final AtomicInteger productCount = new AtomicInteger(0);
        // 最大产品数阈值
        private static final int MAX_PRODUCT = 100;

        // 可重入锁，替代synchronized
        private final ReentrantLock lock = new ReentrantLock();
        // 生产者条件：库存满时等待
        private final Condition producerCondition = lock.newCondition();
        // 消费者条件：库存空时等待
        private final Condition consumerCondition = lock.newCondition();

        // 生产产品方法
        public void produce() throws InterruptedException {
            lock.lock(); // 获取锁
            try {
                // 产品数≥100时，生产者等待
                while (productCount.get() >= MAX_PRODUCT) {
                    System.out.println("产品数量已达上限(" + MAX_PRODUCT + ")，生产者暂停生产");
                    producerCondition.await(); // 生产者等待，释放锁
                }

                // 原子性增加产品数量（AtomicInteger保证线程安全）
                int newCount = productCount.incrementAndGet();
                System.out.println(Thread.currentThread().getName() +
                        " 生产了1个产品，当前库存：" + newCount);

                // 生产后通知消费者（库存有货了）
                consumerCondition.signalAll();

                // 生产者每10ms生产一次
                Thread.sleep(10);
            } finally {
                lock.unlock(); // 确保锁释放
            }
        }

        // 消费产品方法
        public void consume() throws InterruptedException {
            lock.lock(); // 获取锁
            try {
                // 产品数为0时，消费者等待
                while (productCount.get() <= 0) {
                    System.out.println("产品库存为空，消费者暂停消费");
                    consumerCondition.await(); // 消费者等待，释放锁
                }

                // 原子性减少产品数量（AtomicInteger保证线程安全）
                int newCount = productCount.decrementAndGet();
                System.out.println(Thread.currentThread().getName() +
                        " 消费了1个产品，当前库存：" + newCount);

                // 消费后通知生产者（库存有空位了）
                producerCondition.signalAll();

                // 消费者每20ms消费一次
                Thread.sleep(20);
            } finally {
                lock.unlock(); // 确保锁释放
            }
        }

        // 获取最终库存（供主线程输出）
        public int getProductCount() {
            return productCount.get();
        }
    }

    // 生产者线程（逻辑与之前一致）
    static class Producer extends Thread {
        private Warehouse warehouse;

        public Producer(Warehouse warehouse, String name) {
            super(name);
            this.warehouse = warehouse;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    warehouse.produce();
                }
            } catch (InterruptedException e) {
                System.out.println(getName() + "被中断，停止生产");
                Thread.currentThread().interrupt();
            }
        }
    }

    // 消费者线程（逻辑与之前一致）
    static class Consumer extends Thread {
        private Warehouse warehouse;

        public Consumer(Warehouse warehouse, String name) {
            super(name);
            this.warehouse = warehouse;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    warehouse.consume();
                }
            } catch (InterruptedException e) {
                System.out.println(getName() + "被中断，停止消费");
                Thread.currentThread().interrupt();
            }
        }
    }

    // 主方法测试
    public static void main(String[] args) {
        Warehouse warehouse = new Warehouse();

        Producer producer = new Producer(warehouse, "生产者-1");
        Consumer consumer = new Consumer(warehouse, "消费者-1");

        producer.start();
        consumer.start();

        // 运行10秒后停止
        try {
            Thread.sleep(10000);
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

        System.out.println("程序结束，最终库存：" + warehouse.getProductCount());
    }
}