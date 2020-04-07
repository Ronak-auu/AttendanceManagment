package com.example.attendancemanagment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private EditText etName,etRollno,etClass,etEmail,etId,edContact,etDob;
    private Button register;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = (EditText) findViewById(R.id.etName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etRollno = (EditText) findViewById(R.id.etRollno);
        etClass = (EditText) findViewById(R.id.etClass);
        etId = (EditText) findViewById(R.id.etId);
        edContact = (EditText) findViewById(R.id.edContact);
        etDob = (EditText) findViewById(R.id.etDob);

        register = (Button) findViewById(R.id.Register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    String Id = etId.getText().toString();
                    firebaseAuth = FirebaseAuth.getInstance();
                    db = FirebaseFirestore.getInstance();
                    DocumentReference dr = db.collection("Institute").document("DDU").collection("Student").document(Id);
                    dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Toast.makeText(Register.this, "User already exist", Toast.LENGTH_LONG).show();
                                } else {
                                    Map<String, Object> user = new HashMap<>();
                                    Map<String, Object> usere = new HashMap<>();
                                    user.put("Id",etId.getText().toString());
                                    user.put("Name",etName.getText().toString());
                                    user.put("Dob",etDob.getText().toString());
                                    user.put("Class",etClass.getText().toString().toUpperCase());
                                    user.put("RollNo",etRollno.getText().toString().toUpperCase());
                                    user.put("ContactNo",edContact.getText().toString());
                                    user.put("Email",etEmail.getText().toString().toLowerCase());
                                    user.put("Icard","No");
                                    db.collection("Institute").document("DDU")
                                            .collection("Class").document(etClass.getText().toString())
                                            .collection("Students").document(etId.getText().toString())
                                            .set(usere).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(Register.this, "User added", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    db.collection("Institute").document("DDU").collection("Student").document(etId.getText().toString()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(Register.this, "User added", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(Register.this, MainActivity.class));
                                            SendMail();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Register.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
                                            Log.d("Error", e.getMessage());
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        });
    }
    public void SendMail(){
        String mail = etEmail.getText().toString();
        String subject = "Attendance Management";
        String message = "This Mail from Attendance Managment\nYour Account is Added\nYour Id : "+etId.getText().toString()+"\nYour Password : "+etDob.getText().toString();
        Sendmail sm = new Sendmail(this,mail,subject,message);
        sm.execute();
    }
    private boolean validate(){
        boolean result = false;
        String Id = etId.getText().toString();
        String Name = etName.getText().toString();
        String Dob = etDob.getText().toString();
        String Contact = edContact.getText().toString();
        String Class = etClass.getText().toString();
        String RollNo = etRollno.getText().toString();
        String Email = etEmail.getText().toString();
        //Toast.makeText(this,"User added",Toast.LENGTH_LONG).show();
        if(Id.isEmpty())
            Toast.makeText(this,"Please Enter a Id",Toast.LENGTH_LONG);
        else if(Name.isEmpty())
            Toast.makeText(this,"Please Enter a Name",Toast.LENGTH_LONG);
        else if (Dob.isEmpty())
            Toast.makeText(this,"Please Enter a Dob",Toast.LENGTH_LONG);
        else if (Class.isEmpty())
            Toast.makeText(this,"Please Enter a Class",Toast.LENGTH_LONG);
        else if (RollNo.isEmpty())
            Toast.makeText(this,"Please Enter a RollNo",Toast.LENGTH_LONG);
        else if (Email.isEmpty())
            Toast.makeText(this,"Please Enter a Email",Toast.LENGTH_LONG);
        else if (Contact.isEmpty())
            Toast.makeText(this,"Please Enter a Contact No.",Toast.LENGTH_LONG);
        else{
            //Toast.makeText(this,"User",Toast.LENGTH_LONG).show();
            result = true;
        }
        return result;
    }
}