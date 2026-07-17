package com.example.m_hike;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.m_hike.database.DatabaseHelper;
import com.example.m_hike.utils.SecurityUtils;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private CheckBox cbRememberMe;

    private Button btnLogin;
    private TextView tvGoToRegister;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        SharedPreferences preferences =
                getSharedPreferences("MHIKE", MODE_PRIVATE);

        boolean isLogin =
                preferences.getBoolean("isLogin", false);

        if (isLogin) {

            startActivity(new Intent(
                    LoginActivity.this,
                    MainActivity.class));

            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        cbRememberMe = findViewById(R.id.cbRememberMe);

        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        databaseHelper = new DatabaseHelper(this);

        btnLogin.setOnClickListener(v -> login());

        tvGoToRegister.setOnClickListener(v -> {

            startActivity(new Intent(
                    LoginActivity.this,
                    RegisterActivity.class));

        });

    }

    private void login() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

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

        boolean success = databaseHelper.checkLogin(email, SecurityUtils.hashPassword(password));

        if (success) {

            SharedPreferences preferences =
                    getSharedPreferences("MHIKE", MODE_PRIVATE);

            int userId = databaseHelper.getUserIdByEmail(email);

            preferences.edit()
                    .putBoolean("isLogin", cbRememberMe.isChecked())
                    .putInt("userId", userId)
                    .putString("email", email)
                    .putString(
                            "username",
                            databaseHelper.getUsernameByEmail(email)
                    )
                    .apply();

            Toast.makeText(this,
                    "Login Successfully",
                    Toast.LENGTH_SHORT).show();

            startActivity(new Intent(
                    LoginActivity.this,
                    MainActivity.class));

            finish();

        } else {

            Toast.makeText(this,
                    "Invalid email or password",
                    Toast.LENGTH_SHORT).show();

        }

    }

}