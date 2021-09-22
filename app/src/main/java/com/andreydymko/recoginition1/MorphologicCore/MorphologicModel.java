package com.andreydymko.recoginition1.MorphologicCore;

import android.graphics.Point;

public class MorphologicModel {
    private boolean[][] mask;
    private Point mainPixel;

    public MorphologicModel(boolean[][] mask, Point mainPixel) {
        this.mask = mask;
        this.mainPixel = mainPixel;
    }

    public MorphologicModel(boolean[][] mask) {
        this.mask = mask;
        this.mainPixel = new Point(0, 0);
    }

    public boolean[][] getMask() {
        return mask;
    }

    public void setMask(boolean[][] mask) {
        this.mask = mask;
    }

    public Point getMainPixel() {
        return mainPixel;
    }

    public void setMainPixel(Point mainPixel) {
        this.mainPixel = mainPixel;
    }
}
