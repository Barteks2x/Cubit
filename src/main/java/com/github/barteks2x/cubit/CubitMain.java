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

import com.github.barteks2x.cubit.location.BlockLocation;
import com.github.barteks2x.cubit.location.ChunkLocation;
import com.github.barteks2x.cubit.location.EntityLocation;
import com.github.barteks2x.cubit.location.Vec3I;
import com.github.barteks2x.cubit.profiling.GPUProfiler;
import com.github.barteks2x.cubit.profiling.GPUTaskProfile;
import com.github.barteks2x.cubit.render.BitmapFont;
import com.github.barteks2x.cubit.render.Texture;
import com.github.barteks2x.cubit.render.TextureLoader;
import com.github.barteks2x.cubit.render.block.BlockTextureManager;
import com.github.barteks2x.cubit.render.block.SpritesheetTextureManager;
import com.github.barteks2x.cubit.render.renderer.DebugRenderer;
import com.github.barteks2x.cubit.render.renderer.Renderer;
import com.github.barteks2x.cubit.render.renderer.WorldRenderer;
import com.github.barteks2x.cubit.util.Timer;
import com.github.barteks2x.cubit.util.Version;
import com.github.barteks2x.cubit.util.logging.LoggerUtil;
import com.github.barteks2x.cubit.world.CubitWorld;
import com.github.barteks2x.cubit.world.chunk.ChunkCube16;
import com.github.barteks2x.cubit.world.chunk.ChunkCube16Factory;
import com.github.barteks2x.cubit.world.chunk.Chunk;
import com.github.barteks2x.cubit.world.chunk.ChunkFactory;
import com.github.barteks2x.cubit.world.chunkloader.ChunkLoader;
import com.github.barteks2x.cubit.world.chunkloader.RAMChunkLoader;
import com.github.barteks2x.cubit.world.generator.HeightmapChunkGenerator;
import java.io.IOException;
import java.nio.FloatBuffer;
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
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glEndList;
import static org.lwjgl.opengl.GL11.glGenLists;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glNewList;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;
import static org.lwjgl.util.glu.GLU.gluErrorString;

public class CubitMain<C extends Chunk, World extends CubitWorld<C>> {

    private static final Logger logger = LoggerUtil.getLogger(CubitMain.class);

    private static CubitMain<ChunkCube16, CubitWorld<ChunkCube16>> instance;
    private static final int TICKRATE = 20;
    private static final long UPDATE_TIME = 1000000000L / TICKRATE;
    private final int renderDistance = 64;
    //OpenGL
    private final String title;
    private boolean isRunning = true;
    //Rendering
    private int selectionDisplayList;
    //movement
    private final Timer timer;
    final double mouseSensitivity;
    private boolean grabMouse;
    private final Player player;

    private double time;

    private final World world;

    private long lastUpdateTime = System.nanoTime();

    private final ChunkFactory<C> chunkFactory;

    private Renderer debugRenderer;
    private WorldRenderer worldRenderer;

    public CubitMain(World world, ChunkFactory<C> chunkFactory, ChunkLoader<C> chunkLoader) {
        this.chunkFactory = chunkFactory;
        this.title = "Cubit " + Version.getVersion();

        this.world = world;

        timer = new Timer();
        player = new Player(world);
        mouseSensitivity = 0.6F;

        this.world.joinPlayer(player);
    }

