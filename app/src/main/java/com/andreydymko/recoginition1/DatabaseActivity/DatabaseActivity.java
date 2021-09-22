package com.andreydymko.recoginition1.DatabaseActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;

import com.andreydymko.recoginition1.DatabaseActivity.DatabaseRecyclerView.ItemTouchHelperCallback;
import com.andreydymko.recoginition1.DatabaseActivity.DatabaseRecyclerView.MyAdapter;
import com.andreydymko.recoginition1.DatabaseActivity.DatabaseRecyclerView.ViewModel;
import com.andreydymko.recoginition1.R;
import com.andreydymko.recoginition1.RecognitionCore.RecognitionCore;
import com.andreydymko.recoginition1.RecognitionCore.RecognitionDatabase;
import com.andreydymko.recoginition1.RecognitionCore.RecognitionModel;

import java.io.IOException;

public class DatabaseActivity extends AppCompatActivity implements MyAdapter.DataSetChangingListener {
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ItemTouchHelper mItemTouchHelper;
    private RecognitionDatabase databaseInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        Toolbar toolbar = findViewById(R.id.database_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        layoutManager = new LinearLayoutManager(this);
        mAdapter = new MyAdapter();
        mAdapter.setOnDataSetChangeListener(this);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        databaseInstance = RecognitionDatabase.getInstance(this);
        fillListFromDatabase();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void fillListFromDatabase() {
        for (RecognitionModel model : databaseInstance.loadModels()) {
            mAdapter.addItem(new ViewModel(model.getName(),
                    RecognitionCore.generateBitmap(model, Color.BLACK, Color.WHITE)));
        }
    }

    @Override
    public void onItemRemoving(ViewModel viewModel, int pos) {
        try {
            databaseInstance.deleteModel(viewModel.getName());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        mAdapter.deleteItem(pos);
    }

    @Override
    public void onItemAdding(ViewModel viewModel) {

    }
}