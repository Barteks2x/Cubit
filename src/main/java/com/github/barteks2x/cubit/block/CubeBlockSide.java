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
package com.github.barteks2x.cubit.block;

import com.github.barteks2x.cubit.location.Vec3I;

/**
 * Enum representing side of Cube block. Contains vector representing the side.
 */
public enum CubeBlockSide {

    UP(new Vec3I(0, 1, 0)),
    DOWN(new Vec3I(0, -1, 0)),
    NORTH(new Vec3I(1, 0, 0)),
    SOUTH(new Vec3I(-1, 0, 0)), 
    EAST(new Vec3I(0, 0, 1)),
    WEST(new Vec3I(0, 0, -1));

    private final Vec3I vector;

    private CubeBlockSide(Vec3I vector) {
        this.vector = vector;
    }

    /**
     * @return Vector representing the side.
     */
    public Vec3I getVector() {
        return this.vector;
    }
}
