package com.example.attendancemanagment;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ManageAttendance extends AppCompatActivity {

    Button scan;
    EditText classs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_attendance);

        scan=(Button)findViewById(R.id.scanbtn);
        classs=(EditText)findViewById(R.id.etclasss);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(ManageAttendance.this);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                intentIntegrator.setCameraId(0);
                intentIntegrator.setOrientationLocked(false);
                intentIntegrator.setPrompt("scanning");
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setBarcodeImageEnabled(true);
                intentIntegrator.initiateScan();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null && result.getContents()!=null)
        {
            Date d = new Date();
            final String date = DateFormat.getDateInstance().format(d);


            new AlertDialog.Builder(ManageAttendance.this)
                    .setTitle("Scan Result")
                    .setMessage(result.getContents())
                    .setPositiveButton("Next",new DialogInterface.OnClickListener(){

                        public void onClick(DialogInterface dialog,int which){
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            Map<String, Object> user = new HashMap<>();
                            user.put("Class",classs.getText().toString().trim());
                            user.put("Id", result.getContents());
                            user.put("attendance","p");
                            db.collection("Institute").document("DDU").collection("Attendance").document(String.valueOf(date)).collection(classs.getText().toString().trim()).document(result.getContents()).set(user);

                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
