package com.github.barteks2x.openmine;

public class BlockPosition extends IntPosition{
	public BlockPosition(int x, int y, int z){
		super(x, y, z);
	}
	public ChunkPosition getChunkPosition(){
		return new ChunkPosition(x>>4, x>>4, x>>4);
	}

	@Override
	public boolean isValid() {
		return true;
	}
}