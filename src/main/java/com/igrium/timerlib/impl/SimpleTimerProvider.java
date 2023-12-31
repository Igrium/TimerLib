package com.igrium.timerlib.impl;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.LongSupplier;

import com.igrium.timerlib.api.TimerProvider;
import com.igrium.timerlib.api.handle.IntervalHandle;
import com.igrium.timerlib.api.handle.TimeoutHandle;

public final class SimpleTimerProvider implements TimerProvider {
    private final TimerUnit unit;
    private final LongSupplier timeSupplier;

    private final Deque<SimpleTimeoutHandle> timeouts = new ConcurrentLinkedDeque<>();
    private final Deque<SimpleIntervalHandle> intervals = new ConcurrentLinkedDeque<>();

    public SimpleTimerProvider(TimerUnit unit, LongSupplier timeSupplier) {
        this.unit = unit;
        this.timeSupplier = timeSupplier;
    }

    @Override
    public TimerUnit getTimerUnit() {
        return unit;
    }

    public void tick() {
        long time = timeSupplier.getAsLong();
        tickTimeouts(time);
        tickIntervals(time);
    }

    private void tickTimeouts(long time) {
        var iter = timeouts.iterator();
        SimpleTimeoutHandle handle;
        while (iter.hasNext()) {
            handle = iter.next();
            if (handle.tick(time)) iter.remove();
        }
    }

    private void tickIntervals(long time) {
        for (SimpleIntervalHandle handle : intervals) {
            handle.tick(time);
        }
    }

    @Override
    public synchronized TimeoutHandle setTimeout(int delay, Runnable callback) throws IllegalArgumentException {
        if (delay < 0) throw new IllegalArgumentException("Delay cannot be negative!");
        long time = timeSupplier.getAsLong();

        SimpleTimeoutHandle handle = new SimpleTimeoutHandle(time + delay, callback);
        timeouts.add(handle);

        return handle;
    }

    @Override
    public synchronized IntervalHandle setInterval(int delay, Runnable callback) throws IllegalArgumentException {
        if (delay <= 0) throw new IllegalArgumentException("Delay must be at least 1.");
        long firstExecution = timeSupplier.getAsLong() + delay;

        SimpleIntervalHandle handle = new SimpleIntervalHandle(delay, firstExecution, callback);
        intervals.add(handle);
        return handle;
    }

    public synchronized void clear() {
        for (SimpleTimeoutHandle handle : timeouts) {
            handle.isActive = false;
        }
        timeouts.clear();

        for (SimpleIntervalHandle handle : intervals) {
            handle.isActive = false;
        }
        intervals.clear();
    }

    private class SimpleTimeoutHandle implements TimeoutHandle {

        private final long executionTime;
        private final Runnable callback;

        private boolean isActive = true;
        private boolean isExpired = false;

        public SimpleTimeoutHandle(long executionTime, Runnable callback) {
            this.executionTime = executionTime;
            this.callback = callback;
        }

        @Override
        public boolean isActive() {
            return isActive;
        }

        @Override
        public void cancel() throws IllegalStateException {
            if (!isActive) throw new IllegalStateException("Timeout is not active!");
            isActive = false;
            timeouts.remove(this);
        }

        @Override
        public boolean isExpired() {
            return isExpired;
        }
        
        boolean tick(long time) {
            if (time >= executionTime) {
                callback.run();
                isExpired = true;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public TimerProvider getProvider() {
            return SimpleTimerProvider.this;
        }
    }

    private class SimpleIntervalHandle implements IntervalHandle {

        private final int interval;
        private final Runnable callback;

        private boolean isActive = true;
        private long nextExecution;

        public SimpleIntervalHandle(int interval, long firstExecution, Runnable callback) {
            this.interval = interval;
            this.nextExecution = firstExecution;
            this.callback = callback;
        }

        @Override
        public boolean isActive() {
            return isActive;
        }

        @Override
        public void cancel() throws IllegalStateException {
            if (!isActive) throw new IllegalStateException("Interval is not active!");
            isActive = false;
            intervals.remove(this);
        }

        @Override
        public int getInterval() {
            return interval;
        }
        
        void tick(long time) {
            if (time >= nextExecution) {
                callback.run();
                nextExecution = time + interval;
            }
        }

        @Override
        public TimerProvider getProvider() {
            return SimpleTimerProvider.this;
        }
    }
}
