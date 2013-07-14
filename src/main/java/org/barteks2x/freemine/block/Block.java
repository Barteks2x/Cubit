package org.barteks2x.freemine.block;

import java.util.HashMap;
import java.util.Map;

public abstract class Block {
	//Block list

	public static final Map<Integer, Block> blocks = new HashMap<Integer, Block>(256);
	//Blocks
	public static final Block STONE = new BlockStone(1);
	public static final Block DIRT = new BlockDirt(2);
	public static final Block GRASS = new BlockGrass(3);
	public static final Block COBBLESTONE = new BlockCobbleStone(4);
	private final int ID;

	@SuppressWarnings("LeakingThisInConstructor")
	public Block(int id) {
		this.ID = id;
		blocks.put(id, this);
	}

	public int getID() {
		return this.ID;
	}

	/**
	 *
	 * @param side
	 *                0 : x + 1
	 *                1 : x - 1
	 *                2 : y + 1
	 *                3 : y - 1
	 *                4 : z + 1
	 *                5 : z - 1
	 * <p/>
	 * @return texture id
	 */
	public abstract int getTextureForSide(int side);

	@Override
	public int hashCode() {
		return this.ID;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Block other = (Block)obj;
		if (this.ID != other.getID()) {
			return false;
		}
		return true;
	}

	public static Block byId(int id) {
		return blocks.get(id);
	}
}
