package com.andreydymko.recoginition1.RecognitionCore;

import java.util.BitSet;

public class RecognitionModel {
    private String name;
    private BitSet data;
    private int width;
    private int height;

    public RecognitionModel(String name, BitSet data, int width, int height) {
        this.name = name;
        this.data = data;
        this.width = width;
        this.height = height;
    }

    public RecognitionModel(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BitSet getData() {
        return data;
    }

    public void setData(BitSet data) {
        this.data = data;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
