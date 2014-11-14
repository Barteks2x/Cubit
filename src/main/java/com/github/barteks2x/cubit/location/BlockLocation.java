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
import com.github.barteks2x.cubit.world.IWorld;

public class BlockLocation extends Vec3I {
    private final IWorld world;
    public BlockLocation(IWorld world, int x, int y, int z) {
        super(x, y, z);
        this.world = world;
    }

    public IWorld getWorld(){
        return this.world;
    }
    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public BlockLocation add(int x, int y, int z) {
        return new BlockLocation(this.getWorld(), this.getX() + x, this.getY() + y, this.getZ() + z);
    }
    
    @Override
    public BlockLocation mod(Vec3I vec) {
        return new BlockLocation(
                this.getWorld(), 
                this.getX() % vec.getX(),
                this.getY() % vec.getY(),
                this.getZ() % vec.getZ());
    }
    
    @Override
    public BlockLocation modP(Vec3I vec) {
        return new BlockLocation(
                this.getWorld(),
                MathUtil.modP(this.getX(), vec.getX()),
                MathUtil.modP(this.getY(), vec.getY()),
                MathUtil.modP(this.getZ(), vec.getZ()));
    }
}
