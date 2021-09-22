package com.andreydymko.recoginition1.DrawingFragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.IdRes;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.andreydymko.recoginition1.R;

public class DrawingFragment extends Fragment implements RadioGroup.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {

    private static final String TAG = DrawingFragment.class.getName();

    private DrawingView mDrawingView;
    private RadioGroup mGroupBrush;
    private SeekBar mAlphaSeekBar;

    public DrawingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drawing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDrawingView = view.findViewById(R.id.drawing_view);
        view.setBackgroundColor(Color.WHITE);
        mGroupBrush = view.findViewById(R.id.group_brush);
        mGroupBrush.setOnCheckedChangeListener(this);
        SeekBar brushSeekBar = view.findViewById(R.id.brush_size_seek_bar);
        brushSeekBar.setOnSeekBarChangeListener(this);
        mAlphaSeekBar = view.findViewById(R.id.alpha_seek_bar);
        mAlphaSeekBar.setOnSeekBarChangeListener(this);
        mDrawingView.setBrushSize(brushSeekBar.getProgress());
        mDrawingView.setBrushColor(getColorToSet(mGroupBrush.getCheckedRadioButtonId(), getCurrAlpha()));
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group.getId() == R.id.group_brush) {
            setBrushColor(getColorToSet(checkedId, getCurrAlpha()));
        }
    }

    @IntRange(from=0, to=255)
    private int getCurrAlpha() {
        if (mAlphaSeekBar != null) {
            return mAlphaSeekBar.getProgress();
        }
        return 0;
    }

    private @ColorInt int getColorToSet(@IdRes int radioButtonId, @IntRange(from=0, to=255) int alpha) {
        int color;
        switch (radioButtonId) {
            case R.id.radio_button_white:
                color = Color.WHITE;
                break;
            case R.id.radio_button_black:
            default:
                color = Color.BLACK;
        }
        return ColorUtils.setAlphaComponent(color, alpha);
    }

    private void setBrushColor(@ColorInt int color) {
        if (mDrawingView != null) {
            mDrawingView.setBrushColor(color);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.brush_size_seek_bar:
                setBrushSize(progress);
                break;
            case R.id.alpha_seek_bar:
                if (mGroupBrush != null) {
                    setBrushColor(getColorToSet(mGroupBrush.getCheckedRadioButtonId(), getCurrAlpha()));
                }
            default:
                break;
        }
    }

    private void setBrushSize(float size) {
        if (mDrawingView != null) {
            mDrawingView.setBrushSize(size);
        }
    }

    public void clearCanvas() {
        if (mDrawingView != null) {
            mDrawingView.clearCanvas();
        }
    }

    public Bitmap getImageBitmap() {
        if (mDrawingView != null) {
            return mDrawingView.getCanvasBitmap();
        }
        return null;
    }

    public void setImageBitmap(Bitmap bitmap) {
        if (mDrawingView != null) {
            mDrawingView.setCanvasBitmap(bitmap);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}