package com.github.barteks2x.openmine;

import org.lwjgl.Sys;

public class Timer {

    private int delta = 0;
    private long lastFrame = getTime();
    private int frames = 0;
    private int FPSUpdateTime = 0;
    private int fps = 0;

    public long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    public int nextDelta() {
        long time = getTime();
        int dt = (int)(time - lastFrame);
        lastFrame = time;
        this.delta = dt;
        return dt;
    }

    public int getDelta() {
        return delta;
    }

    public int nextFrame() {
        FPSUpdateTime += nextDelta();
        frames++;
        if(FPSUpdateTime >= 1000) {
            fps = frames;
            frames = FPSUpdateTime = 0;
        }
        return getDelta();
    }

    public int getFPS() {
        return fps;
    }
}
