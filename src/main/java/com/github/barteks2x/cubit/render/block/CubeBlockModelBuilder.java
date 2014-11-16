/* 
 * The MIT License
 *
 * Copyright 2014 Bartosz Skrzypczak.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.barteks2x.cubit.render.block;

import com.github.barteks2x.cubit.block.Block;
import com.github.barteks2x.cubit.block.CubeBlockSide;
import com.github.barteks2x.cubit.location.Vec3D;
import com.github.barteks2x.cubit.render.Quad;
import com.github.barteks2x.cubit.render.TextureCoords;
import com.github.barteks2x.cubit.render.Vertex;
import com.github.barteks2x.cubit.world.IWorld;
import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * General Cube block renderer.
 */
public class CubeBlockModelBuilder implements IBlockModelBuilder {

    private final CubeBlockSide sides[] = new CubeBlockSide[]{
        CubeBlockSide.NORTH,
        CubeBlockSide.SOUTH,
        CubeBlockSide.UP,
        CubeBlockSide.DOWN,
        CubeBlockSide.EAST,
        CubeBlockSide.WEST
    };

    //[side][vertex]
    private final Vec3D vecs[][] = new Vec3D[][]{
        {vec(1, 1, 1), vec(1, 0, 1), vec(1, 0, 0), vec(1, 1, 0)},//NORTH
        {vec(0, 1, 0), vec(0, 0, 0), vec(0, 0, 1), vec(0, 1, 1)},//SOUTH
        {vec(0, 1, 1), vec(1, 1, 1), vec(1, 1, 0), vec(0, 1, 0)},//UP
        {vec(0, 0, 0), vec(1, 0, 0), vec(1, 0, 1), vec(0, 0, 1)},//DOWN
        {vec(0, 1, 1), vec(0, 0, 1), vec(1, 0, 1), vec(1, 1, 1)},//EAST
        {vec(1, 1, 0), vec(1, 0, 0), vec(0, 0, 0), vec(0, 1, 0)}//WEST
    };

    private final Color colors[] = new Color[]{
        new Color(0.7F, 0.7F, 0.7F),//NORTH
        new Color(0.7F, 0.7F, 0.7F),//SOUTH
        new Color(1.0F, 1.0F, 1.0F),//UP
        new Color(0.6F, 0.6F, 0.6F),//DOWN
        new Color(0.8F, 0.8F, 0.8F),//EAST
        new Color(0.8F, 0.8F, 0.8F)//WEST
    };

    @Override
    public List<Quad> build(IBlockTextureManager textureMgr, IWorld world,
            int worldX, int worldY, int worldZ) {

        boolean renderSides[] = new boolean[]{
            world.getBlockAt(worldX + 1, worldY, worldZ).isTransparent(),
            world.getBlockAt(worldX - 1, worldY, worldZ).isTransparent(),
            world.getBlockAt(worldX, worldY + 1, worldZ).isTransparent(),
            world.getBlockAt(worldX, worldY - 1, worldZ).isTransparent(),
            world.getBlockAt(worldX, worldY, worldZ + 1).isTransparent(),
            world.getBlockAt(worldX, worldY, worldZ - 1).isTransparent()
        };

        List<Quad> quads = new LinkedList<Quad>();
        Block block = world.getBlockAt(worldX, worldY, worldZ);

        for (int i = 0; i < sides.length; i++) {
            if (!renderSides[i]) {
                continue;
            }
            String tex = block.getTextures()[block.getTextureForSide(sides[i].getVector())];
            TextureCoords texCoords = textureMgr.getTextureCoordsForName(tex);
            
            List<Vertex> verticies = new ArrayList<Vertex>(4);
            for (int j = 0; j < 4; j++) {
                verticies.add(new Vertex(this.vecs[i][j], this.colors[i], tex));
            }
            quads.add(new Quad(texCoords, verticies));
        }
        return quads;
    }

    private static Vec3D vec(int x, int y, int z) {
        return new Vec3D(x, y, z);
    }
}
