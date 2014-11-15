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
package com.github.barteks2x.cubit.render;

import com.github.barteks2x.cubit.render.TextureLoader;
import com.github.barteks2x.cubit.render.Texture;
import java.awt.Color;
import static org.lwjgl.opengl.GL11.*;

public class BitmapFont {
    private static final int ASCII_CHARS = 256;
    public double texSize = 256;

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

    public BitmapFont drawString(double x, double y, double scale, Color color, String text) {
        char[] chars = text.toCharArray();
        glPushMatrix();
        glScalef((float)scale, (float)scale, (float)scale);
        glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
        int line = 0;
        glTranslatef((float)x, (float)y, 0);
        for(int i = 0; i < chars.length; ++i) {
            if(chars[i] == '\n') {
                line++;
                glLoadIdentity();
                glTranslatef((float)x, (float)y, 0);
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
            double x1 = ((double)(gridX << 4)) / texSize;
            double x2 = ((double)((gridX + 1) << 4)) / texSize;
            double y1 = ((double)(gridY << 4)) / texSize;
            double y2 = ((double)((gridY + 1) << 4)) / texSize;
            glTexCoord2f((float)x1, (float)y1);
            glVertex2f(0, 0);

            glTexCoord2f((float)x1, (float)y2);
            glVertex2f(0, 16);

            glTexCoord2f((float)x2, (float)y2);
            glVertex2f(16, 16);

            glTexCoord2f((float)x2, (float)y1);
            glVertex2f(16, 0);
            glEnd();
            glEndList();
        }
        return this;
    }
}
