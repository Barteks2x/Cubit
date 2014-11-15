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
package com.github.barteks2x.cubit.location;

import com.github.barteks2x.cubit.util.MathUtil;
import com.github.barteks2x.cubit.world.CubitWorld;
import com.github.barteks2x.cubit.world.chunk.IChunk;
import com.github.barteks2x.cubit.world.IWorld;
import java.text.DecimalFormat;

/**
 *
 * @author bartosz
 */
public class EntityLocation extends Vec3D {

    private static final DecimalFormat format = new DecimalFormat("#.##");
    private IWorld world;

    public EntityLocation(IWorld world, double x, double y, double z) {
        super(x, y, z);
        this.world = world;
    }

    public EntityLocation(EntityLocation loc) {
        super(loc);
        this.world = loc.getWorld();
    }

    public EntityLocation(IWorld world, Vec3D loc) {
        super(loc);
        this.world = world;
    }

    public EntityLocation(IWorld world, Vec3I loc) {
        this(world, loc.getX(), loc.getY(), loc.getZ());
    }

    public IWorld getWorld() {
        return world;
    }

    public BlockLocation toBlockLocation() {
        return new BlockLocation(world,
                MathUtil.floor(this.getX()),
                MathUtil.floor(this.getY()),
                MathUtil.floor(this.getZ()));
    }

    @Override
    public EntityLocation add(double x, double y, double z) {
        return new EntityLocation(world, super.add(x, y, z));
    }

    @Override
    public EntityLocation add(Vec3D vec) {
        return new EntityLocation(world, super.add(vec));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(75);
        //ChunkLocation(%d, %d, %d)
        return sb.append("EntityLocation(").
                append(format.format(this.getX())).
                append(", ").
                append(format.format(this.getY())).
                append(", ").
                append(format.format(this.getZ())).
                append(")").toString();
    }
}
