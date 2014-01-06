package com.github.barteks2x.openmine.generator;

public abstract class ANoiseGenerator2d implements INoiseGenerator2d {

    protected double grid;
    protected double persistance;
    protected int octaves;
    protected double fq;
    protected long seed;

    public ANoiseGenerator2d(double grid, double persistance, int octaves, double fq, long seed) {
        this.grid = grid;
        this.persistance = persistance;
        this.octaves = octaves;
        this.fq = fq;
        this.seed = seed;
    }

    @Override
    public double getFBMValueAt(int x, int z) {
        double total = 0.0D;
        double frequency = 1.0D / grid;
        double amplitude = persistance;

        for(int i = 0; i < octaves; ++i) {
            total += getRawValueAt(x * frequency, z * frequency) * amplitude;
            frequency *= fq;
            amplitude *= persistance;
        }
        return total;
    }
}
