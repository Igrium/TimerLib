package com.igrium.timerlib.api;

import com.igrium.timerlib.api.handle.IntervalHandle;
import com.igrium.timerlib.api.handle.TimeoutHandle;

/**
 * A "tickable" object (server, entity, etc) that allows you to set timeouts on
 * it. All methods in this class must be thread-safe.
 */
public interface TimerProvider {
    /**
     * A measuring unit that a timer may use
     */
    public static enum TimerUnit { TICKS, MILLISECONDS }

    /**
     * Get the timer unit that this provider uses.
     */
    public TimerUnit getTimerUnit();

    /**
     * Run a function after a set delay.
     * 
     * @param delay    Amount of time to wait, in units defined by
     *                 {@link #getTimerUnit()}.
     * @param callback Function to run.
     * @return A handle controlling the timeout before it's expired.
     * @throws IllegalArgumentException If <code>delay < 1</code>.
     */
    public TimeoutHandle setTimeout(int delay, Runnable callback) throws IllegalArgumentException;
    
    /**
     * Repeatedly call a function with a fixed delay between each call.
     * 
     * @param delay    Amount of time to wait between calls, in units defined by
     *                 {@link #getTimerUnit()}
     * @param callback Functino to run.
     * @return A handle controlling the interval.
     * @throws IllegalArgumentException If <code>delay < 1</code>
     */
    public IntervalHandle setInterval(int delay, Runnable callback) throws IllegalArgumentException;
}
