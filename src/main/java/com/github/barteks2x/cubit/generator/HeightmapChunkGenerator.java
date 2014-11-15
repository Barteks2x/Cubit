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
package com.github.barteks2x.cubit.generator;

import com.github.barteks2x.cubit.world.IChunk;
import com.github.barteks2x.cubit.block.Block;
import com.github.barteks2x.cubit.location.ChunkLocation;
import com.github.barteks2x.cubit.location.Vec3I;
import com.github.barteks2x.cubit.world.AWorldBase;
import com.github.barteks2x.cubit.world.IChunkFactory;

public class HeightmapChunkGenerator<T extends IChunk> extends AChunkGenerator<T> {

    private final INoiseGenerator2d noiseGen;

    public HeightmapChunkGenerator(IChunkFactory<T> chunkBuilder, long seed) {
        super(chunkBuilder, seed);
        this.noiseGen = new ValueNoiseGenerator(128, 0.8D, 4, 2.24564D, seed);
    }

    @Override
    protected int getApproximateHeightAt(int x, int z) {
        return (int) (noiseGen.getValueOctaves(x, z) * 32);
    }

    @Override
    protected void generateTerrain(ChunkLocation<T> location, T chunk) {
        Vec3I chunkSize = chunk.getSize();
        final int maxX = chunkSize.getX(),
                maxY = chunkSize.getY(),
                maxZ = chunkSize.getZ();
        for (int x = 0; x < maxX; ++x) {
            for (int z = 0; z < maxZ; ++z) {
                double v = noiseGen.
                        getValueOctaves(
                                (location.getX() * maxX) + x,
                                (location.getZ() * maxZ) + z);
                v *= 32;
                v -= location.getY() * maxY;
                if (v <= 0) {
                    continue;
                }
                if (v >= maxY) {
                    for (int y = 0; y < maxY; y++) {
                        Block block = Block.STONE;
                        if (((int) v) - y == 0) {
                            block = Block.GRASS;
                        } else if (((int) v) - y < 4) {
                            block = Block.DIRT;
                        }
                        chunk.setBlockAt(x, y, z, block);
                    }
                    continue;
                }
                for (int y = (int) v; y >= 0; --y) {
                    Block block = Block.STONE;
                    if (((int) v) - y == 0) {
                        block = Block.GRASS;
                    } else if (((int) v) - y < 4) {
                        block = Block.DIRT;
                    }
                    chunk.setBlockAt(x, y, z, block);
                }
            }
        }
    }
}
