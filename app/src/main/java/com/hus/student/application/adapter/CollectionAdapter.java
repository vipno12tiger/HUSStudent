package com.hus.student.application.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hus.student.application.R;
import com.hus.student.application.module.Const;
import com.hus.student.application.module.OnClickCollection;
import com.hus.student.application.module.Transaction;
import com.hus.student.application.object.Collection;
import com.hus.student.application.object.Pay;

import java.util.List;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.CollectionHolder> {


    private List<Collection> collections;


    private OnClickCollection onClickCollection;


    public void setOnClickCollection(OnClickCollection onClickCollection) {
        this.onClickCollection = onClickCollection;
    }

    public void setCollections(List<Collection> collections) {
        this.collections = collections;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CollectionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_collection, parent, false);
        return new CollectionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionHolder holder, int position) {
        holder.tv_title.setText(collections.get(position).getTitle());
        holder.tv_title.setOnClickListener(v -> onClickCollection.OnClick(v, Const.COLLECTION, position));
        holder.itemView.setOnClickListener(v -> onClickCollection.OnClick(v, Const.COLLECTION, position));
    }

    @Override
    public int getItemCount() {
        if (collections != null) {
            return collections.size();
        }
        return 0;
    }

    public static class CollectionHolder extends RecyclerView.ViewHolder {

        public TextView tv_title;

        public CollectionHolder(@NonNull View itemView) {
            super(itemView);

            tv_title = itemView.findViewById(R.id.tv_title);
        }
    }
}
