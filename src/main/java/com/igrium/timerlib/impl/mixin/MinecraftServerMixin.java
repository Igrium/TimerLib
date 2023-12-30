package com.igrium.timerlib.impl.mixin;

import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.igrium.timerlib.api.TimerProvider;
import com.igrium.timerlib.api.TimerProvider.TimerUnit;
import com.igrium.timerlib.impl.SimpleTimerProvider;
import com.igrium.timerlib.impl.TimerProviderHolder;

import net.minecraft.server.MinecraftServer;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements TimerProviderHolder {

    @Shadow
    private int ticks;

    @Unique
    private SimpleTimerProvider timerProvider = new SimpleTimerProvider(TimerUnit.TICKS, () -> ticks);

    @Override
    public TimerProvider getTimerProvider() {
        return timerProvider;
    }

    @Inject(method = "tickWorlds", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/function/CommandFunctionManager;tick()V", shift = Shift.AFTER))
    void timerlib$onTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        timerProvider.tick();
    }
}
