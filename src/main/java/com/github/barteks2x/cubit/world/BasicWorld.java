/*
 * Copyright (C) 2014 bartosz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.barteks2x.cubit.world;

import com.github.barteks2x.cubit.Player;
import com.github.barteks2x.cubit.block.Block;
import com.github.barteks2x.cubit.location.BlockLocation;
import com.github.barteks2x.cubit.location.ChunkLocation;
import com.github.barteks2x.cubit.location.EntityLocation;
import com.github.barteks2x.cubit.location.Vec3I;
import com.github.barteks2x.cubit.util.MathUtil;
import com.github.barteks2x.cubit.world.chunkloader.IChunkLoader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Bartosz Skrzypczak
 */
public class BasicWorld extends AWorldBase<ChunkCube16> {

    private final Set<Player> players;
    private final int loadDistance = 128;

    private BasicWorld(IChunkLoader<ChunkCube16> chunkLoader, long seed) {
        super(chunkLoader, seed);
        this.players = new HashSet<Player>(2);
    }

    @Override
    protected Vec3I getChunkSize() {
        return ChunkCube16.chunkSize();
    }

    private void loadChunksWithinRadius(BlockLocation location, int blockRadius) {
        long time = System.nanoTime();
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
                    this.loadChunkAt(new ChunkLocation<ChunkCube16>(this, chunkSize, x, y, z));
                }
            }
        }
        System.out.println((System.nanoTime()-time)/1000000);
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

    @Override
    protected void onBlockUpdate(BlockLocation location, Block old, Block updated) {
        System.out.println("TODO: OnBlockUpdate()");
    }

    @Override
    protected void onChunkLoad(ChunkLocation<ChunkCube16> location) {
        System.out.println("TODO: OnChunkLoad()");
    }

    @Override
    public void joinPlayer(Player player) {
        this.players.add(player);
    }

    public static class BasicWorldBuilder {

        private IChunkLoader<ChunkCube16> chunkLoader;
        private Long seed;//to allow null

        private BasicWorldBuilder() {
        }

        public BasicWorldBuilder setChunkLoader(IChunkLoader<ChunkCube16> chunkLoader) {
            if(chunkLoader == null) {
                throw new IllegalArgumentException("ChunkLoader cannot be null!");
            }
            this.chunkLoader = chunkLoader;
            return this;
        }

        public BasicWorldBuilder setSeed(long seed) {
            this.seed = seed;
            return this;
        }

        public BasicWorld build() {
            return new BasicWorld(chunkLoader, seed);
        }
    }

    public static BasicWorldBuilder newWorld() {
        return new BasicWorldBuilder();
    }
}
