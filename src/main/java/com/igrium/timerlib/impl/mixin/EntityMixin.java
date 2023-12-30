package com.igrium.timerlib.impl.mixin;

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

import net.minecraft.entity.Entity;

@Mixin(Entity.class)
public class EntityMixin implements TimerProviderHolder {

    @Unique
    private SimpleTimerProvider timerProvider;

    @Shadow
    public int age;

    @Override
    public TimerProvider getTimerProvider() {
        if (timerProvider == null) {
            timerProvider = new SimpleTimerProvider(TimerUnit.TICKS, () -> age);
        }
        return timerProvider;
    
    
    }
    
    @Inject(method = "baseTick", at = @At(value = "INVOKE_STRING",
            target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
            args = "ldc=entityBaseTick",
            shift = Shift.AFTER))
    void timerlib$onTick(CallbackInfo ci) {
        if (timerProvider != null) timerProvider.tick();
    }
}
