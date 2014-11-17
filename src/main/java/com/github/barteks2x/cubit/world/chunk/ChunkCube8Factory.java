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
package com.github.barteks2x.cubit.world.chunk;

import com.github.barteks2x.cubit.location.ChunkLocation;
import com.github.barteks2x.cubit.location.Vec3I;
import com.github.barteks2x.cubit.world.chunk.ChunkCube8;
import com.github.barteks2x.cubit.world.chunk.ChunkFactory;

/**
 *
 * @author Bartosz Skrzypczak
 */
public class ChunkCube8Factory implements ChunkFactory<ChunkCube8> {

    private ChunkLocation<ChunkCube8> location;
    @Override
    public ChunkFactory<ChunkCube8> clear() {
        this.location = null;
        return this;
    }

    @Override
    public ChunkFactory<ChunkCube8> setLocation(ChunkLocation<ChunkCube8> loc) {
        this.location = loc;
        return this;
    }

    @Override
    public ChunkCube8 build() {
        return new ChunkCube8(location);
    }

    @Override
    public Vec3I getChunkSize() {
        return ChunkCube8.chunkSize();
    }

}
