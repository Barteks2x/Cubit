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
package com.github.barteks2x.cubit.render.renderer;

import com.github.barteks2x.cubit.Player;
import com.github.barteks2x.cubit.block.Block;
import com.github.barteks2x.cubit.location.BlockLocation;
import com.github.barteks2x.cubit.location.Vec3D;
import com.github.barteks2x.cubit.location.Vec3I;
import com.github.barteks2x.cubit.render.Quad;
import com.github.barteks2x.cubit.render.TextureCoords;
import com.github.barteks2x.cubit.render.Vertex;
import com.github.barteks2x.cubit.render.block.BlockModelBuilder;
import com.github.barteks2x.cubit.render.block.BlockTextureManager;
import com.github.barteks2x.cubit.world.World;
import java.awt.Color;
import java.util.List;
import static org.lwjgl.opengl.GL11.GL_COMPILE;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glCallList;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDeleteLists;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glEndList;
import static org.lwjgl.opengl.GL11.glGenLists;
import static org.lwjgl.opengl.GL11.glNewList;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;

/**
 *
 * @author Bartosz Skrzypczak
 */
public class ChunkRenderer implements Renderer {

    private final Vec3I renderChunkSize;
    private final BlockLocation startLocation;
    private final World world;
    private final BlockTextureManager blockTextureManager;

    private int displayList = -1;

    public ChunkRenderer(Vec3I renderChunkSize, BlockLocation startLocation, BlockTextureManager texMgr, World world) {
        this.renderChunkSize = renderChunkSize;
        this.startLocation = startLocation;
        this.world = world;
        this.blockTextureManager = texMgr;
        this.update();
    }

    @Override
    public void render() {
        if(this.displayList == -1) {
            return;
        }
        int chunkXBase = this.startLocation.getX();
        int chunkYBase = this.startLocation.getY();
        int chunkZBase = this.startLocation.getZ();
        glPushMatrix();
        glTranslatef(chunkXBase, chunkYBase, chunkZBase);
        glCallList(displayList);
        glPopMatrix();
    }

    @Override
    public final void update() {
        if(this.displayList == -1) {
            this.displayList = glGenLists(1);
        }

        glNewList(this.displayList, GL_COMPILE);
        glBegin(GL_QUADS);

        final int maxX = this.renderChunkSize.getX();
        final int maxY = this.renderChunkSize.getY();
        final int maxZ = this.renderChunkSize.getZ();

        int chunkXBase = this.startLocation.getX();
        int chunkYBase = this.startLocation.getY();
        int chunkZBase = this.startLocation.getZ();

        int quads = 0;
        for(int x = 0; x < maxX; ++x) {
            for(int y = 0; y < maxY; ++y) {
                for(int z = 0; z < maxZ; ++z) {
                    int worldX = x + chunkXBase;
                    int worldY = y + chunkYBase;
                    int worldZ = z + chunkZBase;
                    quads += this.drawBlock(world, worldX, worldY, worldZ, x, y, z);
                }
            }
        }
        glEnd();
        glEndList();
        if(quads == 0) {
            glDeleteLists(this.displayList, 1);
            this.displayList = -1;
        }
    }

    private int drawBlock(World world, int wX, int wY, int wZ, int x, int y, int z) {
        Block block = this.world.getBlockAt(wX, wY, wZ);
        BlockModelBuilder modelBuilder = block.getModelBuilder();
        List<Quad> quads = modelBuilder.build(this.blockTextureManager, world, wX, wY, wZ);
        int q = 0;
        for(Quad quad : quads) {
            drawQuad(quad, x, y, z);
            q++;
        }
        return q;
    }

    private void drawQuad(Quad quad, int x, int y, int z) {
        TextureCoords texCoords = quad.getTextureCoords();
        List<Vertex> verticies = quad.getVerticies();

        assert verticies.size() == 4;

        float sizeInv = 1.0F / texCoords.getSize();

        float texX = texCoords.getX() * sizeInv;
        float texY = texCoords.getY() * sizeInv;

        float tx[] = new float[]{texX + sizeInv, texX + sizeInv, texX, texX};
        float ty[] = new float[]{texY, texY + sizeInv, texY + sizeInv, texY};

        int i = 0;
        for(Vertex vertex : verticies) {
            Color color = vertex.getColor();
            glColor3f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
            glTexCoord2f(tx[i], ty[i]);
            Vec3D pos = vertex.getPosition();
            glVertex3f((float)pos.getX() + x, (float)pos.getY() + y, (float)pos.getZ() + z);
            i++;
        }
    }

    @Override
    public void updateWindowDimensions(int width, int height) {
        //do nothing
    }

    @Override
    public void delete() {
        if(displayList == -1) {
            return;
        }
        glDeleteLists(displayList, 1);
    }

}
