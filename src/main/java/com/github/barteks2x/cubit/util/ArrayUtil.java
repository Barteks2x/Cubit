/* 
 * The MIT License
 *
 * Copyright (C) contributors
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
package com.github.barteks2x.cubit.util;
//code for primitive types is exactly the same as code for generics... but primitives are not classes.

import java.util.Arrays;

/**
 * Contains simple helper methods for operations on Arrays.
 */
public class ArrayUtil {

    /**
     * Copies source array content to destination array.
     * <p>
     * @param src  Source array
     * @param dest Destination array
     * <p>
     * @return Destinarion array
     */
    public static int[] clone(int[] src, int[] dest) {
        if (src.length != dest.length) {
            throw new IllegalArgumentException("Arrays differ in sizes!");
        }
        System.arraycopy(src, 0, dest, 0, src.length);

        return dest;
    }

    /**
     * Copies source array content to destination array.
     * <p>
     * @param <T>  Array type
     * @param src  Source array
     * @param dest Destination array
     * <p>
     * @return Destinarion array
     */
    public static <T> T[] clone(T[] src, T[] dest) {
        if (src.length != dest.length) {
            throw new IllegalArgumentException("Arrays differ in sizes!");
        }
        System.arraycopy(src, 0, dest, 0, src.length);

        return dest;
    }

    /**
     * Copies source array content to destination array.
     * <p>
     * @param src  Source array
     * @param dest Destination array
     * <p>
     * @return Destinarion array
     */
    public static int[][] clone(int[][] src, int[][] dest) {
        if (!checkEqualLength(src, dest)) {
            throw new IllegalArgumentException("Arrays differ in sizes!");
        }
        for (int i = 0; i < src.length; i++) {
            System.arraycopy(src[i], 0, dest[i], 0, src[i].length);
        }
        return dest;
    }

    /**
     * Copies source array content to destination array.
     * <p>
     * @param <T>  Array type
     * @param src  Source array
     * @param dest Destination array
     * <p>
     * @return Destinarion array
     */
    public static <T> T[][] clone(T[][] src, T[][] dest) {
        if (!checkEqualLength(src, dest)) {
            throw new IllegalArgumentException("Arrays differ in sizes!");
        }
        for (int i = 0; i < src.length; i++) {
            System.arraycopy(src[i], 0, dest[i], 0, src[i].length);
        }
        return dest;
    }

    /**
     * Copies source array content to destination array.
     * <p>
     * @param src  Source array
     * @param dest Destination array
     * <p>
     * @return Destinarion array
     */
    public static int[][][] clone(int[][][] src, int[][][] dest) {
        if (!checkEqualLength(src, dest)) {
            throw new IllegalArgumentException("Arrays differ in sizes!");
        }
        for (int i = 0; i < src.length; i++) {
            for (int j = 0; j < src[i].length; j++) {
                System.arraycopy(src[i][j], 0, dest[i][j], 0, src[i][j].length);
            }
        }
        return dest;
    }

    /**
     * Copies source array content to destination array.
     * <p>
     * @param <T>  Array type
     * @param src  Source array
     * @param dest Destination array
     * <p>
     * @return Destinarion array
     */
    public static <T> T[][][] clone(T[][][] src, T[][][] dest) {
        if (!checkEqualLength(src, dest)) {
            throw new IllegalArgumentException("Arrays differ in sizes!");
        }
        for (int i = 0; i < src.length; i++) {
            for (int j = 0; j < src[i].length; j++) {
                System.arraycopy(src[i][j], 0, dest[i][j], 0, src[i][j].length);
            }
        }
        return dest;
    }

    /**
     * Fills array with specified object.
     * <p>
     * @param <T> Array type
     * @param arr Array to fill (array contents will change)
     * @param val Object to fill array with
     * <p>
     * @return Filled array
     */
    public static <T> T[][][] fill(T[][][] arr, T val) {
        for (T[][] i : arr) {
            for (T[] j : i) {
                Arrays.fill(j, val);
            }
        }
        return arr;
    }

    /**
     * Fills array with specified object.
     * <p>
     * @param arr Array to fill (array contents will change)
     * @param val Object to fill array with
     * <p>
     * @return Filled array
     */
    public static int[][][] fill(int[][][] arr, int val) {
        for (int[][] i : arr) {
            for (int[] j : i) {
                Arrays.fill(j, val);
            }
        }
        return arr;
    }

    /**
     * Fills array with specified object.
     * <p>
     * @param <T> Array type
     * @param arr Array to fill (array contents will change)
     * @param val Object to fill array with
     * <p>
     * @return Filled array
     */
    public static <T> T[][] fill(T[][] arr, T val) {
        for (T[] i : arr) {
            Arrays.fill(arr, val);
        }
        return arr;
    }

    /**
     * Fills array with specified object.
     * <p>
     * @param arr Array to fill (array contents will change)
     * @param val Object to fill array with
     * <p>
     * @return Filled array
     */
    public static int[][] fill(int[][] arr, int val) {
        for (int[] i : arr) {
            Arrays.fill(arr, val);
        }
        return arr;
    }

    /**
     * Checks if 2 arrays have equal length. Works with non-rectangular arrays.
     * <p>
     * @param <T>  Array type
     * @param arr1 Array 1
     * @param arr2 Array 2
     * <p>
     * @return true if length is equal, false otherwise.
     */
    public static <T> boolean checkEqualLength(T[][] arr1, T[][] arr2) {
        if (arr1.length != arr2.length) {
            return false;
        }
        for (int i = 0; i < arr1.length; i++) {
            if (arr1[i].length != arr2[i].length) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if 2 arrays have equal length. Works with non-rectangular arrays.
     * <p>
     * @param arr1 Array 1
     * @param arr2 Array 2
     * <p>
     * @return true if length is equal, false otherwise.
     */
    public static boolean checkEqualLength(int[][] arr1, int[][] arr2) {
        if (arr1.length != arr2.length) {
            return false;
        }
        for (int i = 0; i < arr1.length; i++) {
            if (arr1[i].length != arr2[i].length) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if 2 arrays have equal length. Works with non-rectangular arrays.
     * <p>
     * @param <T>  Array type
     * @param arr1 Array 1
     * @param arr2 Array 2
     * <p>
     * @return true if length is equal, false otherwise.
     */
    public static <T> boolean checkEqualLength(T[][][] arr1, T[][][] arr2) {
        if (arr1.length != arr2.length) {
            return false;
        }
        for (int i = 0; i < arr1.length; i++) {
            if (arr1[i].length != arr2[i].length) {
                return false;
            }
        }
        for (int i = 0; i < arr1.length; i++) {
            for (int j = 0; j < arr1[i].length; j++) {
                if (arr1[i][j].length != arr2[i][j].length) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if 2 arrays have equal length. Works with non-rectangular arrays.
     * <p>
     * @param arr1 Array 1
     * @param arr2 Array 2
     * <p>
     * @return true if length is equal, false otherwise.
     */
    public static boolean checkEqualLength(int[][][] arr1, int[][][] arr2) {
        if (arr1.length != arr2.length) {
            return false;
        }
        for (int i = 0; i < arr1.length; i++) {
            if (arr1[i].length != arr2[i].length) {
                return false;
            }
        }
        for (int i = 0; i < arr1.length; i++) {
            for (int j = 0; j < arr1[i].length; j++) {
                if (arr1[i][j].length != arr2[i][j].length) {
                    return false;
                }
            }
        }
        return true;
    }

    private ArrayUtil() {
        throw new UnsupportedOperationException(
                "Cannot instantiate utility class");
    }
}
