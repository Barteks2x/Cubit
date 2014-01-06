package com.github.barteks2x.openmine.generator;

import java.util.Random;

public class ValueNoiseGenerator extends ANoiseGenerator2d {

    private final Random r = new Random();

    public ValueNoiseGenerator(double grid, double persistance, int octaves, double fq, long seed) {
        super(grid, persistance, octaves, fq, seed);
    }

    @Override
    public double getRawValueAt(double x, double z) {
        int intX = (int)x;
        int intZ = (int)z;

        double fx = x - intX;
        double fz = z - intZ;
        int x1 = 1, z1 = 1;
        if(x < 0) {
            fx *= -1;
            x1 = -1;
        }
        if(z < 0) {
            fz *= -1;
            z1 = -1;
        }

        double v1, v2, v3, v4, t1, t2;

        v1 = random(intX, intZ);
        v2 = random(intX + x1, intZ);
        v3 = random(intX, intZ + z1);
        v4 = random(intX + x1, intZ + z1);
        t1 = lerp(v1, v2, fx);
        t2 = lerp(v3, v4, fx);
        return lerp(t1, t2, fz);
    }

    protected double lerp(double x1, double x2, double a) {
        return (x1 * (1 - a) + x2 * a);
    }

    private double random(int x, int z) {
        r.setSeed(seed + new Random(x).nextInt() + new Random(z).nextInt());
        return r.nextDouble();
    }
}
