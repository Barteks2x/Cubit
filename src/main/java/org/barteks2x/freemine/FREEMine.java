package org.barteks2x.freemine;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.*;
import org.barteks2x.freemine.Timer;
import org.barteks2x.freemine.block.Block;
import org.barteks2x.freemine.generator.ChunkGenerator;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;

import static org.lwjgl.util.glu.GLU.gluErrorString;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import static org.lwjgl.opengl.GL11.*;

public class FREEMine {
	//Unused. In OpenJDk, the logManager internally only keeps weak references. So the logger can be removed by garbage collector if there is no hard reference

	private static Logger logger = Logger.getLogger(FREEMine.class.getName());
	//OpenGL
	private final String title;
	private int fov;
	private float aspectRatio;
	private float zNear, zFar;
	private int width, height;
	private byte maxFPS = 60;
	private boolean isRunning = true;
	//Generator
	private FloatBuffer perspectiveProjMatrix = BufferUtils.createFloatBuffer(16);
	private FloatBuffer orthographicProjMatrix = BufferUtils.createFloatBuffer(16);
	private ChunkGenerator chunkGenerator;
	private Chunk chunkArray[];
	private Map<ChunkPosition, Integer> chunkDisplayLists;
	private int selectionDisplayList;
	private long seed = System.currentTimeMillis();
	//movement
	private Timer timer;
	private float forwardMove = 0, sideMove = 0, upMove = 0, rX = 0, rY = 0;
	private float playerSpeed = 0.003F;//units per milisecond
	private float mouseSensitivity;
	private boolean grabMouse;
	private Player player;
	//Textures and fonts
	private BitmapFont font;
	private DecimalFormat formatter = new DecimalFormat("#.###");
	private Texture tex;
	//world constants
	private int minWorldChunkX = -5;
	private int maxWorldChunkX = 5;
	private int minWorldChunkZ = -5;
	private int maxWorldChunkZ = 5;
	private int minWorldChunkY = -5;
	private int maxWorldChunkY = 4;
	private float time;

	public static void main(String args[]) {
		FREEMine fm = new FREEMine();
		fm.start(800, 600);
	}

	public FREEMine() {
		FileHandler fh = null;
		try {
			fh = new FileHandler("FREEMine.log");
		} catch (IOException ex) {
			Logger.getLogger(FREEMine.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SecurityException ex) {
			Logger.getLogger(FREEMine.class.getName()).log(Level.SEVERE, null, ex);
		}
		if (fh != null) {
			logger.addHandler(fh);
		}
		this.title = FREEMine.class.getSimpleName() + " " + Version.getVersion();
		int chunks = (maxWorldChunkX - minWorldChunkX) * (maxWorldChunkY - minWorldChunkY) *
				(maxWorldChunkZ - minWorldChunkZ);
		this.chunkArray = new Chunk[chunks];
		chunkGenerator = new ChunkGenerator(seed);
		timer = new Timer();
		player = new Player();
		mouseSensitivity = 0.6F;
		this.chunkDisplayLists = new HashMap<ChunkPosition, Integer>(chunks);
	}

	private void start(int width, int height) {
		this.width = width;
		this.height = height;
		this.fov = 60;
		this.aspectRatio = (float)width / (float)height;
		zNear = 0.1F;
		zFar = 200F;
		initDisplay();
		initGL();
		loadFonts();
		loadTextures();
		generateChunks(seed);
		IntPosition spawn = chunkGenerator.getSpawnPoint();
		player.setX(spawn.x);
		player.setY(spawn.y + 1.6F);
		player.setZ(spawn.z);
		initDisplayLists();
		while (isRunning) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			timer.nextFrame();
			input(timer.getDelta());

			glLoadIdentity();

			tex.bind();
			renderChunks();
			renderSelection();
			renderText();
			Display.update();
			errorCheck("Main");
			if (Display.isCloseRequested()) {
				isRunning = false;
			}
		}
		onClose(0);
	}

