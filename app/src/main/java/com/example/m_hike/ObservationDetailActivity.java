package com.example.m_hike;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.m_hike.database.DatabaseHelper;
import com.example.m_hike.model.Observation;

public class ObservationDetailActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvTime;
    private TextView tvNote;

    private Button btnUpdate;
    private Button btnDelete;

    private DatabaseHelper databaseHelper;

    private int observationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation_detail);

        tvTitle = findViewById(R.id.tvTitle);
        tvTime = findViewById(R.id.tvTime);
        tvNote = findViewById(R.id.tvNote);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        databaseHelper = new DatabaseHelper(this);

        observationId = getIntent().getIntExtra("OBSERVATION_ID",-1);


        loadObservation();

        btnUpdate.setOnClickListener(v -> {

            Intent intent = new Intent(
                    this,
                    AddObservationActivity.class
            );

            intent.putExtra(
                    "OBSERVATION_ID",
                    observationId
            );

            startActivity(intent);

        });

        btnDelete.setOnClickListener(v -> {

            new AlertDialog.Builder(this)
                    .setTitle("Delete")
                    .setMessage("Move Observation to Trash?")
                    .setPositiveButton("Delete",(dialog,which)->{

                        databaseHelper.softDeleteObservation(observationId);

                        Toast.makeText(
                                this,
                                "Moved to Trash",
                                Toast.LENGTH_SHORT
                        ).show();

                        finish();

                    })
                    .setNegativeButton("Cancel",null)
                    .show();

        });

    }

    private void loadObservation(){

        Observation observation =
                databaseHelper.getObservationById(observationId);

        if(observation==null) return;

        tvTitle.setText(observation.getTitle());

        tvTime.setText(
                "Time : " +
                        observation.getObservationTime());

        tvNote.setText(
                "Note :\n"+
                        observation.getNote());

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadObservation();
    }

}