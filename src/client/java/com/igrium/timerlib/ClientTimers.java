package com.igrium.timerlib;

import com.igrium.timerlib.api.TimerProvider;
import com.igrium.timerlib.impl.TimerProviderHolder;

import net.minecraft.client.world.ClientWorld;

/**
 * A universal class for obtaining default timer providers specific to the client.
 */
public class ClientTimers {

    /**
     * Get the timer provider belonging to a client world.
     * @param world Client world to use.
     * @return The timer provider.
     */
    public static TimerProvider getWorld(ClientWorld world) {
        return ((TimerProviderHolder) world).getTimerProvider();
    }
}
