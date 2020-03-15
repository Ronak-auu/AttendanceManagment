package com.example.attendancemanagment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RecordActivity extends AppCompatActivity {


    private EditText editText;
    private Button button;
    private CalendarView calendarView;
    private RecyclerView mlist;
    private FirebaseFirestore firebaseFirestore;

    private FirestoreRecyclerAdapter adapter;

    String today;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        editText = (EditText)findViewById(R.id.editTextclass);
        button = (Button)findViewById(R.id.buttonrecord);
        mlist =findViewById(R.id.recyclerView);
        calendarView=findViewById(R.id.calendarView2);

        firebaseFirestore = FirebaseFirestore.getInstance();

        final Date date=Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        today = dateFormat.format(date);
       // Toast.makeText(RecordActivity.this,today, Toast.LENGTH_LONG).show();

        String classs ="x" ;
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {

                if(month/10 == 0 && dayOfMonth/10 == 0)
                    today ="0"+dayOfMonth+"-"+"0"+(month+1)+"-"+year;
                else if(dayOfMonth/10 == 0 )
                    today ="0"+dayOfMonth+"-"+(month+1)+"-"+year;
                else if(month/10 == 0)
                    today =dayOfMonth+"-"+"0"+(month+1)+"-"+year;
                else
                    today =dayOfMonth+"-"+(month+1)+"-"+year;
               //    Toast.makeText(RecordActivity.this,today, Toast.LENGTH_LONG).show();
            }
        });

        Query query = firebaseFirestore.collection("Institute").document("DDU").collection("Attendance").document(String.valueOf(today)).collection(String.valueOf(classs));

        FirestoreRecyclerOptions<Attendance> options = new FirestoreRecyclerOptions.Builder<Attendance>()
                .setQuery(query,Attendance.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Attendance, AttendanceViewHolder>(options) {
            @NonNull
            @Override
            public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single,parent,false);
                return new AttendanceViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position, @NonNull Attendance model) {
                holder.list_id.setText(model.getId());
                holder.list_attendance.setText(model.getAttendance());
            }
        };

        mlist.setHasFixedSize(true);
        mlist.setLayoutManager(new LinearLayoutManager(this));
        mlist.setAdapter(adapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String clas  ;
                clas = editText.getText().toString().trim();
                Query query = firebaseFirestore.collection("Institute").document("DDU").collection("Attendance").document(String.valueOf(today)).collection(String.valueOf(clas));

                FirestoreRecyclerOptions<Attendance> options = new FirestoreRecyclerOptions.Builder<Attendance>()
                        .setQuery(query,Attendance.class)
                        .build();

                adapter = new FirestoreRecyclerAdapter<Attendance, AttendanceViewHolder>(options) {
                    @NonNull
                    @Override
                    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single,parent,false);
                        return new AttendanceViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position, @NonNull Attendance model) {
                        holder.list_id.setText(model.getId());
                        holder.list_attendance.setText(model.getAttendance());
                    }
                };

                mlist.setHasFixedSize(true);
                mlist.setLayoutManager(new LinearLayoutManager(RecordActivity.this));
                mlist.setAdapter(adapter);

                adapter.startListening();
            }

        });

    }

    private class AttendanceViewHolder extends RecyclerView.ViewHolder {

        private TextView list_id;
        private TextView list_attendance;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);

            list_id = itemView.findViewById(R.id.list_id);
            list_attendance = itemView.findViewById(R.id.list_attendance);

        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
}
