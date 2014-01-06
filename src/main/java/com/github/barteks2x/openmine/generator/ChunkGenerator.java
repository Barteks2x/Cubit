package com.github.barteks2x.openmine.generator;

import com.github.barteks2x.openmine.world.Chunk;
import static com.github.barteks2x.openmine.BlockInChunkPosition.*;
import com.github.barteks2x.openmine.block.Block;

public class ChunkGenerator extends AChunkGenerator{
    protected INoiseGenerator2d noiseGen;

    public ChunkGenerator(long seed) {
        super(seed);
        this.noiseGen = new ValueNoiseGenerator(128, 0.8D, 4, 2.24564D, seed);
    }
    
    @Override
    protected void generateTerrain(int x, int y, int z, int[] blocks) {
        for(int i = 0; i < Chunk.CHUNK_X; ++i) {
            for(int j = 0; j < Chunk.CHUNK_Z; ++j) {
                double v = noiseGen.getFBMValueAt((x * 16) + i, (z * 16) + j);
                v *= 32;
                v -= y << 4;
                if(v <= 0) {
                    continue;
                }
                if(v >= Chunk.CHUNK_Y) {
                    for(int k = 0; k < Chunk.CHUNK_Y; k++) {
                        int id = Block.STONE.getID();
                        if(((int)v) - k == 0) {
                            id = Block.GRASS.getID();
                        } else if(((int)v) - k < 4) {
                            id = Block.DIRT.getID();
                        }
                        blocks[getIndexFromXYZ(i, k, j)] = id;
                    }
                    continue;
                }
                for(int k = (int)v; k >= 0; --k) {
                    int id = Block.STONE.getID();
                    if(((int)v) - k == 0) {
                        id = Block.GRASS.getID();
                    } else if(((int)v) - k < 4) {
                        id = Block.DIRT.getID();
                    }
                    blocks[getIndexFromXYZ(i, k, j)] = id;
                }
            }
        }
    }

    @Override
    protected int getApproximateHeightAt(int x, int z) {
        return (int)(noiseGen.getFBMValueAt(x, z) * 32);
    }
}
