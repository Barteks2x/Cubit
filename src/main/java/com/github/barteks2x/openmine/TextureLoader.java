package com.github.barteks2x.openmine;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.fastpng.PNGDecoder;
import net.sourceforge.fastpng.PNGDecoder.TextureFormat;

public class TextureLoader {

    public static Texture loadTexture(InputStream in) throws IOException {
        PNGDecoder decoder = new PNGDecoder(in);
        int width = decoder.getWidth();
        int height = decoder.getHeight();
        ByteBuffer buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
        decoder.decode(buf, decoder.getWidth() * 4, TextureFormat.RGBA);
        buf.flip();
        return new Texture(width, height, buf);
    }

    public static Texture loadTexture(String name) throws IOException {
        return loadTexture(Thread.currentThread().getContextClassLoader().getResourceAsStream(name));
    }

    public static Texture loadTextureSafe(InputStream in) {
        try {
            return loadTexture(in);
        } catch(IOException ex) {
            Logger.getLogger(TextureLoader.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static Texture loadTextureSafe(String name) {
        try {
            return loadTexture(name);
        } catch(IOException ex) {
            Logger.getLogger(TextureLoader.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
