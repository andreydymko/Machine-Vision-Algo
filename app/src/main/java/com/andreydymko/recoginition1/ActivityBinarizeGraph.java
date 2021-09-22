package com.andreydymko.recoginition1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.andreydymko.recoginition1.BinarizationCore.BinarizationCore;

public class ActivityBinarizeGraph extends AppCompatActivity {

    private SeekBar seekBarLower, seekBarUpper;
    private ImageView imageViewGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binarize_graph);

        Toolbar toolbar = findViewById(R.id.binarize_graph_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        seekBarLower = findViewById(R.id.seekBarBinarizeLower);
        seekBarUpper = findViewById(R.id.seekBarBinarizeUpper);
        imageViewGraph = findViewById(R.id.imageViewGraph);

//        if (getIntent().hasExtra("Image")) {
//            try {
//                generateBitmapGraph(MarkingCore.getColorsDataSet((Bitmap) getIntent().getParcelableExtra("Image")));
//            } catch (NullPointerException e) {
//                e.printStackTrace();
//            }
//
//        }
    }

    private void generateBitmapGraph(int[] data) {
        int height = imageViewGraph.getHeight();
        int width = imageViewGraph.getWidth();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSliders();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveSliders();
    }

    private void loadSliders() {
        SharedPreferences preferences = getSharedPreferences(MainActivity.appPrefMainKey, MODE_PRIVATE);
        seekBarLower.setProgress(preferences.getInt(BinarizationCore.lowerBinarizationBoundPrefKey, BinarizationCore.minBinarizationBound));
        seekBarUpper.setProgress(preferences.getInt(BinarizationCore.upperBinarizationBoundPrefKey, BinarizationCore.maxBinarizationBound));
    }

    private void saveSliders() {
        SharedPreferences.Editor editor = getSharedPreferences(MainActivity.appPrefMainKey, MODE_PRIVATE).edit();
        editor.putInt(BinarizationCore.lowerBinarizationBoundPrefKey, seekBarLower.getProgress());
        editor.putInt(BinarizationCore.upperBinarizationBoundPrefKey, seekBarUpper.getProgress());
        editor.apply();
    }
}