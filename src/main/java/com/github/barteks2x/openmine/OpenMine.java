package com.github.barteks2x.openmine;

import com.github.barteks2x.openmine.world.Chunk;
import com.github.barteks2x.openmine.Timer;
import com.github.barteks2x.openmine.block.Block;
import com.github.barteks2x.openmine.generator.ChunkGenerator;
import com.github.barteks2x.openmine.world.World;
import java.awt.Color;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

public class OpenMine {
    //Unused. In OpenJDk, the logManager internally only keeps weak references. So the logger can be removed by garbage collector if there is no hard reference
    private static final Logger logger = Logger.getLogger(OpenMine.class.getName());
    public int renderDistance = 4;
    //OpenGL
    private final String title;
    private int fov;
    private float aspectRatio;
    private float zNear, zFar;
    private int width, height;
    private final byte maxFPS;
    private boolean isRunning = true;
    private final FloatBuffer perspectiveProjMatrix = BufferUtils.createFloatBuffer(16);
    private final FloatBuffer orthographicProjMatrix = BufferUtils.createFloatBuffer(16);
    //Rendering
    private final Map<Chunk, Integer> chunkDisplayLists;
    private int selectionDisplayList;
    //movement
    private final Timer timer;
    private float forwardMove = 0, sideMove = 0, upMove = 0, rX = 0, rY = 0;
    private final float playerSpeed = 0.003F;
    private final float mouseSensitivity;
    private boolean grabMouse;
    private final Player player;
    //Textures and fonts
    private BitmapFont font;
    private final DecimalFormat formatter;
    private Texture tex;
    private float time;
    private int itime = 0;
    private int placeid;

    private final World world;

    public static void main(String args[]) {
        OpenMine fm = new OpenMine();
        fm.start(800, 600);
    }

    public static Logger getLogger() {
        return Logger.getLogger(OpenMine.class.getName());
    }

    public OpenMine() {
        this.formatter = new DecimalFormat("#.###");
        long seed = System.currentTimeMillis();
        this.maxFPS = 60;
        FileHandler fileHandler = null;
        this.title = OpenMine.class.getSimpleName() + " " + Version.getVersion();
        try {
            fileHandler = new FileHandler(title + new Date().toString() + ".log");
        } catch(IOException ex) {
            getLogger().log(Level.SEVERE, null, ex);
        } catch(SecurityException ex) {
            getLogger().log(Level.SEVERE, null, ex);
        }
        if(fileHandler != null) {
            logger.addHandler(fileHandler);
        }
        this.world = new World(new ChunkGenerator(seed), seed);
        timer = new Timer();
        player = new Player();
        mouseSensitivity = 0.6F;
        this.chunkDisplayLists = new HashMap<Chunk, Integer>();
    }

