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
package com.github.barteks2x.cubit.world;

import com.github.barteks2x.cubit.generator.AChunkGenerator;
import com.github.barteks2x.cubit.generator.HeightmapChunkGenerator;

public class HeightmapChunkGeneratorBuilder<T extends IChunk> implements
        IChunkGeneratorBuilder<T> {

    private long seed;
    private IWorld world;
    private IChunkFactory<T> chunkFactory;

    @Override
    public HeightmapChunkGeneratorBuilder<T> setSeed(long seed) {
        this.seed = seed;
        return this;
    }

    @Override
    public AChunkGenerator<T> build() {
        if (this.world == null) {
            throw new IncompleteBuildException("No world specified.");
        }
        return new HeightmapChunkGenerator<T>(chunkFactory, seed);
    }

    @Override
    public HeightmapChunkGeneratorBuilder<T> setChunkFactory(IChunkFactory<T> factory) {
        this.chunkFactory = factory;
        return this;
    }
}
