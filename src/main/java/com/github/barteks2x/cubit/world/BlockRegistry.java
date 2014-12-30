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

import com.github.barteks2x.cubit.block.Block;

public interface BlockRegistry {

    /**
     * Returns ID assigned to the block. The same instance of IBlockReistry will
     * return the same ID for the same block.
     * <p>
     * @param block block to get ID for
     * <p>
     * @return ID for given block
     */
    public int getID(Block block);

    /**
     * Returns Block for which the ID is assigned. THIS SHOULD BE USED ONLY TO
     * STORE BLOCK DATA IN ONE WORLD!.
     * <p>
     * @param id block ID
     * <p>
     * @return Block for which the ID is assigned
     */
    public Block fromID(int id);
}
