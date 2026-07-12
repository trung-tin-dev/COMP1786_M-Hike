package com.example.m_hike.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_hike.R;
import com.example.m_hike.model.Hike;

import java.util.List;

public class TrashHikeAdapter
        extends RecyclerView.Adapter<TrashHikeAdapter.ViewHolder> {

    public interface OnTrashActionListener {

        void onRestore(Hike hike);

        void onDeleteForever(Hike hike);

    }

    private final List<Hike> hikeList;

    private final OnTrashActionListener listener;

    public TrashHikeAdapter(List<Hike> hikeList,
                            OnTrashActionListener listener) {

        this.hikeList = hikeList;
        this.listener = listener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_trash_hike,
                        parent,
                        false
                );

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        Hike hike = hikeList.get(position);

        holder.tvName.setText(hike.getName());

        holder.tvDeletedDate.setText(
                "Deleted: " + hike.getDeletedAt()
        );

        holder.btnRestore.setOnClickListener(v ->
                listener.onRestore(hike));

        holder.btnDeleteForever.setOnClickListener(v ->
                listener.onDeleteForever(hike));

    }

    @Override
    public int getItemCount() {

        return hikeList.size();

    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvDeletedDate;

        Button btnRestore;
        Button btnDeleteForever;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            tvName = itemView.findViewById(R.id.tvTrashName);

            tvDeletedDate = itemView.findViewById(R.id.tvTrashDate);

            btnRestore = itemView.findViewById(R.id.btnRestore);

            btnDeleteForever =
                    itemView.findViewById(R.id.btnDeleteForever);

        }
    }
}