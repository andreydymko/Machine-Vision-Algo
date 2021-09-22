package com.andreydymko.recoginition1.Utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.IntRange;

import com.andreydymko.recoginition1.BinarizationCore.BinarizationCore;
import com.andreydymko.recoginition1.MainActivity;
import com.andreydymko.recoginition1.MarkingCore.MarkingCore;
import com.andreydymko.recoginition1.MarkingCore.PhysicalProperties;
import com.andreydymko.recoginition1.MaskingCore.MaskModel;
import com.andreydymko.recoginition1.MaskingCore.MaskingCore;
import com.andreydymko.recoginition1.MorphologicCore.MorphologicCore;
import com.andreydymko.recoginition1.MorphologicCore.MorphologicModel;
import com.andreydymko.recoginition1.RecognitionCore.RecognitionCore;

import java.lang.ref.WeakReference;

public class AsyncTasks {
    private AsyncTasks() {}

    public static class CountHolesTask extends AsyncTask<Bitmap, Void, Integer> {
        private WeakReference<MainActivity> mainActivityWeakRef;

        public CountHolesTask(MainActivity context) {
            this.mainActivityWeakRef = new WeakReference<>(context);
        }

        @Override
        protected Integer doInBackground(Bitmap... bitmaps) {
            int result = 0;
            for (Bitmap bitmap: bitmaps) {
                result += RecognitionCore.countHoles(bitmap);
                if (isCancelled()) break;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            MainActivity activity = mainActivityWeakRef.get();
            if (activity == null || activity.isFinishing()) return;
            activity.onCountingHolesFinish(result);
        }
    }

    public static class ApplyMaskTask extends AsyncTask<Bitmap, Void, Bitmap> {
        private WeakReference<MainActivity> mainActivityWeakRef;
        private MaskModel maskModel;

        public ApplyMaskTask(MainActivity context, MaskModel maskModel) {
            this.mainActivityWeakRef = new WeakReference<>(context);
            this.maskModel = maskModel;
        }

        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {
            Bitmap result = null;
            for (Bitmap bitmap: bitmaps) {
                result = MaskingCore.applyMaskToBitmap(bitmap, maskModel);
                break;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            MainActivity activity = mainActivityWeakRef.get();
            if (activity == null || activity.isFinishing()) return;
            activity.onApplyMaskFinish(result);
        }
    }

    public static class MarkItemsTask extends AsyncTask<Bitmap, Void, Bitmap> {
        private final static String TAG = MarkItemsTask.class.getSimpleName();
        private WeakReference<MainActivity> mainActivityWeakRef;

        public MarkItemsTask(MainActivity context) {
            this.mainActivityWeakRef = new WeakReference<>(context);
        }

        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {
            Bitmap result = null;
            for (Bitmap bitmap: bitmaps) {
                MarkingCore.MarkingResult markResult = MarkingCore.markObjects(bitmap);
                result = MarkingCore.colorMarkedBitmap(bitmap, markResult);
                break;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            MainActivity activity = mainActivityWeakRef.get();
            if (activity == null || activity.isFinishing()) return;
            activity.onMarkingFinish(result);
        }
    }

    public static class BinarizeBitmap extends AsyncTask<Bitmap, Void, Bitmap> {
        private WeakReference<MainActivity> mainActivityWeakRef;
        private @IntRange(from=0, to=255) int lowerLimit, upperLimit;

        public BinarizeBitmap(MainActivity context,
                              @IntRange(from=0, to=255) int lowerLimit,
                              @IntRange(from=0, to=255) int upperLimit) {
            this.mainActivityWeakRef = new WeakReference<>(context);
            this.lowerLimit = lowerLimit;
            this.upperLimit = upperLimit;
        }

        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {
            Bitmap result = null;
            for (Bitmap bitmap: bitmaps) {
                result = BinarizationCore.convertBitmapBinary(bitmap, lowerLimit, upperLimit);
                break;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            MainActivity activity = mainActivityWeakRef.get();
            if (activity == null || activity.isFinishing()) return;
            activity.onBinarizingFinish(result);
        }
    }

