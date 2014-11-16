/*
 * The MIT License
 *
 * Copyright 2014 Bartosz Skrzypczak.
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
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL15.GL_QUERY_RESULT;
import static org.lwjgl.opengl.GL15.GL_QUERY_RESULT_AVAILABLE;
import static org.lwjgl.opengl.GL15.glGetQueryObjectui;
import static org.lwjgl.opengl.GL33.glGetQueryObjectui64;

public class GPUTaskProfile {

    private GPUTaskProfile parent;

    private String name;

    private int startQuery, endQuery;

    private final List<GPUTaskProfile> children;

    public GPUTaskProfile() {

        children = new ArrayList<GPUTaskProfile>(10);

    }

    public GPUTaskProfile init(GPUTaskProfile parent, String name, int startQuery) {

        this.parent = parent;
        this.name = name;
        this.startQuery = startQuery;

        if(parent != null) {
            parent.addChild(this);
        }

        return this;
    }

    private void addChild(GPUTaskProfile profilerTask) {
        children.add(profilerTask);
    }

    public GPUTaskProfile end(int endQuery) {
        this.endQuery = endQuery;
        return parent;
    }

    public GPUTaskProfile getParent() {
        return parent;
    }

    public boolean resultsAvailable() {
        return glGetQueryObjectui(endQuery, GL_QUERY_RESULT_AVAILABLE) == GL_TRUE;
    }

    public String getName() {
        return name;
    }

    public int getStartQuery() {
        return startQuery;
    }

    public int getEndQuery() {
        return endQuery;
    }

    public long getStartTime() {
        return glGetQueryObjectui64(startQuery, GL_QUERY_RESULT);
    }

    public long getEndTime() {
        return glGetQueryObjectui64(endQuery, GL_QUERY_RESULT);
    }

    public long getTimeTaken() {
        return getEndTime() - getStartTime();
    }

    public List<GPUTaskProfile> getChildren() {
        return children;
    }

    public void reset() {
        startQuery = -1;
        endQuery = -1;
        children.clear();
    }

    public void dump() {
        dump(0);
    }

    private void dump(int indentation) {
        for(int i = 0; i < indentation; i++) {
            System.out.print("    ");
        }
        System.out.println(name + " : " + getTimeTaken() / 1000 / 1000f + "ms");
        for(int i = 0; i < children.size(); i++) {
            children.get(i).dump(indentation + 1);
        }
    }
}
