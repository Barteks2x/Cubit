package org.barteks2x.freemine.generator;

import java.util.Random;
import org.barteks2x.freemine.Chunk;
import org.barteks2x.freemine.block.Block;

import static org.barteks2x.freemine.BlockInChunkPosition.getIndexFromXYZ;
import org.barteks2x.freemine.BlockPosition;
import org.barteks2x.freemine.IntPosition;

public class ChunkGenerator {

	protected Random rand;
	protected long seed;
	protected INoiseGenerator2d noiseGen;

	public ChunkGenerator(long seed) {
		this.rand = new Random(seed);
		this.seed = seed;
		this.noiseGen = new ValueNoiseGenerator(128, 0.8D, 4, 2.24564D, seed);
	}

	public Chunk generateChunk(int x, int y, int z) {
		Random r1 = new Random(x);
		Random r2 = new Random(y);
		Random r3 = new Random(z);
		rand.setSeed(seed + r1.nextLong() + r2.nextLong() + r3.nextLong());

		int[] blocks = new int[Chunk.SIZE];
		generateTerrain(x, y, z, blocks);
		Chunk chunk = new Chunk(x, y, z, blocks);
		return chunk;
	}

	public IntPosition getSpawnPoint() {
		Random r = new Random();
		int x = r.nextInt(64)-32;
		int z = r.nextInt(64)-32;
		int y = (int)(noiseGen.getFBMValueAt(x, z)*32);
		return new BlockPosition(x, y, z);
	}

	protected void generateTerrain(int x, int y, int z, int[] blocks) {
		for (int i = 0; i < Chunk.CHUNK_X; ++i) {
			for (int j = 0; j < Chunk.CHUNK_Z; ++j) {
				double v = noiseGen.getFBMValueAt((x * 16) + i, (z * 16) + j);
				v *= 32;
				v -= y << 4;
				if (v <= 0) {
					continue;
				}
				if (v >= Chunk.CHUNK_Y) {
					for (int k = 0; k < Chunk.CHUNK_Y; k++) {
						int id = Block.STONE.getID();
						if (((int)v) - k == 0) {
							id = Block.GRASS.getID();
						} else if (((int)v) - k < 4) {
							id = Block.DIRT.getID();
						}
						blocks[getIndexFromXYZ(i, k, j)] = id;
					}
					continue;
				}
				for (int k = (int)v; k >= 0; --k) {
					int id = Block.STONE.getID();
					if (((int)v) - k == 0) {
						id = Block.GRASS.getID();
					} else if (((int)v) - k < 4) {
						id = Block.DIRT.getID();
					}
					blocks[getIndexFromXYZ(i, k, j)] = id;
				}
			}
		}
	}
}
