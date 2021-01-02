package com.hus.student.application.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hus.student.application.R;
import com.hus.student.application.module.OnClickItemRecyclerView;
import com.hus.student.application.object.Selection;

import java.util.List;

public class SelectionAdapter extends RecyclerView.Adapter<SelectionAdapter.SelectionHolder> {

    private List<Selection> selections;
    private OnClickItemRecyclerView onClickItemRecyclerView;

    public void setOnClickItemRecyclerView(OnClickItemRecyclerView onClickItemRecyclerView) {
        this.onClickItemRecyclerView = onClickItemRecyclerView;
    }

    public void setSelections(List<Selection> selections) {
        this.selections = selections;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SelectionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_selection, parent, false);
        return new SelectionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectionHolder holder, int position) {
        holder.bt_icon.setBackgroundResource(selections.get(position).getID());
        holder.tv_icon.setText(selections.get(position).getText());
        holder.tv_icon.setOnClickListener(v -> onClickItemRecyclerView.onClickItem(v, position));
        holder.bt_icon.setOnClickListener(v -> onClickItemRecyclerView.onClickItem(v, position));
        holder.itemView.setOnClickListener(v -> onClickItemRecyclerView.onClickItem(v, position));
    }

    @Override
    public int getItemCount() {
        if (selections != null) {
            return selections.size();
        }
        return 0;
    }

    public static class SelectionHolder extends RecyclerView.ViewHolder {
        public Button bt_icon;
        public TextView tv_icon;

        public SelectionHolder(@NonNull View itemView) {
            super(itemView);
            bt_icon = itemView.findViewById(R.id.bt_icon);
            tv_icon = itemView.findViewById(R.id.tv_icon);
        }
    }
}
