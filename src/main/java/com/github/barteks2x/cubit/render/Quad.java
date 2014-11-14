/*
 * Copyright (C) 2014 bartosz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.barteks2x.cubit.render;

import com.github.barteks2x.cubit.util.ArrayUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Quad {

    private final TextureCoords texCoords;
    private final List<Vertex> verticies;

    public Quad(TextureCoords texCoords, List<Vertex> verticies) {
        if (verticies.size() != 4) {
            throw new IllegalArgumentException(String.format(
                    "Wrong amount of verticies! Expected: %d, got: %d",
                    4, verticies.size()));
        }
        this.texCoords = texCoords;
        this.verticies = new ArrayList<Vertex>(verticies);
    }

    public TextureCoords getTextureCoords() {
        return this.texCoords;
    }

    public List<Vertex> getVerticies() {
        return Collections.unmodifiableList(this.verticies);
    }
}
