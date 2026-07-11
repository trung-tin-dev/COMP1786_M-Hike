package com.example.m_hike;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.m_hike.database.DatabaseHelper;
import com.example.m_hike.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etEmail;
    private EditText etPassword;

    private Button btnSignUp;
    private TextView tvGoToLogin;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etRegisterUsername);
        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);

        btnSignUp = findViewById(R.id.btnSignUp);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        databaseHelper = new DatabaseHelper(this);

        btnSignUp.setOnClickListener(v -> registerUser());

        tvGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

    }

    private void registerUser() {

        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty()) {
            etUsername.setError("Username is required");
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email");
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return;
        }

        if (databaseHelper.isEmailExists(email)) {
            Toast.makeText(this,
                    "Email already exists",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String createdAt = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
        ).format(new Date());

        User user = new User(
                username,
                email,
                password,
                "",
                createdAt
        );

        boolean success = databaseHelper.insertUser(user);

        if (success) {

            Toast.makeText(this,
                    "Register Successfully",
                    Toast.LENGTH_SHORT).show();

            startActivity(new Intent(
                    RegisterActivity.this,
                    LoginActivity.class
            ));

            finish();

        } else {

            Toast.makeText(this,
                    "Register Failed",
                    Toast.LENGTH_SHORT).show();

        }

    }

}