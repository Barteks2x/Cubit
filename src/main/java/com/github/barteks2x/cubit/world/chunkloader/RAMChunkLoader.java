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
package com.github.barteks2x.cubit.world.chunkloader;

import com.github.barteks2x.cubit.location.BlockLocation;
import com.github.barteks2x.cubit.location.ChunkLocation;
import com.github.barteks2x.cubit.world.chunk.Chunk;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads chunks from caache. If chunk is nt stored in cache - uses chained chunk loader.
 * <p>
 * @param <C> Chunk class used by this chunk loader
 */
public class RAMChunkLoader<C extends Chunk> implements ChunkLoader<C> {

    private final Map<ChunkLocation<C>, C> chunks;

    private final List<ChunkLoader<C>> chained;

    /**
     * Constructs new RAMChunkLoader with initial capacity large enough to store chunks within 10 chunks from player.
     */
    public RAMChunkLoader() {
        this(10);
    }

    /**
     * Constructs new RAMChunkLoader with initial capacity large enough to store chunks within {@code radius} chunks
     * from player.
     * <p>
     * @param radius Specifies initial capacity
     */
    public RAMChunkLoader(int radius) {
        int d = 2 * radius;
        d += 1;
        this.chunks = new HashMap<ChunkLocation<C>, C>(d * d * d);
        this.chained = new ArrayList<ChunkLoader<C>>(2);
    }

    @Override
    public C loadChunk(ChunkLocation<C> location) {
        if(this.hasChunk(location)) {
            return this.getChunk(location);
        }
        for(ChunkLoader<C> loader : chained) {
            C chunk = loader.loadChunk(location);
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
    public C getChunk(ChunkLocation<C> location) {
        if(this.chunks.containsKey(location)) {
            return this.chunks.get(location);
        }
        for(ChunkLoader<C> loader : chained) {
            C chunk = loader.getChunk(location);
            if(chunk != null) {
                return chunk;
            }
        }
        return null;
    }

    @Override
    public void unloadChunk(ChunkLocation<C> location) {
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
    public void addChainedChunkLoader(ChunkLoader<C> loader) {
        this.removeChainedChunkLoader(loader);
        chained.add(loader);
    }

    @Override
    public boolean removeChainedChunkLoader(ChunkLoader<C> loader) {
        if(this.chained.contains(loader)) {
            this.chained.remove(loader);
            return true;
        }
        return false;
    }

    @Override
    public List<ChunkLoader<C>> getChainedChunkLoaders() {
        return new ArrayList<ChunkLoader<C>>(chained);
    }

    @Override
    public boolean hasChunk(ChunkLocation<C> location) {
        if(this.chunks.containsKey(location)) {
            return true;
        }
        for(ChunkLoader<C> loader : chained) {
            if(loader.hasChunk(location)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public BlockLocation getSpawnPoint() {
        for(ChunkLoader<C> loader : chained) {
            BlockLocation loc = loader.getSpawnPoint();
            if(loc != null) {
                return loc;
            }
        }
        return null;
    }

}
