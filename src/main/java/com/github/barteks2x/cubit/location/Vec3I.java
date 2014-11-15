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
package com.github.barteks2x.cubit.location;

import com.github.barteks2x.cubit.util.MathUtil;

public class Vec3I {

    protected int x, y, z;

    public Vec3I(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3I(double x, double y, double z) {
        this(MathUtil.floor(x), MathUtil.floor(y), MathUtil.floor(z));
    }

    public Vec3I(Vec3I v) {
        this(v.getX(), v.getY(), v.getZ());
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public Vec3I setX(int x) {
        this.x = x;
        return this;
    }

    public Vec3I setY(int y) {
        this.y = y;
        return this;
    }

    public Vec3I setZ(int z) {
        this.z = z;
        return this;
    }

    public Vec3I add(int x, int y, int z) {
        return new Vec3I(this.getX() + x, this.getY() + y, this.getZ() + z);
    }

    public Vec3I add(Vec3I vec) {
        return this.add(vec.getX(), vec.getY(), vec.getZ());
    }

    public Vec3I div(int x, int y, int z) {
        return new Vec3I(
                MathUtil.floor(this.getX() / (double) x),
                MathUtil.floor(this.getY() / (double) y),
                MathUtil.floor(this.getZ() / (double) z));
    }

    public Vec3I div(Vec3I vec) {
        return this.div(vec.getX(), vec.getY(), vec.getZ());
    }

    public Vec3I mod(Vec3I vec) {
        return new Vec3I(
                this.getX() % vec.getX(),
                this.getY() % vec.getY(),
                this.getZ() % vec.getZ());
    }

    public Vec3I modP(Vec3I vec) {
        return new Vec3I(
                MathUtil.modP(this.getX(), vec.getX()),
                MathUtil.modP(this.getY(), vec.getY()),
                MathUtil.modP(this.getZ(), vec.getZ()));
    }

    public boolean isValid() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        Vec3I other = (Vec3I) obj;
        return other.x == this.x && other.y == this.y && other.z == this.z;
    }

    @Override
    public int hashCode() {
        return y ^ x << 10 ^ z << 21;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(50);
        //ChunkLocation(%d, %d, %d)
        return sb.append("Vec3I(").
                append(this.getX()).
                append(", ").
                append(this.getY()).
                append(", ").
                append(this.getZ()).
                append(")").toString();
    }
}
