/* 
 * The MIT License
 *
 * Copyright (C) contributors
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

import com.github.barteks2x.cubit.Player;
import com.github.barteks2x.cubit.block.Block;
import com.github.barteks2x.cubit.location.BlockLocation;
import com.github.barteks2x.cubit.location.ChunkLocation;
import com.github.barteks2x.cubit.location.EntityLocation;
import com.github.barteks2x.cubit.location.Vec3I;
import com.github.barteks2x.cubit.util.MathUtil;
import com.github.barteks2x.cubit.world.chunk.Chunk;
import com.github.barteks2x.cubit.world.chunk.ChunkFactory;
import com.github.barteks2x.cubit.world.chunkloader.ChunkLoader;
import java.util.HashSet;
import java.util.Set;

/**
 * Basic world functionality, implemented using chunks and chunk generator.
 * <p>
 * @param <C> Chunk class used by this world
 */
public class CubitWorld<C extends Chunk> implements World {

    private final ChunkLoader<C> chunkLoader;
    private final ChunkFactory<C> chunkFactory;
    protected final long seed;
    private final SimpleBlockRegistry blockRegistry;
    private Vec3I spawnPoint;
    private final Set<Player> players;
    private final int loadDistance = 128;

    /**
     *
     * @param chunkLoader this chunk loader will be used to load chunks.
     * @param seed        Seed used to generate terrain.
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public CubitWorld(ChunkLoader<C> chunkLoader, ChunkFactory<C> chunkFactory, long seed) {
        this.chunkLoader = chunkLoader;

        this.chunkFactory = chunkFactory;

        this.seed = seed;

        this.blockRegistry = new SimpleBlockRegistry(this);

        this.players = new HashSet<Player>(2);
        this.registerBlocks();
    }

    public C getChunkAt(BlockLocation location) {
        return this.getChunkAt(this.toChunkLocation(location));
    }

    public C getChunkAt(int x, int y, int z) {
        return this.getChunkAt(new ChunkLocation<C>(this, this.getChunkSize(), x, y, z));
    }

    public C getChunkAt(ChunkLocation<C> pos) {
        return this.chunkLoader.getChunk(pos);
    }

    public boolean isChunkLoaded(int x, int y, int z) {
        return this.isChunkLoaded(new ChunkLocation<C>(this,
                this.getChunkSize(), x, y, z));
    }

    public boolean isChunkLoaded(ChunkLocation<C> location) {
        return this.chunkLoader.hasChunk(location);
    }

    public boolean isChunkLoaded(BlockLocation location) {
        return this.chunkLoader.hasChunk(this.toChunkLocation(location));
    }

    @Override
    public Block getBlockAt(int x, int y, int z) {
        return this.getBlockAt(new BlockLocation(this, x, y, z));
    }

    @Override
    public Block getBlockAt(BlockLocation location) {
        if(!this.isValidBlockLocation(location)) {
            return Block.AIR;
        }
        if(!this.isChunkLoaded(location)) {
            return Block.AIR;
        }
        C chunk = this.getChunkAt(location);
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
        if(!this.isValidBlockLocation(location)) {
            return false;
        }
        if(!this.isChunkLoaded(location)) {
            return false;
        }

        C chunk = this.getChunkAt(location);
        BlockLocation locInChunk = location.modP(chunk.getSize());
        int localX = locInChunk.getX();
        int localY = locInChunk.getY();
        int localZ = locInChunk.getZ();
        Block old = chunk.getBlockAt(localX, localY, localZ);
        boolean success = chunk.setBlockAt(localX, localY, localZ, block);
        if(success) {
            assert chunk.getBlockAt(localX, localY, localZ) == block : "Wrong block after setting block!";
            this.onBlockUpdate(location, old, block);
        }
        return success;
    }

    public C loadChunkAt(ChunkLocation<C> location) {
        return this.chunkLoader.loadChunk(location);
    }

    @Override
    public Vec3I getSpawnPoint() {
        if(spawnPoint == null) {
            spawnPoint = this.chunkLoader.getSpawnPoint();
        }
        return this.spawnPoint == null ? new Vec3I(0, 0, 0) : this.spawnPoint;
    }

    @Override
    public BlockRegistry getBlockRegistry() {
        return this.blockRegistry;
    }

    @Override
    public void setSpawnPoint(Vec3I loc) {
        this.spawnPoint = loc;
    }

    protected void onBlockUpdate(BlockLocation location, Block old, Block updated) {
        System.out.println("TODO: OnBlockUpdate()");
    }

    protected Vec3I getChunkSize() {
        return this.chunkFactory.getChunkSize();
    }

    protected void onChunkLoad(ChunkLocation<Chunk> location) {
        System.out.println("TODO: OnChunkLoad()");
    }

    public void joinPlayer(Player player) {
        this.players.add(player);
    }

    private void registerBlocks() {
        for(Block block : Block.blocks) {
            this.blockRegistry.registerBlock(block);
        }
    }

    public ChunkLocation<C> toChunkLocation(BlockLocation location) {
        return new ChunkLocation<C>(this, this.chunkFactory.getChunkSize(), location);
    }

    @Override
    public boolean isValidBlockLocation(int x, int y, int z) {
        return true;
    }

    @Override
    public boolean isValidBlockLocation(BlockLocation position) {
        return true;
    }

    @Override
    public boolean hasInvalidLocations() {
        return false;
    }

    @Override
    public byte[] getSeedBytes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long getSeedLong() {
        return this.seed;
    }

    @Override
    public void tick(int tickrate) throws IllegalArgumentException {
        for(Player player : players) {
            EntityLocation playerLoc = player.getLocation();
            BlockLocation playerBlockLoc = new BlockLocation(playerLoc);
            this.loadChunksWithinRadius(playerBlockLoc, loadDistance);
        }
    }

    private void loadChunksWithinRadius(BlockLocation location, int blockRadius) {
        Vec3I chunkSize = this.getChunkSize();

        double xSize = chunkSize.getX();
        double ySize = chunkSize.getY();
        double zSize = chunkSize.getZ();

        int startX = MathUtil.floor((location.getX() - blockRadius) / xSize);
        int startY = MathUtil.floor((location.getY() - blockRadius) / ySize);
        int startZ = MathUtil.floor((location.getZ() - blockRadius) / zSize);

        int endX = MathUtil.ceil((location.getX() + blockRadius) / xSize);
        int endY = MathUtil.ceil((location.getY() + blockRadius) / ySize);
        int endZ = MathUtil.ceil((location.getZ() + blockRadius) / zSize);

        for(int x = startX; x <= endX; x++) {
            for(int y = startY; y <= endY; y++) {
                for(int z = startZ; z <= endZ; z++) {
                    this.loadChunkAt(new ChunkLocation<C>(this, chunkSize, x, y, z));
                }
            }
        }
    }

    public static class CubitWorldBuilder<C extends Chunk> {

        private ChunkLoader<C> chunkLoader;
        private ChunkFactory<C> chunkFactory;
        private Long seed;//to allow null

        private CubitWorldBuilder() {
        }

        public CubitWorldBuilder<C> setChunkLoader(ChunkLoader<C> chunkLoader) {
            if(chunkLoader == null) {
                throw new IllegalArgumentException("ChunkLoader cannot be null!");
            }
            this.chunkLoader = chunkLoader;
            return this;
        }

        public CubitWorldBuilder<C> setChunkFactory(ChunkFactory<C> chunkFactory) {
            if(chunkFactory == null) {
                throw new IllegalArgumentException("ChunkFactory cannot be null!");
            }
            this.chunkFactory = chunkFactory;
            return this;
        }

        public CubitWorldBuilder<C> setSeed(long seed) {
            this.seed = seed;
            return this;
        }

        public CubitWorld<C> build() {
            if(seed == null || chunkFactory == null || chunkLoader == null) {
                throw new IncompleteBuildException("Not fully built.");
            }
            return new CubitWorld<C>(chunkLoader, chunkFactory, seed);
        }
    }

    public static <T extends Chunk> CubitWorldBuilder<T> newWorld(Class<T> chunkCLass) {
        return new CubitWorldBuilder<T>();
    }
}
