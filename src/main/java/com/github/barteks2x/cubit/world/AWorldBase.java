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
package com.github.barteks2x.cubit.world;

import com.github.barteks2x.cubit.MathHelper;
import com.github.barteks2x.cubit.block.Block;
import com.github.barteks2x.cubit.generator.AChunkGenerator;
import com.github.barteks2x.cubit.location.BlockLocation;
import com.github.barteks2x.cubit.location.ChunkLocation;
import com.github.barteks2x.cubit.location.Vec3I;
import com.github.barteks2x.cubit.util.MathUtil;
import com.github.barteks2x.cubit.world.chunkloader.IChunkLoader;

/**
 * Basic world functionality, implemented using chunks and chunk generator.
 * <p>
 * @param <Chunk> Chunk class used by this world
 */
public abstract class AWorldBase<Chunk extends IChunk<Chunk>> implements IWorld {

    private final IChunkLoader<Chunk> chunkLoader;
    protected final long seed;
    private final BlockRegistry blockRegistry;
    private Vec3I spawnPoint;

    /**
     *
     * @param chunkLoader this chunk loader will be used to load chunks.
     * @param seed        Seed used to generate terrain.
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public AWorldBase(IChunkLoader<Chunk> chunkLoader, long seed) {
        this.spawnPoint = chunkLoader.getSpawnPoint();

        this.chunkLoader = chunkLoader;

        this.seed = seed;

        this.blockRegistry = new BlockRegistry(this);
        this.registerBlocks();
    }

    public Chunk getChunkAt(int x, int y, int z) {
        return this.getChunkAt(
                new ChunkLocation<Chunk>(this, this.getChunkSize(), x, y, z));
    }

    public Chunk getChunkAt(ChunkLocation<Chunk> pos) {
        return this.chunkLoader.getChunk(pos);
    }

    public boolean isChunkLoaded(int x, int y, int z) {
        return this.isChunkLoaded(new ChunkLocation<Chunk>(this,
                this.getChunkSize(), x, y, z));
    }

    public boolean isChunkLoaded(ChunkLocation<Chunk> location) {
        return this.chunkLoader.hasChunk(location);
    }

    public boolean isChunkLoaded(BlockLocation location) {
        return this.chunkLoader.hasChunk(this.toChunkLocation(location));
    }

    public abstract Chunk getChunkAt(BlockLocation location);

    @Override
    public Block getBlockAt(int x, int y, int z) {
        return this.getBlockAt(new BlockLocation(this, x, y, z));
    }

    @Override
    public Block getBlockAt(BlockLocation location) {
        if (!this.isValidBlockLocation(location)) {
            return Block.AIR;
        }
        if (!this.isChunkLoaded(location)) {
            return Block.AIR;
        }
        Chunk chunk = this.getChunkAt(location);
        BlockLocation locInChunk = location.modP(chunk.getSize());

        int x = locInChunk.getX();
        int y = locInChunk.getY();
        int z = locInChunk.getZ();
        return chunk.getBlockAt(x, y, z);
    }

    @Override
    public boolean setBlockAt(int x, int y, int z, Block block) {
        return this.setBlockAt(new BlockLocation(this, x, y, z), block);
    }

    @Override
    public boolean setBlockAt(BlockLocation location, Block block) {
        if (!this.isValidBlockLocation(location)) {
            return false;
        }
        if (!this.isChunkLoaded(location)) {
            return false;
        }

        Chunk chunk = this.getChunkAt(location);
        BlockLocation locInChunk = location.modP(chunk.getSize());
        return chunk.setBlockAt(
                locInChunk.getX(),
                locInChunk.getY(),
                locInChunk.getZ(),
                block);
    }

    public Chunk loadChunkAt(ChunkLocation<Chunk> location) {
        return this.chunkLoader.loadChunk(location);
    }

    @Override
    public Vec3I getSpawnPoint() {
        return this.spawnPoint;
    }

    public void loadChunksWithinRadius(ChunkLocation<Chunk> location,
            int radiusX, int radiusY, int radiusZ) {
        for (int x = -radiusX; x <= radiusX; ++x) {
            for (int y = -radiusY; y <= radiusY; ++y) {
                for (int z = -radiusZ; z <= radiusZ; ++z) {
                    loadChunkAt(location.add(x, y, z));
                }
            }
        }
    }

    @Override
    public IBlockRegistry getBlockRegistry() {
        return this.blockRegistry;
    }

    @Override
    public void setSpawnPoint(Vec3I loc) {
        this.spawnPoint = loc;
    }

    protected abstract void onBlockUpdate(int x, int y, int z);

    protected abstract Vec3I getChunkSize();

    public abstract ChunkLocation<Chunk> toChunkLocation(BlockLocation location);

    private void registerBlocks() {
        for (Block block : Block.blocks) {
            this.blockRegistry.registerBlock(block);
        }
    }
}
