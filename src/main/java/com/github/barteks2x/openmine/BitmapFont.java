package com.github.barteks2x.openmine;

import java.awt.Color;
import static org.lwjgl.opengl.GL11.*;

public class BitmapFont {
    private static final int ASCII_CHARS = 256;
    public float texSize = 256;

    private final int[] ASCIIdisplayLists;
    Texture tex;

    public BitmapFont(String texture) {
        this(TextureLoader.loadTextureSafe(texture));
    }

    public BitmapFont(Texture texture) {
        this.ASCIIdisplayLists = new int[ASCII_CHARS];
        this.tex = texture;
        int startDispList = glGenLists(ASCII_CHARS);
        for(int i = 0; i < ASCII_CHARS; ++i) {
            ASCIIdisplayLists[i] = startDispList + i;
        }
    }

    public BitmapFont drawString(float x, float y, float scale, Color color, String text) {
        char[] chars = text.toCharArray();
        glPushMatrix();
        glScalef(scale, scale, scale);
        glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
        int line = 0;
        glTranslatef(x, y, 0);
        for(int i = 0; i < chars.length; ++i) {
            if(chars[i] == '\n') {
                line++;
                glLoadIdentity();
                glTranslatef(x, y, 0);
                glTranslatef(0, 16 * line, 0);
                continue;
            }
            glCallList(ASCIIdisplayLists[chars[i]]);
            glTranslatef(16, 0, 0);
        }
        glPopMatrix();
        return this;
    }

    public BitmapFont bind() {
        tex.bind();
        return this;
    }

    public BitmapFont init() {
        for(int i = 0; i < ASCII_CHARS; ++i) {
            glNewList(ASCIIdisplayLists[i], GL_COMPILE);
            glBegin(GL_QUADS);
            int gridX = i & 0xf;
            int gridY = i >> 4;
            float x1 = ((float)(gridX << 4)) / texSize;
            float x2 = ((float)((gridX + 1) << 4)) / texSize;
            float y1 = ((float)(gridY << 4)) / texSize;
            float y2 = ((float)((gridY + 1) << 4)) / texSize;
            glTexCoord2f(x1, y1);
            glVertex2f(0, 0);

            glTexCoord2f(x1, y2);
            glVertex2f(0, 16);

            glTexCoord2f(x2, y2);
            glVertex2f(16, 16);

            glTexCoord2f(x2, y1);
            glVertex2f(16, 0);
            glEnd();
            glEndList();
        }
        return this;
    }
}
