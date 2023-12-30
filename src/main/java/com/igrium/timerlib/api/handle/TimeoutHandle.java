package com.igrium.timerlib.api.handle;

/**
 * A thread-safe handle on a specific timeout.
 */
public interface TimeoutHandle extends TimerHandle {

    /**
     * Get whether this timeout has executed its function.
     * 
     * @return <code>true</code> if the timer expired and executed its function.
     *         <code>false</code> if it was canceled or is still active.
     */
    boolean isExpired();
}
