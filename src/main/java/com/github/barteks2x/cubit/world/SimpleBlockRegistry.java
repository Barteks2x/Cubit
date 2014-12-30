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
import com.github.barteks2x.cubit.util.logging.LoggerUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Basic block registry using integer IDs.
 */
public class SimpleBlockRegistry implements BlockRegistry {

    private static final Logger logger = LoggerUtil.getLogger(SimpleBlockRegistry.class);

    private final Map<Integer, Block> fromId = new HashMap<Integer, Block>(256);
    private final Map<Block, Integer> toId = new HashMap<Block, Integer>(256);

    private int nextId = 0;

    private final World world;

    /**
     * Constructs new BlockRegistry for given world.
     * <p>
     * @param world World in which this block registry will be used.
     */
    public SimpleBlockRegistry(World world) {
        this.world = world;
    }

    @Override
    public int getID(Block block) {
        if (!toId.containsKey(block)) {
            logger.log(Level.WARNING,
                    "Attempt to get ID of unregistered block: {0} in world: {1}. Registering block...",
                    new Object[]{block, world});
            registerBlock(block);
        }
        return toId.get(block);
    }

    @Override
    public Block fromID(int id) {
        return fromId.get(id);
    }

    protected void registerBlock(Block block) {
        if (toId.containsKey(block)) {
            assert fromId.containsValue(block) : "Block not fully registered.";
            throw new BlockAlredyRegisteredException(block, world);
        }

        assert !fromId.containsValue(block) : "Block not fully registered.";
        assert !fromId.containsKey(nextId) : "Id " + nextId + "already used!";

        this.toId.put(block, nextId);
        this.fromId.put(nextId, block);

        logger.log(Level.FINE, "Block {0} successfully registered with ID {1}",
                new Object[]{block, nextId});
        nextId++;
    }

}
