package com.igrium.timerlib.api.handle;

import com.igrium.timerlib.api.TimerProvider;

public interface TimerHandle {
    
    /**
     * Determine whether this timer is currently active. The timer function may only
     * be called if it's active.
     * 
     * @return If the timer is active.
     */
    public boolean isActive();

    /**
     * Cancel the timer without calling its function.
     * @throws IllegalStateException If the timer is not active.
     */
    public void cancel() throws IllegalStateException;

    /**
     * Get the timer provider that this handle belongs to.
     */
    public TimerProvider getProvider();
}
