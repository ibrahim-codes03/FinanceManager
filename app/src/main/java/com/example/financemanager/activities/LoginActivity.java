package com.example.financemanager.activities;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.financemanager.R;
import com.example.financemanager.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    FirebaseAuth auth;
    Button loginButton, signUpButton;
    CheckBox rememberMeCheckBox;
    SharedPreferences prefs;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });




        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        boolean rememberMe = prefs.getBoolean("rememberMe", false);
        if (rememberMe) {
            binding.emailInput.setText(prefs.getString("email", ""));
            binding.passwordInput.setText(prefs.getString("password", ""));
            rememberMeCheckBox.setChecked(true);
        }

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.emailInput.getText().toString().trim();
                String password = binding.passwordInput.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill all fields",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(LoginActivity.this,
                            "Password must be at least 6 characters long!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.setMessage("Logging in...");
                progressDialog.show();

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    boolean remember = rememberMeCheckBox.isChecked();
                                    if (remember) {
                                        prefs.edit()
                                                .putString("email", email)
                                                .putString("password", password)
                                                .putBoolean("rememberMe", true)
                                                .apply();
                                    } else {
                                        prefs.edit().clear().apply();
                                    }

                                    Toast.makeText(LoginActivity.this,
                                            "Login successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this,
                                            "Login failed: " + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        binding.forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.emailInput.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(LoginActivity.this,
                            "Please enter your email first", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.setMessage("Sending password reset email...");
                progressDialog.show();

                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this,
                                            "Password reset email sent", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LoginActivity.this,
                                            "Error: " + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
    @Override
    protected void onDestroy() {
        binding = null;
        super.onDestroy();
    }
}
