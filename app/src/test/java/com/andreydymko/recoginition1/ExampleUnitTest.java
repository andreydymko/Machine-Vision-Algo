package com.andreydymko.recoginition1;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void is_arrays_by_reference() {
        int[][] arr = new int[2][2];
        for (int[] subArr : arr) {
            for (int i = 0; i < subArr.length; i++) {
                subArr[i] = 8;
            }
        }
        assertArrayEquals(new int[][] {{8, 8}, {8, 8}}, arr);
    }
}