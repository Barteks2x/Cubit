package com.github.barteks2x.openmine.generator;

import com.github.barteks2x.openmine.world.Chunk;
import com.github.barteks2x.openmine.*;
import java.util.Random;

public abstract class AChunkGenerator {
    protected final Random rand;
    protected final long seed;

    protected AChunkGenerator(long seed) {
        this.rand = new Random(seed);
        this.seed = seed;
    }

    public Chunk generateChunk(int x, int y, int z) {
        int[] blocks = new int[Chunk.SIZE];
        generateTerrain(x, y, z, blocks);
        Chunk chunk = new Chunk(x, y, z, blocks);
        return chunk;
    }

    public IntPosition getSpawnPoint() {
        this.rand.setSeed(seed);
        int x = rand.nextInt(64) - 32;
        int z = rand.nextInt(64) - 32;
        int y = getApproximateHeightAt(x, z);
        return new BlockPosition(x, y, z);
    }

    protected abstract void generateTerrain(int x, int y, int z, int[] blocks);

    protected abstract int getApproximateHeightAt(int x, int z);
}
