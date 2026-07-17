package com.example.m_hike;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
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

public class AddObservationActivity extends AppCompatActivity {
    private EditText etTitle;
    private EditText etTime;
    private EditText etNote;
    private Button btnSave, btnAddPhoto;
    private TextView tvScreenTitle;
    private RecyclerView recyclerPhotos;
    private PhotoAdapter photoAdapter;
    private ArrayList<Photo> photoList = new ArrayList<>();
    private ArrayList<String> tempPhotoPaths = new ArrayList<>();

    private DatabaseHelper databaseHelper;
    private int observationId = -1;
    private int hikeId;
    private String currentPhotoPath;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) showImagePicker();
                else Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            });

    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    if (selectedImage != null) addTempPhoto(selectedImage.toString());
                }
            });

    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
                if (result) addTempPhoto(currentPhotoPath);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_observation);

        etTitle = findViewById(R.id.etTitle);
        etTime = findViewById(R.id.etTime);
        etNote = findViewById(R.id.etNote);
        btnSave = findViewById(R.id.btnSave);
        btnAddPhoto = findViewById(R.id.btnAddPhoto);
        tvScreenTitle = findViewById(R.id.tvScreenTitle);
        recyclerPhotos = findViewById(R.id.recyclerPhotos);

        databaseHelper = new DatabaseHelper(this);
        hikeId = getIntent().getIntExtra("HIKE_ID", -1);
        observationId = getIntent().getIntExtra("OBSERVATION_ID", -1);

        setupRecyclerView();

        if (observationId == -1) {
            tvScreenTitle.setText("Add Observation");
            // Set current time by default
            String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            etTime.setText(currentTime);
        } else {
            tvScreenTitle.setText("Update Observation");
            loadObservation();
            loadExistingPhotos();
        }

        btnAddPhoto.setOnClickListener(v -> checkPermissionsAndShowPicker());
        btnSave.setOnClickListener(v -> {
            if (observationId == -1) saveObservation();
            else updateObservation();
        });
    }

    private void setupRecyclerView() {
        photoAdapter = new PhotoAdapter(photoList);
        recyclerPhotos.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerPhotos.setAdapter(photoAdapter);
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
                Uri photoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
                cameraLauncher.launch(photoUri);
            }
        } catch (IOException ex) {
            Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile("JPEG_" + timeStamp + "_", ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void addTempPhoto(String path) {
        if (observationId != -1) {
            // If editing, save immediately
            Photo photo = new Photo();
            photo.setObservationId(observationId);
            photo.setPhotoPath(path);
            photo.setCreatedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
            databaseHelper.insertPhoto(photo);
            loadExistingPhotos();
        } else {
            // If adding new, store in temp list
            tempPhotoPaths.add(path);
            Photo photo = new Photo();
            photo.setPhotoPath(path);
            photoList.add(photo);
            photoAdapter.notifyDataSetChanged();
        }
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
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        observation.setCreatedAt(now);

        long newId = databaseHelper.insertObservation(observation);

        if (newId != -1) {
            // Save temp photos
            for (String path : tempPhotoPaths) {
                Photo photo = new Photo();
                photo.setObservationId((int) newId);
                photo.setPhotoPath(path);
                photo.setCreatedAt(now);
                databaseHelper.insertPhoto(photo);
            }
            Toast.makeText(this, "Observation Added", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadObservation() {
        Observation observation = databaseHelper.getObservationById(observationId);
        if (observation != null) {
            etTitle.setText(observation.getTitle());
            etTime.setText(observation.getObservationTime());
            etNote.setText(observation.getNote());
        }
    }

    private void loadExistingPhotos() {
        photoList.clear();
        photoList.addAll(databaseHelper.getPhotosByObservationId(observationId));
        photoAdapter.notifyDataSetChanged();
    }

    private void updateObservation() {
        String title = etTitle.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("Required");
            return;
        }

        Observation observation = databaseHelper.getObservationById(observationId);
        observation.setTitle(title);
        observation.setObservationTime(time);
        observation.setNote(note);
        observation.setUpdatedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));

        if (databaseHelper.updateObservation(observation)) {
            Toast.makeText(this, "Observation Updated", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show();
        }
    }
}
