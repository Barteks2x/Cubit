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

package com.github.barteks2x.cubit.world.chunk;

import com.github.barteks2x.cubit.location.ChunkLocation;
import com.github.barteks2x.cubit.location.Vec3I;
import com.github.barteks2x.cubit.world.chunk.ChunkCube8;
import com.github.barteks2x.cubit.world.chunk.IChunkFactory;

/**
 *
 * @author Bartosz Skrzypczak
 */
public class ChunkCube8Factory implements IChunkFactory<ChunkCube8> {

    private ChunkLocation<ChunkCube8> location;
    @Override
    public IChunkFactory<ChunkCube8> clear() {
        this.location = null;
        return this;
    }

    @Override
    public IChunkFactory<ChunkCube8> setLocation(ChunkLocation<ChunkCube8> loc) {
        this.location = loc;
        return this;
    }

    @Override
    public ChunkCube8 build() {
        return new ChunkCube8(location);
    }

    @Override
    public Vec3I getChunkSize() {
        return ChunkCube8.chunkSize();
    }

}
