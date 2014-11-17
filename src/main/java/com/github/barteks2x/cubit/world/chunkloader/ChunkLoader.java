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
import com.github.barteks2x.cubit.world.chunk.Chunk;
import java.util.List;

/**
 * Chunk loader loads chunks from disk, unloads and saves them and generates chunks it they don't exist. If the chunk
 * loader is unable to perform some operation - chained chunkloaders are used.
 * <p>
 * @param <T> Chunk class supported by the chunk loader.
 */
public interface ChunkLoader<T extends Chunk> {

    /**
     * Returns chunk at given location or loads/generates it if not loaded yet. If Chunk is already loaded - it should
     * be returned. If the chunk is not loaded - It should attempt to load, if loading is impossible - call chained
     * chunk loader in the oreder in which they have been added. It's not guaranted that after calling this method
     * getChunk() will return the chunk.
     * <p>
     * @param location location of chunk to load
     * <p>
     * @return loaded chunk
     */
    public T loadChunk(ChunkLocation<T> location);

    /**
     * Returns chunk at given location if it's loaded by this chunkloader or any chained chunkloader. If multimple
     * chained chunkloaders contain the chunk - chunk from the first added chunkloader should be returned. Returns null
     * if chunk is not loaded.
     * <p>
     * @param location location of chunk
     * <p>
     * @return chunk at given location
     */
    public T getChunk(ChunkLocation<T> location);

    /**
     * Marks chunk for unloading or unloads it and calls the same method of all chained loaders. It's not guarranted
     * that after calling this method the chunk will not be loaded and will be saved. If you need this feature see
     * {@link #unloadChunks()}
     * <p>
     * @param location location of chunk to unload
     */
    public void unloadChunk(ChunkLocation<T> location);

    /**
     * All chunks chunks for which unloadChunk() has been called will be unloaded and saved after calling this method.
     * It also calls this method for all chained chunk loaders. Note: It may take some time to finish this operation.
     */
    public void unloadChunks();

    /**
     * Updates chunk loader and all chained chunkloaders.
     */
    public void tick();

    /**
     * @return true if this chunk loader supports chained chunkloaders, false otherwise.
     */
    public boolean canChainChunkLoaders();

    /**
     * Adds new chained chunk loader. If it has been already added - previous chunkloader should be removed and new
     * should be added. Order in which chunkloaders are added changes chunk loading order.
     * <p>
     * @param loader chunk loader to add
     */
    public void addChainedChunkLoader(ChunkLoader<T> loader);

    /**
     * Removes chained chunk loader. If the chunk loader have never been added - returns false.
     * <p>
     * @param loader chunk loader to remove
     * <p>
     * @return true if chunk loader has been removed, false otherwise.
     */
    public boolean removeChainedChunkLoader(ChunkLoader<T> loader);

    /**
     * Returns copy of list of all chained chunk loaders. Removing or adding elements from the returned list should not
     * affect list of chained chunk loaders.
     * <p>
     * @return Copy of chained chunk loader list.
     */
    public List<ChunkLoader<T>> getChainedChunkLoaders();

    /**
     * @param location locatioon of chunk
     * <p>
     * @return true if getChunk() would not return null for that location
     */
    public boolean hasChunk(ChunkLocation<T> location);

    /**
     * @return Spawnpoint location loaded by this or chained chunkloaders.
     */
    public BlockLocation getSpawnPoint();
}
