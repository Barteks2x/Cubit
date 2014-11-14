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

package com.github.barteks2x.cubit;

import com.github.barteks2x.cubit.location.ChunkLocation;
import com.github.barteks2x.cubit.world.ChunkCube16;
import com.github.barteks2x.cubit.world.IChunkFactory;

/**
 *
 * @author Bartosz Skrzypczak
 */
class ChunkCube16Factory implements IChunkFactory<ChunkCube16> {

    private ChunkLocation<ChunkCube16> location;
    @Override
    public IChunkFactory<ChunkCube16> clear() {
        this.location = null;
        return this;
    }

    @Override
    public IChunkFactory<ChunkCube16> setLocation(ChunkLocation<ChunkCube16> loc) {
        this.location = loc;
        return this;
    }

    @Override
    public ChunkCube16 build() {
        return new ChunkCube16(location);
    }

}
