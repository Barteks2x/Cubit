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
package com.github.barteks2x.cubit;

import com.github.barteks2x.cubit.render.TextureLoader;
import com.github.barteks2x.cubit.render.Texture;
import com.github.barteks2x.cubit.world.chunk.ChunkCube8Factory;
import com.github.barteks2x.cubit.block.Block;
import com.github.barteks2x.cubit.world.generator.HeightmapChunkGenerator;
import com.github.barteks2x.cubit.location.BlockLocation;
import com.github.barteks2x.cubit.location.ChunkLocation;
import com.github.barteks2x.cubit.location.EntityLocation;
import com.github.barteks2x.cubit.location.Vec3D;
import com.github.barteks2x.cubit.location.Vec3I;
import com.github.barteks2x.cubit.render.Quad;
import com.github.barteks2x.cubit.render.TextureCoords;
import com.github.barteks2x.cubit.render.Vertex;
import com.github.barteks2x.cubit.render.block.IBlockModelBuilder;
import com.github.barteks2x.cubit.render.block.IBlockTextureManager;
import com.github.barteks2x.cubit.render.block.SpritesheetTextureManager;
import com.github.barteks2x.cubit.util.MathUtil;
import com.github.barteks2x.cubit.util.logging.LoggerFactory;
import com.github.barteks2x.cubit.world.CubitWorld;
import com.github.barteks2x.cubit.world.chunk.ChunkCube8;
import com.github.barteks2x.cubit.world.chunk.IChunk;
import com.github.barteks2x.cubit.world.chunk.IChunkFactory;
import com.github.barteks2x.cubit.world.IWorld;
import com.github.barteks2x.cubit.world.chunkloader.IChunkLoader;
import com.github.barteks2x.cubit.world.chunkloader.RAMChunkLoader;
import java.awt.Color;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.GL_ALPHA_TEST;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_COMPILE;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_PROJECTION_MATRIX;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glCallList;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDeleteLists;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glEndList;
import static org.lwjgl.opengl.GL11.glGenLists;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glGetFloat;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glLoadMatrix;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glNewList;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotated;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.util.glu.GLU.gluErrorString;
import static org.lwjgl.util.glu.GLU.gluPerspective;

public class CubitMain<Chunk extends IChunk, World extends CubitWorld<Chunk>> {

    private static final Logger logger = LoggerFactory.getLogger(CubitMain.class);

    private static CubitMain<ChunkCube8, CubitWorld<ChunkCube8>> instance;
    private static final int TICKRATE = 20;
    private static final long UPDATE_TIME = 1000000000L / TICKRATE;
    private int renderDistance = 64;
    //OpenGL
    private final String title;
    private int fov;
    private double aspectRatio;
    private double zNear, zFar;
    private int width, height;
    private boolean isRunning = true;
    private final FloatBuffer perspectiveProjMatrix = BufferUtils.createFloatBuffer(16);
    private final FloatBuffer orthographicProjMatrix = BufferUtils.createFloatBuffer(16);
    //Rendering
    private final Map<ChunkLocation<Chunk>, Integer> chunkDisplayLists;
    private int selectionDisplayList;
    //movement
    private final Timer timer;
    final double mouseSensitivity;
    private boolean grabMouse;
    private final Player player;
    //Textures and fonts
    private BitmapFont font;
    private Texture tex;
    private double time;
    public int placeid;

    private final World world;

    private long lastUpdateTime = System.nanoTime();

    private final IBlockTextureManager textureManager;

    private final IChunkFactory<Chunk> chunkFactory;

    public CubitMain(World world, IChunkFactory<Chunk> chunkFactory, IChunkLoader<Chunk> chunkLoader) {
        this.chunkFactory = chunkFactory;
        this.title = "Cubit " + Version.getVersion();

        this.world = world;//BasicWorld.newWorld().setChunkLoader(chunkLoader).setSeed(seed).build();

        timer = new Timer();
        player = new Player(world);
        mouseSensitivity = 0.6F;

        this.world.joinPlayer(player);

        this.chunkDisplayLists = new HashMap<ChunkLocation<Chunk>, Integer>(10000);
        this.textureManager = new SpritesheetTextureManager();
    }