    public FloatBuffer asFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        return (FloatBuffer)buffer.put(data).flip();
    }

    private void start(int width, int height) {
        initDisplay(width, height);
        initGL();
        BitmapFont font = loadFonts();
        Texture tex = loadTextures();
        Vec3I spawn = world.getSpawnPoint();
        player.setPosition(new EntityLocation(world, spawn));
        initDisplayLists();

        int fov = 70;
        float zNear = 0.1F;
        BlockTextureManager textureManager = new SpritesheetTextureManager();

        this.debugRenderer = new DebugRenderer(font, timer, this.chunkFactory.getChunkSize(), width, height);
        this.worldRenderer = WorldRenderer.newRenderer().
                setFov(fov).
                setWidth(width).
                setHeight(height).
                setRenderChunkSize(this.chunkFactory.getChunkSize()).setBlockTextureManager(textureManager).
                setTexture(tex).
                setViewDistanceBlocks(this.renderDistance).
                setzNear(zNear).build();

        while(isRunning) {
            GPUProfiler.startFrame();
            GPUProfiler.start("MainRender");

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            timer.nextFrame();
            if(System.nanoTime() - this.lastUpdateTime > UPDATE_TIME) {
                input();
                this.lastUpdateTime = System.nanoTime();
                world.tick(TICKRATE);
            }

            glLoadIdentity();
            GPUProfiler.start("WorldRender");

            GPUProfiler.start("Update");
            this.worldRenderer.update(player);
            GPUProfiler.endStart("Render");
            this.worldRenderer.render();

            GPUProfiler.endStart("SelectionRender");
            renderSelection();
            GPUProfiler.end();

            GPUProfiler.endStart("DebugGui");
            GPUProfiler.start("Update");
            this.debugRenderer.update(player);
            GPUProfiler.endStart("Render");
            this.debugRenderer.render();
            GPUProfiler.end();

            GPUProfiler.endStart("Display.update");
            Display.update();
            GPUProfiler.end();

            GPUProfiler.end();
            GPUProfiler.endFrame();

            GPUTaskProfile tp;
            while((tp = GPUProfiler.getFrameResults()) != null) {

                tp.dump(); //Dumps the frame to System.out.
                //or use the functions of GPUTaskProfile to extract information about the frame:
                //getName(), getStartTime(), getEndTime(), getTimeTaken(), getChildren()
                //Then you can draw the result as fancy graphs or something.

                GPUProfiler.recycle(tp); //Recycles GPUTaskProfile instances and their OpenGL query objects.
            }
            errorCheck("Main");
            if(Display.isCloseRequested()) {
                isRunning = false;
            }
        }
        onClose(0);
    }

    private void renderSelection() {
        if(player.getSelectionLocation() == null) {
            return;
        }
        glBindTexture(GL_TEXTURE_2D, 0);
        glColor4f(1F, 1F, 1F, (float)Math.sin(time * .005F) / 4F + .75F);
        glPushMatrix();
        BlockLocation pos = player.getSelectionLocation();
        glTranslatef(pos.getX(), pos.getY(), pos.getZ());
        glCallList(selectionDisplayList);
        glPopMatrix();
    }

    private void initDisplay(int width, int height) {
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

    private void onClose(int i) {
        this.worldRenderer.delete();
        this.debugRenderer.delete();
        System.exit(i);
    }

    private void input() {
        if(Mouse.isGrabbed() != grabMouse) {
            Mouse.setGrabbed(grabMouse);
        }
        player.update();
    }

    public void onBlockUpdate(BlockLocation blockPos) {
        this.worldRenderer.onBlockUpdate(blockPos);
    }

    private Texture loadTextures() {
        Texture tex = null;
        try {
            tex = TextureLoader.loadTexture(Thread.currentThread().
                    getContextClassLoader().getResourceAsStream("texture.png"));
        } catch(IOException ex) {
            Logger.getLogger(CubitMain.class.getName()).log(Level.SEVERE, null, ex);
            onClose(-1);
        }
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        return tex;
    }

    private BitmapFont loadFonts() {
        BitmapFont font = new BitmapFont("Font256.png");
        return font.init();
    }

    private void errorCheck(String msg) {
        int e = glGetError();
        if(e != GL_NO_ERROR) {
            Logger.getLogger(this.getClass().getName()).
                    log(Level.SEVERE, "OpenGL Error! {2} {0} - {1}", new Object[]{e, gluErrorString(e), msg});
        }
    }

    void pauseInvert() {
        this.grabMouse = !grabMouse;
    }

    private ChunkLocation<C> getPlayerChunkLocation() {
        BlockLocation blockLoc = new BlockLocation(player.getLocation());
        return new ChunkLocation<C>(world, this.chunkFactory.getChunkSize(), blockLoc);
    }

    public static CubitMain<ChunkCube16, CubitWorld<ChunkCube16>> getGame() {
        return instance;
    }

    public static void main(String args[]) throws IOException {
        LoggerUtil.initLoggers();
        long seed = System.nanoTime();
        ChunkFactory<ChunkCube16> factory = new ChunkCube16Factory();

        ChunkLoader<ChunkCube16> chunkLoader = new RAMChunkLoader<ChunkCube16>();
        
        CubitWorld.CubitWorldBuilder<ChunkCube16> builder = CubitWorld.newWorld(ChunkCube16.class);
        CubitWorld<ChunkCube16> world = builder.
                setChunkFactory(factory).
                setChunkLoader(chunkLoader).
                setSeed(seed).build();

        ChunkLoader<ChunkCube16> chunkGenerator = new HeightmapChunkGenerator<ChunkCube16>(factory, world);
        chunkLoader.addChainedChunkLoader(chunkGenerator);

        instance = new CubitMain<ChunkCube16, CubitWorld<ChunkCube16>>(world, factory, chunkLoader);
        instance.start(800, 600);
    }

    public static Logger getLogger() {
        return Logger.getLogger(CubitMain.class.getName());
    }
}
