package com.github.stantonk.concurrency.demos.encapsulation;

import java.util.concurrent.atomic.AtomicLong;

public class SafeAtomic implements ICounter {
    private AtomicLong val = new AtomicLong(0l);

    @Override
    public long getVal() {
        return val.get();
    }

    @Override
    public void incrVal() {
        val.incrementAndGet();
    }
}
