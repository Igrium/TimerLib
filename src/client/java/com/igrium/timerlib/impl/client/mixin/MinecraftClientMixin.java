package com.igrium.timerlib.impl.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.igrium.timerlib.api.TimerProvider;
import com.igrium.timerlib.api.TimerProvider.TimerUnit;
import com.igrium.timerlib.impl.SimpleTimerProvider;
import com.igrium.timerlib.impl.client.ClientTimerProviderHolder;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements ClientTimerProviderHolder {
    
    @Unique
    private int tickTime;
    private SimpleTimerProvider tickProvider = new SimpleTimerProvider(TimerUnit.TICKS, () -> tickTime);
    
    @Inject(method = "tick", at = @At("HEAD"))
    void timerlib$onTick(CallbackInfo ci) {
        tickProvider.tick();
        tickTime++;
    }

    @Override
    public TimerProvider getTickTimer() {
        return tickProvider;
    }

    private SimpleTimerProvider frameProvider = new SimpleTimerProvider(TimerUnit.MILLISECONDS, () -> Util.getMeasuringTimeMs());

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Mouse;updateMouse()V"))
    void timerlib$onFrame(boolean tick, CallbackInfo ci) {
        frameProvider.tick();
    }

    @Override
    public TimerProvider getFrameTimer() {
        return frameProvider;
    }
}