    private static class PhysCalcRes {
        private Bitmap bitmap;
        private PhysicalProperties.PhysProps physProps;

        public PhysCalcRes(Bitmap bitmap, PhysicalProperties.PhysProps physProps) {
            this.bitmap = bitmap;
            this.physProps = physProps;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public PhysicalProperties.PhysProps getPhysProps() {
            return physProps;
        }
    }

    public static class CalcPhysProps extends AsyncTask<Bitmap, Void, PhysCalcRes> {
        private WeakReference<MainActivity> mainActivityWeakRef;

        public CalcPhysProps(MainActivity context) {
            this.mainActivityWeakRef = new WeakReference<>(context);
        }

        @Override
        protected PhysCalcRes doInBackground(Bitmap... bitmaps) {
            Bitmap result = null;
            PhysicalProperties.PhysProps physProps = null;
            for (Bitmap bitmap: bitmaps) {
                MarkingCore.MarkingResult markResult = MarkingCore.markObjects(bitmap);
                physProps = PhysicalProperties.findAll(markResult);
                result = MarkingCore.colorMarkedBitmap(bitmap, markResult);
                Canvas canvas = new Canvas(result);
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setColor(Color.BLACK);
                Paint redPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                redPaint.setColor(Color.RED);
                float density = 0;
                int textSize = 32;
                if (mainActivityWeakRef != null) {
                    density = mainActivityWeakRef.get().getApplicationContext().getResources().getDisplayMetrics().density;
                    paint.setTextSize(density * textSize);
                } else {
                    paint.setTextSize(textSize*3);
                }
                int x, y;
                for (int i = 0; i < physProps.getCenterOfMass().length; i++) {
                    x = physProps.getCenterOfMass()[i].x;
                    y = physProps.getCenterOfMass()[i].y;
                    canvas.drawCircle(x, y, 5, redPaint);
                    if (density != 0) {
                        x -= density*textSize/2;
                        y += density*textSize/2;
                    }
                    canvas.drawText(String.valueOf(i), x, y, paint);
                }
                break;
            }
            return new PhysCalcRes(result, physProps);
        }

        @Override
        protected void onPostExecute(PhysCalcRes result) {
            super.onPostExecute(result);
            MainActivity activity = mainActivityWeakRef.get();
            if (activity == null || activity.isFinishing()) return;
            activity.onPhysCalcFinish(result.getBitmap(), result.getPhysProps());
        }
    }


    public static class MorphBitmap extends AsyncTask<Bitmap, Void, Bitmap> {
        private WeakReference<MainActivity> mainActivityWeakRef;
        private MorphologicModel morphologicModel;
        private int mode;

        public MorphBitmap(MainActivity context, MorphologicModel morphologicModel, int mode) {
            this.mainActivityWeakRef = new WeakReference<>(context);
            this.morphologicModel = morphologicModel;
            this.mode = mode;
        }

        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {
            Bitmap result = null;
            for (Bitmap bitmap: bitmaps) {
                result = MorphologicCore.morphBitmap(bitmap, morphologicModel, mode);
                break;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            MainActivity activity = mainActivityWeakRef.get();
            if (activity == null || activity.isFinishing()) return;
            activity.onMorphFinish(result);
        }
    }

    public static class SmartBinarizeBitmap extends AsyncTask<Bitmap, Void, Bitmap> {
        private WeakReference<MainActivity> mainActivityWeakRef;

        public SmartBinarizeBitmap(MainActivity context) {
            this.mainActivityWeakRef = new WeakReference<>(context);
        }

        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {
            Bitmap result = null;
            for (Bitmap bitmap: bitmaps) {
                int threshold = BinarizationCore.getBinarizationThreshold(bitmap);
                Log.d("threshold", "===" + threshold);
                result = BinarizationCore.convertBitmapBinary(bitmap, threshold, threshold);
                break;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            MainActivity activity = mainActivityWeakRef.get();
            if (activity == null || activity.isFinishing()) return;
            activity.onSmartBinarizingFinish(result);
        }
    }
}
