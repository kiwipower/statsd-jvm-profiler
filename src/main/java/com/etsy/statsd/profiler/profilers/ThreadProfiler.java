package com.etsy.statsd.profiler.profilers;

import com.etsy.statsd.profiler.Arguments;
import com.etsy.statsd.profiler.Profiler;
import com.etsy.statsd.profiler.reporter.Reporter;

import java.util.concurrent.TimeUnit;

import static java.lang.Thread.State.*;


public class ThreadProfiler extends Profiler {

    public static final long PERIOD = 10;

    @Override
    public void profile() {
        recordStats();
    }

    @Override
    public void flushData() {
        recordStats();
    }

    @Override
    public long getPeriod() {
        return PERIOD;
    }

    @Override
    public TimeUnit getTimeUnit() {
        return TimeUnit.SECONDS;
    }

    @Override
    protected void handleArguments(Arguments arguments) { /* No arguments needed */ }

    public ThreadProfiler(Reporter reporter, Arguments arguments) {
        super(reporter, arguments);
    }

    private void recordStats() {
        recordGaugeValue("thread.count", Thread.activeCount());
        Thread[] threads = new Thread[Thread.activeCount()];
        Thread.enumerate(threads);
        int runnable = 0;
        int blocked = 0;
        int waiting = 0;

        for (Thread t: threads) {
            if (t.getState() == RUNNABLE) { runnable++; }
            if (t.getState() == BLOCKED) { blocked++; }
            if (t.getState() == WAITING || t.getState() == TIMED_WAITING) { waiting++; }
        }

        recordGaugeValue("thread.runnable", runnable);
        recordGaugeValue("thread.blocked", blocked);
        recordGaugeValue("thread.waiting", waiting);
    }
}
