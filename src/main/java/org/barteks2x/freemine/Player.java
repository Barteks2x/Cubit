package org.barteks2x.freemine;

public class Player {

	protected float x, y, z, rx, ry;
	protected BlockPosition selectedBlock = null;
	protected BlockPosition blockOnSelected = null;

	public Player() {
		x = 0;
		y = 0;
		z = 0;
		rx = 0;
		ry = 0;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public float getRx() {
		return rx;
	}

	public void setRx(float rx) {
		this.rx = rx;
	}

	public float getRy() {
		return ry;
	}

	public void setRy(float ry) {
		this.ry = ry;
	}

	public void setSelectedBlock(int x, int y, int z) {
		selectedBlock = new BlockPosition(x, y, z);
	}

	public void setSelectedBlock(BlockPosition pos) {
		selectedBlock = pos;
	}

	public BlockPosition getSelectedBlock() {
		return selectedBlock;
	}

	public void setBlockOnSelectedBlock(int x, int y, int z) {
		blockOnSelected = new BlockPosition(x, y, z);
	}

	public void setBlockOnSelectedBlock(BlockPosition pos) {
		blockOnSelected = pos;
	}

	public BlockPosition getBlockOnSelectedBlock() {
		return blockOnSelected;
	}
}
