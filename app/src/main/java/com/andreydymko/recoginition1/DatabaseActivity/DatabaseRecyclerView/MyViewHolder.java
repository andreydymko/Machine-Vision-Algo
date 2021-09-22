package com.andreydymko.recoginition1.DatabaseActivity.DatabaseRecyclerView;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.andreydymko.recoginition1.R;


public class MyViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperCallback.ItemTouchListener {
    public View view;
    public TextView textName;
    public ImageView imageView;
    public MyViewHolder(View view) {
        super(view);
        this.view = view;
        this.textName = view.findViewById(R.id.textViewName);
        this.imageView = view.findViewById(R.id.imageView);
    }

    @Override
    public void onItemSelected() {
        itemView.setBackgroundColor(Color.LTGRAY);
    }

    @Override
    public void onItemClear() {
        itemView.setBackgroundColor(0);
    }
}
