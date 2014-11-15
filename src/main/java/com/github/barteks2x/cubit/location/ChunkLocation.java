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
import com.github.barteks2x.cubit.world.CubitWorld;
import com.github.barteks2x.cubit.world.chunk.IChunk;

public class ChunkLocation<C extends IChunk> extends Vec3I {

    private final CubitWorld<C> world;
    private final Vec3I chunkSize;

    public ChunkLocation(CubitWorld<C> world, Vec3I size, int x, int y, int z) {
        super(x, y, z);
        this.world = world;
        this.chunkSize = size;
    }

    public ChunkLocation(CubitWorld<C> world, Vec3I size, Vec3I loc) {
        super(loc);
        this.world = world;
        this.chunkSize = size;
    }

    public ChunkLocation(CubitWorld<C> world, Vec3I size, BlockLocation loc) {
        super(loc.div(size));
        this.world = world;
        this.chunkSize = size;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + this.world.hashCode();
        hash = 83 * hash + this.getX();
        hash = 83 * hash + this.getY();
        hash = 83 * hash + this.getZ();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ChunkLocation<?> other = (ChunkLocation<?>) obj;
        if (this.world != other.world &&
                (this.world == null || !this.world.equals(other.world))) {
            return false;
        }
        if (this.getX() != other.getX()) {
            return false;
        }
        if (this.getY() != other.getY()) {
            return false;
        }
        return this.getY() == other.getY();
    }

    @Override
    public ChunkLocation<C> add(int x, int y, int z) {
        return new ChunkLocation<C>(this.world, this.chunkSize, super.add(x, y, z));
    }

    @Override
    public ChunkLocation<C> add(Vec3I vec) {
        return this.add(vec.getX(), vec.getY(), vec.getZ());
    }

    @Override
    public ChunkLocation<C> div(int x, int y, int z) {
        return new ChunkLocation<C>(this.world, this.chunkSize, super.div(x, y, z));
    }

    @Override
    public ChunkLocation<C> div(Vec3I vec) {
        return this.div(vec.getX(), vec.getY(), vec.getZ());
    }

    public CubitWorld<C> getWorld() {
        return this.world;
    }

    public Vec3I getChunkSize() {
        return this.chunkSize;
    }

    @Override
    public String toString() {
        /*
         * Maximum length is:
         * ChunkLoaction( 14
         * X 11 (max 10 digits+sign)
         * Y 11
         * Z 11
         * , 4
         * ) 1
         * total 52
         */
        StringBuilder sb = new StringBuilder(52);
        //ChunkLocation(%d, %d, %d)
        return sb.append("ChunkLocation(").append(this.getX()).append(", ").
                append(this.getY()).append(", ").append(this.getZ()).append(")").
                toString();
    }
}
