package com.example.m_hike;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class TrashActivity extends AppCompatActivity {

    private Button btnTrashHike;
    private Button btnTrashObservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        setTitle("Trash");

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