	private void renderChunks() {

		glRotated(player.getRy(), 1, 0, 0);
		glRotated(player.getRx(), 0, 1, 0);
		glTranslatef(-player.getX(), -player.getY(), -player.getZ());

		for (int x = minWorldChunkX; x < maxWorldChunkX; ++x) {
			for (int y = minWorldChunkY; y < maxWorldChunkY; ++y) {
				for (int z = minWorldChunkZ; z < maxWorldChunkZ; ++z) {
					glPushMatrix();
					glTranslatef(x << 4, y << 4, z << 4);
					glCallList(chunkDisplayLists.get(new ChunkPosition(x, y, z)));
					glPopMatrix();
				}
			}
		}
	}

	private void renderSelection() {
		if (player.getSelectedBlock() == null) {
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
		font.bind().drawString(0, 0, new StringBuilder("FPS: ").append(timer.getFPS()).append("\n").
				append("X: ").append(x).append("\nY: ").append(y).append("\nZ: ").append(z).append(
				"\nselX: ").append(selx).append("\nsely: ").append(sely).append("\nselz: ").append(
				selz).append("\nonselX: ").append(selx2).append("\nonselY: ").append(sely2).append(
				"\nonselZ: ").append(selz2).append("\nplace: ").append(Block.byId(placeid) != null ?
				Block.byId(placeid).toString() : "no block").toString());

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
		} catch (LWJGLException ex) {
			Logger.getLogger(FREEMine.class.getName()).log(Level.SEVERE, null, ex);
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

	private void generateChunks(long seed) {
		int i = 0;
		for (int x = minWorldChunkX; x < maxWorldChunkX; ++x) {
			for (int y = minWorldChunkY; y < maxWorldChunkY; ++y) {
				for (int z = minWorldChunkZ; z < maxWorldChunkZ; ++z) {
					chunkArray[i] = chunkGenerator.generateChunk(x, y, z);
					++i;
				}
			}
		}
	}

	private void initDisplayLists() {
		//timer.nextDelta();
		for (int x = minWorldChunkX; x < maxWorldChunkX; ++x) {
			for (int y = minWorldChunkY; y < maxWorldChunkY; ++y) {
				for (int z = minWorldChunkZ; z < maxWorldChunkZ; ++z) {
					buildChunkDisplayList(x, y, z);
				}
			}
		}
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

	private void onClose(int i) {
		Collection<Integer> lists = chunkDisplayLists.values();
		for (Integer x : lists) {
			glDeleteLists(x, 1);
		}
		System.exit(i);
	}
	private int itime = 0;
	private int placeid;

	private void input(int dt) {
		time += dt;
		if (Mouse.isGrabbed() != grabMouse) {
			Mouse.setGrabbed(grabMouse);
		}
		itime += dt;
		while (Mouse.next()) {
			if (Mouse.isGrabbed()) {
				rX += Mouse.getDX() * mouseSensitivity;
				rX %= 360;
				rY = Math.max(-90, Math.min(90, rY - Mouse.getDY() * mouseSensitivity));
				if (Mouse.getEventButtonState() == true) {
					if (Mouse.getEventButton() == 0) {
						BlockPosition b = player.getSelectedBlock();
						if (b != null && getBlockAt(b.x, b.y, b.z) != null) {
							Chunk c = getChunkAt(b.x >> 4, b.y >> 4, b.z >> 4);
							c.setBlockAt(b.x, b.y, b.z, 0);
							int cx = b.x >> 4;
							int cy = b.y >> 4;
							int cz = b.z >> 4;
							buildChunkDisplayList(cx, cy, cz);
							if ((b.x & 0xf) == 0) {
								buildChunkDisplayList(cx - 1, cy, cz);
							}
							if ((b.x & 0xf) == 0xf) {
								buildChunkDisplayList(cx + 1, cy, cz);
							}
							if ((b.y & 0xf) == 0) {
								buildChunkDisplayList(cx, cy - 1, cz);
							}
							if ((b.y & 0xf) == 0xf) {
								buildChunkDisplayList(cx, cy + 1, cz);
							}
							if ((b.z & 0xf) == 0) {
								buildChunkDisplayList(cx, cy, cz - 1);
							}
							if ((b.z & 0xf) == 0xf) {
								buildChunkDisplayList(cx, cy, cz + 1);
							}
						}
					}
					if (Mouse.getEventButton() == 1) {
						Mouse.setGrabbed(false);
						BlockPosition b = player.getBlockOnSelectedBlock();
						if (b != null && getChunkAt(b.x >> 4, b.y >> 4, b.z >> 4) != null) {
							Chunk c = getChunkAt(b.x >> 4, b.y >> 4, b.z >> 4);
							c.setBlockAt(b.x, b.y, b.z, placeid);
							int cx = b.x >> 4;
							int cy = b.y >> 4;
							int cz = b.z >> 4;
							buildChunkDisplayList(cx, cy, cz);
							if ((b.x & 0xf) == 0) {
								buildChunkDisplayList(cx - 1, cy, cz);
							}
							if ((b.x & 0xf) == 0xf) {
								buildChunkDisplayList(cx + 1, cy, cz);
							}
							if ((b.y & 0xf) == 0) {
								buildChunkDisplayList(cx, cy - 1, cz);
							}
							if ((b.y & 0xf) == 0xf) {
								buildChunkDisplayList(cx, cy + 1, cz);
							}
							if ((b.z & 0xf) == 0) {
								buildChunkDisplayList(cx, cy, cz - 1);
							}
							if ((b.z & 0xf) == 0xf) {
								buildChunkDisplayList(cx, cy, cz + 1);
							}
						}
					}
				}
			}
		}
		while (Keyboard.next()) {
			boolean state = Keyboard.getEventKeyState();
			if (Keyboard.getEventKey() == Keyboard.KEY_W) {
				this.forwardMove = state ? playerSpeed : 0;
			}
			if (Keyboard.getEventKey() == Keyboard.KEY_S) {
				this.forwardMove = state ? -playerSpeed : 0;
			}
			if (Keyboard.getEventKey() == Keyboard.KEY_A) {
				this.sideMove = state ? playerSpeed : 0;
			}
			if (Keyboard.getEventKey() == Keyboard.KEY_D) {
				this.sideMove = state ? -playerSpeed : 0;
			}
			if (Keyboard.getEventKey() == Keyboard.KEY_LSHIFT) {
				this.upMove = state ? playerSpeed : 0;
			}
			if (Keyboard.getEventKey() == Keyboard.KEY_SPACE) {
				this.upMove = state ? -playerSpeed : 0;
			}
			if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
				if (itime > 200) {
					this.grabMouse = !this.grabMouse;
					itime = 0;
				}
			}
			String c = String.valueOf(Keyboard.getEventCharacter());
			try {
				placeid = Integer.parseInt(c);
			} catch (NumberFormatException ignore) {
			}
		}

		double sinRX = Math.sin(Math.toRadians(rX));
		double cosRX = Math.cos(Math.toRadians(rX));
		double cosRY = Math.cos(Math.toRadians(rY));
		double sinRY = Math.sin(Math.toRadians(rY));
		float x = player.getX(), y = player.getY(), z = player.getZ();

		player.setZ((float)(z - sideMove * dt * sinRX - forwardMove * dt * cosRX * cosRY));
		player.setX((float)(x - sideMove * dt * cosRX + forwardMove * dt * sinRX * cosRY));
		player.setY((float)(y - upMove * dt - forwardMove * dt * sinRY));
		player.setRx(rX);
		player.setRy(rY);
		player.setSelectedBlock(null);
		float px = 0, py = 0, pz = 0;
		for (float i = 0; i <= 5; i += 0.001F) {
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
			Block b = getBlockAt(px_int, py_int, pz_int);
			if (b != null) {
				player.setSelectedBlock(px_int, py_int, pz_int);
				player.setBlockOnSelectedBlock(px_int_prev, py_int_prev, pz_int_prev);
				break;
			}
		}
	}

	private void buildChunkDisplayList(int cx, int cy, int cz) {
		Chunk chunk = getChunkAt(cx, cy, cz);
		if (chunk == null) {
			return;
		}
		int displayList;
		if (!chunkDisplayLists.containsKey(chunk.getPosition())) {
			displayList = glGenLists(1);
			chunkDisplayLists.put(chunk.getPosition(), displayList);
		} else {
			displayList = chunkDisplayLists.get(chunk.getPosition());
		}
		Chunk cxm = null, cxp = null, cym = null, cyp = null, czm = null, czp = null;
		cxm = getChunkAt(cx - 1, cy, cz);
		cxp = getChunkAt(cx + 1, cy, cz);
		cym = getChunkAt(cx, cy - 1, cz);
		cyp = getChunkAt(cx, cy + 1, cz);
		czm = getChunkAt(cx, cy, cz - 1);
		czp = getChunkAt(cx, cy, cz + 1);
		tex.bind();
		glNewList(displayList, GL_COMPILE);
		glBegin(GL_QUADS);
		for (int x = 0; x < Chunk.CHUNK_X; ++x) {
			for (int y = 0; y < Chunk.CHUNK_Y; ++y) {
				for (int z = 0; z < Chunk.CHUNK_Z; ++z) {
					if (chunk.getBlockAt(x, y, z) != 0) {
						boolean xp = false, xm = false, yp = false, ym = false, zp = false, zm =
								false;

						if (x == Chunk.CHUNK_X - 1) {
							if (cxp == null || cxp.getBlockAt(0, y, z) == 0) {
								xp = true;
							}
						} else {
							if (chunk.getBlockAt(x + 1, y, z) == 0) {
								xp = true;
							}
						}

						if (x == 0) {
							if (cxm == null || cxm.getBlockAt(Chunk.CHUNK_X - 1, y, z) == 0) {
								xm = true;
							}
						} else {
							if (chunk.getBlockAt(x - 1, y, z) == 0) {
								xm = true;
							}
						}

						if (y == Chunk.CHUNK_Y - 1) {
							if (cyp == null || cyp.getBlockAt(x, 0, z) == 0) {
								yp = true;
							}
						} else {
							if (chunk.getBlockAt(x, y + 1, z) == 0) {
								yp = true;
							}
						}

						if (y == 0) {
							if (cym == null || cym.getBlockAt(x, Chunk.CHUNK_Y - 1, z) == 0) {
								ym = true;
							}
						} else {
							if (chunk.getBlockAt(x, y - 1, z) == 0) {
								ym = true;
							}
						}

						if (z == Chunk.CHUNK_Z - 1) {
							if (czp == null || czp.getBlockAt(x, y, 0) == 0) {
								zp = true;
							}
						} else {
							if (chunk.getBlockAt(x, y, z + 1) == 0) {
								zp = true;
							}
						}

						if (z == 0) {
							if (czm == null || czm.getBlockAt(x, y, Chunk.CHUNK_Z - 1) == 0) {
								zm = true;
							}
						} else {
							if (chunk.getBlockAt(x, y, z - 1) == 0) {
								zm = true;
							}
						}
						Block b = Block.blocks.get(chunk.getBlockAt(x, y, z));
						if (xp) {
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
						if (xm) {
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
						if (yp) {
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
						if (ym) {
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
						if (zp) {
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
						if (zm) {
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

	public Block getBlockAt(int x, int y, int z) {
		Chunk chunk = getChunkAt(x >> 4, y >> 4, z >> 4);
		if (chunk == null) {
			return null;
		}
		return Block.byId(chunk.getBlockAt(x, y, z));
	}

	public Chunk getChunkAt(int x, int y, int z) {
		if (x >= maxWorldChunkX ||
				y >= maxWorldChunkY ||
				z >= maxWorldChunkZ ||
				x < minWorldChunkX ||
				y < minWorldChunkY ||
				z < minWorldChunkZ) {
			return null;
		}
		return chunkArray[(z - minWorldChunkZ) + (y - minWorldChunkY) * (maxWorldChunkZ -
				minWorldChunkZ) + (x - minWorldChunkX) * (maxWorldChunkY - minWorldChunkY) *
				(maxWorldChunkZ - minWorldChunkZ)];
	}

	private void loadTextures() {
		try {
			tex = TextureLoader.loadTexture(Thread.currentThread().getContextClassLoader().
					getResourceAsStream("texture.png"));
		} catch (IOException ex) {
			Logger.getLogger(FREEMine.class.getName()).log(Level.SEVERE, null, ex);
			onClose(-1);
		}
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	}

	public FloatBuffer asFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		return (FloatBuffer)buffer.put(data).flip();
	}

	@SuppressWarnings("unchecked")
	private void loadFonts() {
		font = new BitmapFont("Font256.png");
		font.init();
	}

	private void errorCheck(String msg) {
		int e = glGetError();
		if (e != GL_NO_ERROR) {
			Logger.getLogger(this.getClass().getName()).
					log(Level.SEVERE, "OpenGL Error! {2} {0} - {1}", new Object[]{e, gluErrorString(
				e), msg});
		}
	}
}
