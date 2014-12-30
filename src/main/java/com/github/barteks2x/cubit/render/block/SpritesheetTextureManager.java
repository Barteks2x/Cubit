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
package com.github.barteks2x.cubit.render.block;

import com.github.barteks2x.cubit.render.TextureCoords;
import java.util.HashMap;
import java.util.Map;

public class SpritesheetTextureManager implements BlockTextureManager {

    private final Map<String, TextureCoords> mapping;
    
    public SpritesheetTextureManager(){
        mapping = new HashMap<String, TextureCoords>(5);
        //TODO: TEXTURE MANAGER!!!
        //FIXME!!!
        mapping.put("stone", new TextureCoords(4, 0, 0));
        mapping.put("grass", new TextureCoords(4, 1, 0));
        mapping.put("dirtgrass", new TextureCoords(4, 2, 0));
        mapping.put("dirt", new TextureCoords(4, 3, 0));
        mapping.put("cobblestone", new TextureCoords(4, 4, 0));
        mapping.put("sand", new TextureCoords(4, 6, 0));
        mapping.put("wood", new TextureCoords(4, 7, 0));
        mapping.put("cutwood", new TextureCoords(4, 8, 0));
    }
    @Override
    public TextureCoords getTextureCoordsForName(String name) {
        return mapping.get(name);
    }

}
