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
import com.github.barteks2x.cubit.world.IChunk;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Bartosz Skrzypczak
 */
public class RAMChunkLoader<Chunk extends IChunk> implements IChunkLoader<Chunk> {

    private final Map<ChunkLocation<Chunk>, Chunk> chunks;

    private final List<IChunkLoader<Chunk>> chained;

    public RAMChunkLoader() {
        this.chunks = new HashMap<ChunkLocation<Chunk>, Chunk>(21 * 21 * 21);
        this.chained = new ArrayList<IChunkLoader<Chunk>>(2);
    }

    @Override
    public Chunk loadChunk(ChunkLocation<Chunk> location) {
        if(this.hasChunk(location)) {
            return this.getChunk(location);
        }
        for(IChunkLoader<Chunk> loader : chained) {
            Chunk chunk = loader.loadChunk(location);
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
    public Chunk getChunk(ChunkLocation<Chunk> location) {
        if(this.chunks.containsKey(location)) {
            return this.chunks.get(location);
        }
        for(IChunkLoader<Chunk> loader : chained) {
            Chunk chunk = loader.getChunk(location);
            if(chunk != null) {
                return chunk;
            }
        }
        return null;
    }

    @Override
    public void unloadChunk(ChunkLocation<Chunk> location) {
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
    public void addChainedChunkLoader(IChunkLoader<Chunk> loader) {
        this.removeChainedChunkLoader(loader);
        chained.add(loader);
    }

    @Override
    public boolean removeChainedChunkLoader(IChunkLoader<Chunk> loader) {
        if(this.chained.contains(loader)) {
            this.chained.remove(loader);
            return true;
        }
        return false;
    }

    @Override
    public List<IChunkLoader<Chunk>> getChainedChunkLoaders() {
        return new ArrayList<IChunkLoader<Chunk>>(chained);
    }

    @Override
    public boolean hasChunk(ChunkLocation<Chunk> location) {
        if(this.chunks.containsKey(location)) {
            return true;
        }
        for(IChunkLoader<Chunk> loader : chained) {
            if(loader.hasChunk(location)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public BlockLocation getSpawnPoint() {
        for(IChunkLoader<Chunk> loader : chained) {
            BlockLocation loc = loader.getSpawnPoint();
            if(loc != null) {
                return loc;
            }
        }
        return null;
    }

}
