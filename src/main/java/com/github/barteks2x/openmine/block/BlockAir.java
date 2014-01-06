package com.github.barteks2x.openmine.block;

public class BlockAir extends Block {

    public BlockAir(int i) {
        super(i, "Air");
    }
    
    @Override
    public boolean renderBlock(){
        return false;
    }
    
    @Override
    public boolean isTransparent(){
        return true;
    }

    @Override
    public int getTextureForSide(int side) {
        return 0;
    }
}
