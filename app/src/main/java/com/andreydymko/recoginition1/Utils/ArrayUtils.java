package com.andreydymko.recoginition1.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

public class ArrayUtils {
    public static void replaceAll(int[] array, int toReplace, int replacement) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == toReplace) {
                array[i] = replacement;
            }
        }
    }

    public static void replaceAll(int[][] array, int toReplace, int replacement) {
        for (int[] element: array) {
            replaceAll(element, toReplace, replacement);
        }
    }

    public static <E> void addUnique(Collection<E> collection, E item) {
        if (!collection.contains(item)) {
            collection.add(item);
        }
    }

    public static <E> void replaceEntries(Vector<Vector<E>> vectors, E toReplace, E replacement) {
        // firstly, replace all occurrences inside inner list
        for (int i = 0; i < vectors.size(); i++) {
            int index = 0;
            if (vectors.get(i) == null) {
                continue;
            }
            while ((index = vectors.get(i).indexOf(toReplace, index)) != -1 ) {
                vectors.get(i).set(index, replacement);
            }
        }
    }

    public static <E> void moveIndexEntries(Vector<Vector<E>> vectors, int fromIndex, int toIndex) {
        // then, move inner list components fromIndex one index (=fromIndex) toIndex another (=toIndex)
        if (vectors.size() < Math.max(fromIndex, toIndex)) {
            vectors.setSize(Math.max(fromIndex, toIndex) + 1);
        }
        vectors.get(toIndex).addAll(vectors.get(fromIndex));
        vectors.get(fromIndex).clear();
    }

    public static <E> Collection<Integer> allNonEmptyIndexes(Vector<Vector<E>> vectors) {
        ArrayList<Integer> nonEmptyIndexList = new ArrayList<>();
        for (int i = 0; i < vectors.size(); i++) {
            if (vectors.get(i) == null) {
                continue;
            }
            if (!vectors.get(i).isEmpty()) {
                nonEmptyIndexList.add(i);
            }
        }
        return nonEmptyIndexList;
    }

    public static <E> Collection<E> allUniqueValues(Vector<Vector<E>> vectors) {
        ArrayList<E> uniqueValues = new ArrayList<>();
        for (Collection<E> collection : vectors) {
            if (collection == null) {
                continue;
            }
            for (E subItem : collection) {
                addUnique(uniqueValues, subItem);
            }
        }
        return uniqueValues;
    }

    public static int getOrDefault(int[] arr, int index, int defaultValue) {
        return index < arr.length && index >= 0 ? arr[index] : defaultValue;
    }

    public static <E> E getOrDefault(List<E> list, int index, E defaultValue) {
        return index < list.size() && index >= 0 ? list.get(index) : defaultValue;
    }

    public static int maxValueIdx(double[] arr) {
        if (arr.length == 0) {
            return -1;
        }
        int maxAt = 0;
        for (int i = 1, end = arr.length; i < end; i++) {
            maxAt = arr[i] > arr[maxAt] ? i : maxAt;
        }
        return maxAt;
    }

    public static int minValueIdx(double[] arr) {
        if (arr.length == 0) {
            return -1;
        }
        int minAt = 0;
        for (int i = 1, end = arr.length; i < end; i++) {
            minAt = arr[i] < arr[minAt] ? i : minAt;
        }
        return minAt;
    }

    public static double sumArr(double[] data, int from, int to) {
        double res = 0;
        for (int i = Math.max(from, 0), end = Math.min(to, data.length); i < end; i++) {
            res += data[i];
        }
        return res;
    }
}
