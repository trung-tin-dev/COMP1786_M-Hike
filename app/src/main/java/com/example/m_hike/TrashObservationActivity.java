package com.example.m_hike;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_hike.adapter.TrashObservationAdapter;
import com.example.m_hike.database.DatabaseHelper;
import com.example.m_hike.model.Observation;

import java.util.List;

public class TrashObservationActivity extends AppCompatActivity {

    private RecyclerView rvTrashObservation;

    private DatabaseHelper databaseHelper;

    private TrashObservationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trash_observation);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvTrashObservation =
                findViewById(R.id.rvTrashObservation);

        rvTrashObservation.setLayoutManager(
                new LinearLayoutManager(this));

        databaseHelper = new DatabaseHelper(this);

        loadTrash();
    }

    private void loadTrash() {
        SharedPreferences preferences = getSharedPreferences("MHIKE", Context.MODE_PRIVATE);
        int userId = preferences.getInt("userId", -1);

        List<Observation> list =
                databaseHelper.getDeletedObservations(userId);

        adapter = new TrashObservationAdapter(
                list,
                new TrashObservationAdapter.OnTrashObservationListener() {

                    @Override
                    public void onRestore(Observation observation) {

                        databaseHelper.restoreObservation(
                                observation.getId());

                        Toast.makeText(
                                TrashObservationActivity.this,
                                "Restored",
                                Toast.LENGTH_SHORT
                        ).show();

                        loadTrash();
                    }

                    @Override
                    public void onDeleteForever(Observation observation) {

                        new AlertDialog.Builder(
                                TrashObservationActivity.this)

                                .setTitle("Delete Forever")
                                .setMessage("Delete permanently?")

                                .setPositiveButton("Delete",
                                        (dialog, which) -> {

                                            databaseHelper
                                                    .deleteObservationForever(
                                                            observation.getId());

                                            Toast.makeText(
                                                    TrashObservationActivity.this,
                                                    "Deleted",
                                                    Toast.LENGTH_SHORT
                                            ).show();

                                            loadTrash();

                                        })

                                .setNegativeButton(
                                        "Cancel",
                                        null
                                )

                                .show();
                    }
                });

        rvTrashObservation.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTrash();
    }
}