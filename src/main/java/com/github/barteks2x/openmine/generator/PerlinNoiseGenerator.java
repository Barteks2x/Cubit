package com.github.barteks2x.openmine.generator;

public class PerlinNoiseGenerator extends NoiseGenerator2d {

	public PerlinNoiseGenerator(double grid, double persistance, int octaves, double fq, long seed) {
		super(grid, persistance, octaves, fq, seed);
	}

	@Override
	public double getRawValueAt(double x, double z) {
		//TODO Perlin Noise Generator
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
