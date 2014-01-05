package com.github.barteks2x.openmine;

public class ChunkPosition extends IntPosition{
	public ChunkPosition(int x, int y, int z){
		super(x, y, z);
	}

	@Override
	public boolean isValid() {
		return true;
	}
}