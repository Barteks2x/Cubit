package org.barteks2x.freemine;

public class BlockInChunkPosition extends IntPosition {

	public BlockInChunkPosition(int x, int y, int z) {
		super(x & 0xf, y & 0xf, z & 0xf);
	}

	public static BlockInChunkPosition getFromIndex(int i) {
		return new BlockInChunkPosition((i & 0x0f0) >> 4, i & 0x00f, (i & 0xf00) >> 8);
	}

	public static int getIndexFromXYZ(int x, int y, int z) {
		return y | x << 4 | z << 8;
	}

	public int toIndex() {
		return y | x << 4 | z << 8;
	}

	public BlockPosition getBlockPosition(ChunkPosition chunkPos) {
		return new BlockPosition(chunkPos.x << 4 | x, chunkPos.y << 4 | y, chunkPos.z << 4 | z);
	}

	@Override
	public boolean isValid() {
		return x >> 4 == 0 && y >> 4 == 0 && z >> 4 == 0;
	}
}
