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
package com.github.barteks2x.cubit.world.chunk;

import com.github.barteks2x.cubit.block.Block;
import com.github.barteks2x.cubit.location.ChunkLocation;
import com.github.barteks2x.cubit.location.Vec3I;
import com.github.barteks2x.cubit.world.CubitWorld;

/**
 * Basic chunk interface for block storage.
 */
public interface Chunk {

    /**
     * @return x (positive towards north) location of the chunk. Chunk at x
     *         location = 0 contains blocks at x cooreinates from 0 to chunkXSize
     */
    public int getX();

    /**
     * @return y (height) location of the chunk. Chunk at y location = 0
     *         contains blocks at y cooreinates from 0 to chunkYSize
     */
    public int getY();

    /**
     * @return z (positive towards east) location of the chunk. Chunk at z
     *         location = 0 contains blocks at z cooreinates from 0 to chunkZSize
     */
    public int getZ();

    /**
     * @return Location of the chunk. Location contains information about chunk
     *         size.
     */
    public ChunkLocation<? extends Chunk> getLocation();

    /**
     * @return Chunk size. X, Y and Z must be positive.
     */
    public Vec3I getSize();
    
    /**
     * Sets block at local chunk x/y/z location. If eighter x y or z are outside
     * of correct position range IllegalArgumentException should be thrown.
     * Method does not accept null values.
     * <p>
     * @param localX block X location relative to chunk origin
     * @param localY block Y location relative to chunk origin
     * @param localZ block Z location relative to chunk origin
     * @param block  block to be set
     * <p>
     * @return true if setting was successful, false otherwise
     */
    public boolean setBlockAt(int localX, int localY, int localZ, Block block);

    /**
     * @param localX block X location relative to chunk origin
     * @param localY block Y location relative to chunk origin
     * @param localZ block Z location relative to chunk origin
     * <p>
     * @return block at x/y/z location
     */
    public Block getBlockAt(int localX, int localY, int localZ);

    /**
     * @return default block returned if block at given locatio hasn't been set.
     *         Null is not allowed.
     */
    public Block getDefaultBlock();

    /**
     * @return World containing the chunk. Null if chunk it not loaded yet or
     *         has been unloaded.
     */
    public CubitWorld<? extends Chunk> getWorld();

    /**
     * @return true if load method has been calles and unload method has not
     *         been called. false otherwise.
     */
    public boolean isLoaded();

    /**
     * Called after unloading chunk from world. After calling this
     * method isLoaded method returns false, load method has no effect, result
     * of any other operation is undefined.
     */
    public void unload();

    /**
     * Called before loading chunk into world. This method does NOT
     * load the chunk into world. This method does NOT load any data from disk.
     */
    public void load();
}
