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

public class Profile extends AppCompatActivity {

    private EditText id,name,email,contact,dob;
    private Button edit,submit;
    FirebaseFirestore db;
    private FirebaseAuth auth;
    String etId;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        id = (EditText)findViewById(R.id.editText2);
        name = (EditText)findViewById(R.id.editText5);
        email = (EditText)findViewById(R.id.editText3);
        contact = (EditText)findViewById(R.id.editText4);
        dob = (EditText)findViewById(R.id.editText6);
        edit = (Button)findViewById(R.id.edit);
        submit = (Button)findViewById(R.id.update);
        submit.setVisibility(View.INVISIBLE);

        sharedpreferences = getSharedPreferences("Am", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        etId = sharedpreferences.getString("Id","");

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        DocumentReference dr = db.collection("Institute").document("DDU").collection("Faculty").document(etId.toString().trim());

        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        id.setText(document.get("Id").toString());
                        id.setEnabled(false);
                        name.setText(document.get("Name").toString());
                        name.setEnabled(false);
                        email.setText(document.get("Email").toString());
                        email.setEnabled(false);
                        contact.setText(document.get("ContactNumber").toString());
                        contact.setEnabled(false);
                        dob.setText(document.get("Dob").toString());
                        dob.setEnabled(false);

                    } else {
                        Toast.makeText(Profile.this, "User Not Found", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setEnabled(true);
                email.setEnabled(true);
                contact.setEnabled(true);
                dob.setEnabled(true);
                edit.setVisibility(View.INVISIBLE);
                submit.setVisibility(View.VISIBLE);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                auth = FirebaseAuth.getInstance();
                db = FirebaseFirestore.getInstance();
                DocumentReference dr = db.collection("Institute").document("DDU").collection("Faculty").document(etId.toString().trim());
                dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            Map<String, Object> user = new HashMap<>();
                            user.put("Id",etId.toString());
                            user.put("Name",name.getText().toString());
                            user.put("Dob",dob.getText().toString());
                            user.put("Email",email.getText().toString());
                            user.put("ContactNumber",contact.getText().toString());
                            user.put("Icard","No");

                            db.collection("Institute").document("DDU").collection("Faculty").document(etId.toString()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Profile.this, "Profile updated", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(Profile.this, MainActivity.class));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Profile.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
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
