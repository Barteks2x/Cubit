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

import com.github.barteks2x.cubit.block.Block;
import com.github.barteks2x.cubit.location.ChunkLocation;
import com.github.barteks2x.cubit.location.Vec3I;
import com.github.barteks2x.cubit.util.logging.LoggerUtil;
import java.util.logging.Logger;

public class ChunkCube32 extends AbstractChunkCubeN<ChunkCube32> {

    private static final Logger logger = LoggerUtil.getLogger(ChunkCube16.class);
    private static final Vec3I SIZE = new Vec3I(32, 32, 32);

    public ChunkCube32(ChunkLocation<ChunkCube32> location) {
        super(location, EMPTY_BLOCK);
    }

    public ChunkCube32(ChunkLocation<ChunkCube32> location, Block fill) {
        super(location, fill);
    }

    public ChunkCube32(ChunkLocation<ChunkCube32> location, Block[][][] data) {
        super(location, data);
    }

    @Override
    public String toString() {
        return new StringBuilder(100).append("Chunk8(").append(this.getLocation().toString()).append(")").toString();
    }

    @Override
    public Vec3I getSize() {
        return SIZE;
    }

    public static Vec3I chunkSize() {
        return SIZE;
    }
}
