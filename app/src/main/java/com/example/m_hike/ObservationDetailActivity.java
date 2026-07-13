package com.example.m_hike;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_hike.adapter.PhotoAdapter;
import com.example.m_hike.database.DatabaseHelper;
import com.example.m_hike.model.Observation;
import com.example.m_hike.model.Photo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ObservationDetailActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvTime;
    private TextView tvNote;
    private Button btnUpdate;
    private Button btnDelete;
    private RecyclerView recyclerPhotos;

    private PhotoAdapter photoAdapter;

    private ArrayList<Photo> photoList;

    private Button btnAddPhoto;

    private DatabaseHelper databaseHelper;

    private int observationId;

    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_GALLERY = 200;
    private static final int CAMERA_PERMISSION = 300;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation_detail);

        if(android.os.Build.VERSION.SDK_INT >= 23){

            if(checkSelfPermission(
                    android.Manifest.permission.CAMERA
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED){


                requestPermissions(
                        new String[]{
                                android.Manifest.permission.CAMERA
                        },
                        CAMERA_PERMISSION
                );

            }

        }


        databaseHelper =
                new DatabaseHelper(this);


        observationId =
                getIntent()
                        .getIntExtra(
                                "OBSERVATION_ID",
                                -1
                        );


        if(observationId == -1){

            Toast.makeText(
                    this,
                    "Invalid observation",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }


        tvTitle = findViewById(R.id.tvTitle);
        tvTime = findViewById(R.id.tvTime);
        tvNote = findViewById(R.id.tvNote);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        btnAddPhoto =
                findViewById(R.id.btnAddPhoto);


        recyclerPhotos =
                findViewById(R.id.recyclerPhotos);



        photoList = new ArrayList<>();


        photoAdapter =
                new PhotoAdapter(photoList);


        recyclerPhotos.setLayoutManager(
                new androidx.recyclerview.widget.GridLayoutManager(
                        this,
                        3
                )
        );


        recyclerPhotos.setAdapter(photoAdapter);



        loadObservation();

        loadPhotos();



        btnUpdate.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
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
                    .setPositiveButton(
                            "Delete",
                            (dialog,which)->{


                                databaseHelper
                                        .softDeleteObservation(
                                                observationId
                                        );


                                Toast.makeText(
                                        this,
                                        "Moved to Trash",
                                        Toast.LENGTH_SHORT
                                ).show();


                                finish();


                            })
                    .setNegativeButton(
                            "Cancel",
                            null
                    )
                    .show();

        });

        btnAddPhoto.setOnClickListener(v -> {

            showImagePicker();

        });

    }

    private void loadPhotos() {
        photoList.clear();


        photoList.addAll(
                databaseHelper.getPhotosByObservationId(
                        observationId
                )
        );


        photoAdapter.notifyDataSetChanged();
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

    private void showImagePicker(){

        String[] options = {
                "Camera",
                "Gallery"
        };


        new AlertDialog.Builder(this)
                .setTitle("Choose Image")
                .setItems(options, (dialog, which) -> {


                    if(which == 0){

                        openCamera();

                    }
                    else {

                        openGallery();

                    }


                })
                .show();

    }

    private void openGallery(){

        Intent intent =
                new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );


        startActivityForResult(
                intent,
                REQUEST_GALLERY
        );

    }

    private void openCamera(){


        Intent intent =
                new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE
                );


        if(intent.resolveActivity(getPackageManager()) != null){


            startActivityForResult(
                    intent,
                    REQUEST_CAMERA
            );


        }
        else {


            Toast.makeText(
                    this,
                    "Camera not available",
                    Toast.LENGTH_SHORT
            ).show();


        }

    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ){

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );


        if(resultCode == RESULT_OK){


            if(requestCode == REQUEST_GALLERY){


                Uri uri = data.getData();


                savePhoto(
                        uri.toString()
                );


            }


            if(requestCode == REQUEST_CAMERA){


                if(data != null && data.getExtras() != null){


                    Bitmap image =
                            (Bitmap)data.getExtras()
                                    .get("data");


                    String path =
                            MediaStore.Images.Media.insertImage(
                                    getContentResolver(),
                                    image,
                                    "mhike_photo",
                                    null
                            );


                    savePhoto(path);

                }


            }

        }

    }

    private void savePhoto(String path){


        Photo photo =
                new Photo();


        photo.setObservationId(
                observationId
        );


        photo.setPhotoPath(
                path
        );


        photo.setCreatedAt(
                new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault()
                ).format(new Date())
        );


        boolean result =
                databaseHelper.insertPhoto(photo);



        if(result){

            Toast.makeText(
                    this,
                    "Photo added",
                    Toast.LENGTH_SHORT
            ).show();


            loadPhotos();

        }


    }

    @Override
    protected void onResume() {

        super.onResume();

        loadObservation();

        loadPhotos();

    }

}