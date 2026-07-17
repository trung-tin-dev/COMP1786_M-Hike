package com.example.m_hike;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.m_hike.database.DatabaseHelper;
import com.example.m_hike.model.Hike;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HikeDetailActivity extends AppCompatActivity {

    private TextView tvName;
    private TextView tvLocation;
    private TextView tvDate;
    private TextView tvParking;
    private TextView tvLength;
    private TextView tvDifficulty;
    private TextView tvDuration;
    private TextView tvDescription;
    private TextView tvStatus;
    private TextView tvStartTime;
    private TextView tvEndTime;
    private TextView tvActualDuration;

    private Button btnStart;
    private Button btnFinish;

    private Button btnUpdate;
    private Button btnDelete;
    private Button btnObservation;

    private DatabaseHelper databaseHelper;

    private int hikeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hike_detail);

        tvName = findViewById(R.id.tvName);
        tvLocation = findViewById(R.id.tvLocation);
        tvDate = findViewById(R.id.tvDate);
        tvParking = findViewById(R.id.tvParking);
        tvLength = findViewById(R.id.tvLength);
        tvDifficulty = findViewById(R.id.tvDifficulty);
        tvDuration = findViewById(R.id.tvDuration);
        tvDescription = findViewById(R.id.tvDescription);
        tvStatus = findViewById(R.id.tvStatus);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvEndTime = findViewById(R.id.tvEndTime);
        tvActualDuration = findViewById(R.id.tvActualDuration);


        btnStart = findViewById(R.id.btnStart);
        btnFinish = findViewById(R.id.btnFinish);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnStart.setOnClickListener(v -> startHike());
        btnFinish.setOnClickListener(v -> finishHike());
        btnObservation = findViewById(R.id.btnObservation);

        databaseHelper = new DatabaseHelper(this);

        hikeId = getIntent().getIntExtra("HIKE_ID", -1);

        if (hikeId != -1) {
            loadHike();
        }

        btnObservation.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HikeDetailActivity.this,
                    ObservationActivity.class
            );

            intent.putExtra(
                    "HIKE_ID",
                    hikeId
            );

            startActivity(intent);

        });

        btnUpdate.setOnClickListener(v -> {

            Toast.makeText(this,
                    "HIKE ID = " + hikeId,
                    Toast.LENGTH_SHORT).show();


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

        tvStatus.setText(
                "📌 Status: " + hike.getStatus()
        );

        tvStartTime.setText(
                "▶ Start: " +
                        (hike.getStartTime() == null ? "-" : hike.getStartTime())
        );

        tvEndTime.setText(
                "■ Finish: " +
                        (hike.getEndTime() == null ? "-" : hike.getEndTime())
        );

        if (hike.getStartTime() == null ||
                hike.getEndTime() == null) {

            tvActualDuration.setText(
                    "⏳ Actual Duration: -"
            );

        } else {

            tvActualDuration.setText(
                    "⏳ Actual Duration: " +
                            calculateDuration(
                                    hike.getStartTime(),
                                    hike.getEndTime()
                            )
            );

        }
        tvDescription.setText(
                "📝 Description:\n" +
                        hike.getDescription()
        );
    }

    private void startHike() {

        String now = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
        ).format(new Date());

        boolean success =
                databaseHelper.startHike(
                        hikeId,
                        now
                );

        if (success) {

            Toast.makeText(
                    this,
                    "Hike started",
                    Toast.LENGTH_SHORT
            ).show();

            loadHike();
        }
    }

    private void finishHike() {

        String now = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
        ).format(new Date());

        boolean success =
                databaseHelper.finishHike(
                        hikeId,
                        now
                );

        if (success) {

            Toast.makeText(
                    this,
                    "Hike completed",
                    Toast.LENGTH_SHORT
            ).show();

            loadHike();
        }
    }

    private String calculateDuration(String start, String end) {

        try {

            SimpleDateFormat format =
                    new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss",
                            Locale.getDefault()
                    );

            Date startDate = format.parse(start);

            Date endDate = format.parse(end);

            long diff =
                    endDate.getTime() -
                            startDate.getTime();

            long totalMinutes =
                    diff / (1000 * 60);

            long hours =
                    totalMinutes / 60;

            long minutes =
                    totalMinutes % 60;

            return hours +
                    " hour " +
                    minutes +
                    " min";

        } catch (ParseException e) {

            e.printStackTrace();

            return "-";
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        loadHike();
    }
}