package com.igrium.timerlib;

import com.igrium.timerlib.api.TimerProvider;
import com.igrium.timerlib.impl.TimerProviderHolder;
import com.igrium.timerlib.impl.client.ClientTimerProviderHolder;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

/**
 * A universal class for obtaining default timer providers specific to the client.
 */
public class ClientTimers {

    /**
     * A timer provider belonging to the client. This timer updates every client
     * tick; for more precise measurement, use
     * {@link #getClientFrame(MinecraftClient)}
     * 
     * @param client Minecraft client instance.
     * @return The timer provider.
     * @see #getClientFrame(MinecraftClient)
     */
    public static TimerProvider getClient(MinecraftClient client) {
        return ((ClientTimerProviderHolder) client).getTickTimer();
    }

    /**
     * A timer provider belonging to the client. Unlike
     * <code>getClient</code>, this provider updates every frame and uses real-world
     * milliseconds instead of ticks. The callback will <em>not</em> be called
     * during a tick; use with caution.
     * 
     * @param client Minecraft client instance.
     * @return The timer provider.
     * @see #getClient(MinecraftClient)
     */
    public static TimerProvider getClientFrame(MinecraftClient client) {
        return ((ClientTimerProviderHolder) client).getFrameTimer();
    }

    /**
     * Get the timer provider belonging to a client world.
     * @param world Client world to use.
     * @return The timer provider.
     */
    public static TimerProvider getWorld(ClientWorld world) {
        return ((TimerProviderHolder) world).getTimerProvider();
    }
}
