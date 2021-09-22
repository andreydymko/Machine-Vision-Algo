package com.andreydymko.recoginition1;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.andreydymko.recoginition1.BinarizationCore.BinarizationCore;
import com.andreydymko.recoginition1.DatabaseActivity.DatabaseActivity;
import com.andreydymko.recoginition1.DrawingFragment.DrawingFragment;
import com.andreydymko.recoginition1.MarkingCore.PhysicalProperties;
import com.andreydymko.recoginition1.MaskingCore.MaskModel;
import com.andreydymko.recoginition1.MorphologicCore.MorphologicCore;
import com.andreydymko.recoginition1.MorphologicCore.MorphologicModel;
import com.andreydymko.recoginition1.RecognitionCore.RecognitionCore;
import com.andreydymko.recoginition1.Utils.AsyncTasks;
import com.andreydymko.recoginition1.Utils.BitmapUtils;
import com.andreydymko.recoginition1.Utils.MapUtils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity {

    public static final String appPrefMainKey = "RecognitionApplicationPreferencesKey";

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int ACTION_TAG_PICK_IMAGE = 0x16;

    private DrawingFragment mDrawingFragment;
    private RecognitionCore recognitionCore;
    private AlertDialog saveImageDialog, resultsDialog;
    private TextView resultsTextView;
    private Menu toolbarMenu;
    private boolean mReturningWithResult = true;
    private Uri mResUri;
    private PhysicalProperties.PhysProps lastPhysCalcRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        resultsTextView = new TextView(this);

        mDrawingFragment = (DrawingFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawing);
        recognitionCore = new RecognitionCore(this);

        buildDialogs();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mReturningWithResult && mResUri != null) {
            try {
                Bitmap bitmap = BitmapUtils.resizeBitmapKeepAspectRatio(
                        MediaStore.Images.Media.getBitmap(this.getContentResolver(), mResUri),
                        this.getWindow().getDecorView().getWidth(),
                        this.getWindow().getDecorView().getHeight());
                setFragmentBitmap(bitmap.copy(bitmap.getConfig(), true));
                mReturningWithResult = false;
            } catch (IOException e) {
                mReturningWithResult = false;
                e.printStackTrace();
            }
        }
    }

    private void buildDialogs() {
        AlertDialog.Builder saveImageDialogBuilder = new AlertDialog.Builder(this);
        final EditText nameEditText = new EditText(this);
        saveImageDialogBuilder.setTitle(R.string.save_template)
                .setView(nameEditText)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String s = nameEditText.getText().toString();
                        if (!s.isEmpty()) {
                            saveImage(s);
                        }
                        nameEditText.getText().clear();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        nameEditText.getText().clear();
                    }
                });
        saveImageDialog = saveImageDialogBuilder.create();

        AlertDialog.Builder resDialogBuilder = new AlertDialog.Builder(this);
        resDialogBuilder.setTitle(R.string.results)
                .setView(resultsTextView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        resultsDialog = resDialogBuilder.create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.toolbarMenu = menu;
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_toolbar_smart_binarize:
                smartBinarizeBitmap();
                break;
            case R.id.action_toolbar_built_up:
                morphBitmap(MorphologicCore.MORPH_MODE.BUILT_UP);
                break;
            case R.id.action_toolbar_erosion:
                morphBitmap(MorphologicCore.MORPH_MODE.EROSION);
                break;
            case R.id.action_toolbar_closing:
                morphBitmap(MorphologicCore.MORPH_MODE.CLOSING);
                break;
            case R.id.action_toolbar_breaking:
                morphBitmap(MorphologicCore.MORPH_MODE.BREAKING);
                break;
            case R.id.action_toolbar_phys_calc:
                calcPhysProps();
                break;
            case R.id.action_toolbar_phys_res:
                showLastPhysCalcRes();
                break;
            case R.id.action_toolbar_binarize:
                binarizeBitmap();
                break;
            case R.id.action_toolbar_new_binarize_model:
                Intent intent = new Intent(this, ActivityBinarizeGraph.class);
                //intent.putExtra("Image", mDrawingFragment.getImageBitmap());
                startActivity(intent);
                break;
            case R.id.action_toolbar_mark_objects:
                markObjects();
                break;
            case R.id.action_toolbar_create_mask:
                break;
            case R.id.action_toolbar_apply_mask:
                applyMask();
                break;
            case R.id.action_toolbar_open_image:
                openImageFromGallery();
                break;
            case R.id.action_toolbar_count_holes:
                countHoles();
                break;
            case R.id.action_toolbar_save:
                if (saveImageDialog != null) {
                    saveImageDialog.show();
                }
                break;
            case R.id.action_toolbar_recognize:
                recognizeImage();
                break;
            case R.id.action_toolbar_erase_canvas:
                clearCanvas();
                break;
            case R.id.action_toolbar_database:
                startActivity(new Intent(this, DatabaseActivity.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void openImageFromGallery() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, ACTION_TAG_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == ACTION_TAG_PICK_IMAGE) {
                mReturningWithResult = true;
                mResUri = data.getData();
            }
        }
    }

    private void saveImage(String name) {
        if (mDrawingFragment != null) {
            recognitionCore.savePattern(name, mDrawingFragment.getImageBitmap());
            clearCanvas();
        }
    }

    private void clearCanvas() {
        if (mDrawingFragment != null) {
            mDrawingFragment.clearCanvas();
        }
    }

    private void recognizeImage() {
        if (mDrawingFragment != null) {
            Map<String, Double> res = recognitionCore.recognizeBitmap(mDrawingFragment.getImageBitmap());
            showResultDialog(MapUtils.sortMapByValue(res).toString());
        }
    }

    private void countHoles() {
        setToolbarItemState(R.id.action_toolbar_count_holes, false);
        if (mDrawingFragment != null) {
            new AsyncTasks.CountHolesTask(this).execute(mDrawingFragment.getImageBitmap());
        }
    }

    public void onCountingHolesFinish(int result) {
        showResultDialog(MessageFormat.format("Total holes count: {0}", result));
        setToolbarItemState(R.id.action_toolbar_count_holes, true);
    }

    private void applyMask() {
        setToolbarItemState(R.id.action_toolbar_apply_mask, false);
        if (mDrawingFragment != null) {
            new AsyncTasks.ApplyMaskTask(this,
                    new MaskModel(
                            new double[][]{
                                    {0, 1, 0},
                                    {1, -4, 1},
                                    {0, 1, 0},
                            }, new Point(1, 1)
                            )).execute(mDrawingFragment.getImageBitmap());
        }
    }

    public void onApplyMaskFinish(Bitmap result) {
        setFragmentBitmap(result);
        setToolbarItemState(R.id.action_toolbar_apply_mask, true);
    }

    private void markObjects() {
        setToolbarItemState(R.id.action_toolbar_mark_objects, false);
        if (mDrawingFragment != null) {
            new AsyncTasks.MarkItemsTask(this).execute(mDrawingFragment.getImageBitmap());
        }
    }

    public void onMarkingFinish(Bitmap result) {
        setFragmentBitmap(result);
        setToolbarItemState(R.id.action_toolbar_mark_objects, true);
    }

    private void smartBinarizeBitmap() {
        setToolbarItemState(R.id.action_toolbar_smart_binarize, false);
        if (mDrawingFragment != null) {
            new AsyncTasks.SmartBinarizeBitmap(this)
                    .execute(mDrawingFragment.getImageBitmap());
        }
    }

    public void onSmartBinarizingFinish(Bitmap result) {
        setFragmentBitmap(result);
        setToolbarItemState(R.id.action_toolbar_smart_binarize, true);
    }

    private void binarizeBitmap() {
        SharedPreferences preferences = getSharedPreferences(appPrefMainKey, MODE_PRIVATE);
        setToolbarItemState(R.id.action_toolbar_binarize, false);
        if (mDrawingFragment != null) {
            new AsyncTasks.BinarizeBitmap(this,
                    preferences.getInt(BinarizationCore.lowerBinarizationBoundPrefKey, BinarizationCore.minBinarizationBound),
                    preferences.getInt(BinarizationCore.upperBinarizationBoundPrefKey, BinarizationCore.maxBinarizationBound))
                    .execute(mDrawingFragment.getImageBitmap());
        }
    }

    public void onBinarizingFinish(Bitmap result) {
        setFragmentBitmap(result);
        setToolbarItemState(R.id.action_toolbar_binarize, true);
    }

    public void calcPhysProps() {
        setToolbarItemState(R.id.action_toolbar_phys_calc, false);
        if (mDrawingFragment != null) {
            new AsyncTasks.CalcPhysProps(this).execute(mDrawingFragment.getImageBitmap());
        }
    }

    public void onPhysCalcFinish(Bitmap result, PhysicalProperties.PhysProps physProps) {
        this.lastPhysCalcRes = physProps;
        setFragmentBitmap(result);
        setToolbarItemState(R.id.action_toolbar_phys_calc, true);
        showLastPhysCalcRes();
    }

    private void showLastPhysCalcRes() {
        if (lastPhysCalcRes != null) {
            showResultDialog(this.lastPhysCalcRes.toString());
        }
    }

    private void morphBitmap(int mode) {
        setToolbarItemState(R.id.action_toolbar_erosion, false);
        setToolbarItemState(R.id.action_toolbar_built_up, false);
        setToolbarItemState(R.id.action_toolbar_breaking, false);
        setToolbarItemState(R.id.action_toolbar_closing, false);
        if (mDrawingFragment != null) {
            new AsyncTasks.MorphBitmap(this,
                    new MorphologicModel(
                            new boolean[][]{
                                    {true, true, true, true, true, true, true},
                                    {true, true, true, true, true, true, true},
                                    {true, true, true, true, true, true, true},
                                    {true, true, true, true, true, true, true},
                                    {true, true, true, true, true, true, true},
                                    {true, true, true, true, true, true, true},
                                    {true, true, true, true, true, true, true}
                            }, new Point(3, 3)
                    ), mode).execute(mDrawingFragment.getImageBitmap());
        }
    }

    public void onMorphFinish(Bitmap result) {
        setToolbarItemState(R.id.action_toolbar_erosion, true);
        setToolbarItemState(R.id.action_toolbar_built_up,true);
        setToolbarItemState(R.id.action_toolbar_breaking,true);
        setToolbarItemState(R.id.action_toolbar_closing, true);
        setFragmentBitmap(result);
    }

    private void setFragmentBitmap(Bitmap bitmap) {
        if (mDrawingFragment != null) {
            mDrawingFragment.setImageBitmap(bitmap);
        }
    }

    private void setToolbarItemState(@IdRes int resId, boolean enabled) {
        if (toolbarMenu != null) {
            toolbarMenu.findItem(resId).setEnabled(enabled);
        }
    }

    private void showResultDialog(String text) {
        resultsTextView.setText(text);
        resultsDialog.show();

        TreeSet
    }
}