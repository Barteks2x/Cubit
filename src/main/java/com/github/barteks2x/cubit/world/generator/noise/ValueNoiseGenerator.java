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
package com.github.barteks2x.cubit.world.generator.noise;

import com.github.barteks2x.cubit.util.MathUtil;
import java.util.Random;

public class ValueNoiseGenerator extends AbstractNoiseGenerator2d {

    private final Random rand = new Random();

    public ValueNoiseGenerator(double grid, double persistance, int octaves, double fq, long seed) {
        super(grid, persistance, octaves, fq, seed);
    }

    @Override
    public double getRawValueAt(double x, double z) {
        int intX = MathUtil.floor(x);
        int intZ = MathUtil.floor(z);

        double fx = x - intX;
        double fz = z - intZ;

        double v1, v2, v3, v4, t1, t2;

        v1 = random(intX, intZ);
        v2 = random(intX + 1, intZ);
        v3 = random(intX, intZ + 1);
        v4 = random(intX + 1, intZ + 1);

        t1 = lerp(v1, v2, fx);
        t2 = lerp(v3, v4, fx);
        return lerp(t1, t2, fz);
    }

    protected double lerp(double x1, double x2, double a) {
        return (x1 * (1 - a) + x2 * a);
    }

    private double random(int x, int z) {
        rand.setSeed(this.getSeed() + new Random(x).nextInt() + new Random(z).nextInt());
        return rand.nextDouble();
    }
}
