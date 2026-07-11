package com.example.m_hike.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_hike.R;
import com.example.m_hike.model.Hike;

import java.util.List;

public class HikeAdapter extends RecyclerView.Adapter<HikeAdapter.HikeViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Hike hike);
    }

    private final List<Hike> hikeList;
    private OnItemClickListener listener;
    public HikeAdapter(List<Hike> hikeList,
                       OnItemClickListener listener) {

        this.hikeList = hikeList;
        this.listener = listener;

    }

    @NonNull
    @Override
    public HikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hike, parent, false);

        return new HikeViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HikeViewHolder holder, int position) {

        Hike hike = hikeList.get(position);

        holder.tvName.setText(hike.getName());
        holder.tvLocation.setText(hike.getLocation());
        holder.tvDate.setText(hike.getDate());
        holder.tvDifficulty.setText(hike.getDifficulty());
        holder.itemView.setOnClickListener(v -> {

            listener.onItemClick(hike);

        });
    }

    @Override
    public int getItemCount() {
        return hikeList.size();
    }

    static class HikeViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvLocation;
        TextView tvDate;
        TextView tvDifficulty;

        public HikeViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvHikeName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDifficulty = itemView.findViewById(R.id.tvDifficulty);
        }
    }
}