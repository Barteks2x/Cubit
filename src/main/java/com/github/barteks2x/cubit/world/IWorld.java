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

import com.github.barteks2x.cubit.block.Block;
import com.github.barteks2x.cubit.location.BlockLocation;
import com.github.barteks2x.cubit.location.Vec3I;

/**
 * General world interface that allows to perform basic operations on blocks and
 * entities.
 */
public interface IWorld {

    //these are Javadic comments
    /**
     * @param x x coordinate of the block (higher are towards north)
     * @param y y coordinate of the block (height, positive are higher)
     * @param z z coordinate of the block (higher are towards east)
     * <p>
     * @return block at (x, y, z). Returns null if location is not valid
     *         location in this world
     */
    public Block getBlockAt(int x, int y, int z);

    /**
     * @param x     x coordinate of the block (higher are towards north)
     * @param y     y coordinate of the block (height, positive are higher)
     * @param z     z coordinate of the block (higher are towards east)
     * @param block block to set, null is not allowed
     * <p>
     * @return true if setting block was successful, false otherwise. If this
     *         method returns false nothing changed in the world
     */
    public boolean setBlockAt(int x, int y, int z, Block block);

    /**
     * @param location location of the block
     * <p>
     * @return block at (x, y, z). Returns null if location is not valid
     *         location in this world
     */
    public Block getBlockAt(BlockLocation location);

    /**
     * @param location location of the block
     * @param block    block to set, null is not allowed
     * <p>
     * @return true if setting block was successful, false otherwise. If this
     *         method returns false nothing changed in the world
     */
    public boolean setBlockAt(BlockLocation location, Block block);

    /**
     * @param x x coordinate (block units, higher are towards north)
     * @param y y coordinate (block units, height, positive are higher)
     * @param z z coordinate (block units, higher are towards east)
     * <p>
     * @return true if this is valid location, false otherwise
     */
    public boolean isValidBlockLocation(int x, int y, int z);

    /**
     * @param position location to check
     * <p>
     * @return true if this is valid location, false otherwise
     */
    public boolean isValidBlockLocation(BlockLocation position);

    /**
     * @return true if for any location isValidLocation returns false, false
     *         otherwise
     */
    public boolean hasInvalidLocations();

    /**
     * @return array of byytes representing worls seed
     */
    public byte[] getSeedBytes();

    /**
     * @return The last 8 bytes of seed as long value
     */
    public long getSeedLong();

    /**
     * Updates the world with specified tickrate. Ticktrate must be greater or
     * equal 10
     *
     * @param tickrate world tickrate. Throws IllegalArgumentException if
     *                 tickrate is less than 10
     */
    public void tick(int tickrate) throws IllegalArgumentException;
    
    /**
     * @return block registtry used by this world.
     */
    public IBlockRegistry getBlockRegistry();
    
    /**
     * @return location of the spawnpoint
     */
    public Vec3I getSpawnPoint();
    
    /**
     * Changes spawnpooint location.
     * @param location spawnpoint llocation to set
     */
    public void setSpawnPoint(Vec3I location);
}
