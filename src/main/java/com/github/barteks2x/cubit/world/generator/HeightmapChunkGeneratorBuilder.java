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

import com.github.barteks2x.cubit.world.IWorld;
import com.github.barteks2x.cubit.world.IncompleteBuildException;
import com.github.barteks2x.cubit.world.generator.IChunkGeneratorBuilder;
import com.github.barteks2x.cubit.world.chunk.IChunkFactory;
import com.github.barteks2x.cubit.world.chunk.IChunk;
import com.github.barteks2x.cubit.world.generator.AChunkGenerator;
import com.github.barteks2x.cubit.world.generator.HeightmapChunkGenerator;

public class HeightmapChunkGeneratorBuilder<T extends IChunk> implements
        IChunkGeneratorBuilder<T> {

    private long seed;
    private IWorld world;
    private IChunkFactory<T> chunkFactory;

    @Override
    public HeightmapChunkGeneratorBuilder<T> setSeed(long seed) {
        this.seed = seed;
        return this;
    }

    @Override
    public AChunkGenerator<T> build() {
        if (this.world == null) {
            throw new IncompleteBuildException("No world specified.");
        }
        return new HeightmapChunkGenerator<T>(chunkFactory, seed);
    }

    @Override
    public HeightmapChunkGeneratorBuilder<T> setChunkFactory(IChunkFactory<T> factory) {
        this.chunkFactory = factory;
        return this;
    }
}
