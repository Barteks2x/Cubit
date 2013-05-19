package org.barteks2x.freemine.generator;

public interface INoiseGenerator2d {

	double getRawValueAt(double x, double z);

	double getFBMValueAt(int x, int z);
}
