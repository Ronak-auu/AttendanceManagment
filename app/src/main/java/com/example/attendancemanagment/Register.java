package com.example.attendancemanagment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private EditText inputEmail,inputPassword;
    private Button register;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);

        register = (Button) findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();


                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(),"Enter email address!!",Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(),"Password is empty!!",Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(),"Username is empty!!",Toast.LENGTH_SHORT).show();
                }



                if(password.length()<6){
                    Toast.makeText(getApplicationContext(),"Password is short",Toast.LENGTH_SHORT).show();
                }




                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(Register.this,"createuseremailcomplete"+task.isSuccessful(),Toast.LENGTH_SHORT).show();

                        if(!task.isSuccessful()){

                            Toast.makeText(Register.this,"AuthFailed"+task.getException(),Toast.LENGTH_SHORT).show();
                        }

                        else{
                            FirebaseUser currentperson = FirebaseAuth.getInstance().getCurrentUser();

                            DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Teachers").child(currentperson.getUid());



                            ref.child("Email").setValue(email);

                            ref.child("Password").setValue(password);

                            startActivity(new Intent(Register.this,LoginActivity.class));


                        }
                    }
                });

            }

        });

    }
}
