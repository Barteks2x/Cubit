package com.github.barteks2x.openmine;

public class FloatPosition {
    protected float x, y, z;

    public FloatPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ() {
        return this.z;
    }

    public FloatPosition setX(float x) {
        this.x = x;
        return this;
    }

    public FloatPosition setY(float y) {
        this.y = y;
        return this;
    }

    public FloatPosition setZ(float z) {
        this.z = z;
        return this;
    }

    public boolean isValid(){
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        IntPosition other = (IntPosition)obj;
        return other.x == this.x && other.y == this.y && other.z == this.z;
    }

    @Override
    public int hashCode() {
        int x_ = Float.floatToIntBits(this.x);
        int y_ = Float.floatToIntBits(this.y);
        int z_ = Float.floatToIntBits(this.z);
        return y_ ^ x_ << 10 ^ z_ << 21;
    }
}
