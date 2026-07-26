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

    private List<Hike> hikeList;
    private OnItemClickListener listener;

    public void updateList(List<Hike> newList) {
        this.hikeList = newList;
        notifyDataSetChanged();
    }
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

        // Set difficulty color
        int color;
        switch (hike.getDifficulty()) {
            case "Easy":
                color = holder.itemView.getContext().getResources().getColor(R.color.success, null);
                break;
            case "Medium":
                color = holder.itemView.getContext().getResources().getColor(R.color.warning, null);
                break;
            case "Hard":
                color = holder.itemView.getContext().getResources().getColor(R.color.danger, null);
                break;
            default:
                color = holder.itemView.getContext().getResources().getColor(R.color.text_green, null);
                break;
        }
        holder.tvDifficulty.setTextColor(color);
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