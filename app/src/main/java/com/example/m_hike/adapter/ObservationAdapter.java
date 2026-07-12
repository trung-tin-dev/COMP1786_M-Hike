package com.example.m_hike.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_hike.R;
import com.example.m_hike.model.Observation;

import java.util.List;

public class ObservationAdapter
        extends RecyclerView.Adapter<ObservationAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Observation observation);
    }

    private final List<Observation> observationList;
    private final OnItemClickListener listener;

    public ObservationAdapter(
            List<Observation> observationList,
            OnItemClickListener listener) {

        this.observationList = observationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_observation,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        Observation observation =
                observationList.get(position);

        holder.tvTitle.setText(
                observation.getTitle());

        holder.tvTime.setText(
                observation.getObservationTime());

        holder.tvNote.setText(
                observation.getNote());

        holder.itemView.setOnClickListener(v -> {

            listener.onItemClick(observation);

        });
    }

    @Override
    public int getItemCount() {
        return observationList.size();
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvTime;
        TextView tvNote;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle =
                    itemView.findViewById(R.id.tvObservationTitle);

            tvTime =
                    itemView.findViewById(R.id.tvObservationTime);

            tvNote =
                    itemView.findViewById(R.id.tvObservationNote);
        }
    }
}