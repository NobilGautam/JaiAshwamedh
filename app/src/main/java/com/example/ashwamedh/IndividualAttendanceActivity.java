package com.example.ashwamedh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.ashwamedh.adapter.AllAttendanceRecyclerViewAdapter;
import com.example.ashwamedh.adapter.IndividualAttendanceRecyclerViewAdapter;
import com.example.ashwamedh.model.Confirmation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IndividualAttendanceActivity extends AppCompatActivity {
    private TextView userGreetingTextView;
    private RecyclerView recyclerView;
    private IndividualAttendanceRecyclerViewAdapter individualAttendanceRecyclerViewAdapter;
    private BottomNavigationView bottomNavigationView;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Confirmations");

    private List<Confirmation> confirmationList;
    private String userId;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(IndividualAttendanceActivity.this, BatchmateAttendance.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_attendance);

        userId = getIntent().getStringExtra("userId");

        Objects.requireNonNull(getSupportActionBar()).hide();

        userGreetingTextView = findViewById(R.id.indivAttendance_header_textView);

        recyclerView = findViewById(R.id.individual_attendance_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        confirmationList = new ArrayList<>();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.attendance_button);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home_button) {
                    Intent intent = new Intent(IndividualAttendanceActivity.this, Dashboard.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (item.getItemId() == R.id.works_button) {
                    Intent intent = new Intent(IndividualAttendanceActivity.this, WorksActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (item.getItemId() == R.id.council_button) {
                    Intent intent = new Intent(IndividualAttendanceActivity.this, CouncilActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (item.getItemId() == R.id.attendance_button) {
                    Intent intent = new Intent(IndividualAttendanceActivity.this, BatchmateAttendance.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (item.getItemId() == R.id.manage_practice_confirmation_button) {
                    Intent intent = new Intent(IndividualAttendanceActivity.this, ManagePracticeConfirmationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        userId = getIntent().getStringExtra("userId");
        collectionReference.whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                Confirmation confirmation = snapshot.toObject(Confirmation.class);
                                confirmationList.add(confirmation);
                            }


                            individualAttendanceRecyclerViewAdapter = new IndividualAttendanceRecyclerViewAdapter(confirmationList, IndividualAttendanceActivity.this);
                            recyclerView.setAdapter(individualAttendanceRecyclerViewAdapter);
                            individualAttendanceRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}