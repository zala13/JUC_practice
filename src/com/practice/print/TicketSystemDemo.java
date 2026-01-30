package com.practice.print;

public class TicketSystemDemo {
    private static final int TOTAL_TICKETS = 100;
    private static int remainingTickets = TOTAL_TICKETS;
    private static final Object lock = new Object();

    public static void main(String[] args) {
        for (int i = 1; i <= 4; i++) {
            new Thread(new TicketWindow(i)).start();
        }
    }

    static class TicketWindow implements Runnable {
        private int windowNumber;

        public TicketWindow(int windowNumber) {
            this.windowNumber = windowNumber;
        }

        @Override
        public void run() {
            while (true) {
                synchronized (lock) {
                    if (remainingTickets > 0) {
                        buyTicket();
                    } else {
                        System.out.println("ticket sole out " + windowNumber + "window closed");
                        break;
                    }
                }
                try {
                    Thread.sleep((long) (Math.random() * 1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void buyTicket() {
            remainingTickets--;
            System.out.println("Window " + windowNumber + " sold 1 ticket. Remaining tickets: " + remainingTickets);
        }
    }
}
