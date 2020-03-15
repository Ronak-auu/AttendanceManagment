package com.example.attendancemanagment;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.content.Context;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText etName, etPassword;
    private TextView Info;
    private Button Login;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    FirebaseFirestore db;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedpreferences = getSharedPreferences("Am", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        if (!sharedpreferences.getString("Id","").isEmpty()){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
        //Toast.makeText(getApplicationContext(), sharedpreferences.getString("Id",""), Toast.LENGTH_SHORT).show();
        etName = (EditText)findViewById(R.id.etName);
        etPassword = (EditText)findViewById(R.id.etPassword);
        Info = (TextView)findViewById(R.id.tvInfo);
        Login = (Button)findViewById(R.id.btnLogin);
        //progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,Register.class));
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Id = etName.getText().toString().trim();
                final String Password = etPassword.getText().toString().trim();
                if (TextUtils.isEmpty(Id)) {
                    Toast.makeText(getApplicationContext(), "Enter Id", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(Password)) {
                    Toast.makeText(getApplicationContext(), "Enter the password", Toast.LENGTH_SHORT).show();
                    return;
                }
                auth = FirebaseAuth.getInstance();
                db = FirebaseFirestore.getInstance();
                DocumentReference dr = db.collection("Institute").document("DDU").collection("Faculty").document(Id);
                dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                if(document.get("Dob").equals(Password)){
                                    sharedpreferences = getSharedPreferences("Am", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString("type","Faculty");
                                    editor.putString("Id",Id);
                                    editor.commit();
                                    Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                }
                                else{
                                    Toast.makeText(LoginActivity.this, "Password is Wrong", Toast.LENGTH_LONG).show();
                                }
                            }
                            else {
                                DocumentReference dr = db.collection("Institute").document("DDU").collection("Student").document(Id);
                                dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                if(document.get("Dob").equals(Password)){
                                                    sharedpreferences = getSharedPreferences("Am", Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                                    editor.putString("Id","Student");
                                                    editor.commit();
                                                    Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_LONG).show();
                                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                }
                                                else{
                                                    Toast.makeText(LoginActivity.this, "Password is Wrong", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                            else
                                                Toast.makeText(LoginActivity.this, "User Not Found", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
                /*auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            if (password.length() < 6) {
                                etPassword.setError(getString(R.string.minimum_password));
                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                    }
                });*/
            }
        });
    }
}