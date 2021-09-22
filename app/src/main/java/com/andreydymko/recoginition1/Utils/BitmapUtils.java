package com.andreydymko.recoginition1.Utils;

import android.graphics.Bitmap;

import androidx.annotation.ColorInt;

import java.util.Map;

public class BitmapUtils {
    private static final String TAG = BitmapUtils.class.getSimpleName();

    private BitmapUtils() {}

    public static Bitmap resizeBitmapKeepAspectRatio(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
        }
        return image;
    }

    public static void colorBitmap(Bitmap bitmap, int[][] colorScheme, int[] colors, @ColorInt int defaultColor) {
        for (int i = 0; i < bitmap.getHeight() && i < colorScheme.length; i++) {
            for (int j = 0; j < bitmap.getWidth() && j < colorScheme[i].length; j++) {
                if (colorScheme[i][j] >= 0) {
                    bitmap.setPixel(j, i, ArrayUtils.getOrDefault(colors, colorScheme[i][j], defaultColor));
                }
            }
        }
    }
}
