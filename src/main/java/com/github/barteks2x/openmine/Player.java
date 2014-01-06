package com.github.barteks2x.openmine;

public class Player {

    protected final FloatPosition location;
    protected float rx, ry;
    protected BlockPosition selectedBlock = null;
    protected BlockPosition blockOnSelected = null;

    public Player() {
        this.location = new FloatPosition(0, 0, 0);
        rx = 0;
        ry = 0;
    }

    public float getX() {
        return location.getX();
    }

    public void setX(float x) {
        this.location.setX(x);
    }

    public float getY() {
        return location.getY();
    }

    public void setY(float y) {
        this.location.setY(y);
    }

    public float getZ() {
        return location.getZ();
    }

    public void setZ(float z) {
        this.location.setZ(z);
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
    
    public FloatPosition getLocation(){
        return this.location;
    }
}
