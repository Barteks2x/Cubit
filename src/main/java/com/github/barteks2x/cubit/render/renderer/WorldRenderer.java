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
import com.github.barteks2x.cubit.location.ChunkLocation;
import com.github.barteks2x.cubit.location.EntityLocation;
import com.github.barteks2x.cubit.location.Vec3D;
import com.github.barteks2x.cubit.location.Vec3I;
import com.github.barteks2x.cubit.render.Quad;
import com.github.barteks2x.cubit.render.Texture;
import com.github.barteks2x.cubit.render.TextureCoords;
import com.github.barteks2x.cubit.render.Vertex;
import com.github.barteks2x.cubit.render.block.IBlockModelBuilder;
import com.github.barteks2x.cubit.render.block.IBlockTextureManager;
import com.github.barteks2x.cubit.util.MathUtil;
import static com.github.barteks2x.cubit.util.MathUtil.floor;
import static com.github.barteks2x.cubit.util.MathUtil.mod;
import com.github.barteks2x.cubit.world.IWorld;
import com.github.barteks2x.cubit.world.IncompleteBuildException;
import com.github.barteks2x.cubit.world.chunk.IChunk;
import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.GL_COMPILE;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_PROJECTION_MATRIX;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glCallList;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDeleteLists;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glEndList;
import static org.lwjgl.opengl.GL11.glGenLists;
import static org.lwjgl.opengl.GL11.glGetFloat;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glLoadMatrix;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glNewList;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotated;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.util.glu.GLU.gluPerspective;

/**
 *
 * @author Bartosz Skrzypczak
 */
public class WorldRenderer implements IRenderer<Player> {

    private final FloatBuffer perspectiveProjMatrix = BufferUtils.createFloatBuffer(16);
    private final int fov;
    private final float zNear, zFar;
    private final int viewDistanceBlocks;
    private final Vec3I renderChunkSize;
    private final Texture texture;
    private final IBlockTextureManager blockTextureManager;

    private float rX, rY;
    private float playerX, playerY, playerZ;
    private ChunkLocation<?> playerChunkLocation;
    private IWorld world;

    private final Map<ChunkLocation<?>, Integer> chunkDisplayLists;

    private WorldRenderer(WorldRendererBuilder builder) {
        this.zNear = builder.zNear;
        this.renderChunkSize = builder.renderChunkSize;
        //sqrt(x*x+y*y+z*z), x=y=z=viewDist
        this.zFar = (float)(builder.viewDistBlocks * Math.sqrt(3));
        this.viewDistanceBlocks = builder.viewDistBlocks;
        this.fov = builder.fov;
        this.texture = builder.texture;
        this.blockTextureManager = builder.blockTextureManager;

        this.updateWindowDimensions(builder.width, builder.height);

        this.chunkDisplayLists = new HashMap<ChunkLocation<?>, Integer>(20000);
    }

    @Override
    public void render() {
        this.texture.bind();

        glMatrixMode(GL_PROJECTION);
        glLoadMatrix(perspectiveProjMatrix);
        glMatrixMode(GL_MODELVIEW);

        glLoadIdentity();

        glRotated(rY, 1, 0, 0);
        glRotated(rX, 0, 1, 0);
        glTranslatef(-playerX, -playerY, -playerZ);

        int xStep = this.renderChunkSize.getX();
        int yStep = this.renderChunkSize.getY();
        int zStep = this.renderChunkSize.getZ();

        int radiusX = floor(this.viewDistanceBlocks / (double)xStep) * xStep + xStep;
        int radiusY = floor(this.viewDistanceBlocks / (double)yStep) * yStep + yStep;
        int radiusZ = floor(this.viewDistanceBlocks / (double)zStep) * zStep + zStep;

        for(int x = -radiusX; x <= radiusX; x += xStep) {
            for(int y = -radiusY; y <= radiusY; y += yStep) {
                for(int z = -radiusZ; z <= radiusZ; z += yStep) {
                    int chunkX = floor(x / (double)xStep);
                    int chunkY = floor(y / (double)yStep);
                    int chunkZ = floor(z / (double)zStep);

                    ChunkLocation<?> loc = playerChunkLocation.add(chunkX, chunkY, chunkZ);
                    int d = getChunkDisplayList(loc);
                    if(d < 1) {
                        continue;
                    }
                    glPushMatrix();
                    glTranslatef(loc.getX() * xStep, loc.getY() * yStep, loc.getZ() * zStep);
                    glCallList(d);
                    glPopMatrix();
                }
            }
        }
    }

    @Override
    public void update(Player player) {
        this.rX = (float)player.getRx();
        this.rY = (float)player.getRy();

        this.playerX = (float)player.getX();
        this.playerY = (float)player.getY();
        this.playerZ = (float)player.getZ();

        EntityLocation playerLocation = player.getLocation();
        BlockLocation playerBlockLocation = new BlockLocation(playerLocation);
        this.playerChunkLocation = new ChunkLocation<IChunk>(null, this.renderChunkSize, playerBlockLocation);

        this.world = playerLocation.getWorld();

        this.generateDisplayLists(playerBlockLocation);
    }

