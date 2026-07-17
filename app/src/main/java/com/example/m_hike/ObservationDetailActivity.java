package com.example.m_hike;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_hike.adapter.PhotoAdapter;
import com.example.m_hike.database.DatabaseHelper;
import com.example.m_hike.model.Observation;
import com.example.m_hike.model.Photo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ObservationDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvTime, tvNote;
    private Button btnUpdate, btnDelete, btnAddPhoto;
    private RecyclerView recyclerPhotos;
    private PhotoAdapter photoAdapter;
    private ArrayList<Photo> photoList;
    private DatabaseHelper databaseHelper;
    private int observationId;
    private Uri photoUri;
    private String currentPhotoPath;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    showImagePicker();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    if (selectedImage != null) {
                        savePhoto(selectedImage.toString());
                    }
                }
            });

    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
                if (result) {
                    savePhoto(currentPhotoPath);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_observation_detail);

        databaseHelper = new DatabaseHelper(this);
        observationId = getIntent().getIntExtra("OBSERVATION_ID", -1);

        if (observationId == -1) {
            Toast.makeText(this, "Invalid observation", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        loadObservation();
        loadPhotos();
        setupListeners();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        tvTime = findViewById(R.id.tvTime);
        tvNote = findViewById(R.id.tvNote);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnAddPhoto = findViewById(R.id.btnAddPhoto);
        recyclerPhotos = findViewById(R.id.recyclerPhotos);
    }

    private void setupRecyclerView() {
        photoList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(photoList);
        recyclerPhotos.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerPhotos.setAdapter(photoAdapter);
    }

    private void setupListeners() {
        btnUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddObservationActivity.class);
            intent.putExtra("OBSERVATION_ID", observationId);
            startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete")
                    .setMessage("Move Observation to Trash?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        databaseHelper.softDeleteObservation(observationId);
                        Toast.makeText(this, "Moved to Trash", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        btnAddPhoto.setOnClickListener(v -> checkPermissionsAndShowPicker());
    }

    private void checkPermissionsAndShowPicker() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            showImagePicker();
        }
    }

    private void showImagePicker() {
        String[] options = {"Camera", "Gallery"};
        new AlertDialog.Builder(this)
                .setTitle("Choose Image")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) openCamera();
                    else openGallery();
                })
                .show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void openCamera() {
        try {
            File photoFile = createImageFile();
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
                cameraLauncher.launch(photoUri);
            }
        } catch (IOException ex) {
            Log.e("ObservationDetail", "Error creating image file", ex);
            Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void savePhoto(String path) {
        Photo photo = new Photo();
        photo.setObservationId(observationId);
        photo.setPhotoPath(path);
        photo.setCreatedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));

        if (databaseHelper.insertPhoto(photo)) {
            Toast.makeText(this, "Photo added", Toast.LENGTH_SHORT).show();
            loadPhotos();
        }
    }

    private void loadPhotos() {
        photoList.clear();
        photoList.addAll(databaseHelper.getPhotosByObservationId(observationId));
        photoAdapter.notifyDataSetChanged();
    }

    private void loadObservation() {
        Observation observation = databaseHelper.getObservationById(observationId);
        if (observation == null) return;
        tvTitle.setText(observation.getTitle());
        tvTime.setText("Time : " + observation.getObservationTime());
        tvNote.setText("Note :\n" + observation.getNote());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadObservation();
        loadPhotos();
    }
}
