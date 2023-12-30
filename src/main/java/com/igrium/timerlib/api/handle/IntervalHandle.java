package com.igrium.timerlib.api.handle;

/**
 * A thread-safe handle on a specific interval.
 */
public interface IntervalHandle extends TimerHandle {

    /**
     * The interval at which the function fires, in the units defined by the timer provider.
     */
    public int getInterval();
}
