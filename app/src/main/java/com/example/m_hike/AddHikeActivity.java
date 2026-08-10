package com.example.m_hike;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.m_hike.database.DatabaseHelper;
import com.example.m_hike.model.Hike;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddHikeActivity extends AppCompatActivity {

    private TextInputEditText etHikeName;
    private TextInputEditText etLocation;
    private TextInputEditText etDate;
    private TextInputEditText etLength;
    private TextInputEditText etDescription;
    private TextView tvEstimatedDuration;
    private AutoCompleteTextView actDifficulty;
    private SwitchMaterial swParking;
    private Button btnSave;
    private int hikeId = -1;
    private boolean isEdit = false;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_hike);

        etHikeName = findViewById(R.id.etHikeName);
        etLocation = findViewById(R.id.etLocation);
        etDate = findViewById(R.id.etDate);
        etLength = findViewById(R.id.etLength);
        etDescription = findViewById(R.id.etDescription);
        tvEstimatedDuration = findViewById(R.id.tvEstimatedDuration);
        actDifficulty = findViewById(R.id.actDifficulty);
        swParking = findViewById(R.id.swParking);
        btnSave = findViewById(R.id.btnSave);
        databaseHelper = new DatabaseHelper(this);

        hikeId = getIntent().getIntExtra("HIKE_ID", -1);

        if (hikeId != -1) {

            isEdit = true;

            loadHike();

            btnSave.setText("UPDATE");
        }
        etDate.setOnClickListener(v -> showDatePicker());
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.difficulty_array,
                        android.R.layout.simple_dropdown_item_1line
                );
        actDifficulty.setAdapter(adapter);
        actDifficulty.setOnClickListener(v -> actDifficulty.showDropDown());
        actDifficulty.setOnItemClickListener((parent, view, position, id) -> {updateEstimatedDuration();});
        etLength.addTextChangedListener(textWatcher);
        btnSave.setOnClickListener(v -> saveHike());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog =
                new DatePickerDialog(
                        this,
                        (view, y, m, d) -> {

                            String date = d + "/" + (m + 1) + "/" + y;

                            etDate.setText(date);
                        },
                        year,
                        month,
                        day
                );
        dialog.show();
    }

    private final TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s,
                                      int start,
                                      int count,
                                      int after) {

        }

        @Override
        public void onTextChanged(CharSequence s,
                                  int start,
                                  int before,
                                  int count) {

            updateEstimatedDuration();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private void updateEstimatedDuration() {

        String lengthText = etLength.getText().toString().trim();

        String difficulty = actDifficulty.getText().toString().trim();

        if (lengthText.isEmpty() || difficulty.isEmpty()) {

            tvEstimatedDuration.setText("About 0 hour");

            return;
        }

        double length;
        try {
            length = Double.parseDouble(lengthText);
        } catch (NumberFormatException e) {
            etLength.setError("Invalid number");
            etLength.requestFocus();
            return;
        }

        double minutesPerKm;

        switch (difficulty) {

            case "Easy":
                minutesPerKm = 30;
                break;

            case "Medium":
                minutesPerKm = 45;
                break;

            default:
                minutesPerKm = 60;
                break;
        }

        int totalMinutes = (int) (length * minutesPerKm);

        int hours = totalMinutes / 60;

        int minutes = totalMinutes % 60;

        String result;

        if (minutes == 0) {

            result = "About " + hours + " hour";

        } else {

            result = "About " + hours + " hour " + minutes + " min";
        }

        tvEstimatedDuration.setText(result);
    }

    private void saveHike() {
        String hikeName = etHikeName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String length = etLength.getText().toString().trim();
        String difficulty = actDifficulty.getText().toString().trim();
        String duration = tvEstimatedDuration.getText().toString();
        String description = etDescription.getText().toString().trim();

        boolean parking = swParking.isChecked();

        if (hikeName.isEmpty()) {
            etHikeName.setError("Please enter hike name");
            etHikeName.requestFocus();
            return;
        }

        if (location.isEmpty()) {
            etLocation.setError("Please enter location");
            etLocation.requestFocus();
            return;
        }

        if (date.isEmpty()) {
            Toast.makeText(this, "Please select date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (length.isEmpty()) {
            etLength.setError("Please enter hike length");
            etLength.requestFocus();
            return;
        }

        if (difficulty.isEmpty()) {
            actDifficulty.setError("Select difficulty");
            actDifficulty.requestFocus();
            return;
        }

        // Prepare Hike details for confirmation
        String message = "Name: " + hikeName + "\n" +
                "Location: " + location + "\n" +
                "Date: " + date + "\n" +
                "Parking: " + (parking ? "Yes" : "No") + "\n" +
                "Length: " + length + " km\n" +
                "Difficulty: " + difficulty + "\n" +
                "Duration: " + duration + "\n" +
                "Description: " + (description.isEmpty() ? "None" : description);

        new AlertDialog.Builder(this)
                .setTitle("Confirm Hike Details")
                .setMessage(message)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    performSave(hikeName, location, date, parking, length, difficulty, duration, description);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performSave(String hikeName, String location, String date, boolean parking, 
                             String length, String difficulty, String duration, String description) {
        Hike hike = new Hike();

        SharedPreferences preferences = getSharedPreferences("MHIKE", MODE_PRIVATE);
        int userId = preferences.getInt("userId", -1);

        hike.setUserId(userId);
        hike.setName(hikeName);
        hike.setLocation(location);
        hike.setDate(date);
        hike.setParkingAvailable(parking);

        try {
            hike.setLength(Double.parseDouble(length));
        } catch (NumberFormatException e) {
            etLength.setError("Invalid length");
            etLength.requestFocus();
            return;
        }

        hike.setDifficulty(difficulty);
        hike.setEstimatedDuration(duration);
        hike.setDescription(description);
        hike.setStatus("ACTIVE");

        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        hike.setCreatedAt(now);
        hike.setUpdatedAt(now);
        hike.setDeletedAt(null);

        boolean success;
        if (isEdit) {
            hike.setId(hikeId);
            success = databaseHelper.updateHike(hike);
        } else {
            success = databaseHelper.insertHike(hike);
        }

        if (success) {
            Toast.makeText(this, isEdit ? "Hike updated successfully" : "Hike added successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Save hike failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadHike() {

        Hike hike = databaseHelper.getHikeById(hikeId);

        if (hike == null) return;

        etHikeName.setText(hike.getName());
        etLocation.setText(hike.getLocation());
        etDate.setText(hike.getDate());
        etLength.setText(String.valueOf(hike.getLength()));
        actDifficulty.setText(hike.getDifficulty(), false);
        tvEstimatedDuration.setText(hike.getEstimatedDuration());
        etDescription.setText(hike.getDescription());
        swParking.setChecked(hike.isParkingAvailable());
    }
}