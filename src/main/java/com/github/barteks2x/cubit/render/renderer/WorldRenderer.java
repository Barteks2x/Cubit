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
import com.github.barteks2x.cubit.location.BlockLocation;
import com.github.barteks2x.cubit.location.ChunkLocation;
import com.github.barteks2x.cubit.location.EntityLocation;
import com.github.barteks2x.cubit.location.Vec3I;
import com.github.barteks2x.cubit.render.Texture;
import com.github.barteks2x.cubit.render.block.BlockTextureManager;
import com.github.barteks2x.cubit.util.MathUtil;
import static com.github.barteks2x.cubit.util.MathUtil.mod;
import com.github.barteks2x.cubit.world.World;
import com.github.barteks2x.cubit.world.IncompleteBuildException;
import com.github.barteks2x.cubit.world.chunk.Chunk;
import java.nio.FloatBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_PROJECTION_MATRIX;
import static org.lwjgl.opengl.GL11.glGetFloat;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glLoadMatrix;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotated;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.util.glu.GLU.gluPerspective;

/**
 *
 * @author Bartosz Skrzypczak
 */
public class WorldRenderer implements Renderer {

    private final FloatBuffer perspectiveProjMatrix = BufferUtils.createFloatBuffer(16);
    private final int fov;
    private final float zNear, zFar;
    private final int viewDistanceBlocks;
    private final Vec3I renderChunkSize;
    private final Texture texture;
    private final BlockTextureManager blockTextureManager;

    private float rX, rY;
    private float playerX, playerY, playerZ;
    private ChunkLocation<?> playerChunkLocation;
    private World world;

    private final Map<ChunkLocation<?>, ChunkRenderer> chunkRenderers;

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

        this.chunkRenderers = new HashMap<ChunkLocation<?>, ChunkRenderer>(10000);
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

        for(ChunkRenderer renderer : chunkRenderers.values()) {
            renderer.render();
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
        this.playerChunkLocation = new ChunkLocation<Chunk>(null, this.renderChunkSize, playerBlockLocation);

        this.world = playerLocation.getWorld();

        this.createChunkRenderers(playerBlockLocation);
    }

    public void onBlockUpdate(BlockLocation location) {
        ChunkLocation<?> chunkPos = new ChunkLocation<Chunk>(null, this.renderChunkSize, location);
        Vec3I chunkSize = chunkPos.getChunkSize();
        this.chunkRenderers.get(chunkPos).update(null);
        if(mod(location.getX(), chunkSize.getX()) == 0) {
            this.chunkRenderers.get(chunkPos.add(-1, 0, 0)).update(null);
        }
        if(mod(location.getX(), chunkSize.getX()) == chunkSize.getX() - 1) {
            this.chunkRenderers.get(chunkPos.add(1, 0, 0)).update(null);
        }
        if(mod(location.getY(), chunkSize.getY()) == 0) {
            this.chunkRenderers.get(chunkPos.add(0, -1, 0)).update(null);
        }
        if(mod(location.getY(), chunkSize.getY()) == chunkSize.getY() - 1) {
            this.chunkRenderers.get(chunkPos.add(0, 1, 0)).update(null);
        }
        if(mod(location.getZ(), chunkSize.getZ()) == 0) {
            this.chunkRenderers.get(chunkPos.add(0, 0, -1)).update(null);
        }
        if(mod(location.getZ(), chunkSize.getZ()) == chunkSize.getZ() - 1) {
            this.chunkRenderers.get(chunkPos.add(0, 0, 1)).update(null);
        }
    }

    private void createChunkRenderers(BlockLocation center) {
        ChunkLocation<?> chunkLoc = new ChunkLocation<Chunk>(null, this.renderChunkSize, center);

        int xSize = this.renderChunkSize.getX();
        int ySize = this.renderChunkSize.getY();
        int zSize = this.renderChunkSize.getZ();

        int radiusX = MathUtil.ceil((double)this.viewDistanceBlocks / xSize);
        int radiusY = MathUtil.ceil((double)this.viewDistanceBlocks / ySize);
        int radiusZ = MathUtil.ceil((double)this.viewDistanceBlocks / zSize);

        Collection<ChunkLocation<?>> current =
                new HashSet<ChunkLocation<?>>((radiusX * 2 + 1) * (radiusY * 2 + 1) * (radiusZ * 2 + 1));

        for(int x = -radiusX; x <= radiusX; ++x) {
            for(int y = -radiusY; y <= radiusY; ++y) {
                for(int z = -radiusZ; z <= radiusZ; ++z) {
                    ChunkLocation<?> loc = chunkLoc.add(x, y, z);
                    current.add(loc);
                    if(this.chunkRenderers.containsKey(loc)) {
                        continue;
                    }
                    BlockLocation start =
                            new BlockLocation(world, loc.getX() * xSize, loc.getY() * ySize, loc.getZ() * zSize);
                    this.chunkRenderers.put(loc, new ChunkRenderer(renderChunkSize, start, blockTextureManager, world));
                }
            }
        }
        for(ChunkLocation<?> loc : new HashSet<ChunkLocation<?>>(this.chunkRenderers.keySet())) {
            if(!current.contains(loc)) {
                this.chunkRenderers.remove(loc);
            }
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
    public void delete() {
        for(ChunkRenderer renderer : this.chunkRenderers.values()) {
            renderer.delete();
        }
        this.chunkRenderers.clear();
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
        private BlockTextureManager blockTextureManager;

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

        public WorldRendererBuilder setTexture(BlockTextureManager texMgr) {
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
