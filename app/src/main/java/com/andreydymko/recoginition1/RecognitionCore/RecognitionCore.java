package com.andreydymko.recoginition1.RecognitionCore;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.ColorInt;

import com.andreydymko.recoginition1.BinarizationCore.BinarizationCore;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class RecognitionCore {
    private static final String TAG = RecognitionCore.class.getSimpleName();

    private RecognitionDatabase database;
    private static final int recognitionDiluteMultiplier = 10;

    public RecognitionCore(Context context) {
        database = RecognitionDatabase.getInstance(context);
    }

    public void savePattern(String name, Bitmap bitmap) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap,
                bitmap.getWidth()/recognitionDiluteMultiplier,
                bitmap.getHeight()/recognitionDiluteMultiplier,
                false);
        database.saveModel(new RecognitionModel(name,
                BinarizationCore.convertBitmapToGrayscaleBitSet(resizedBitmap),
                resizedBitmap.getWidth(),
                resizedBitmap.getHeight()));
    }

    public static Bitmap generateBitmap(final RecognitionModel model,
                                        @ColorInt int colorT,
                                        @ColorInt int colorF) {
        Bitmap bitmap = Bitmap.createBitmap(model.getWidth(), model.getHeight(), Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(colorF);
        for (int i = 0; i < model.getHeight(); i++) {
            for (int j = 0; j < model.getWidth(); j++) {
                if (model.getData().get(i*model.getWidth() + j)) {
                    bitmap.setPixel(j, i, colorT);
                }
            }
        }
        return bitmap;
    }

    public Map<String, Double> recognizeBitmap(final Bitmap bitmap) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap,
                bitmap.getWidth()/recognitionDiluteMultiplier,
                bitmap.getHeight()/recognitionDiluteMultiplier,
                false);
        HashMap<String, Integer> results = new HashMap<>();
        BitSet original = BinarizationCore.convertBitmapToGrayscaleBitSet(resizedBitmap);
        for (RecognitionModel model : database.loadModels()) {
            results.put(model.getName(), countMatches(original, model.getData()));
        }
        return probability(results);
    }

    private static int countMatches(BitSet bitSet1, BitSet bitSet2) {
        int length = Math.min(bitSet1.length(), bitSet2.length());
        int result = 0;
        for (int i = 0; i < length; i++) {
            if (bitSet1.get(i) == bitSet2.get(i)) {
                result++;
            }
        }
        return result;
    }

    private static Map<String, Double> probability(Map<String, Integer> results) {
        double sum = 0;
        for (Map.Entry<String, Integer> entry : results.entrySet()) {
            sum += entry.getValue();
        }
        HashMap<String, Double> probability = new HashMap<>();
        for (Map.Entry<String, Integer> entry : results.entrySet()) {
            probability.put(entry.getKey(), entry.getValue()/sum);
        }
        return probability;
    }

    public static int countHoles(final Bitmap bitmap) {
        BitSet bitset = BinarizationCore.convertBitmapToGrayscaleBitSet(bitmap);
        int countX = 0;
        int countO = 0;
        int tempCounter = 0;
        for (int i = 0; i < bitmap.getHeight()-1; i++) {
            for (int j = 0; j < bitmap.getWidth()-1; j++) {
                for (int k = i; k <= i+1; k++) {
                    for (int l = j; l <= j+1; l++) {
                        if (bitset.get(k*bitmap.getWidth() + l)) {
                            tempCounter++;
                        }
                    }
                }
                switch (tempCounter) {
                    case 1:
                        countX++;
                        break;
                    case 3:
                        countO++;
                        break;
                    default:
                        break;
                }
                tempCounter = 0;
            }
        }
        return (countX - countO)/4;
    }

}
