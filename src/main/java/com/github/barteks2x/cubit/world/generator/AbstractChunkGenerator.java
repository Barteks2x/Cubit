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
import com.github.barteks2x.cubit.world.World;
import com.github.barteks2x.cubit.world.chunk.ChunkFactory;
import com.github.barteks2x.cubit.world.chunkloader.ChunkLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Abstract chunk generator implementation that implements most ChunkLoader mathods.
 * <p>
 * @param <T> Chunk type
 */
public abstract class AbstractChunkGenerator<T extends Chunk> implements ChunkLoader<T> {

    private final Random rand;
    private final long seed;
    private final ChunkFactory<T> chunkFactory;

    private World world = null;

    /**
     *
     * @param chunkFactory Chunk factory instance, used to construct new Chunks of type T
     * @param world        World instance in which the generator is used.
     */
    protected AbstractChunkGenerator(ChunkFactory<T> chunkFactory, World world) {
        this.seed = world.getSeedLong();
        this.rand = new Random(seed);
        this.world = world;
        this.chunkFactory = chunkFactory;
    }

    /**
     * Generates new chunk at {@code location}
     * <p>
     * @param location Chunk location.
     * <p>
     * @return New chunk at {@code location}
     */
    public T generateChunk(ChunkLocation<T> location) {
        T chunk = this.chunkFactory.setLocation(location).build();
        generateTerrain(chunk);
        return chunk;
    }

    /**
     * Generates world spawnpoint based on seed.
     * <p>
     * @return Randomly generated spawnpoint based on world seed
     */
    @Override
    public BlockLocation getSpawnPoint() {
        this.rand.setSeed(seed);
        int x = rand.nextInt(64) - 32;
        int z = rand.nextInt(64) - 32;
        int y = getApproximateHeightAt(x, z);
        return new BlockLocation(world, x, y, z);
    }

    /**
     * Generates terrain in the chunk.
     * <p>
     * @param chunk Chunk instance
     */
    protected abstract void generateTerrain(T chunk);

    /**
     * Returns approximate height at x/z location.
     * <p>
     * @param x X coordinate
     * @param z z coordinate
     * <p>
     * @return approximate height used to generate spawnpoint.
     */
    protected abstract int getApproximateHeightAt(int x, int z);

    /**
     * Generates chunk at {@code location}. Calls {@link AbstractChunkGenerator#generateTerrain}.
     * <p>
     * @return new chunk at {@code location}
     */
    @Override
    public final T loadChunk(ChunkLocation<T> location) {
        return this.generateChunk(location);
    }

    /**
     * Chunk generator does not load/unload chunks, always returns null.
     * <p>
     * @return null
     */
    @Override
    public final T getChunk(ChunkLocation<T> location) {
        return null;
    }

    /**
     * Chunk generator does not load/unload chunks, does nothing.
     */
    @Override
    public final void unloadChunk(ChunkLocation<T> location) {
    }

    /**
     * Chunk generator does not load/unload chunks, does nothing.
     */
    @Override
    public final void unloadChunks() {
    }

    /**
     * Updates the chunk generator, alled every game tick.
     */
    @Override
    public void tick() {
    }

    /**
     * Chunk generators cannot chain chunk loaders, returns false.
     */
    @Override
    public final boolean canChainChunkLoaders() {
        return false;
    }

    /**
     * Chunk generators cannot chain chunk loaders, throws {@link UnsupportedOperationException}.
     */
    @Override
    public final void addChainedChunkLoader(ChunkLoader<T> loader) {
        throw new UnsupportedOperationException(
                "Chunk generator doesn't support chained chunk loaders.");
    }

    /**
     * Chunk generators cannot chain chunk loaders, throws {@link UnsupportedOperationException}.
     */
    @Override
    public boolean removeChainedChunkLoader(ChunkLoader<T> loader) {
        throw new UnsupportedOperationException(
                "Chunk generator doesn't support chained chunk loaders.");
    }

    /**
     * Chunk generators cannot chain chunk loaders, returns empty list.
     */
    @Override
    public List<ChunkLoader<T>> getChainedChunkLoaders() {
        return new ArrayList<ChunkLoader<T>>(0);
    }

    /**
     * Chunk generators cannot load/unload chunks, returns false.
     */
    @Override
    public boolean hasChunk(ChunkLocation<T> location) {
        return false;
    }
}