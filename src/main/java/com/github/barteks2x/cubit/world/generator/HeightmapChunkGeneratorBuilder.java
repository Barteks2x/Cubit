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
package com.github.barteks2x.cubit.world.generator;

import com.github.barteks2x.cubit.world.World;
import com.github.barteks2x.cubit.world.IncompleteBuildException;
import com.github.barteks2x.cubit.world.generator.ChunkGeneratorBuilder;
import com.github.barteks2x.cubit.world.chunk.ChunkFactory;
import com.github.barteks2x.cubit.world.chunk.Chunk;
import com.github.barteks2x.cubit.world.generator.AbstractChunkGenerator;
import com.github.barteks2x.cubit.world.generator.HeightmapChunkGenerator;

public class HeightmapChunkGeneratorBuilder<T extends Chunk> implements
        ChunkGeneratorBuilder<T> {

    private long seed;
    private World world;
    private ChunkFactory<T> chunkFactory;

    @Override
    public HeightmapChunkGeneratorBuilder<T> setSeed(long seed) {
        this.seed = seed;
        return this;
    }

    @Override
    public AbstractChunkGenerator<T> build() {
        if (this.world == null) {
            throw new IncompleteBuildException("No world specified.");
        }
        return new HeightmapChunkGenerator<T>(chunkFactory, seed);
    }

    @Override
    public HeightmapChunkGeneratorBuilder<T> setChunkFactory(ChunkFactory<T> factory) {
        this.chunkFactory = factory;
        return this;
    }
}
