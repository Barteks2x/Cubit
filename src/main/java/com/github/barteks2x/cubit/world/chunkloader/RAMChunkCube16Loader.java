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
package com.github.barteks2x.cubit.world.chunkloader;

import com.github.barteks2x.cubit.location.BlockLocation;
import com.github.barteks2x.cubit.location.ChunkLocation;
import com.github.barteks2x.cubit.world.ChunkCube16;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Bartosz Skrzypczak
 */
public class RAMChunkCube16Loader implements IChunkLoader<ChunkCube16> {

    private final Map<ChunkLocation<ChunkCube16>, ChunkCube16> chunks;

    private final List<IChunkLoader<ChunkCube16>> chained;

    public RAMChunkCube16Loader() {
        this.chunks = new HashMap<ChunkLocation<ChunkCube16>, ChunkCube16>(21 * 21 * 21);
        this.chained = new ArrayList<IChunkLoader<ChunkCube16>>(2);
    }

    @Override
    public ChunkCube16 loadChunk(ChunkLocation<ChunkCube16> location) {
        if(this.hasChunk(location)) {
            return this.getChunk(location);
        }
        for(IChunkLoader<ChunkCube16> loader : chained) {
            ChunkCube16 chunk = loader.loadChunk(location);
            if(chunk == null) {
                continue;
            }
            //If ths chunk loader is generator, or didn't save the chunk for some other reason
            if(!loader.hasChunk(location)) {
                this.chunks.put(location, chunk);
            }
            return chunk;
        }
        return null;
    }

    @Override
    public ChunkCube16 getChunk(ChunkLocation<ChunkCube16> location) {
        if(this.chunks.containsKey(location)) {
            return this.chunks.get(location);
        }
        for(IChunkLoader<ChunkCube16> loader : chained) {
            ChunkCube16 chunk = loader.getChunk(location);
            if(chunk != null) {
                return chunk;
            }
        }
        return null;
    }

    @Override
    public void unloadChunk(ChunkLocation<ChunkCube16> location) {
        //TODO: unload chunk
    }

    @Override
    public void unloadChunks() {
        //TODO unload chunks
    }

    @Override
    public void tick() {
    }

    @Override
    public boolean canChainChunkLoaders() {
        return true;
    }

    @Override
    public void addChainedChunkLoader(IChunkLoader<ChunkCube16> loader) {
        this.removeChainedChunkLoader(loader);
        chained.add(loader);
    }

    @Override
    public boolean removeChainedChunkLoader(IChunkLoader<ChunkCube16> loader) {
        if(this.chained.contains(loader)) {
            this.chained.remove(loader);
            return true;
        }
        return false;
    }

    @Override
    public List<IChunkLoader<ChunkCube16>> getChainedChunkLoaders() {
        return new ArrayList<IChunkLoader<ChunkCube16>>(chained);
    }

    @Override
    public boolean hasChunk(ChunkLocation<ChunkCube16> location) {
        if(this.chunks.containsKey(location)) {
            return true;
        }
        for(IChunkLoader<ChunkCube16> loader : chained) {
            if(loader.hasChunk(location)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public BlockLocation getSpawnPoint() {
        for(IChunkLoader<ChunkCube16> loader : chained) {
            BlockLocation loc = loader.getSpawnPoint();
            if(loc != null) {
                return loc;
            }
        }
        return null;
    }

}
