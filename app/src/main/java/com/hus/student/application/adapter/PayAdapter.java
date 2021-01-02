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
import com.hus.student.application.object.Pay;

import java.util.List;

public class PayAdapter extends RecyclerView.Adapter<PayAdapter.PayHolder> {


    private List<Pay> pays;


    private OnClickCollection onClickCollection;


    public void setOnClickCollection(OnClickCollection onClickCollection) {
        this.onClickCollection = onClickCollection;
    }

    public void setPays(List<Pay> pays) {
        this.pays = pays;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PayHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_collection, parent, false);
        return new PayHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PayHolder holder, int position) {
        holder.tv_title.setText(pays.get(position).getTitle());
        holder.tv_title.setOnClickListener(v -> onClickCollection.OnClick(v, Const.PAY, position));
        holder.itemView.setOnClickListener(v -> onClickCollection.OnClick(v, Const.PAY, position));
    }
    @Override
    public int getItemCount() {
        if (pays != null) {
            return pays.size();
        }
        return 0;
    }

    public static class PayHolder extends RecyclerView.ViewHolder {

        public TextView tv_title;

        public PayHolder(@NonNull View itemView) {
            super(itemView);

            tv_title = itemView.findViewById(R.id.tv_title);
        }
    }
}
