package com.github.barteks2x.openmine;

import org.lwjgl.util.Color;

import static org.lwjgl.opengl.GL11.*;

public class BitmapFont {

	protected Color color = new Color(255, 255, 255);
	Texture tex;
	private static final int ASCII_CHARS = 256;
	private int[] ASCIIdisplayLists = new int[ASCII_CHARS];
	public float texSize = 256;

	public BitmapFont(String texture) {
		this(TextureLoader.loadTextureSafe(texture));
	}

	public BitmapFont(Texture texture) {
		this.tex = texture;
		int startDispList = glGenLists(ASCII_CHARS);
		for(int i = 0; i<ASCII_CHARS; ++i){
			ASCIIdisplayLists[i] = startDispList+i;
		}
	}

	public BitmapFont drawString(float x, float y, String text) {
		char[] chars = text.toCharArray();
		glPushMatrix();
		glColor3f(1, 1, 1);
		int line = 0;
		glTranslatef(x, 0, y);
		for(int i=0; i<chars.length; ++i){
			if(chars[i]=='\n'){
				line++;
				glLoadIdentity();
				glTranslatef(x, 0, y);
				glTranslatef(0, 16*line, 0);
				continue;
			}
			glCallList(ASCIIdisplayLists[chars[i]]);
			glTranslatef(16, 0, 0);
		}
		glPopMatrix();
		return this;
	}
	
	public BitmapFont bind(){
		tex.bind();
		return this;
	}

	public BitmapFont init() {
		for(int i = 0; i<ASCII_CHARS; ++i){
			glNewList(ASCIIdisplayLists[i], GL_COMPILE);
			glBegin(GL_QUADS);
			int gridX = i&0xf;
			int gridY = i>>4;
			float x1 = ((float)(gridX<<4))/texSize;
			float x2 = ((float)((gridX+1)<<4))/texSize;
			float y1 = ((float)(gridY<<4))/texSize;
			float y2 = ((float)((gridY+1)<<4))/texSize;
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

	public BitmapFont setColor(Color c) {
		this.color = c;
		return this;
	}

	public BitmapFont setColor(byte r, byte g, byte b) {
		return this.setColor(new Color(r, g, b));
	}

	public Color getColor() {
		return this.color;
	}
}
