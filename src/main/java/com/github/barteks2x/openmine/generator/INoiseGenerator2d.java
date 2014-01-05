package com.github.barteks2x.openmine.generator;

public interface INoiseGenerator2d {

    double getRawValueAt(double x, double z);

    double getFBMValueAt(int x, int z);
}
