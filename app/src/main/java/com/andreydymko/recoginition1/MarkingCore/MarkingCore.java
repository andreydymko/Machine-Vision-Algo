package com.andreydymko.recoginition1.MarkingCore;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.andreydymko.recoginition1.BinarizationCore.BinarizationCore;
import com.andreydymko.recoginition1.Utils.ArrayUtils;
import com.andreydymko.recoginition1.Utils.BitmapUtils;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Vector;

public class MarkingCore {
    private static final int[] COLORS = new int[]
            {Color.BLUE, Color.CYAN, Color.DKGRAY,
                    Color.GRAY, Color.GREEN, Color.MAGENTA,
                    Color.RED, Color.YELLOW};
    private static final String TAG = MarkingCore.class.getSimpleName();

    public static MarkingResult markObjects(final Bitmap originalBitmap) {
        // array indexes used as replacement numbers, so keep them un-shuffled
        // indexing starts from 1, because '0' means nothing in context of image
        Vector<Vector<Integer>> replacementVector = new Vector<>();
        BitSet grayscale = BinarizationCore.convertBitmapToGrayscaleBitSet(originalBitmap);
        int[][] bitmapArr = new int[originalBitmap.getHeight()][originalBitmap.getWidth()];
        // counting new-liners (aka new objects) on the image
        int currCount = 0;
        for (int i = 0; i < bitmapArr.length; i++) {
            for (int j = 0; j < bitmapArr[i].length; j++) {
                if (grayscale.get(i*originalBitmap.getWidth() + j)) {
                    // something in current position
                    if (j > 0 && grayscale.get(i*originalBitmap.getWidth() + j - 1)) {
                        // something on the left
                        // copying element from the left
                        bitmapArr[i][j] = bitmapArr[i][j-1];
                        if (i > 0 && bitmapArr[i-1][j] != 0) {
                            // some figure found at the top
                            int maxElem = Math.max(bitmapArr[i-1][j], bitmapArr[i][j]);
                            if (replacementVector.size() <= maxElem) {
                                // resizing vector to use it's index as replacement number
                                replacementVector.setSize(maxElem + 1);
                            }
                            if (replacementVector.get(bitmapArr[i][j]) == null) {
                                replacementVector.set(bitmapArr[i][j], new Vector<Integer>());
                            }
                            if (replacementVector.get(bitmapArr[i-1][j]) == null) {
                                replacementVector.set(bitmapArr[i-1][j], new Vector<Integer>());
                            }
                            ArrayUtils.addUnique(replacementVector.get(bitmapArr[i-1][j]), bitmapArr[i][j]);
                            ArrayUtils.addUnique(replacementVector.get(bitmapArr[i][j]), bitmapArr[i-1][j]);
                        }
                    } else {
                        // nothing on the left
                        if (i > 0 && grayscale.get((i - 1)*originalBitmap.getWidth() + j)) {
                            // something at the top
                            // copying element from the top
                            bitmapArr[i][j] = bitmapArr[i-1][j];
                        } else {
                            // nothing at the top
                            // creating new-liner
                            bitmapArr[i][j] = ++currCount;
                        }
                    }
                }
            }
        }
        Log.d(TAG, "Finished creating arr");

        for (int i = 1; i < replacementVector.size(); i++) {
            if (replacementVector.get(i) == null) continue;
            int toReplace, replacement;
            for (int j = 0; j < replacementVector.get(i).size(); j++) {
                toReplace = replacementVector.get(i).get(j); // 1 ----> 0, if "0: 1"
                replacement = i;
                if (toReplace == replacement || toReplace <= 0) continue;
                //Log.d(TAG, MessageFormat.format( "replacing {0} with {1}", toReplace, replacement));
                ArrayUtils.replaceEntries(replacementVector, toReplace, replacement);
                ArrayUtils.moveIndexEntries(replacementVector, toReplace, replacement);
                ArrayUtils.replaceAll(bitmapArr, toReplace, replacement);
            }
        }
        Log.d(TAG, "Finished uniting new-liners");

        ArrayList<Integer> indexesVector = (ArrayList<Integer>) ArrayUtils.allNonEmptyIndexes(replacementVector);
        Log.d(TAG, indexesVector.size()  + " ::: " + indexesVector.toString());
        ArrayUtils.replaceAll(bitmapArr, 0, -1);
        for (int i = 0; i < indexesVector.size(); i++) {
            ArrayUtils.replaceAll(bitmapArr, indexesVector.get(i), i);
        }
        return new MarkingResult(bitmapArr, indexesVector.size());
    }

    public static class MarkingResult {
        int[][] data;
        int objCount;

        public MarkingResult(int[][] data, int objCount) {
            this.data = data;
            this.objCount = objCount;
        }

        public int[][] getData() {
            return data;
        }

        public int getObjCount() {
            return objCount;
        }
    }

    public static Bitmap colorMarkedBitmap(final Bitmap bitmap, final MarkingResult markResult) {
        Bitmap result = bitmap.copy(bitmap.getConfig(), true);
        int[] colorsArr = new int[markResult.getObjCount()];
        for (int i = 0; i < colorsArr.length; i++) {
            colorsArr[i] = COLORS[i % COLORS.length];
        }
        BitmapUtils.colorBitmap(result, markResult.getData(), colorsArr, Color.WHITE);
        return result;
    }
}
