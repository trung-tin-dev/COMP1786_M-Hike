package com.example.m_hike;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

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
    private Button btnReset;

    private Button btnUpdate;
    private Button btnDelete;
    private Button btnObservation;

    private DatabaseHelper databaseHelper;

    private int hikeId;

    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private Runnable timerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hike_detail);

        // 1. Ánh xạ Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);

        // 2. Thiết lập Toolbar làm ActionBar cho Activity
        setSupportActionBar(toolbar);

        // 3. Kích hoạt nút quay lại (mũi tên)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Ẩn title mặc định nếu bạn muốn dùng TextView tvName riêng
        }

        // 4. Xử lý sự kiện nhấn vào nút quay lại
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed(); // Hoặc finish();
        });

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
        btnReset = findViewById(R.id.btnReset);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnStart.setOnClickListener(v -> startHike());
        btnFinish.setOnClickListener(v -> finishHike());
        btnReset.setOnClickListener(v -> resetHike());
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

        tvLocation.setText("Location: " + hike.getLocation());

        tvDate.setText("Date: " + hike.getDate());

        tvParking.setText(
                "Parking: " +
                        (hike.isParkingAvailable() ? "Yes" : "No")
        );

        tvLength.setText(
                "Length: " +
                        hike.getLength() +
                        " km"
        );

        tvDifficulty.setText(
                "Difficulty: " +
                        hike.getDifficulty()
        );

        // Set difficulty color
        int color;
        switch (hike.getDifficulty()) {
            case "Easy":
                color = getResources().getColor(R.color.success, null);
                break;
            case "Medium":
                color = getResources().getColor(R.color.warning, null);
                break;
            case "Hard":
                color = getResources().getColor(R.color.danger, null);
                break;
            default:
                color = getResources().getColor(R.color.text_green, null);
                break;
        }
        tvDifficulty.setTextColor(color);

        tvDuration.setText(
                "Estimated Duration: " +
                        hike.getEstimatedDuration()
        );

        tvStatus.setText("Status: " + hike.getStatus());

        // Manage Buttons and Timer based on Status
        stopTimer();
        if ("ONGOING".equals(hike.getStatus())) {
            btnStart.setVisibility(View.GONE);
            btnFinish.setVisibility(View.VISIBLE);
            btnReset.setVisibility(View.VISIBLE);
            startTimer();
        } else if ("COMPLETED".equals(hike.getStatus())) {
            btnStart.setVisibility(View.GONE);
            btnFinish.setVisibility(View.GONE);
            btnReset.setVisibility(View.VISIBLE);
        } else {
            btnStart.setVisibility(View.VISIBLE);
            btnFinish.setVisibility(View.GONE);
            btnReset.setVisibility(View.GONE);
        }

        tvStartTime.setText(
                "Start: " +
                        (hike.getStartTime() == null ? "-" : hike.getStartTime())
        );

        tvEndTime.setText(
                "Finish: " +
                        (hike.getEndTime() == null ? "-" : hike.getEndTime())
        );

        if (hike.getStartTime() == null) {
            tvActualDuration.setText("Actual Duration: -");
        } else if (hike.getEndTime() == null) {
            // Hike is ONGOING, calculate duration until NOW
            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            tvActualDuration.setText("Actual Duration: " + calculateDuration(hike.getStartTime(), now));
        } else {
            // Hike is COMPLETED
            tvActualDuration.setText("Actual Duration: " + calculateDuration(hike.getStartTime(), hike.getEndTime()));
        }

        tvDescription.setText(
                "Description:\n" +
                        hike.getDescription()
        );
    }

    private void startTimer() {
        if (timerRunnable != null) return;
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                Hike hike = databaseHelper.getHikeById(hikeId);
                if (hike != null && "ONGOING".equals(hike.getStatus())) {
                    String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                    tvActualDuration.setText("Actual Duration: " + calculateDuration(hike.getStartTime(), now));
                    timerHandler.postDelayed(this, 1000);
                }
            }
        };
        timerHandler.post(timerRunnable);
    }

    private void stopTimer() {
        if (timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
            timerRunnable = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
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

    private void resetHike() {
        new AlertDialog.Builder(this)
                .setTitle("Reset Time")
                .setMessage("Are you sure you want to reset the start and finish times?")
                .setPositiveButton("Reset", (dialog, which) -> {
                    boolean success = databaseHelper.resetHike(hikeId);
                    if (success) {
                        Toast.makeText(this, "Hike time reset", Toast.LENGTH_SHORT).show();
                        loadHike();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
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

            long totalSeconds = diff / 1000;

            long hours = totalSeconds / 3600;

            long minutes = (totalSeconds % 3600) / 60;

            long seconds = totalSeconds % 60;

            if (hours > 0) {
                return String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
            } else {
                return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
            }

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