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
import com.github.barteks2x.cubit.location.Vec3I;
import com.github.barteks2x.cubit.render.BitmapFont;
import com.github.barteks2x.cubit.util.Timer;
import com.github.barteks2x.cubit.world.chunk.IChunk;
import java.awt.Color;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_PROJECTION_MATRIX;
import static org.lwjgl.opengl.GL11.glGetFloat;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glLoadMatrix;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

/**
 *
 * @author Bartosz Skrzypczak
 */
public class DebugRenderer implements IRenderer {

    private final FloatBuffer orthographicProjMatrix = BufferUtils.createFloatBuffer(16);
    private final BitmapFont font;
    private final Timer timer;
    private final Vec3I chunkSize;

    private int fps = 0;
    private Block toPlace;
    private EntityLocation playerLoc;
    private ChunkLocation<? extends IChunk> playerChunkLocation;
    private BlockLocation selectionLocation;
    private BlockLocation placeLocation;

    public DebugRenderer(BitmapFont font, Timer timer, Vec3I chunkSize, int width, int height) {
        this.font = font;
        this.timer = timer;
        this.chunkSize = chunkSize;
        this.updateWindowDimensions(width, height);
    }

    @Override
    public void render() {
        glMatrixMode(GL_PROJECTION);
        glLoadMatrix(orthographicProjMatrix);
        glMatrixMode(GL_MODELVIEW);

        glLoadIdentity();

        this.font.bind();

        Color fpsColor;
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

        font.drawString(0, y, 1, Color.WHITE, String.format("Player%s", playerLoc));
        y += 16;

        font.drawString(0, y, 1, Color.WHITE, String.format("Player%s", playerChunkLocation));
        y += 16;

        font.drawString(0, y, 1, Color.WHITE, String.format("Selection=%s", selectionLocation));
        y += 16;

        font.drawString(0, y, 1, Color.WHITE, String.format("Place=%s", placeLocation));
        y += 16;

        font.drawString(0, y, 1, Color.WHITE, String.format("PlaceBlock(%s)", toPlace));

        font.drawString((Display.getWidth() / 2F) / 3F - 8, (Display.getHeight() / 2F) / 3F - 8, 3, Color.ORANGE, "X");
    }

    @Override
    public void update(Player player) {
        this.playerLoc = player.getLocation();
        this.playerChunkLocation = getPlayerChunkLocation(player);
        this.selectionLocation = player.getSelectionLocation();
        this.placeLocation = player.getPlaceBlockLocation();
        this.toPlace = player.getBlockToPlace();
        this.fps = timer.getFPS();
    }

    @Override
    public final void updateWindowDimensions(int width, int height) {
        glPushMatrix();
        {
            glMatrixMode(GL_PROJECTION);
            glOrtho(0, width, height, 0, -1, 1);
            glGetFloat(GL_PROJECTION_MATRIX, orthographicProjMatrix);
            glMatrixMode(GL_MODELVIEW);
        }
        glPopMatrix();

    }

    private ChunkLocation<? extends IChunk> getPlayerChunkLocation(Player player) {
        BlockLocation blockLoc = new BlockLocation(player.getLocation());
        return new ChunkLocation<IChunk>(null, this.chunkSize, blockLoc);
    }

    @Override
    public void delete() {
        font.delete();
    }

}
