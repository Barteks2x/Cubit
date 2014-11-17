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

import com.github.barteks2x.cubit.location.BlockLocation;
import com.github.barteks2x.cubit.location.Vec3I;
import com.github.barteks2x.cubit.world.chunk.Chunk;
import com.github.barteks2x.cubit.location.ChunkLocation;
import com.github.barteks2x.cubit.world.CubitWorld;
import com.github.barteks2x.cubit.world.chunk.ChunkFactory;
import com.github.barteks2x.cubit.world.chunkloader.ChunkLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class AbstractChunkGenerator<T extends Chunk> implements ChunkLoader<T> {

    protected final Random rand;
    protected final long seed;
    protected final ChunkFactory<T> chunkBuilder;

    private CubitWorld<T> world = null;

    protected AbstractChunkGenerator(ChunkFactory<T> chunkBuilder, long seed) {
        this.rand = new Random(seed);
        this.seed = seed;
        this.chunkBuilder = chunkBuilder;
    }

    public void init(CubitWorld<T> world) {
        if (this.world != null) {
            throw new AlreadyInitializedException();
        }
        this.world = world;
    }

    public T generateChunk(ChunkLocation<T> location) {
        T chunk = this.chunkBuilder.clear().setLocation(location).build();
        generateTerrain(location, chunk);
        return chunk;
    }

    @Override
    public BlockLocation getSpawnPoint() {
        this.rand.setSeed(seed);
        int x = rand.nextInt(64) - 32;
        int z = rand.nextInt(64) - 32;
        int y = getApproximateHeightAt(x, z);
        return new BlockLocation(world, x, y, z);
    }

    protected abstract void generateTerrain(ChunkLocation<T> locationn, T chunk);

    protected abstract int getApproximateHeightAt(int x, int z);

    @Override
    public T loadChunk(ChunkLocation<T> location) {
        return this.generateChunk(location);
    }

    @Override
    public T getChunk(ChunkLocation<T> location) {
        return null;
    }

    @Override
    public void unloadChunk(ChunkLocation<T> location) {
    }

    @Override
    public void unloadChunks() {
    }

    @Override
    public void tick() {
    }

    @Override
    public boolean canChainChunkLoaders() {
        return false;
    }

    @Override
    public void addChainedChunkLoader(ChunkLoader<T> loader) {
        throw new UnsupportedOperationException(
                "Chunk generator doesn't support chained chunk loaders.");
    }

    @Override
    public boolean removeChainedChunkLoader(ChunkLoader<T> loader) {
        throw new UnsupportedOperationException(
                "Chunk generator doesn't support chained chunk loaders.");
    }

    @Override
    public List<ChunkLoader<T>> getChainedChunkLoaders() {
        return new ArrayList<ChunkLoader<T>>(0);
    }

    @Override
    public boolean hasChunk(ChunkLocation<T> location) {
        return false;
    }
}
