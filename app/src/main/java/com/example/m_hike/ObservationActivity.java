package com.example.m_hike;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_hike.adapter.ObservationAdapter;
import com.example.m_hike.database.DatabaseHelper;
import com.example.m_hike.model.Observation;

import java.util.List;

public class ObservationActivity extends AppCompatActivity {

    private RecyclerView rvObservation;

    private Button btnAddObservation;

    private DatabaseHelper databaseHelper;

    private ObservationAdapter adapter;

    private int hikeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation);

        databaseHelper = new DatabaseHelper(this);

        rvObservation = findViewById(R.id.rvObservation);

        btnAddObservation = findViewById(R.id.btnAddObservation);
        btnAddObservation.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ObservationActivity.this,
                    AddObservationActivity.class
            );

            intent.putExtra(
                    "HIKE_ID",
                    hikeId
            );

            startActivity(intent);

        });
        databaseHelper = new DatabaseHelper(this);

        rvObservation.setLayoutManager(
                new LinearLayoutManager(this));

        hikeId = getIntent().getIntExtra(
                "HIKE_ID",
                -1);

        loadObservations();
    }

    private void loadObservations() {

        List<Observation> list =
                databaseHelper.getObservationsByHikeId(hikeId);

        adapter = new ObservationAdapter(
                list,
                observation -> {

                    Intent intent = new Intent(
                            ObservationActivity.this,
                            ObservationDetailActivity.class
                    );

                    intent.putExtra(
                            "OBSERVATION_ID",
                            observation.getId()
                    );

                    startActivity(intent);

                });

        rvObservation.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadObservations();
    }
}