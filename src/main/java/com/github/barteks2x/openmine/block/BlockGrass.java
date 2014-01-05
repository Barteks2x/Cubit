package com.github.barteks2x.openmine.block;

public class BlockGrass extends Block {

    public BlockGrass(int id) {
        super(id, "Grass");
    }

    @Override
    public int getTextureForSide(int side) {
        if(side == 2) {
            return 1;
        }
        if(side == 3) {
            return 3;
        }
        return 2;
    }
}
