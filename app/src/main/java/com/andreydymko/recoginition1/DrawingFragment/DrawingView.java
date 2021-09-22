package com.andreydymko.recoginition1.DrawingFragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

import java.text.MessageFormat;


public class DrawingView extends View {

    private static final String TAG = DrawingView.class.getSimpleName();

    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;

    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    public void setBrushColor(@ColorInt int paintColor) {
        drawPaint.setColor(paintColor);
//        if (paintColor == Color.TRANSPARENT) {
//            drawPaint.setColor(paintColor);
//            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//        } else {
//            drawPaint.setColor(paintColor);
//            drawPaint.setXfermode(null);
//        }
    }

    public void setBrushSize(float size) {
        drawPaint.setStrokeWidth(size);
    }

    public Bitmap getCanvasBitmap() {
        return canvasBitmap;
    }

    public void setCanvasBitmap(Bitmap bitmap) {
        if (bitmap.getWidth() < canvasBitmap.getWidth() || bitmap.getHeight() < canvasBitmap.getHeight()) {
            canvasBitmap = Bitmap.createScaledBitmap(bitmap, canvasBitmap.getWidth(), bitmap.getHeight(), true);
        } else {
            canvasBitmap = Bitmap.createBitmap(bitmap,
                    bitmap.getWidth() / 2 - canvasBitmap.getWidth() / 2,
                    bitmap.getHeight() / 2 - canvasBitmap.getHeight() / 2,
                    canvasBitmap.getWidth(),
                    canvasBitmap.getHeight());
        }
        setupCanvas();
        invalidate();
    }

    public void clearCanvas() {
        canvasBitmap = null;
        setupCanvas(this.getWidth(), this.getHeight());
        invalidate();
    }

    private void setupCanvas() {
        setupCanvas(canvasBitmap.getWidth(), canvasBitmap.getHeight());
    }

    private void setupCanvas(int width, int height) {
        if (canvasBitmap == null) {
            canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            drawCanvas = new Canvas(canvasBitmap);
            drawCanvas.drawColor(Color.WHITE);
        } else {
            canvasBitmap = Bitmap.createScaledBitmap(canvasBitmap, width, height, false);
            drawCanvas = new Canvas(canvasBitmap);
        }
    }

    private void setupDrawing() {
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(Color.BLACK);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (oldh != h || oldw != w) {
            setupCanvas(w, h);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }
}
