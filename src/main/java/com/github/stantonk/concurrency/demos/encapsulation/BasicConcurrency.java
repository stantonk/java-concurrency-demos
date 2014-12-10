package com.github.stantonk.concurrency.demos.encapsulation;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class BasicConcurrency {

    public static class Incrementer implements Runnable {
        private final ICounter counter;
        private final CountDownLatch startSignal;
        private final CountDownLatch endSignal;
        private final long iterations;

        public Incrementer(ICounter counter, CountDownLatch startSignal,
                           CountDownLatch endSignal, long iterations) {

            this.counter = counter;
            this.startSignal = startSignal;
            this.endSignal = endSignal;
            this.iterations = iterations;
        }

        @Override
        public void run() {
            try {
                startSignal.await();
            } catch (InterruptedException e) { e.printStackTrace(); return; }

            for (long i=0; i < iterations; ++i) {
                counter.incrVal();
            }

            endSignal.countDown();
        }
    }

    /**
     *   java -cp concurrency-demos-0.1.0-SNAPSHOT.jar -DcounterClass="SafeSynchronized" -DloopIterations=100000 com.github.stantonk.concurrency.demos.encapsulation.BasicConcurrency
     java -cp concurrency-demos-0.1.0-SNAPSHOT.jar -DcounterClass="SafeSynchronized" -DloopIterations=100000 com.github.stantonk.concurrency.demos.encapsulation.BasicConcurrency
     java -cp concurrency-demos-0.1.0-SNAPSHOT.jar -DcounterClass="SafeAtomic" -DloopIterations=100000 com.github.stantonk.concurrency.demos.encapsulation.BasicConcurrency
     java -cp concurrency-demos-0.1.0-SNAPSHOT.jar -DcounterClass="SafeAtomic" -DloopIterations=100000 com.github.stantonk.concurrency.demos.encapsulation.BasicConcurrency
     java -cp concurrency-demos-0.1.0-SNAPSHOT.jar -DcounterClass="SafeSynchronized" -DloopIterations=100000 com.github.stantonk.concurrency.demos.encapsulation.BasicConcurrency
     java -cp concurrency-demos-0.1.0-SNAPSHOT.jar -DcounterClass="SafeSynchronized" -DthreadCount=1000 -DloopIterations=100000 com.github.stantonk.concurrency.demos.encapsulation.BasicConcurrency
     java -cp concurrency-demos-0.1.0-SNAPSHOT.jar -DcounterClass="SafeAtomic" -DthreadCount=1000 -DloopIterations=100000 com.github.stantonk.concurrency.demos.encapsulation.BasicConcurrency
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        final int threadCount = Integer.parseInt(System.getProperty("threadCount", "100"));
        final int loopIterations = Integer.parseInt(System.getProperty("loopIterations", "1000"));
        final String counterClass = System.getProperty("counterClass", "Unsafe");

        final long expectedFinalCount = threadCount * loopIterations;
        final CountDownLatch startSignal = new CountDownLatch(1);
        final CountDownLatch endSignal = new CountDownLatch(threadCount);

        ICounter counter = null;
        if ("Unsafe".equals(counterClass)) counter = new Unsafe();
        else if ("SafeAtomic".equals(counterClass)) counter = new SafeAtomic();
        else if ("SafeSynchronized".equals(counterClass)) counter = new SafeSynchronized();
        else System.exit(1);

        ArrayList<Thread> threads = new ArrayList<Thread>();
        for (int i=0; i< threadCount; ++i) {
            threads.add(new Thread(new Incrementer(counter, startSignal, endSignal, loopIterations)));
        }

        for (Thread t : threads) t.start();

        final long s = System.currentTimeMillis();
        startSignal.countDown();

        endSignal.await();
        final long e = System.currentTimeMillis();
        final long duration = e - s;
        System.out.println(counterClass + ',' + threadCount + ',' + duration + ',' + (expectedFinalCount==counter.getVal()));
    }
}
