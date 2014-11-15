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

import com.github.barteks2x.cubit.world.CubitWorld;

public class Vec3D {

    private final double x, y, z;

    public Vec3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3D(Vec3D pos) {
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Vec3D add(Vec3D vec) {
        return new Vec3D(this.getX() + vec.getX(),
                this.getY() + vec.getY(),
                this.getZ() + vec.getZ());
    }

    public Vec3D add(double x, double y, double z) {
        return this.add(new Vec3D(x, y, z));
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
        Vec3I other = (Vec3I) obj;
        return other.getX() == this.getX() && other.getY() == this.getY() &&
                other.getZ() == this.getZ();
    }

    @Override
    public int hashCode() {
        long x_ = Double.doubleToLongBits(this.getX());
        x_ ^= x_ >>> 32;
        int result = 31;
        result += x_;

        long y_ = Double.doubleToLongBits(this.getY());
        y_ ^= y_ >>> 32;
        result *= 31;
        result += y_;

        long z_ = Double.doubleToLongBits(this.getZ());
        z_ ^= z_ >>> 32;
        result *= 31;
        result += z_;

        return result;
    }
}
