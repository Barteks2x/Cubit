package com.github.barteks2x.openmine.block;

public class BlockStone extends Block {

	public BlockStone(int id) {
		super(id, "Stone");
	}

	@Override
	public int getTextureForSide(int side) {
		return 0;
	}
}
