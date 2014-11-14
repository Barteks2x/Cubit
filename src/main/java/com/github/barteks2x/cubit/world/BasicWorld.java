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

import com.github.barteks2x.cubit.location.BlockLocation;
import com.github.barteks2x.cubit.location.ChunkLocation;
import com.github.barteks2x.cubit.location.Vec3I;
import com.github.barteks2x.cubit.world.chunkloader.IChunkLoader;

/**
 *
 * @author Bartosz Skrzypczak
 */
public class BasicWorld extends AWorldBase<ChunkCube16>{

    public BasicWorld(IChunkLoader<ChunkCube16> chunkLoader, long seed) {
        super(chunkLoader, seed);
    }

    @Override
    public ChunkCube16 getChunkAt(BlockLocation location) {
        return this.getChunkAt(this.toChunkLocation(location));
    }

    @Override
    protected void onBlockUpdate(int x, int y, int z) {
        
    }

    @Override
    protected Vec3I getChunkSize() {
        return ChunkCube16.chunkSize();
    }

    @Override
    public ChunkLocation<ChunkCube16> toChunkLocation(BlockLocation location) {
        return new ChunkLocation<ChunkCube16>(this, ChunkCube16.chunkSize(), location);
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
        
    }
}
