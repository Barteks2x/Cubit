/* 
 * The MIT License
 *
 * Copyright (C) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.barteks2x.cubit.profiling;

import java.util.ArrayList;
import java.util.List;
import static org.lwjgl.opengl.GL15.glGenQueries;
import static org.lwjgl.opengl.GL33.GL_TIMESTAMP;
import static org.lwjgl.opengl.GL33.glQueryCounter;

public class GPUProfiler {

    private static final boolean PROFILING_ENABLED = false;

    private static final ArrayList<Integer> queryObjects;

    private static int frameCounter;

    private static GPUTaskProfile currentTask;

    private static final ArrayList<GPUTaskProfile> completedFrames;

    static {
        queryObjects = new ArrayList<Integer>(100);

        frameCounter = 0;

        completedFrames = new ArrayList<GPUTaskProfile>(100);
    }

    public static void startFrame() {

        if(currentTask != null) {
            throw new IllegalStateException("Previous frame not ended properly!");
        }
        if(PROFILING_ENABLED) {
            currentTask = new GPUTaskProfile().init(null, "Frame " + (++frameCounter), getQuery());
        }
    }

    public static void start(String name) {
        if(PROFILING_ENABLED && currentTask != null) {
            currentTask = new GPUTaskProfile().init(currentTask, name, getQuery());
        }
    }

    public static void end() {
        if(PROFILING_ENABLED && currentTask != null) {
            currentTask = currentTask.end(getQuery());
        }

    }

    public static void endStart(String name) {
        end();
        start(name);
    }

    public static void endFrame() {

        if(PROFILING_ENABLED) {
            if(currentTask.getParent() != null) {
                throw new IllegalStateException("Error ending frame. Not all tasks finished.");
            }
            currentTask.end(getQuery());

            if(completedFrames.size() < 5) {
                completedFrames.add(currentTask);
            } else {
                recycle(currentTask);
            }

            currentTask = null;
        }
    }

    public static GPUTaskProfile getFrameResults() {
        if(completedFrames.isEmpty()) {
            return null;
        }

        GPUTaskProfile frame = completedFrames.get(0);
        if(frame.resultsAvailable()) {
            return completedFrames.remove(0);
        } else {
            return null;
        }
    }

    public static void recycle(GPUTaskProfile task) {
        queryObjects.add(task.getStartQuery());
        queryObjects.add(task.getEndQuery());

        List<GPUTaskProfile> children = task.getChildren();
        for(GPUTaskProfile children1 : children) {
            recycle(children1);
        }
    }

    private static int getQuery() {
        int query;
        if(!queryObjects.isEmpty()) {
            query = queryObjects.remove(queryObjects.size() - 1);
        } else {
            query = glGenQueries();
        }

        glQueryCounter(query, GL_TIMESTAMP);

        return query;
    }
}
