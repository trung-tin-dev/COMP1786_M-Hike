package com.example.m_hike;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class TrashActivity extends AppCompatActivity {

    private Button btnTrashHike;
    private Button btnTrashObservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trash);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        btnTrashHike = findViewById(R.id.btnTrashHike);
        btnTrashObservation = findViewById(R.id.btnTrashObservation);

        btnTrashHike.setOnClickListener(v -> {

            Intent intent = new Intent(
                    TrashActivity.this,
                    TrashHikeActivity.class
            );

            startActivity(intent);

        });

        btnTrashObservation.setOnClickListener(v -> {

            Intent intent = new Intent(
                    TrashActivity.this,
                    TrashObservationActivity.class
            );

            startActivity(intent);

        });

    }
}