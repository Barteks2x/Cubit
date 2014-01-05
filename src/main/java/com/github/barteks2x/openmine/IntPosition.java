package com.github.barteks2x.openmine;

public abstract class IntPosition {

	protected int x, y, z;

	public IntPosition(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}

	public IntPosition setX(int x) {
		this.x = x;
		return this;
	}

	public IntPosition setY(int y) {
		this.y = y;
		return this;
	}

	public IntPosition setZ(int z) {
		this.z = z;
		return this;
	}

	public abstract boolean isValid();

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		IntPosition other = (IntPosition)obj;
		return other.x == this.x && other.y == this.y && other.z == this.z;
	}

	@Override
	public int hashCode() {
		return y ^ x << 10 ^ z << 21;
	}
}
