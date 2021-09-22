package com.andreydymko.recoginition1.MaskingCore;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;

import androidx.annotation.ColorInt;

public class MaskingCore {
    private static final String TAG = MaskingCore.class.getName();

    //-2  4 -2
    // 4 -8  4
    //-2  4 -2

    public static Bitmap applyMaskToBitmap(final Bitmap originalBitmap, final MaskModel model) {
        Bitmap bitmap = Bitmap.createBitmap(originalBitmap);
        for (int i = 0; i < bitmap.getHeight(); i++) {
            for (int j = 0; j < bitmap.getWidth() ; j++) {
                bitmap.setPixel(j, i, getMaskedColorForPixel(originalBitmap, model, j, i));
            }
        }
        return bitmap;
    }

    @ColorInt
    private static int getMaskedColorForPixel(final Bitmap bitmap,
                                              final MaskModel model,
                                              int x,
                                              int y) {
        double sumAlpha, sumRed, sumGreen, sumBlue;
        sumAlpha = sumRed = sumGreen = sumBlue = 0;
        double[][] mask = model.getMask();
        Point mainPixel = model.getMainPixel();
        int workPixelColor;
        double workMaskElem;
        for (int i = 0, ix = x - mainPixel.x; i < mask.length; i++, ix++) {
            for (int j = 0, jy = y - mainPixel.y; j < mask[i].length; j++, jy++) {
                if (0 <= ix && ix < bitmap.getWidth() && 0 <= jy && jy < bitmap.getHeight()) {
                    workPixelColor = bitmap.getPixel(ix, jy);
                } else {
                    workPixelColor = bitmap.getPixel(x, y);
                }
                workMaskElem = mask[i][j];
                sumAlpha += workMaskElem * Color.alpha(workPixelColor);
                sumRed += workMaskElem * Color.red(workPixelColor);
                sumGreen += workMaskElem * Color.green(workPixelColor);
                sumBlue += workMaskElem * Color.blue(workPixelColor);
            }
        }
        //Log.d(TAG, "sum == " + sum);
        double maskWeight = model.getMaskWeightAbs();
        return Color.argb(
                (int) Math.round(sumAlpha/maskWeight),
                (int) Math.round(sumRed/maskWeight),
                (int) Math.round(sumGreen/maskWeight),
                (int) Math.round(sumBlue/maskWeight)
        );
    }
}
