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
package com.github.barteks2x.cubit.world.chunkloader;

import com.github.barteks2x.cubit.location.BlockLocation;
import com.github.barteks2x.cubit.location.ChunkLocation;
import com.github.barteks2x.cubit.world.chunk.IChunk;
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