    public FloatBuffer asFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        return (FloatBuffer)buffer.put(data).flip();
    }

    private void start(int width, int height) {
        this.width = width;
        this.height = height;
        this.fov = 60;
        this.aspectRatio = (double)width / height;
        zNear = 0.1F;
        zFar = 200F;
        initDisplay();
        initGL();
        loadFonts();
        loadTextures();
        Vec3I spawn = world.getSpawnPoint();
        player.setPosition(new EntityLocation(world, spawn));
        initDisplayLists();
        while(isRunning) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            timer.nextFrame();
            if(System.nanoTime() - this.lastUpdateTime > UPDATE_TIME) {
                input();
                this.lastUpdateTime = System.nanoTime();
                world.tick(TICKRATE);
            }

            glLoadIdentity();

            tex.bind();
            this.generateDisplayListsNearLocation(new BlockLocation(player.getLocation()));
            renderChunks();
            renderSelection();
            renderText();
            Display.update();
            errorCheck("Main");
            if(Display.isCloseRequested()) {
                isRunning = false;
            }
        }
        onClose(0);
    }

    private void renderChunks() {
        glRotated(player.getRy(), 1, 0, 0);
        glRotated(player.getRx(), 0, 1, 0);
        glTranslatef((float)-player.getX(), (float)-player.getY(), (float)-player.getZ());

        ChunkLocation<Chunk> playerChunkLoc = this.getPlayerChunkLocation();

        Vec3I chunkSize = playerChunkLoc.getChunkSize();

        int xStep = chunkSize.getX();
        int yStep = chunkSize.getY();
        int zStep = chunkSize.getZ();

        //int playerX = playerChunkLoc.getX() * xStep;
        //int playerY = playerChunkLoc.getY() * yStep;
        //int playerZ = playerChunkLoc.getZ() * zStep;
        int radiusX = MathUtil.floor(renderDistance / (double)xStep) * xStep + xStep;
        int radiusY = MathUtil.floor(renderDistance / (double)yStep) * yStep + yStep;
        int radiusZ = MathUtil.floor(renderDistance / (double)zStep) * zStep + zStep;

        for(int x = -radiusX; x <= radiusX; x += xStep) {
            for(int y = -radiusY; y <= radiusY; y += yStep) {
                for(int z = -radiusZ; z <= radiusZ; z += yStep) {
                    int chunkX = MathUtil.floor(x / (double)xStep);
                    int chunkY = MathUtil.floor(y / (double)yStep);
                    int chunkZ = MathUtil.floor(z / (double)zStep);
                    //System.out.printf("(%d, %d, %d)\n", chunkX, chunkY, chunkZ);
                    ChunkLocation<Chunk> loc = playerChunkLoc.add(chunkX, chunkY, chunkZ);
                    int d = getChunkDisplayList(loc);
                    if(d == -1) {
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

    private void renderSelection() {
        if(player.getSelectedBlock() == null) {
            return;
        }
        glBindTexture(GL_TEXTURE_2D, 0);
        glColor4f(1F, 1F, 1F, (float)Math.sin(time * .005F) / 4F + .75F);
        glPushMatrix();
        BlockLocation pos = player.getSelectedBlock();
        glTranslatef(pos.getX(), pos.getY(), pos.getZ());
        glCallList(selectionDisplayList);
        glPopMatrix();
    }

    private void renderText() {
        glPushMatrix();
        glMatrixMode(GL_PROJECTION);
        glLoadMatrix(orthographicProjMatrix);
        glMatrixMode(GL_MODELVIEW);

        glLoadIdentity();

        BitmapFont font = this.font.bind();

        Color fpsColor;
        int fps = timer.getFPS();
        if(fps <= 1) {
            fpsColor = Color.RED.darker();
        } else if(fps <= 10) {
            fpsColor = Color.RED;
        } else if(fps <= 20) {
            fpsColor = Color.ORANGE.darker();
        } else if(fps < 28) {
            fpsColor = Color.ORANGE;
        } else if(fps <= 35) {
            fpsColor = Color.YELLOW;
        } else if(fps <= 58) {
            fpsColor = Color.GREEN;
        } else if(fps <= 98) {
            fpsColor = Color.GREEN.brighter();
        } else {
            fpsColor = Color.WHITE;
        }
        int y = 0;
        font.drawString(0, y, 1, fpsColor, String.format("FPS: %d", fps));
        y += 16;

        double playerX = player.getX();
        double playerY = player.getY();
        double playerZ = player.getZ();

        ChunkLocation<? extends IChunk> chunkLoc = this.getPlayerChunkLocation();
        int chunkX = chunkLoc.getX();
        int chunkY = chunkLoc.getY();
        int chunkZ = chunkLoc.getZ();
        font.drawString(0, y, 1, Color.WHITE, String.format("Location(%.3f, %.3f, %.3f)", playerX, playerY, playerZ));
        y += 16;
        font.drawString(0, y, 1, Color.WHITE, String.format("PlayerChunk(%d, %d, %d)", chunkX, chunkY, chunkZ));
        y += 16;

        BlockLocation selectedPos = player.getSelectedBlock();
        String selx = selectedPos != null ? String.format("%d", selectedPos.getX()) : "no selection";
        String sely = selectedPos != null ? String.format("%d", selectedPos.getY()) : "no selection";
        String selz = selectedPos != null ? String.format("%d", selectedPos.getZ()) : "no selection";
        font.drawString(0, y, 1, Color.WHITE, String.format("Selection(%s, %s, %s)", selx, sely, selz));
        y += 16;

        BlockLocation blockOnSelected = player.getBlockOnSelectedBlock();
        selx = blockOnSelected != null ? String.format("%d", blockOnSelected.getX()) : "no selection";
        sely = blockOnSelected != null ? String.format("%d", blockOnSelected.getY()) : "no selection";
        selz = blockOnSelected != null ? String.format("%d", blockOnSelected.getZ()) : "no selection";
        font.drawString(0, y, 1, Color.WHITE, String.format("BlockOnSelection(%s, %s, %s)", selx, sely, selz));
        y += 16;

        Block placeBlock = this.world.getBlockRegistry().fromID(placeid);
        String place = placeBlock == null ? "No block to place" : placeBlock.toString();
        font.drawString(0, y, 1, Color.WHITE, String.format("PlaceBlock(%s)", place));

        font.drawString((Display.getWidth() / 2F) / 3F - 8, (Display.getHeight() / 2F) / 3F - 8, 3, Color.ORANGE, "X");
        glMatrixMode(GL_PROJECTION);
        glLoadMatrix(perspectiveProjMatrix);
        glMatrixMode(GL_MODELVIEW);
        glPopMatrix();
    }

    private void initDisplay() {
        try {
            Display.setTitle(title);
            Display.setDisplayMode(new DisplayMode(width, height));
            Display.create();
        } catch(LWJGLException ex) {
            Logger.getLogger(CubitMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initGL() {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(fov, (float)aspectRatio, (float)zNear, (float)zFar);
        glViewport(0, 0, width, height);
        glMatrixMode(GL_MODELVIEW);

        glGetFloat(GL_PROJECTION_MATRIX, perspectiveProjMatrix);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, width, height, 0, -1, 1);
        glGetFloat(GL_PROJECTION_MATRIX, orthographicProjMatrix);
        glLoadMatrix(perspectiveProjMatrix);
        glMatrixMode(GL_MODELVIEW);

        glEnable(GL_TEXTURE_2D);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glEnable(GL_DEPTH_TEST);

        glEnable(GL_BLEND);
        glEnable(GL_ALPHA_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glClearColor(0.3F, 0.3F, 1F, 1F);

        Mouse.setGrabbed(true);
    }

    private void initDisplayLists() {
        selectionDisplayList = glGenLists(1);
        glNewList(selectionDisplayList, GL_COMPILE);
        glBegin(GL_QUADS);
        int x = 0, y = 0, z = 0;
        float m = 0.001F;
        glVertex3f(x + 1 + m, y + 1 + m, z + 1 + m);
        glVertex3f(x + 1 + m, y - m, z + 1 + m);
        glVertex3f(x + 1 + m, y - m, z - m);
        glVertex3f(x + 1 + m, y + 1 + m, z - m);

        glVertex3f(x - m, y + 1 + m, z - m);
        glVertex3f(x - m, x - m, z - m);
        glVertex3f(x - m, x - m, z + 1 + m);
        glVertex3f(x - m, y + 1 + m, z + 1 + m);

        glVertex3f(x - m, y + 1 + m, z + 1 + m);
        glVertex3f(x + 1 + m, y + 1 + m, z + 1 + m);
        glVertex3f(x + 1 + m, y + 1 + m, z - m);
        glVertex3f(x - m, y + 1 + m, z - m);

        glVertex3f(x - m, x - m, z - m);
        glVertex3f(x + 1 + m, x - m, z - m);
        glVertex3f(x + 1 + m, x - m, z + 1 + m);
        glVertex3f(x - m, x - m, z + 1 + m);

        glVertex3f(x - m, x - m, z + 1 + m);
        glVertex3f(x + 1 + m, x - m, z + 1 + m);
        glVertex3f(x + 1 + m, y + 1 + m, z + 1 + m);
        glVertex3f(x - m, y + 1 + m, z + 1 + m);

        glVertex3f(x - m, y + 1 + m, z - m);
        glVertex3f(x + 1 + m, y + 1 + m, z - m);
        glVertex3f(x + 1 + m, x - m, z - m);
        glVertex3f(x - m, x - m, z - m);
        glEnd();
        glEndList();
    }

    private void generateDisplayListsNearLocation(BlockLocation pos) {
        Set<ChunkLocation<Chunk>> chunkDisplayListsToCompile = new HashSet<ChunkLocation<Chunk>>(20000);
        ChunkLocation<Chunk> chunkLoc = new ChunkLocation<Chunk>(world, this.chunkFactory.getChunkSize(), pos);
        int radiusX = renderDistance / chunkFactory.getChunkSize().getX();
        int radiusY = renderDistance / chunkFactory.getChunkSize().getY();
        int radiusZ = renderDistance / chunkFactory.getChunkSize().getZ();
        for(int x = -radiusX; x <= radiusX; ++x) {
            for(int y = -radiusY; y <= radiusY; ++y) {
                for(int z = -radiusZ; z <= radiusZ; ++z) {
                    ChunkLocation<Chunk> loc = chunkLoc.add(x, y, z);
                    if(getChunkDisplayList(loc) != -1) {
                        continue;
                    }
                    if(this.world.getChunkAt(loc) == null) {
                        continue;
                    }
                    chunkDisplayListsToCompile.add(loc);
                }
            }
        }
        for(ChunkLocation<Chunk> position : chunkDisplayListsToCompile) {
            if(world.isChunkLoaded(position)) {
                buildChunkDisplayList(position);
            }
        }
    }

    private void onClose(int i) {
        Collection<Integer> lists = chunkDisplayLists.values();
        for(Integer x : lists) {
            glDeleteLists(x, 1);
        }
        System.exit(i);
    }

    private void input() {
        if(Mouse.isGrabbed() != grabMouse) {
            Mouse.setGrabbed(grabMouse);
        }
        player.update();
    }

    public void blockRenderUpdate(BlockLocation blockPos) {
        ChunkLocation<Chunk> chunkPos = new ChunkLocation<Chunk>(world, this.chunkFactory.getChunkSize(), blockPos);
        Vec3I chunkSize = chunkPos.getChunkSize();
        buildChunkDisplayList(chunkPos);
        if(MathUtil.modP(blockPos.getX(), chunkSize.getX()) == 0) {
            buildChunkDisplayList(chunkPos.add(-1, 0, 0));
        }
        if(MathUtil.modP(blockPos.getX(), chunkSize.getX()) == chunkSize.getX() - 1) {
            buildChunkDisplayList(chunkPos.add(1, 0, 0));
        }
        if(MathUtil.modP(blockPos.getY(), chunkSize.getY()) == 0) {
            buildChunkDisplayList(chunkPos.add(0, -1, 0));
        }
        if(MathUtil.modP(blockPos.getY(), chunkSize.getY()) == chunkSize.getY() - 1) {
            buildChunkDisplayList(chunkPos.add(0, 1, 0));
        }
        if(MathUtil.modP(blockPos.getZ(), chunkSize.getZ()) == 0) {
            buildChunkDisplayList(chunkPos.add(0, 0, -1));
        }
        if(MathUtil.modP(blockPos.getZ(), chunkSize.getZ()) == chunkSize.getZ() - 1) {
            buildChunkDisplayList(chunkPos.add(0, 0, 1));
        }
    }

    private void buildChunkDisplayList(ChunkLocation<Chunk> pos) {
        int displayList = getChunkDisplayList(pos);
        if(displayList == -1) {
            displayList = glGenLists(1);
            chunkDisplayLists.put(pos, displayList);
        }

        tex.bind();
        glNewList(displayList, GL_COMPILE);
        glBegin(GL_QUADS);

        Vec3I chunkSize = this.chunkFactory.getChunkSize();

        final int maxX = chunkSize.getX();
        final int maxY = chunkSize.getY();
        final int maxZ = chunkSize.getZ();

        int chunkXBase = pos.getX() * maxX;
        int chunkYBase = pos.getY() * maxY;
        int chunkZBase = pos.getZ() * maxZ;

        for(int x = 0; x < maxX; ++x) {
            for(int y = 0; y < maxY; ++y) {
                for(int z = 0; z < maxZ; ++z) {
                    int worldX = x + chunkXBase;
                    int worldY = y + chunkYBase;
                    int worldZ = z + chunkZBase;
                    this.drawBlock(world, worldX, worldY, worldZ, x, y, z);
                }
            }
        }
        glEnd();
        glEndList();
    }

    private void loadTextures() {
        try {
            tex = TextureLoader.loadTexture(Thread.currentThread().
                    getContextClassLoader().getResourceAsStream("texture.png"));
        } catch(IOException ex) {
            Logger.getLogger(CubitMain.class.getName()).log(Level.SEVERE, null, ex);
            onClose(-1);
        }
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    }

    @SuppressWarnings("unchecked")
    private void loadFonts() {
        font = new BitmapFont("Font256.png");
        font.init();
    }

    private void errorCheck(String msg) {
        int e = glGetError();
        if(e != GL_NO_ERROR) {
            Logger.getLogger(this.getClass().getName()).
                    log(Level.SEVERE, "OpenGL Error! {2} {0} - {1}", new Object[]{e, gluErrorString(e), msg});
        }
    }

    private int getChunkDisplayList(ChunkLocation<Chunk> pos) {
        Integer d = chunkDisplayLists.get(pos);
        return d == null ? -1 : d;
    }

    void pauseInvert() {
        this.grabMouse = !grabMouse;
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

    private void drawBlock(IWorld world, int wX, int wY, int wZ, int x, int y, int z) {
        Block block = this.world.getBlockAt(wX, wY, wZ);
        IBlockModelBuilder modelBuilder = block.getModelBuilder();
        List<Quad> quads = modelBuilder.build(textureManager, world, wX, wY, wZ);
        for(Quad quad : quads) {
            drawQuad(quad, x, y, z);
        }
    }

    private ChunkLocation<Chunk> getPlayerChunkLocation() {
        BlockLocation blockLoc = new BlockLocation(player.getLocation());
        return new ChunkLocation<Chunk>(world, this.chunkFactory.getChunkSize(), blockLoc);
    }

    public static CubitMain<ChunkCube8, CubitWorld<ChunkCube8>> getGame() {
        return instance;
    }

    public static void main(String args[]) throws IOException {
        LoggerFactory.initLoggers();
        long seed = System.nanoTime();
        IChunkFactory<ChunkCube8> factory = new ChunkCube8Factory();

        IChunkLoader<ChunkCube8> chunkLoader = new RAMChunkLoader<ChunkCube8>();
        IChunkLoader<ChunkCube8> chunkGenerator = new HeightmapChunkGenerator<ChunkCube8>(factory, seed);
        chunkLoader.addChainedChunkLoader(chunkGenerator);

        CubitWorld.CubitWorldBuilder<ChunkCube8> builder = CubitWorld.newWorld(ChunkCube8.class);
        CubitWorld<ChunkCube8> world =
                builder.setChunkFactory(factory).setChunkLoader(chunkLoader).setSeed(seed).build();

        instance = new CubitMain<ChunkCube8, CubitWorld<ChunkCube8>>(world, factory, chunkLoader);
        instance.start(800, 600);
    }

    public static Logger getLogger() {
        return Logger.getLogger(CubitMain.class.getName());
    }
}
