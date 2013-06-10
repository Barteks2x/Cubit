package org.barteks2x.freemine;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class Texture {

	protected int texID;

	public Texture(int width, int height, ByteBuffer buf) {
		this(width, height, buf, GL_NEAREST, GL_NEAREST);
	}

	public Texture(int width, int height, ByteBuffer buf, int magFilter, int minFilter) {
		texID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texID);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter);
	}

	public void bind() {
		glBindTexture(GL_TEXTURE_2D, texID);
	}
}
