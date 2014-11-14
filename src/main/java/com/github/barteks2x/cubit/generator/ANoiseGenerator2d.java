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
package com.github.barteks2x.cubit.generator;

public abstract class ANoiseGenerator2d implements INoiseGenerator2d {

    protected double grid;
    protected double persistance;
    protected int octaves;
    protected double lacunarity;
    protected long seed;

    public ANoiseGenerator2d(double grid, double persistance, int octaves, double fq, long seed) {
        this.grid = grid;
        this.persistance = persistance;
        this.octaves = octaves;
        this.lacunarity = fq;
        this.seed = seed;
    }

    @Override
    public double getValueOctaves(int x, int z) {
        double total = 0.0D;
        double frequency = 1.0D / grid;
        double amplitude = persistance;

        for(int i = 0; i < octaves; ++i) {
            total += getRawValueAt(x * frequency, z * frequency) * amplitude;
            frequency *= lacunarity;
            amplitude *= persistance;
        }
        return total;
    }
}
