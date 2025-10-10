package com.example.financemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText emailET, passwordET;
    Button signupBtn, loginRedirectBtn;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    ProgressDialog progressDialog;
    User user;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");

        emailET = findViewById(R.id.emailInput);
        passwordET = findViewById(R.id.passwordInput);
        signupBtn = findViewById(R.id.signUpButton);
        loginRedirectBtn = findViewById(R.id.loginRedirectButton);

        user = new User();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill all fields",
                            Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    createAccount(email, password);
                }
            }
        });

        loginRedirectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void createAccount(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            id = auth.getCurrentUser().getUid();
                            user.setEmail(email);
                            user.setPassword(password);
                            reference.child(id).setValue(user);
                            progressDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "Account Created Successfully!",
                                    Toast.LENGTH_LONG).show();
                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "Signup Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
