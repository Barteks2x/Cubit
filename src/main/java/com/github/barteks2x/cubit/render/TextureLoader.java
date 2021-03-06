/* 
 * The MIT License
 *
 * Copyright (C) contributors
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

import com.github.barteks2x.cubit.render.Texture;
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
