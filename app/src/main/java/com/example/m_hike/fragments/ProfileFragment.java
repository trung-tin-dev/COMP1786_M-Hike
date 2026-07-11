package com.example.m_hike.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m_hike.LoginActivity;
import com.example.m_hike.R;
import com.example.m_hike.adapter.TrashAdapter;
import com.example.m_hike.database.DatabaseHelper;
import com.example.m_hike.model.Hike;

import java.util.List;

public class ProfileFragment extends Fragment {

    private TextView tvUsername;
    private TextView tvEmail;
    private Button btnLogout;
    private RecyclerView rvTrash;

    private DatabaseHelper databaseHelper;

    private TrashAdapter adapter;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        rvTrash = view.findViewById(R.id.rvTrash);

        databaseHelper = new DatabaseHelper(requireContext());

        rvTrash.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        loadTrash();

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        tvUsername = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);
        btnLogout = view.findViewById(R.id.btnLogout);

        SharedPreferences preferences =
                requireActivity().getSharedPreferences("MHIKE", Context.MODE_PRIVATE);

        String username = preferences.getString("username", "");
        String email = preferences.getString("email", "");

        tvUsername.setText(username);
        tvEmail.setText(email);

        btnLogout.setOnClickListener(v -> logout());

    }

    private void logout() {

        SharedPreferences preferences =
                requireActivity().getSharedPreferences("MHIKE", Context.MODE_PRIVATE);

        preferences.edit().clear().apply();

        Intent intent = new Intent(getActivity(), LoginActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

    }

    private void loadTrash() {

        List<Hike> hikeList =
                databaseHelper.getDeletedHikes();

        adapter = new TrashAdapter(
                hikeList,
                new TrashAdapter.OnTrashActionListener() {

                    @Override
                    public void onRestore(Hike hike) {

                        boolean success =
                                databaseHelper.restoreHike(
                                        hike.getId()
                                );

                        if (success) {

                            Toast.makeText(
                                    getContext(),
                                    "Restored",
                                    Toast.LENGTH_SHORT
                            ).show();

                            loadTrash();

                        }

                    }

                    @Override
                    public void onDeleteForever(Hike hike) {

                        new AlertDialog.Builder(getContext())

                                .setTitle("Delete Forever")

                                .setMessage(
                                        "Delete permanently?"
                                )

                                .setPositiveButton(
                                        "Delete",
                                        (dialog, which) -> {

                                            boolean success =
                                                    databaseHelper.deleteHikeForever(
                                                            hike.getId()
                                                    );

                                            if (success) {

                                                Toast.makeText(
                                                        getContext(),
                                                        "Deleted",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                                loadTrash();

                                            }

                                        })

                                .setNegativeButton(
                                        "Cancel",
                                        null
                                )

                                .show();

                    }

                });

        rvTrash.setAdapter(adapter);

    }

    @Override
    public void onResume() {
        super.onResume();

        loadTrash();
    }
}