    public void onBlockUpdate(BlockLocation location) {
        ChunkLocation<?> chunkPos = new ChunkLocation<IChunk>(null, this.renderChunkSize, location);
        Vec3I chunkSize = chunkPos.getChunkSize();
        buildChunkDisplayList(chunkPos);
        if(mod(location.getX(), chunkSize.getX()) == 0) {
            buildChunkDisplayList(chunkPos.add(-1, 0, 0));
        }
        if(mod(location.getX(), chunkSize.getX()) == chunkSize.getX() - 1) {
            buildChunkDisplayList(chunkPos.add(1, 0, 0));
        }
        if(mod(location.getY(), chunkSize.getY()) == 0) {
            buildChunkDisplayList(chunkPos.add(0, -1, 0));
        }
        if(mod(location.getY(), chunkSize.getY()) == chunkSize.getY() - 1) {
            buildChunkDisplayList(chunkPos.add(0, 1, 0));
        }
        if(mod(location.getZ(), chunkSize.getZ()) == 0) {
            buildChunkDisplayList(chunkPos.add(0, 0, -1));
        }
        if(mod(location.getZ(), chunkSize.getZ()) == chunkSize.getZ() - 1) {
            buildChunkDisplayList(chunkPos.add(0, 0, 1));
        }
    }

    private int getChunkDisplayList(ChunkLocation<?> pos) {
        Integer d = this.chunkDisplayLists.get(pos);
        return d == null ? -1 : d;
    }

    private void generateDisplayLists(BlockLocation center) {
        Set<ChunkLocation<?>> chunkDisplayListsToCompile = new HashSet<ChunkLocation<?>>(20000);
        ChunkLocation<?> chunkLoc = new ChunkLocation<IChunk>(null, this.renderChunkSize, center);

        int radiusX = MathUtil.ceil((double)this.viewDistanceBlocks / this.renderChunkSize.getX());
        int radiusY = MathUtil.ceil((double)this.viewDistanceBlocks / this.renderChunkSize.getY());
        int radiusZ = MathUtil.ceil((double)this.viewDistanceBlocks / this.renderChunkSize.getZ());

        for(int x = -radiusX; x <= radiusX; ++x) {
            for(int y = -radiusY; y <= radiusY; ++y) {
                for(int z = -radiusZ; z <= radiusZ; ++z) {
                    ChunkLocation<?> loc = chunkLoc.add(x, y, z);
                    if(getChunkDisplayList(loc) != -1) {
                        continue;
                    }
                    chunkDisplayListsToCompile.add(loc);
                }
            }
        }
        for(ChunkLocation<?> position : chunkDisplayListsToCompile) {
            buildChunkDisplayList(position);
        }
    }

    private void buildChunkDisplayList(ChunkLocation<?> pos) {
        int displayList = getChunkDisplayList(pos);
        if(displayList == -1) {
            displayList = glGenLists(1);
            this.chunkDisplayLists.put(pos, displayList);
        }

        this.texture.bind();
        glNewList(displayList, GL_COMPILE);
        glBegin(GL_QUADS);

        final int maxX = this.renderChunkSize.getX();
        final int maxY = this.renderChunkSize.getY();
        final int maxZ = this.renderChunkSize.getZ();

        int chunkXBase = pos.getX() * maxX;
        int chunkYBase = pos.getY() * maxY;
        int chunkZBase = pos.getZ() * maxZ;
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
            glDeleteLists(chunkDisplayLists.get(pos), 1);
            chunkDisplayLists.put(pos, 0);
        }
    }

    private int drawBlock(IWorld world, int wX, int wY, int wZ, int x, int y, int z) {
        Block block = this.world.getBlockAt(wX, wY, wZ);
        IBlockModelBuilder modelBuilder = block.getModelBuilder();
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
    public final void updateWindowDimensions(int width, int height) {
        float aspectRatio = (float)width / height;
        glPushMatrix();
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(fov, aspectRatio, zNear, zFar);
        glViewport(0, 0, width, height);
        glMatrixMode(GL_MODELVIEW);
        glPopMatrix();

        glGetFloat(GL_PROJECTION_MATRIX, perspectiveProjMatrix);
    }

    @Override
    public void onExit() {
        for(Integer i : chunkDisplayLists.values()) {
            if(i != null) {
                glDeleteLists(i, 1);
            }
        }
        this.texture.delete();
    }

    public static class WorldRendererBuilder {

        private Integer fov;
        private Float zNear;
        private Integer width;
        private Integer height;
        private Integer viewDistBlocks;
        private Vec3I renderChunkSize;
        private Texture texture;
        private IBlockTextureManager blockTextureManager;

        public WorldRendererBuilder setFov(int fov) {
            this.fov = fov;
            return this;
        }

        public WorldRendererBuilder setzNear(float zNear) {
            this.zNear = zNear;
            return this;
        }

        public WorldRendererBuilder setWidth(int width) {
            this.width = width;
            return this;
        }

        public WorldRendererBuilder setHeight(int height) {
            this.height = height;
            return this;
        }

        public WorldRendererBuilder setViewDistanceBlocks(int dist) {
            this.viewDistBlocks = dist;
            return this;
        }

        public WorldRendererBuilder setRenderChunkSize(Vec3I size) {
            this.renderChunkSize = size;
            return this;
        }

        public WorldRendererBuilder setTexture(Texture texture) {
            this.texture = texture;
            return this;
        }

        public WorldRendererBuilder setTexture(IBlockTextureManager texMgr) {
            this.blockTextureManager = texMgr;
            return this;
        }

        public WorldRenderer build() {
            if(fov == null ||
                    zNear == null ||
                    viewDistBlocks == null ||
                    width == null ||
                    height == null ||
                    renderChunkSize == null ||
                    texture == null ||
                    blockTextureManager == null) {
                throw new IncompleteBuildException(
                        "WorldRenderer requires fov, zNear, viewDist, width, height, renderChunkSize, texture and BlockTextureManager");
            }
            return new WorldRenderer(this);
        }
    }

    public static WorldRendererBuilder newRenderer() {
        return new WorldRendererBuilder();
    }
}
