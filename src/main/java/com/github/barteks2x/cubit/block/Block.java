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
package com.github.barteks2x.cubit.block;

import com.github.barteks2x.cubit.location.Vec3I;
import com.github.barteks2x.cubit.render.block.CubeBlockModelBuilder;
import com.github.barteks2x.cubit.render.block.BlockModelBuilder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import toxi.geom.AABB;
import toxi.geom.Vec3D;

public abstract class Block {

    protected static final BlockModelBuilder CUBE_MODEL_BUILDER =
            new CubeBlockModelBuilder();
    public static final Set<Block> blocks = new HashSet<Block>(256);
    public static final Block AIR = new BlockAir("AIR");
    public static final Block STONE = new BlockStone("STONE");
    public static final Block DIRT = new BlockDirt("DIRT");
    public static final Block GRASS = new BlockGrass("GRASS");
    public static final Block COBBLESTONE = new BlockCobbleStone("COBBLESTONE");
    public static final Block SAND = new BlockSand("SAND");
    public static final Block WOOD = new BlockWood("WOOD");
    public static final Block LEAVES = new BlockLeaves("LEAVES");
    private final String name;
    private final AABB boundingBox;

    /**
     * Constructs new Block with specified name and axis algined bounding box.
     * <p>
     * @param name        name of the block
     * @param boundingBox bounding box of the block. Cube bounding box is min=(0, 0, 0), max=(1, 1, 1).
     */
    public Block(String name, AABB boundingBox) {
        this.name = name;
        blocks.add(this);

        this.boundingBox = boundingBox;
    }

    public Block(String name) {
        this(name, AABB.fromMinMax(new Vec3D(0, 0, 0), new Vec3D(1, 1, 1)));
    }

    /**
     * <p>
     * @param side Vector pointing outwards from block face.
     * <p/>
     * @return texture index
     */
    public abstract int getTextureForSide(Vec3I side);

    @Override
    public String toString() {
        return name;
    }

    public BlockModelBuilder getModelBuilder() {
        return CUBE_MODEL_BUILDER;
    }

    public abstract String[] getTextures();

    public boolean isTransparent() {
        return false;
    }
}
