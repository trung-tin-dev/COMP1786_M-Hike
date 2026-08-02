package com.example.m_hike.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.m_hike.LoginActivity;
import com.example.m_hike.R;
import com.example.m_hike.TrashActivity;
import com.example.m_hike.database.DatabaseHelper;
import com.example.m_hike.model.User;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private TextView tvUsername;
    private TextView tvEmail;
    private ImageView ivAvatar;
    private TextView btnTrash;
    private TextView btnLogout;

    private DatabaseHelper databaseHelper;
    private int userId;
    private String currentPhotoPath;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) showImagePicker();
                else Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            });

    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    if (selectedImage != null) updateAvatar(selectedImage.toString());
                }
            });

    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
                if (result) updateAvatar(currentPhotoPath);
            });

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvUsername = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);
        ivAvatar = view.findViewById(R.id.ivAvatar);
        btnTrash = view.findViewById(R.id.btnTrash);
        btnLogout = view.findViewById(R.id.btnLogout);

        databaseHelper = new DatabaseHelper(requireContext());
        SharedPreferences preferences = requireActivity().getSharedPreferences("MHIKE", Context.MODE_PRIVATE);
        userId = preferences.getInt("userId", -1);

        loadUserData();

        ivAvatar.setOnClickListener(v -> checkPermissionsAndShowPicker());

        btnTrash.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), TrashActivity.class));
        });

        btnLogout.setOnClickListener(v -> logout());

        return view;
    }

    private void loadUserData() {
        User user = databaseHelper.getUserById(userId);
        if (user != null) {
            tvUsername.setText(user.getUserName());
            tvEmail.setText(user.getUserEmail());

            Glide.with(this)
                    .load(user.getAvatarPath())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(ivAvatar);
        }
    }

    private void checkPermissionsAndShowPicker() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            showImagePicker();
        }
    }

    private void showImagePicker() {
        String[] options = {"Camera", "Gallery"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Change Avatar")
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
                Uri photoUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", photoFile);
                cameraLauncher.launch(photoUri);
            }
        } catch (IOException ex) {
            Toast.makeText(getContext(), "Error creating file", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile("AVATAR_" + timeStamp + "_", ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void updateAvatar(String path) {
        if (databaseHelper.updateUserAvatar(userId, path)) {
            loadUserData();
            Toast.makeText(getContext(), "Avatar updated", Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        SharedPreferences preferences = requireActivity().getSharedPreferences("MHIKE", Context.MODE_PRIVATE);
        preferences.edit().clear().apply();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
