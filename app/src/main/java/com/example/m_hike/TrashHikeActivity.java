package com.example.m_hike;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_hike.adapter.TrashHikeAdapter;
import com.example.m_hike.database.DatabaseHelper;
import com.example.m_hike.model.Hike;

import java.util.List;

public class TrashHikeActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trash_hike);

        setTitle("Deleted Hikes");

        recyclerView = findViewById(R.id.rvTrashHike);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this));

        databaseHelper = new DatabaseHelper(this);

        loadData();
    }

    private void loadData(){
        SharedPreferences preferences = getSharedPreferences("MHIKE", Context.MODE_PRIVATE);
        int userId = preferences.getInt("userId", -1);

        List<Hike> list =
                databaseHelper.getDeletedHikes(userId);

        TrashHikeAdapter adapter =
                new TrashHikeAdapter(list,
                        new TrashHikeAdapter.OnTrashActionListener() {

                            @Override
                            public void onRestore(Hike hike) {

                                databaseHelper.restoreHike(hike.getId());

                                loadData();

                            }

                            @Override
                            public void onDeleteForever(Hike hike) {

                                databaseHelper.deleteHikeForever(hike.getId());

                                loadData();

                            }
                        });

        recyclerView.setAdapter(adapter);

    }
}