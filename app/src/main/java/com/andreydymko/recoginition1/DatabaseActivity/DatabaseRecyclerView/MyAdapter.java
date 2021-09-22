package com.andreydymko.recoginition1.DatabaseActivity.DatabaseRecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andreydymko.recoginition1.R;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> implements ItemTouchHelperCallback.ItemSwipeListener {
    private List<ViewModel> mDataset;
    private DataSetChangingListener listener;

    public MyAdapter() {
        mDataset = new ArrayList<>();
    }

    public void setOnDataSetChangeListener(DataSetChangingListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ViewModel viewModel = mDataset.get(position);
        initView(holder, viewModel);
    }

    @Override
    public void onItemDismiss(int pos) {
        listener.onItemRemoving(mDataset.get(pos), pos);
    }

    private void initView(final MyViewHolder holder, ViewModel viewModel) {
        holder.textName.setText(viewModel.getName());
        holder.imageView.setImageBitmap(viewModel.getBitmap());
    }

    public void addItem(ViewModel viewModel) {
        this.mDataset.add(viewModel);
        notifyItemInserted(mDataset.size()-1);
    }

    public void deleteItem(int pos) {
        this.mDataset.remove(pos);
        notifyItemRemoved(pos);
    }

    public interface DataSetChangingListener {
        void onItemRemoving(ViewModel viewModel, int pos);
        void onItemAdding(ViewModel viewModel);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
