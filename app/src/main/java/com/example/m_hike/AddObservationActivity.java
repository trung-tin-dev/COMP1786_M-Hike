package com.example.m_hike;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.m_hike.database.DatabaseHelper;
import com.example.m_hike.model.Observation;

public class AddObservationActivity extends AppCompatActivity {
    private EditText etTitle;

    private EditText etTime;


    private EditText etNote;

    private Button btnSave;
    private TextView tvScreenTitle;

    private DatabaseHelper databaseHelper;
    private int observationId = -1;

    private int hikeId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_observation);

        etTitle = findViewById(R.id.etTitle);

        etTime = findViewById(R.id.etTime);

        etNote = findViewById(R.id.etNote);

        btnSave = findViewById(R.id.btnSave);
        tvScreenTitle = findViewById(R.id.tvScreenTitle);

        databaseHelper = new DatabaseHelper(this);

        hikeId = getIntent().getIntExtra(
                "HIKE_ID",
                -1
        );

        observationId = getIntent().getIntExtra(
                "OBSERVATION_ID",
                -1
        );
        if (observationId == -1) {

            tvScreenTitle.setText("Add Observation");

        } else {

            tvScreenTitle.setText("Update Observation");
            loadObservation();
        }

        if (observationId != -1) {

            loadObservation();

        }

        btnSave.setOnClickListener(v -> {

            if (observationId == -1) {

                saveObservation();

            } else {

                updateObservation();

            }

        });
    }

    private void saveObservation() {

        String title = etTitle.getText().toString().trim();

        String time = etTime.getText().toString().trim();

        String note = etNote.getText().toString().trim();

        if (title.isEmpty()) {

            etTitle.setError("Required");

            return;
        }

        Observation observation = new Observation();

        observation.setHikeId(hikeId);

        observation.setTitle(title);

        observation.setObservationTime(time);

        observation.setNote(note);

        String now = new java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                java.util.Locale.getDefault()
        ).format(new java.util.Date());

        observation.setCreatedAt(now);

        boolean success =
                databaseHelper.insertObservation(observation);

        if (success) {

            Toast.makeText(
                    this,
                    "Observation Added",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Failed",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void loadObservation() {

        Observation observation =
                databaseHelper.getObservationById(observationId);

        if (observation == null) {
            return;
        }

        etTitle.setText(observation.getTitle());

        etTime.setText(observation.getObservationTime());

        etNote.setText(observation.getNote());

    }

    private void updateObservation() {

        String title = etTitle.getText().toString().trim();

        String time = etTime.getText().toString().trim();

        String note = etNote.getText().toString().trim();

        if (title.isEmpty()) {

            etTitle.setError("Required");

            return;

        }

        Observation old =
                databaseHelper.getObservationById(observationId);

        Observation observation = new Observation();

        observation.setId(observationId);
        observation.setHikeId(old.getHikeId());

        observation.setTitle(title);

        observation.setObservationTime(time);

        observation.setNote(note);

        observation.setUpdatedAt(
                new java.text.SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        java.util.Locale.getDefault()
                ).format(new java.util.Date())
        );

        boolean success =
                databaseHelper.updateObservation(observation);

        if (success) {

            Toast.makeText(
                    this,
                    "Observation Updated",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Update Failed",
                    Toast.LENGTH_SHORT
            ).show();

        }

    }
}