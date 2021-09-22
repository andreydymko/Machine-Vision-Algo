package com.andreydymko.recoginition1.MorphologicCore;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;

import com.andreydymko.recoginition1.BinarizationCore.BinarizationCore;

public class MorphologicCore {

    // Наращивание
    public static Bitmap builtUpBitmap(Bitmap originalBitmap, MorphologicModel model) {
        boolean[][] mask = model.getMask();
        Point mainPixel = model.getMainPixel();
        Bitmap bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        int offX, offY;
        for (int i = 0; i < originalBitmap.getHeight(); i++) {
            for (int j = 0; j < originalBitmap.getWidth(); j++) {
                if (BinarizationCore.isColorBlacker(originalBitmap.getPixel(j, i), 155)) {
                    for (int k = 0; k < mask.length; k++) {
                        for (int l = 0; l < mask[k].length; l++) {
                            offX = j - mainPixel.x + l;
                            offY = i - mainPixel.y + k;
                            if (mask[k][l] && 0 <= offX && offX < originalBitmap.getWidth() && 0 <= offY && offY < originalBitmap.getHeight()) {
                                bitmap.setPixel(offX, offY, Color.BLACK);
                            }
                        }
                    }
                }
            }
        }
        return bitmap;
    }

    // эрозия
    public static Bitmap bitmapErosion(Bitmap originalBitmap, MorphologicModel model) {
        boolean[][] mask = model.getMask();
        Point mainPixel = model.getMainPixel();
        Bitmap bitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), originalBitmap.getConfig());
        int offX, offY;
        boolean isMaskHit;
        for (int i = 0; i < originalBitmap.getHeight(); i++) {
            for (int j = 0; j < originalBitmap.getWidth(); j++) {
                if (BinarizationCore.isColorBlacker(originalBitmap.getPixel(j, i), 155)) {
                    isMaskHit = true;
                    // check for mask hit
                    for (int k = 0; k < mask.length; k++) {
                        for (int l = 0; l < mask[k].length; l++) {
                            offX = j - mainPixel.x + l;
                            offY = i - mainPixel.y + k;
                            if (!mask[k][l]) {
                                continue;
                            }
                            if (0 <= offX && offX < originalBitmap.getWidth()
                                    && 0 <= offY && offY < originalBitmap.getHeight()
                                    && !BinarizationCore.isColorBlacker(originalBitmap.getPixel(offX, offY), 155)) {
                                isMaskHit = false;
                            }
                        }
                    }
                    if (isMaskHit) {
                        offX = j + mainPixel.x;
                        offY = i + mainPixel.y;
                        if (offX < originalBitmap.getWidth() && offY < originalBitmap.getHeight()) {
                            bitmap.setPixel(offX, offY, Color.BLACK);
                        }
                    }
                }
            }
        }
        return bitmap;
    }

    // замыкание
    public static Bitmap bitmapClosing(Bitmap bitmap, MorphologicModel morphologicModel) {
        return bitmapErosion(builtUpBitmap(bitmap, morphologicModel), morphologicModel);
    }

    // размыкание
    public static Bitmap bitmapBreaking(Bitmap bitmap, MorphologicModel morphologicModel) {
        return builtUpBitmap(bitmapErosion(bitmap, morphologicModel), morphologicModel);
    }

    public static Bitmap morphBitmap(Bitmap bitmap, MorphologicModel morphologicModel, int mode) {
        Bitmap res = null;
        switch (mode) {
            case MORPH_MODE.BUILT_UP:
                res = builtUpBitmap(bitmap, morphologicModel);
                break;
            case MORPH_MODE.EROSION:
                res = bitmapErosion(bitmap, morphologicModel);
                break;
            case MORPH_MODE.CLOSING:
                res = bitmapClosing(bitmap, morphologicModel);
                break;
            case MORPH_MODE.BREAKING:
                res = bitmapBreaking(bitmap, morphologicModel);
                break;
            default:
                break;
        }
        return res;
    }

    public static final class MORPH_MODE {
        public final static int BUILT_UP = 0;
        public final static int EROSION = 1;
        public final static int CLOSING = 2;
        public final static int BREAKING = 3;
    }
}
