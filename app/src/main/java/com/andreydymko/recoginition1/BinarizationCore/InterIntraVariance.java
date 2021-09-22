package com.andreydymko.recoginition1.BinarizationCore;

import android.graphics.Bitmap;

import com.andreydymko.recoginition1.Utils.ArrayUtils;

public class InterIntraVariance {
    // межгрупповая дисперсия
    static int intergroupVariance(final Bitmap bitmap) {
        double[] resForEveryGroup = new double[BinarizationCore.colorLevelsNum];
        double[] colorsProbability = getColorProbability(bitmap);
        double q1, q2, n1, n2;
        for (int i = 0; i < resForEveryGroup.length; i++) {
            q1 = ArrayUtils.sumArr(colorsProbability, 0, i);
            q2 = 1 - q1;
            n1 = expectedMathValue(colorsProbability, 0, i, q1);
            n2 = expectedMathValue(colorsProbability, i + 1, colorsProbability.length, q2);
            resForEveryGroup[i] = q1 * (1 - q1) * Math.pow(n1 - n2, 2);
        }
        return ArrayUtils.maxValueIdx(resForEveryGroup);
    }

    // Внутригрупповая дисперсия
    static int intragroupVariance(final Bitmap bitmap) {
        double[] resForEveryGroup = new double[BinarizationCore.colorLevelsNum];
        double[] colorsProbability = getColorProbability(bitmap);
        double q1, q2, n1, n2, o1, o2;
        for (int i = 0; i < resForEveryGroup.length; i++) {
            q1 = ArrayUtils.sumArr(colorsProbability, 0, i);
            q2 = 1 - q1;
            n1 = expectedMathValue(colorsProbability, 0, i, q1);
            n2 = expectedMathValue(colorsProbability, i + 1, colorsProbability.length, q2);
            o1 = dispersion(colorsProbability, 0, i, n1, q1);
            o2 = dispersion(colorsProbability, i + 1, colorsProbability.length, n2, q2);
            resForEveryGroup[i] = q1 * o1 + q2 * o2;
        }
        return ArrayUtils.minValueIdx(resForEveryGroup);
    }

    static double dispersion(double[] probes, int from, int to, double n, double q) {
        double result = 0;
        for (int i = Math.max(from, 0), end = Math.min(to, probes.length); i < end; i++) {
            result += Math.pow(i - n, 2) * probes[i] / q;
        }
        return result;
    }

    static double expectedMathValue(double[] probes, int from, int to, double q) {
        double result = 0;
        for (int i = Math.max(from, 0), end = Math.min(to, probes.length); i < end; i++) {
            result += i * probes[i] / q;
        }
        return result;
    }

    static int[] getColorsLevels(final Bitmap bitmap) {
        int[] colorLevels = new int[BinarizationCore.colorLevelsNum];
        for (int i = 0; i < bitmap.getHeight(); i++) {
            for (int j = 0; j < bitmap.getWidth(); j++) {
                colorLevels[BinarizationCore.avgColor(bitmap.getPixel(j, i))]++;
            }
        }
        return colorLevels;
    }

    static double[] getColorProbability(final Bitmap bitmap) {
        int[] colorLevels = getColorsLevels(bitmap);
        double[] colorProb = new double[BinarizationCore.colorLevelsNum];
        double imageArea = bitmap.getWidth()*bitmap.getHeight();
        for (int i = 0; i < colorProb.length; i++) {
            colorProb[i] = colorLevels[i]/imageArea;
        }
        return colorProb;
    }
}