    public FloatBuffer asFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        return (FloatBuffer)buffer.put(data).flip();
    }

    private void start(int width, int height) {
        this.width = width;
        this.height = height;
        this.fov = 60;
        this.aspectRatio = (float)width / height;
        zNear = 0.1F;
        zFar = 200F;
        initDisplay();
        initGL();
        loadFonts();
        loadTextures();
        FloatPosition spawn = world.getSpawnLocation();
        player.setX(spawn.x);
        player.setY(spawn.y);
        player.setZ(spawn.z);
        initDisplayLists();
        while(isRunning) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            timer.nextFrame();
            input(timer.getDelta());

            glLoadIdentity();

            tex.bind();
            this.generateDisplayListsNearLocation(player.getLocation());
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
        glTranslatef(-player.getX(), -player.getY(), -player.getZ());
        FloatPosition spawn = new FloatPosition(player.getX(), player.getY(), player.getZ());
        int spawnChunkX = MathHelper.floor(spawn.getX() / 16F);
        int spawnChunkY = MathHelper.floor(spawn.getY() / 16F);
        int spawnChunkZ = MathHelper.floor(spawn.getZ() / 16F);
        for(int x = -renderDistance; x <= renderDistance; ++x) {
            for(int y = -renderDistance; y <= renderDistance; ++y) {
                for(int z = -renderDistance; z <= renderDistance; ++z) {
                    int chunkX = spawnChunkX + x;
                    int chunkY = spawnChunkY + y;
                    int chunkZ = spawnChunkZ + z;
                    int d = getChunkDisplayList(chunkX, chunkY, chunkZ);
                    if(d == -1) {
                        continue;
                    }
                    glPushMatrix();
                    glTranslatef(chunkX << 4, chunkY << 4, chunkZ << 4);
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
        BlockPosition pos = player.getSelectedBlock();
        glTranslatef(pos.x, pos.y, pos.z);
        glCallList(selectionDisplayList);
        glPopMatrix();
    }

    private void renderText() {
        glPushMatrix();
        glMatrixMode(GL_PROJECTION);
        glLoadMatrix(orthographicProjMatrix);
        glMatrixMode(GL_MODELVIEW);

        glLoadIdentity();
        String x = formatter.format(player.getX());
        String y = formatter.format(player.getY());
        String z = formatter.format(player.getZ());
        BlockPosition pos = player.getSelectedBlock();
        String selx = pos != null ? formatter.format(pos.x) : "no selection";
        String sely = pos != null ? formatter.format(pos.y) : "no selection";
        String selz = pos != null ? formatter.format(pos.z) : "no selection";
        BlockPosition pos2 = player.getBlockOnSelectedBlock();
        String selx2 = pos2 != null ? formatter.format(pos2.x) : "no selection";
        String sely2 = pos2 != null ? formatter.format(pos2.y) : "no selection";
        String selz2 = pos2 != null ? formatter.format(pos2.z) : "no selection";
        BitmapFont f = font.bind();
        f.drawString(0, 0, 1, Color.WHITE, new StringBuilder("FPS: ").append(timer.getFPS()).append("\n").
                append("X: ").append(x).append("\nY: ").append(y).append("\nZ: ").append(z).append(
                        "\nselX: ").append(selx).append("\nsely: ").append(sely).append("\nselz: ").append(
                        selz).append("\nonselX: ").append(selx2).append("\nonselY: ").append(sely2).append(
                        "\nonselZ: ").append(selz2).append("\nplace: ").append(Block.byId(placeid) != null
                        ? Block.byId(placeid).toString() : "no block").toString());
        f.drawString((Display.getWidth() / 2F)/3F - 8, (Display.getHeight() / 2F) / 3F - 8, 3, Color.ORANGE, "X");
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
            Logger.getLogger(OpenMine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initGL() {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(fov, aspectRatio, zNear, zFar);
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
        //timer.nextDelta();
        FloatPosition pos = world.getSpawnLocation();
        world.generateChunksNearSpawn();
        this.generateDisplayListsNearLocation(pos);
        
        //System.out.println(timer.nextDelta());
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
    
    private void generateDisplayListsNearLocation(FloatPosition pos){
        Set<ChunkPosition> chunkDisplayListsToCompile = new HashSet<ChunkPosition>(20);
        for(int x = -renderDistance; x <= renderDistance; ++x) {
            for(int y = -renderDistance; y <= renderDistance; ++y) {
                for(int z = -renderDistance; z <= renderDistance; ++z) {
                    int chunkX = MathHelper.floor(pos.x / 16F) + x;
                    int chunkY = MathHelper.floor(pos.y / 16F) + y;
                    int chunkZ = MathHelper.floor(pos.z / 16F) + z;
                    if(getChunkDisplayList(chunkX, chunkY, chunkZ) == -1){
                        chunkDisplayListsToCompile.add(new ChunkPosition(chunkX, chunkY, chunkZ));
                        chunkDisplayListsToCompile.add(new ChunkPosition(chunkX + 1, chunkY, chunkZ));
                        chunkDisplayListsToCompile.add(new ChunkPosition(chunkX - 1 , chunkY, chunkZ));
                        chunkDisplayListsToCompile.add(new ChunkPosition(chunkX, chunkY + 1, chunkZ));
                        chunkDisplayListsToCompile.add(new ChunkPosition(chunkX, chunkY - 1, chunkZ));
                        chunkDisplayListsToCompile.add(new ChunkPosition(chunkX, chunkY, chunkZ + 1));
                        chunkDisplayListsToCompile.add(new ChunkPosition(chunkX, chunkY, chunkZ - 1));
                    }
                }
            }
        }
        for(ChunkPosition p : chunkDisplayListsToCompile){
            buildChunkDisplayList(p.x, p.y, p.z);
        }
    }

    private void onClose(int i) {
        Collection<Integer> lists = chunkDisplayLists.values();
        for(Integer x: lists) {
            glDeleteLists(x, 1);
        }
        System.exit(i);
    }

    private void input(int dt) {
        time += dt;
        if(Mouse.isGrabbed() != grabMouse) {
            Mouse.setGrabbed(grabMouse);
        }
        itime += dt;
        while(Mouse.next()) {
            if(Mouse.isGrabbed()) {
                rX += Mouse.getDX() * mouseSensitivity;
                rX %= 360;
                rY = Math.max(-90, Math.min(90, rY - Mouse.getDY() * mouseSensitivity));
                if(Mouse.getEventButtonState() == true) {
                    if(Mouse.getEventButton() == 0) {
                        BlockPosition b = player.getSelectedBlock();
                        if(b != null) {
                            world.setBlockAt(b.x, b.y, b.z, Block.AIR);
                            int cx = b.x >> 4;
                            int cy = b.y >> 4;
                            int cz = b.z >> 4;
                            buildChunkDisplayList(cx, cy, cz);
                            if((b.x & 0xf) == 0) {
                                buildChunkDisplayList(cx - 1, cy, cz);
                            }
                            if((b.x & 0xf) == 0xf) {
                                buildChunkDisplayList(cx + 1, cy, cz);
                            }
                            if((b.y & 0xf) == 0) {
                                buildChunkDisplayList(cx, cy - 1, cz);
                            }
                            if((b.y & 0xf) == 0xf) {
                                buildChunkDisplayList(cx, cy + 1, cz);
                            }
                            if((b.z & 0xf) == 0) {
                                buildChunkDisplayList(cx, cy, cz - 1);
                            }
                            if((b.z & 0xf) == 0xf) {
                                buildChunkDisplayList(cx, cy, cz + 1);
                            }
                        }
                    }
                    if(Mouse.getEventButton() == 1) {
                        Mouse.setGrabbed(false);
                        BlockPosition b = player.getBlockOnSelectedBlock();
                        if(b != null) {
                            world.setBlockAt(b.x, b.y, b.z, Block.byId(placeid));
                            int cx = b.x >> 4;
                            int cy = b.y >> 4;
                            int cz = b.z >> 4;
                            buildChunkDisplayList(cx, cy, cz);
                            if((b.x & 0xf) == 0) {
                                buildChunkDisplayList(cx - 1, cy, cz);
                            }
                            if((b.x & 0xf) == 0xf) {
                                buildChunkDisplayList(cx + 1, cy, cz);
                            }
                            if((b.y & 0xf) == 0) {
                                buildChunkDisplayList(cx, cy - 1, cz);
                            }
                            if((b.y & 0xf) == 0xf) {
                                buildChunkDisplayList(cx, cy + 1, cz);
                            }
                            if((b.z & 0xf) == 0) {
                                buildChunkDisplayList(cx, cy, cz - 1);
                            }
                            if((b.z & 0xf) == 0xf) {
                                buildChunkDisplayList(cx, cy, cz + 1);
                            }
                        }
                    }
                }
            }
        }
        while(Keyboard.next()) {
            boolean state = Keyboard.getEventKeyState();
            if(Keyboard.getEventKey() == Keyboard.KEY_W) {
                this.forwardMove = state ? playerSpeed : 0;
            }
            if(Keyboard.getEventKey() == Keyboard.KEY_S) {
                this.forwardMove = state ? -playerSpeed : 0;
            }
            if(Keyboard.getEventKey() == Keyboard.KEY_A) {
                this.sideMove = state ? playerSpeed : 0;
            }
            if(Keyboard.getEventKey() == Keyboard.KEY_D) {
                this.sideMove = state ? -playerSpeed : 0;
            }
            if(Keyboard.getEventKey() == Keyboard.KEY_LSHIFT) {
                this.upMove = state ? playerSpeed : 0;
            }
            if(Keyboard.getEventKey() == Keyboard.KEY_SPACE) {
                this.upMove = state ? -playerSpeed : 0;
            }
            if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
                if(itime > 200) {
                    this.grabMouse = !this.grabMouse;
                    itime = 0;
                }
            }
            String c = String.valueOf(Keyboard.getEventCharacter());
            try {
                placeid = Integer.parseInt(c);
            } catch(NumberFormatException ignore) {
            }
        }

        double sinRX = Math.sin(Math.toRadians(rX));
        double cosRX = Math.cos(Math.toRadians(rX));
        double cosRY = Math.cos(Math.toRadians(rY));
        double sinRY = Math.sin(Math.toRadians(rY));
        float x = player.getX(), y = player.getY(), z = player.getZ();

        player.setZ((float)(z - sideMove * dt * sinRX - forwardMove * dt * cosRX /*** cosRY**/));
        player.setX((float)(x - sideMove * dt * cosRX + forwardMove * dt * sinRX /*** cosRY**/));
        player.setY((float)(y - upMove * dt/** - forwardMove * dt * sinRY**/));
        player.setRx(rX);
        player.setRy(rY);
        player.setSelectedBlock(null);
        float px = 0, py = 0, pz = 0;
        for(float i = 0; i <= 5; i += 0.001F) {
            int px_int_prev = (int)px - (px < 0 ? 1 : 0);
            int py_int_prev = (int)py - (py < 0 ? 1 : 0);
            int pz_int_prev = (int)pz - (pz < 0 ? 1 : 0);
            pz = player.getZ() + (float)(-i * cosRX * cosRY);
            py = player.getY() + (float)(-i * sinRY);
            px = player.getX() + (float)(i * sinRX * cosRY);
            //System.out.println(px + ", " + py + ", " + pz);
            int px_int = (int)px - (px < 0 ? 1 : 0);
            int py_int = (int)py - (py < 0 ? 1 : 0);
            int pz_int = (int)pz - (pz < 0 ? 1 : 0);
            Block b = world.getBlockAt(px_int, py_int, pz_int);
            if(b != Block.AIR) {
                player.setSelectedBlock(px_int, py_int, pz_int);
                player.setBlockOnSelectedBlock(px_int_prev, py_int_prev, pz_int_prev);
                break;
            }
        }
    }

    private void buildChunkDisplayList(int cx, int cy, int cz) {
        Chunk c = world.getChunkAt(cx, cy, cz);
        int displayList = getChunkDisplayList(cx, cy, cz);
        if(displayList == -1) {
            displayList = glGenLists(1);
            chunkDisplayLists.put(c, displayList);
        }
        boolean xp, xm, yp, ym, zp, zm;
        tex.bind();
        glNewList(displayList, GL_COMPILE);
        glBegin(GL_QUADS);

        for(int x = 0; x < Chunk.CHUNK_X; ++x) {
            for(int y = 0; y < Chunk.CHUNK_Y; ++y) {
                for(int z = 0; z < Chunk.CHUNK_Z; ++z) {
                    int worldX = x | (cx << 4);
                    int worldY = y | (cy << 4);
                    int worldZ = z | (cz << 4);
                    if(world.getBlockAt(worldX, worldY, worldZ).renderBlock()) {
                        xp = world.getBlockAt(worldX + 1, worldY, worldZ).isTransparent();
                        xm = world.getBlockAt(worldX - 1, worldY, worldZ).isTransparent();
                        yp = world.getBlockAt(worldX, worldY + 1, worldZ).isTransparent();
                        ym = world.getBlockAt(worldX, worldY - 1, worldZ).isTransparent();
                        zp = world.getBlockAt(worldX, worldY, worldZ + 1).isTransparent();
                        zm = world.getBlockAt(worldX, worldY, worldZ - 1).isTransparent();
                        Block b = world.getBlockAt(worldX, worldY, worldZ);
                        if(xp) {
                            int texid = b.getTextureForSide(0);
                            float tx = (texid & 0xf) / 16f;
                            float ty = (texid >> 4) / 16f;
                            glColor3f(0.7F, 0.7F, 0.7F);
                            glTexCoord2f(tx + 0.0625F, ty);
                            glVertex3f(x + 1, y + 1, z + 1);

                            glTexCoord2f(tx + 0.0625F, ty + 0.0625F);
                            glVertex3f(x + 1, y, z + 1);

                            glTexCoord2f(tx, ty + 0.0625F);
                            glVertex3f(x + 1, y, z);

                            glTexCoord2f(tx, ty);
                            glVertex3f(x + 1, y + 1, z);
                        }
                        if(xm) {
                            int texid = b.getTextureForSide(1);
                            float tx = (texid & 0xf) / 16f;
                            float ty = (texid >> 4) / 16f;
                            glColor3f(0.7F, 0.7F, 0.7F);
                            glTexCoord2f(tx, ty);
                            glVertex3f(x, y + 1, z);

                            glTexCoord2f(tx, ty + 0.0625F);
                            glVertex3f(x, y, z);

                            glTexCoord2f(tx + 0.0625F, ty + 0.0625F);
                            glVertex3f(x, y, z + 1);

                            glTexCoord2f(tx + 0.0625F, ty);
                            glVertex3f(x, y + 1, z + 1);
                        }
                        if(yp) {
                            int texid = b.getTextureForSide(2);
                            float tx = (texid & 0xf) / 16f;
                            float ty = (texid >> 4) / 16f;
                            glColor3f(1.1F, 1.1F, 1.1F);
                            glTexCoord2f(tx + 0.0625F, ty);
                            glVertex3f(x, y + 1, z + 1);

                            glTexCoord2f(tx, ty);
                            glVertex3f(x + 1, y + 1, z + 1);

                            glTexCoord2f(tx, ty + 0.0625F);
                            glVertex3f(x + 1, y + 1, z);

                            glTexCoord2f(tx + 0.0625F, ty + 0.0625F);
                            glVertex3f(x, y + 1, z);
                        }
                        if(ym) {
                            int texid = b.getTextureForSide(3);
                            float tx = (texid & 0xf) / 16f;
                            float ty = (texid >> 4) / 16f;
                            glColor3f(0.6F, 0.6F, 0.6F);
                            glTexCoord2f(tx + 0.0625F, ty + 0.0625F);
                            glVertex3f(x, y, z);

                            glTexCoord2f(tx, ty + 0.0625F);
                            glVertex3f(x + 1, y, z);

                            glTexCoord2f(tx, ty);
                            glVertex3f(x + 1, y, z + 1);

                            glTexCoord2f(tx + 0.0625F, ty);
                            glVertex3f(x, y, z + 1);
                        }
                        if(zp) {
                            int texid = b.getTextureForSide(4);
                            float tx = (texid & 0xf) / 16f;
                            float ty = (texid >> 4) / 16f;
                            glColor3f(0.85F, 0.85F, 0.85F);
                            glTexCoord2f(tx + 0.0625F, ty + 0.0625F);
                            glVertex3f(x, y, z + 1);

                            glTexCoord2f(tx, ty + 0.0625F);
                            glVertex3f(x + 1, y, z + 1);

                            glTexCoord2f(tx, ty);
                            glVertex3f(x + 1, y + 1, z + 1);

                            glTexCoord2f(tx + 0.0625F, ty);
                            glVertex3f(x, y + 1, z + 1);
                        }
                        if(zm) {
                            int texid = b.getTextureForSide(5);
                            float tx = (texid & 0xf) / 16f;
                            float ty = (texid >> 4) / 16f;
                            glColor3f(0.85F, 0.85F, 0.85F);
                            glTexCoord2f(tx + 0.0625F, ty);
                            glVertex3f(x, y + 1, z);

                            glTexCoord2f(tx, ty);
                            glVertex3f(x + 1, y + 1, z);

                            glTexCoord2f(tx, ty + 0.0625F);
                            glVertex3f(x + 1, y, z);

                            glTexCoord2f(tx + 0.0625F, ty + 0.0625F);
                            glVertex3f(x, y, z);
                        }
                    }
                }
            }
        }
        glEnd();
        glEndList();
    }

    private void loadTextures() {
        try {
            tex = TextureLoader.loadTexture(Thread.currentThread().getContextClassLoader().
                    getResourceAsStream("texture.png"));
        } catch(IOException ex) {
            Logger.getLogger(OpenMine.class.getName()).log(Level.SEVERE, null, ex);
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
                    log(Level.SEVERE, "OpenGL Error! {2} {0} - {1}",
                            new Object[] {e, gluErrorString(e), msg});
        }
    }

    private int getChunkDisplayList(int chunkX, int chunkY, int chunkZ) {
        Integer d = chunkDisplayLists.get(world.getChunkAt(chunkX, chunkY, chunkZ));
        return d == null ? -1 : d;
    }
}
