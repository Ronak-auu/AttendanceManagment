package com.example.attendancemanagment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.icu.text.DateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class MainActivity extends AppCompatActivity {
    String TAG = "GenereatQrCode";
    String inputvalue;
    EditText edttxt;
    ImageView qrimg;
    Button start,scan;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    Toolbar toolbar;
    SharedPreferences sharedpreferences;
    private  long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.shitstuff);

        toolbar = findViewById(R.id.toolbar);;
        setSupportActionBar(toolbar);

        View headerView = mNavigationView.getHeaderView(0);

        Menu mn = mNavigationView.getMenu();
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {


            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();

                if (menuItem.getItemId() == R.id.nav_item_managestudent) {
                    startActivity(new Intent(MainActivity.this, Register.class));
                    return true;
                }

                if (menuItem.getItemId() == R.id.nav_item_record) {
                    startActivity(new Intent(MainActivity.this, RecordActivity.class));
                    return true;
                }

                if (menuItem.getItemId() == R.id.nav_item_profile) {
                    startActivity(new Intent(MainActivity.this, Profile.class));
                    return true;
                }

                if (menuItem.getItemId() == R.id.nav_item_attendance) {
                    startActivity(new Intent(MainActivity.this, ManageAttendance.class));
                    return true;
                }

                if (menuItem.getItemId() == R.id.nav_item_editstudent) {
                    startActivity(new Intent(MainActivity.this, EditStudent.class));
                    return true;
                }

                if (menuItem.getItemId() == R.id.nav_item_logout) {
                    sharedpreferences = getSharedPreferences("Am", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.clear();
                    editor.commit();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    return true;
                }
                return false;
            }
        });

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,toolbar,  R.string.app_name,
                R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

        qrimg=(ImageView)findViewById(R.id.qrcode);
        edttxt=(EditText)findViewById(R.id.edittext);
        start=(Button)findViewById(R.id.createbtn);
        scan=(Button)findViewById(R.id.scanbtn);
        start.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                inputvalue = edttxt.getText().toString().trim();
                if(inputvalue.length() > 0){
                    WindowManager manager =(WindowManager)getSystemService(WINDOW_SERVICE);
                    Display display=manager.getDefaultDisplay();
                    Point point = new Point();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                        display.getSize(point);
                    }
                    int width = point.x;
                    int height = point.y;
                    int smallerdimention = width<height ? width:height;
                    smallerdimention = smallerdimention*3/4;
                    qrgEncoder = new QRGEncoder(inputvalue,null, QRGContents.Type.TEXT,smallerdimention);
                    try {
                        bitmap = qrgEncoder.encodeAsBitmap();
                        qrimg.setImageBitmap(bitmap);
                    }
                    catch (WriterException e)
                    {
                        Log.v(TAG,e.toString());
                    }

                }else {
                    edttxt.setError("Required");
                }
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
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

    @Override
    public void onBackPressed(){
        if (backPressedTime+2000 > System.currentTimeMillis()){
            finishAffinity();
            System.exit(0);
        }
        else
            Toast.makeText(getBaseContext(),"Press back again to Exit",Toast.LENGTH_SHORT).show();
        backPressedTime = System.currentTimeMillis();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null && result.getContents()!=null)
        {
            Date d = new Date();
            final String date = DateFormat.getDateInstance().format(d);

            String id = result.getContents();
            final char c = id.charAt(2);

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Scan Result")
                    .setMessage(result.getContents())
                    .setPositiveButton("Next",new DialogInterface.OnClickListener(){

                        public void onClick(DialogInterface dialog,int which){
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            Map<String, Object> user = new HashMap<>();
                            user.put("Class","B");
                            user.put("Id", result.getContents());

                            db.collection("Institute").document("DDU").collection("Attendance").document(String.valueOf(date)).collection(String.valueOf(c)).document(result.getContents()).set(user);

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