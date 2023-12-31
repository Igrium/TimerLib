package com.igrium.timerlib.impl.client.mixin;

import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.igrium.timerlib.api.TimerProvider;
import com.igrium.timerlib.api.TimerProvider.TimerUnit;
import com.igrium.timerlib.impl.SimpleTimerProvider;
import com.igrium.timerlib.impl.TimerProviderHolder;

import net.minecraft.client.world.ClientWorld;

@Mixin(ClientWorld.class)
public class ClientWorldMixin implements TimerProviderHolder {

    @Unique
    private int time;

    private SimpleTimerProvider timerProvider = new SimpleTimerProvider(TimerUnit.TICKS, () -> time);

    @Override
    public TimerProvider getTimerProvider() {
        return timerProvider;
    }
    
    @Inject(method = "tick", at = @At("RETURN"))
    private void timerlib$onTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        timerProvider.tick();
        time++;
    }
}
