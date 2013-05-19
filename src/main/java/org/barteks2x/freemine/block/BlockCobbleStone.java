package org.barteks2x.freemine.block;

public class BlockCobbleStone extends Block {

	public BlockCobbleStone(int id) {
		super(id);
	}

	@Override
	public int getTextureForSide(int side) {
		return 4;
	}
}
