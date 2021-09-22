package com.andreydymko.recoginition1.BinarizationCore;

import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;

import java.util.BitSet;

public class BinarizationCore {
    public static final String lowerBinarizationBoundPrefKey = "lowerBoundBinarizationKey";
    public static final String upperBinarizationBoundPrefKey = "upperBoundBinarizationKey";
    public static final int minBinarizationBound = 0;
    public static final int maxBinarizationBound = 255;
    public static final int colorLevelsNum = 256;

    public static int getBinarizationThreshold(final Bitmap bitmap) {
        return InterIntraVariance.intragroupVariance(bitmap);
    }

    public static BitSet convertBitmapToGrayscaleBitSet(final Bitmap bitmap) {
        BitSet grayscale = new BitSet(bitmap.getHeight() * bitmap.getWidth());
        for (int i = 0; i < bitmap.getHeight(); i++) {
            for (int j = 0; j < bitmap.getWidth(); j++) {
                if (isColorBlacker(bitmap.getPixel(j, i), 155)) {
                    grayscale.set(i*bitmap.getWidth() + j);
                }
            }
        }
        return grayscale;
    }

    public static boolean isColorBlacker(@ColorInt int color,
                                         @IntRange(from=minBinarizationBound, to=maxBinarizationBound) int threshold) {
        return (Color.alpha(color) >= threshold
                && Color.red(color) < threshold
                && Color.green(color) < threshold
                && Color.blue(color) < threshold);
    }

    public static Bitmap convertBitmapBinary(final Bitmap bitmapOrig,
                                             @IntRange(from=minBinarizationBound, to=maxBinarizationBound) int lowerLimit,
                                             @IntRange(from=minBinarizationBound, to=maxBinarizationBound) int upperLimit) {
        Bitmap bitmap = bitmapOrig.copy(bitmapOrig.getConfig(), true);
        if (bitmap == null) {
            throw new RuntimeException("Couldn't create a copy of the original bitmap.");
        }
        int color;
        for (int i = 0; i < bitmap.getHeight(); i++) {
            for (int j = 0; j < bitmap.getWidth(); j++) {
                color = bitmap.getPixel(j, i);
                if (avgColor(color) < lowerLimit) {
                    bitmap.setPixel(j, i, Color.BLACK);
                }
                if (avgColor(color) > upperLimit) {
                    bitmap.setPixel(j, i, Color.WHITE);
                }
            }
        }
        return bitmap;
    }

    public static int[] getColorsDataSet(final Bitmap bitmap) {
        int[] res = new int[maxBinarizationBound];
        for (int i = 0; i < bitmap.getHeight(); i++) {
            for (int j = 0; j < bitmap.getWidth(); j++) {
                res[avgColor(bitmap.getPixel(j, i))]++;
            }
        }
        return res;
    }

    @IntRange(from=0, to=255)
    static int avgColor(@ColorInt int color) {
        return (Color.alpha(color)
                + Color.red(color)
                + Color.green(color)
                + Color.blue(color))/4;
    }
}
