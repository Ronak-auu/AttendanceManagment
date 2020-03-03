package com.example.attendancemanagment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.icu.text.DateFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecordActivity extends AppCompatActivity {


    private EditText editText;
    private Button button;

    private RecyclerView mlist;
    private FirebaseFirestore firebaseFirestore;

    private FirestoreRecyclerAdapter adapter;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        editText = (EditText)findViewById(R.id.editTextclass);
        button = (Button)findViewById(R.id.buttonrecord);
        mlist =findViewById(R.id.recyclerView);

        firebaseFirestore = FirebaseFirestore.getInstance();

        Date d = new Date();
        final String date = DateFormat.getDateInstance().format(d);

        String classs = "d";
        //classs = editText.getText().toString().trim();
        Query query = firebaseFirestore.collection("Institute").document("DDU").collection("Attendance").document(String.valueOf(date)).collection(String.valueOf(classs));

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
