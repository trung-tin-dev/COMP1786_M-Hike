package com.example.m_hike;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.m_hike.database.DatabaseHelper;
import com.example.m_hike.model.Hike;

public class HikeDetailActivity extends AppCompatActivity {

    private TextView tvName;
    private TextView tvLocation;
    private TextView tvDate;
    private TextView tvParking;
    private TextView tvLength;
    private TextView tvDifficulty;
    private TextView tvDuration;
    private TextView tvDescription;

    private Button btnUpdate;
    private Button btnDelete;

    private DatabaseHelper databaseHelper;

    private int hikeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hike_detail);

        tvName = findViewById(R.id.tvName);
        tvLocation = findViewById(R.id.tvLocation);
        tvDate = findViewById(R.id.tvDate);
        tvParking = findViewById(R.id.tvParking);
        tvLength = findViewById(R.id.tvLength);
        tvDifficulty = findViewById(R.id.tvDifficulty);
        tvDuration = findViewById(R.id.tvDuration);
        tvDescription = findViewById(R.id.tvDescription);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        databaseHelper = new DatabaseHelper(this);

        hikeId = getIntent().getIntExtra("HIKE_ID", -1);

        if (hikeId != -1) {
            loadHike();
        }

        btnUpdate.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HikeDetailActivity.this,
                    AddHikeActivity.class
            );

            intent.putExtra(
                    "HIKE_ID",
                    hikeId
            );

            startActivity(intent);

        });


        btnDelete.setOnClickListener(v -> {

            new AlertDialog.Builder(this)
                    .setTitle("Delete Hike")
                    .setMessage("Move this hike to Trash?")
                    .setPositiveButton("Delete", (dialog, which) -> {

                        boolean success =
                                databaseHelper.softDeleteHike(hikeId);

                        if (success) {

                            Toast.makeText(
                                    this,
                                    "Moved to Trash",
                                    Toast.LENGTH_SHORT
                            ).show();

                            finish();

                        }

                    })
                    .setNegativeButton("Cancel", null)
                    .show();

        });
    }

    private void loadHike() {

        Hike hike = databaseHelper.getHikeById(hikeId);

        if (hike == null) {
            return;
        }

        tvName.setText(hike.getName());

        tvLocation.setText("📍 Location: " + hike.getLocation());

        tvDate.setText("📅 Date: " + hike.getDate());

        tvParking.setText(
                "🚗 Parking: " +
                        (hike.isParkingAvailable() ? "Yes" : "No")
        );

        tvLength.setText(
                "📏 Length: " +
                        hike.getLength() +
                        " km"
        );

        tvDifficulty.setText(
                "🎯 Difficulty: " +
                        hike.getDifficulty()
        );

        tvDuration.setText(
                "⏱ Estimated Duration: " +
                        hike.getEstimatedDuration()
        );

        tvDescription.setText(
                "📝 Description:\n" +
                        hike.getDescription()
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadHike();
    }
}