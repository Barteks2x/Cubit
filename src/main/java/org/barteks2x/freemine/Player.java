package org.barteks2x.freemine;

public class Player {

	protected float x, y, z, rx, ry;
	protected BlockPosition selectedBlock = new BlockPosition(0, 0, 0);

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

	public BlockPosition getSelectedBlock() {
		return selectedBlock;
	}
}
