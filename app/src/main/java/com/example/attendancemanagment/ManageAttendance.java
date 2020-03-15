package com.example.attendancemanagment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ManageAttendance extends AppCompatActivity {

    private LayoutInflater inflater;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog;
    Button scan, abs, notify;
    EditText classs;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_attendance);

        scan = (Button) findViewById(R.id.scanbtn);
        classs = (EditText) findViewById(R.id.etclasss);
        abs = (Button) findViewById(R.id.abs);
        notify = (Button) findViewById(R.id.notify);
        final Date date = Calendar.getInstance().getTime();
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        final String today = formatter.format(date);

        abs.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                alertDialogBuilder = new AlertDialog.Builder(ManageAttendance.this);
                inflater = LayoutInflater.from(ManageAttendance.this);
                View view = inflater.inflate(R.layout.confirmationbox, null);

                Button yesButton = (Button) view.findViewById(R.id.cyesButton);
                Button noButton = (Button) view.findViewById(R.id.cnoButton);

                alertDialogBuilder.setView(view);
                dialog = alertDialogBuilder.create();
                dialog.show();

                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("Institute").document("DDU").collection("Class")
                                .document(classs.getText().toString()).collection("Students")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            // QueryDocumentSnapshot documents = (QueryDocumentSnapshot) task.getResult().getDocuments();
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                Map<String, Object> user = new HashMap<>();
                                                user.put("Class", classs.getText().toString());
                                                user.put("Id", document.getId());
                                                user.put("attendance", "a");
                                                db.collection("Institute").document("DDU")
                                                        .collection("Attendance").document(String.valueOf(today))
                                                        .collection(classs.getText().toString()).document(document.getId()).set(user);
                                            }
                                        } else {
                                            Log.d("Tag", "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                        dialog.dismiss();
                    }

                });


            }
        });

        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Institute").document("DDU").collection("Attendance").document(String.valueOf(today))
                        .collection(classs.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (final QueryDocumentSnapshot document : task.getResult()) {

                                        if (document.get("attendance").equals("a")) {
                                            Log.v("Tag", "Id "+document.getId(), task.getException());

                                            //String id = document.get("attendance").toString();
                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                            db.collection("Institute").document("DDU").collection("Student")
                                            .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                for (QueryDocumentSnapshot document1 : task.getResult()) {
                                                                    String id1 = (String) document1.get("Id");
                                                                    String id2 = document.getId();
                                                                    Log.v("Tag", "Id1 "+id1, task.getException());
                                                                    Log.v("Tag", "Id "+id2, task.getException());

                                                                    if(id1.equals(id2)){
                                                                        String mail = document1.get("Email").toString();
                                                                        Log.v("Tag", mail, task.getException());
                                                                        SendMail(mail);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    });



                                        }
                                    }
                                }

                            }
                        });
            }
        });

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

    private void SendMail(String mail) {

        String subject = "Attendance Management";
        String message = "You were Absent today";
        Sendmail sm = new Sendmail(ManageAttendance.this,mail,subject,message);
        sm.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && result.getContents() != null) {
            final Date date = Calendar.getInstance().getTime();
            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            final String today = formatter.format(date);


            new AlertDialog.Builder(ManageAttendance.this)
                    .setTitle("Scan Result")
                    .setMessage(result.getContents())
                    .setPositiveButton("Next", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            Map<String, Object> user = new HashMap<>();
                            user.put("Class", classs.getText().toString().trim());
                            user.put("Id", result.getContents());
                            user.put("attendance", "p");
                            db.collection("Institute").document("DDU").collection("Attendance").document(String.valueOf(today)).collection(classs.getText().toString().trim()).document(result.getContents()).set(user);

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
