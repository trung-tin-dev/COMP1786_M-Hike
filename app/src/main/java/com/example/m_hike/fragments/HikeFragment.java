package com.example.m_hike.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.m_hike.AddHikeActivity;
import com.example.m_hike.HikeDetailActivity;
import com.example.m_hike.R;
import com.example.m_hike.adapter.HikeAdapter;
import com.example.m_hike.database.DatabaseHelper;
import com.example.m_hike.model.Hike;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class HikeFragment extends Fragment {

    private RecyclerView rvHikes;
    private EditText etSearch;
    private FloatingActionButton fabAdd;
    private DatabaseHelper databaseHelper;
    private HikeAdapter adapter;
    private List<Hike> allHikesList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_hike,
                container,
                false);

        rvHikes = view.findViewById(R.id.rvHikes);
        etSearch = view.findViewById(R.id.etSearch);
        fabAdd = view.findViewById(R.id.fabAdd);

        databaseHelper = new DatabaseHelper(requireContext());

        rvHikes.setLayoutManager(
                new LinearLayoutManager(getContext()));

        loadHikes();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        fabAdd.setOnClickListener(v -> {

            startActivity(new Intent(
                    getActivity(),
                    AddHikeActivity.class));

        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        loadHikes();
    }

    private void loadHikes() {
        SharedPreferences preferences = requireContext().getSharedPreferences("MHIKE", MODE_PRIVATE);
        int userId = preferences.getInt("userId", -1);
        allHikesList = databaseHelper.getAllHikes(userId);

        adapter = new HikeAdapter(
                allHikesList,
                hike -> {

                    Intent intent = new Intent(
                            getActivity(),
                            HikeDetailActivity.class);

                    intent.putExtra(
                            "HIKE_ID",
                            hike.getId());

                    startActivity(intent);

                });
        rvHikes.setAdapter(adapter);
    }

    private void filter(String text) {
        if (allHikesList == null) return;
        
        java.util.List<Hike> filteredList = new java.util.ArrayList<>();
        for (Hike item : allHikesList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                item.getLocation().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.updateList(filteredList);
    }
}