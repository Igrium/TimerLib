package com.igrium.timerlib.impl;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.IntSupplier;

import com.igrium.timerlib.api.TimerProvider;
import com.igrium.timerlib.api.handle.IntervalHandle;
import com.igrium.timerlib.api.handle.TimeoutHandle;

public final class SimpleTimerProvider implements TimerProvider {
    private final TimerUnit unit;
    private final IntSupplier timeSupplier;

    private final Deque<SimpleTimeoutHandle> timeouts = new ConcurrentLinkedDeque<>();
    private final Deque<SimpleIntervalHandle> intervals = new ConcurrentLinkedDeque<>();

    public SimpleTimerProvider(TimerUnit unit, IntSupplier timeSupplier) {
        this.unit = unit;
        this.timeSupplier = timeSupplier;
    }

    @Override
    public TimerUnit getTimerUnit() {
        return unit;
    }

    public void tick() {
        int time = timeSupplier.getAsInt();
        tickTimeouts(time);
        tickIntervals(time);
    }

    private void tickTimeouts(int time) {
        var iter = timeouts.iterator();
        SimpleTimeoutHandle handle;
        while (iter.hasNext()) {
            handle = iter.next();
            if (handle.tick(time)) iter.remove();
        }
    }

    private void tickIntervals(int time) {
        for (SimpleIntervalHandle handle : intervals) {
            handle.tick(time);
        }
    }

    @Override
    public TimeoutHandle setTimeout(int delay, Runnable callback) throws IllegalArgumentException {
        if (delay < 0) throw new IllegalArgumentException("Delay cannot be negative!");
        int time = timeSupplier.getAsInt();

        SimpleTimeoutHandle handle = new SimpleTimeoutHandle(time + delay, callback);
        timeouts.add(handle);

        return handle;
    }

    @Override
    public IntervalHandle setInterval(int delay, Runnable callback) throws IllegalArgumentException {
        if (delay <= 0) throw new IllegalArgumentException("Delay must be at least 1.");
        int firstExecution = timeSupplier.getAsInt() + delay;

        SimpleIntervalHandle handle = new SimpleIntervalHandle(delay, firstExecution, callback);
        intervals.add(handle);
        return handle;
    }

    private class SimpleTimeoutHandle implements TimeoutHandle {

        private final int executionTime;
        private final Runnable callback;

        private boolean isActive = true;
        private boolean isExpired = false;

        public SimpleTimeoutHandle(int executionTime, Runnable callback) {
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
        
        boolean tick(int time) {
            if (time >= executionTime) {
                callback.run();
                isExpired = true;
                return true;
            } else {
                return false;
            }
        }
    }

    private class SimpleIntervalHandle implements IntervalHandle {

        private final int interval;
        private final Runnable callback;

        private boolean isActive;
        private int nextExecution;

        public SimpleIntervalHandle(int interval, int firstExecution, Runnable callback) {
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
        
        void tick(int time) {
            if (nextExecution >= time) {
                callback.run();
                nextExecution = time + interval;
            }
        }
    }
}
