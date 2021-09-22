package com.andreydymko.recoginition1.MaskingCore;

import android.graphics.Point;
import android.util.Log;

public class MaskModel {
    private static final String TAG = MaskModel.class.getName();

    private double[][] mask;
    private Point mainPixel;
    private double maskAbsWeight;
    private double maskWeightAbs;

    public MaskModel(double[][] mask) {
        this.mask = mask;
        this.mainPixel = getMainPixelPoint(mask);
        setWeights();

    }

    public MaskModel(double[][] mask, Point mainPixel) {
        this.mask = mask;
        checkBounds(mainPixel);
        this.mainPixel = mainPixel;
        setWeights();
    }

    public void setMask(double[][] mask) {
        this.mask = mask;
        this.mainPixel = getMainPixelPoint(mask);
        setWeights();
    }

    private void setWeights() {
        this.maskAbsWeight = getMaskWeightByAbsSum(mask);
        this.maskWeightAbs = getMaskWeightBySumAbs(mask);
    }

    public double[][] getMask() {
        return mask;
    }

    public void setMainPixel(Point mainPixel) {
        checkBounds(mainPixel);
        this.mainPixel = mainPixel;
    }

    private void checkBounds(Point mainPixel) {
        if (mainPixel.x >= mask.length || mainPixel.y >= mask[mainPixel.x].length) {
            throw new IllegalArgumentException();
        }
    }

    public Point getMainPixel() {
        return mainPixel;
    }

    public double getMaskAbsWeight() {
        return maskAbsWeight;
    }

    public double getMaskWeightAbs() {
        return maskWeightAbs;
    }

    private static Point getMainPixelPoint(double[][] mask) {
        int x, y;
        if (mask.length % 2 == 0 || mask[0].length % 2 == 0) {
            x = y = 0;
        } else {
            x = mask.length / 2;
            y = mask[0].length / 2;
        }
        Log.d(TAG, x+" "+y);
        return new Point(x, y);
    }

    private static double getMaskWeightByAbsSum(double[][] mask) {
        if (mask == null) return 0;
        double sum = 0;
        for (double[] doubles : mask) {
            for (double aDoubles : doubles) {
                sum += aDoubles;
            }
        }
        return Math.abs(sum);
    }

    private static double getMaskWeightBySumAbs(double[][] mask) {
        if (mask == null) return 0;
        double sum = 0;
        for (double[] doubles : mask) {
            for (double aDoubles : doubles) {
                sum += Math.abs(aDoubles);
            }
        }
        return sum;
    }
}
