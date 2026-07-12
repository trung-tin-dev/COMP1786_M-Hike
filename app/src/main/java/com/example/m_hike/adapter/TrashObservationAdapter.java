package com.example.m_hike.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_hike.R;
import com.example.m_hike.model.Observation;

import java.util.List;

public class TrashObservationAdapter
        extends RecyclerView.Adapter<TrashObservationAdapter.ViewHolder> {

    public interface OnTrashObservationListener {

        void onRestore(Observation observation);

        void onDeleteForever(Observation observation);

    }

    private final List<Observation> observationList;
    private final OnTrashObservationListener listener;

    public TrashObservationAdapter(
            List<Observation> observationList,
            OnTrashObservationListener listener) {

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
                        R.layout.item_trash_observation,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        Observation observation = observationList.get(position);

        holder.tvTitle.setText(observation.getTitle());

        holder.tvTime.setText(
                "Time : " + observation.getObservationTime()
        );

        holder.tvDeletedDate.setText(
                "Deleted : " + observation.getDeletedAt()
        );

        holder.btnRestore.setOnClickListener(v ->
                listener.onRestore(observation));

        holder.btnDeleteForever.setOnClickListener(v ->
                listener.onDeleteForever(observation));

    }

    @Override
    public int getItemCount() {
        return observationList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvTime;
        TextView tvDeletedDate;

        Button btnRestore;
        Button btnDeleteForever;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvObservationTitle);
            tvTime = itemView.findViewById(R.id.tvObservationTime);
            tvDeletedDate = itemView.findViewById(R.id.tvDeletedDate);

            btnRestore = itemView.findViewById(R.id.btnRestore);
            btnDeleteForever =
                    itemView.findViewById(R.id.btnDeleteForever);
        }
    }
}