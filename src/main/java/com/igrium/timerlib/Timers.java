package com.igrium.timerlib;

import com.igrium.timerlib.api.TimerProvider;
import com.igrium.timerlib.impl.TimerProviderHolder;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;

/**
 * A universal class for obtaining default timer providers.
 */
public final class Timers {
    private Timers() {};

    /**
     * Get the timer provider belonging to a server instance.
     * @param server Server instance to use.
     * @return The timer provider.
     */
    public static TimerProvider getServer(MinecraftServer server) {
        return ((TimerProviderHolder) server).getTimerProvider();
    }

    /**
     * Get the timer provider belonging to an entity.
     * @param entity Entity to use.
     * @return The timer provider.
     */
    public static TimerProvider getEntity(Entity entity) {
        return ((TimerProviderHolder) entity).getTimerProvider();
    }
}
