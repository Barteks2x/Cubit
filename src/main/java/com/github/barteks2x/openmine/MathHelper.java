package com.github.barteks2x.openmine;

public class MathHelper {
    public static int floor(float f){
        return (int)(f < 0 ? f - 1 : f);
    }
    public static int floor(double d){
        return (int)(d < 0 ? d - 1 : d);
    }
    
}
