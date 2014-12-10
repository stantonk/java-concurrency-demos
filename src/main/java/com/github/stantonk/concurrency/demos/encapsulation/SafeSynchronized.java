package com.github.stantonk.concurrency.demos.encapsulation;

public class SafeSynchronized implements ICounter {
    private long val = 0l;

    @Override
    public synchronized long getVal() {
        return val;
    }

    @Override
    public synchronized void incrVal() {
        val++;
    }
}
