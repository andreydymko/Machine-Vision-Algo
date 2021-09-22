package com.andreydymko.recoginition1.DatabaseActivity.DatabaseRecyclerView;

import android.graphics.Bitmap;

public class ViewModel {
    private String name;
    private Bitmap bitmap;

    public ViewModel(String name, Bitmap bitmap) {
        this.name = name;
        this.bitmap = bitmap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
