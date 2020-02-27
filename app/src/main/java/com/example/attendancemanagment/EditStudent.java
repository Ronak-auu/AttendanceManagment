package com.example.attendancemanagment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditStudent extends AppCompatActivity {


    private EditText etName,etRollno,etClass,etEmail,etId,edContact,etDob;
    private Button edit,go,submit;
    FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student);

        etName = (EditText) findViewById(R.id.etName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etRollno = (EditText) findViewById(R.id.etRollno);
        etClass = (EditText) findViewById(R.id.etClass);
        etId = (EditText) findViewById(R.id.etId);
        edContact = (EditText) findViewById(R.id.edContact);
        etDob = (EditText) findViewById(R.id.etDob);
        edit = (Button) findViewById(R.id.Edit);
        go = (Button) findViewById(R.id.Go);
        submit = (Button) findViewById(R.id.Submit);

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth = FirebaseAuth.getInstance();
                db = FirebaseFirestore.getInstance();
                DocumentReference dr = db.collection("Institute").document("DDU").collection("Student").document(etId.getText().toString().trim());
                dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                etName.setText(document.get("Name").toString());
                                etName.setEnabled(false);
                                etEmail.setText(document.get("Email").toString());
                                etEmail.setEnabled(false);
                                etRollno.setText(document.get("RollNo").toString());
                                etRollno.setEnabled(false);
                                edContact.setText(document.get("ContactNo").toString());
                                edContact.setEnabled(false);
                                etClass.setText(document.get("Class").toString());
                                etClass.setEnabled(false);
                                etDob.setText(document.get("Dob").toString());
                                etDob.setEnabled(false);
                                etId.setEnabled(false);
                            } else {
                                Toast.makeText(EditStudent.this, "User Not Found", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etName.setEnabled(true);
                etEmail.setEnabled(true);
                etRollno.setEnabled(true);
                edContact.setEnabled(true);
                etClass.setEnabled(true);
                etDob.setEnabled(true);
                edit.setVisibility(View.INVISIBLE);
                submit.setVisibility(View.VISIBLE);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                auth = FirebaseAuth.getInstance();
                db = FirebaseFirestore.getInstance();
                DocumentReference dr = db.collection("Institute").document("DDU").collection("Student").document(etId.getText().toString().trim());
                dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                                Map<String, Object> user = new HashMap<>();
                                //user.put("Id",etId.getText().toString());
                                user.put("Name",etName.getText().toString());
                                user.put("Dob",etDob.getText().toString());
                                user.put("Class",etClass.getText().toString());
                                user.put("RollNo",etRollno.getText().toString());
                                user.put("Email",etEmail.getText().toString());
                                user.put("ContactNo",edContact.getText().toString());

                            db.collection("Institute").document("DDU").collection("Student").document(etId.getText().toString()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(EditStudent.this, "User updated", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(EditStudent.this, MainActivity.class));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EditStudent.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
                                        Log.d("Error", e.getMessage());
                                    }
                                });


                        }
                    }
                });
            }
        });

    }
}
