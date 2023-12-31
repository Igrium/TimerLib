package com.igrium.timerlib.impl.client;

import com.igrium.timerlib.api.TimerProvider;

public interface ClientTimerProviderHolder {
    public TimerProvider getTickTimer();
    public TimerProvider getFrameTimer();
}
