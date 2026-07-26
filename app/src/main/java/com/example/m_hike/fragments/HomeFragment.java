package com.example.m_hike.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.m_hike.AddHikeActivity;
import com.example.m_hike.HikeDetailActivity;
import com.example.m_hike.TrashActivity;
import com.example.m_hike.database.DatabaseHelper;
import com.example.m_hike.databinding.FragmentHomeBinding;
import com.example.m_hike.model.Hike;

import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private DatabaseHelper databaseHelper;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Initialize ViewBinding
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        
        // Get User ID from SharedPreferences
        SharedPreferences preferences = requireContext().getSharedPreferences("MHIKE", Context.MODE_PRIVATE);
        userId = preferences.getInt("userId", -1);
        String username = preferences.getString("username", "Hiker");

        // 1. Setup Greeting
        setupGreeting(username);

        // 2. Load Dashboard Data
        loadDashboardData();

        // 3. Setup Quick Actions
        setupListeners();
    }

    private void setupGreeting(String username) {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        String greeting;

        if (timeOfDay < 12) {
            greeting = "Good Morning, ";
        } else if (timeOfDay < 16) {
            greeting = "Good Afternoon, ";
        } else if (timeOfDay < 21) {
            greeting = "Good Evening, ";
        } else {
            greeting = "Good Night, ";
        }

        binding.tvGreeting.setText(greeting + username);
    }

    private void loadDashboardData() {
        // Load Statistics
        loadStatistics();

        // Load Latest Hike
        loadLatestHike();
    }

    private void loadStatistics() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        // Count Hikes
        Cursor cursorHikes = db.rawQuery("SELECT COUNT(*) FROM hikes WHERE user_id = ? AND status != 'DELETED'", 
                new String[]{String.valueOf(userId)});
        if (cursorHikes.moveToFirst()) {
            binding.tvStatHikes.setText(String.valueOf(cursorHikes.getInt(0)));
        }
        cursorHikes.close();

        // Count Observations
        String obsQuery = "SELECT COUNT(*) FROM observations o JOIN hikes h ON o.hike_id = h.id " +
                          "WHERE h.user_id = ? AND o.deleted_at IS NULL";
        Cursor cursorObs = db.rawQuery(obsQuery, new String[]{String.valueOf(userId)});
        if (cursorObs.moveToFirst()) {
            binding.tvStatObservations.setText(String.valueOf(cursorObs.getInt(0)));
        }
        cursorObs.close();

        // Count Photos
        String photoQuery = "SELECT COUNT(*) FROM photos p " +
                            "JOIN observations o ON p.observation_id = o.id " +
                            "JOIN hikes h ON o.hike_id = h.id " +
                            "WHERE h.user_id = ? AND p.deleted_at IS NULL";
        Cursor cursorPhotos = db.rawQuery(photoQuery, new String[]{String.valueOf(userId)});
        if (cursorPhotos.moveToFirst()) {
            binding.tvStatPhotos.setText(String.valueOf(cursorPhotos.getInt(0)));
        }
        cursorPhotos.close();
    }

    private void loadLatestHike() {
        List<Hike> allHikes = databaseHelper.getAllHikes(userId);
        
        if (allHikes != null && !allHikes.isEmpty()) {
            Hike latest = allHikes.get(0); // List is sorted by created_at DESC
            
            binding.tvNoHikes.setVisibility(View.GONE);
            binding.layoutLatestHikeInfo.setVisibility(View.VISIBLE);
            
            binding.tvLatestHikeName.setText(latest.getName());
            binding.tvLatestHikeDate.setText(latest.getDate());
            binding.tvLatestHikeLocation.setText(latest.getLocation());
            binding.tvLatestHikeDifficulty.setText("Difficulty: " + latest.getDifficulty());
            
            // Set difficulty color
            int color;
            switch (latest.getDifficulty()) {
                case "Easy":
                    color = getResources().getColor(com.example.m_hike.R.color.success, null);
                    break;
                case "Medium":
                    color = getResources().getColor(com.example.m_hike.R.color.warning, null);
                    break;
                case "Hard":
                    color = getResources().getColor(com.example.m_hike.R.color.danger, null);
                    break;
                default:
                    color = getResources().getColor(com.example.m_hike.R.color.primary, null);
                    break;
            }
            binding.tvLatestHikeDifficulty.setTextColor(color);
            
            binding.btnViewLatestDetails.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), HikeDetailActivity.class);
                intent.putExtra("HIKE_ID", latest.getId());
                startActivity(intent);
            });
        } else {
            binding.tvNoHikes.setVisibility(View.VISIBLE);
            binding.layoutLatestHikeInfo.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        // Quick Action: Create New Hike
        binding.btnQuickCreate.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AddHikeActivity.class));
        });

        // Quick Action: Open Trash
        binding.btnQuickTrash.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), TrashActivity.class));
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when returning to home
        loadDashboardData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
