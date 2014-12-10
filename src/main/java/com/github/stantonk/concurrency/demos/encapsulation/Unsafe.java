package com.github.stantonk.concurrency.demos.encapsulation;

public class Unsafe implements ICounter {
    private long val = 0l;

    @Override
    public long getVal() {
        return val;
    }

    @Override
    public void incrVal() {
        val++;
    }
}
