package com.example.ashwamedh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashwamedh.adapter.ManageAttendanceRecyclerViewAdapter;
import com.example.ashwamedh.adapter.OnAttendanceClickListener;
import com.example.ashwamedh.model.Confirmation;
import com.example.ashwamedh.util.UserApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.C;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ManagePracticeConfirmationActivity extends AppCompatActivity implements OnAttendanceClickListener {
    private static final String TAG = "PRACTICE_CONFIRMATION";
    private RecyclerView recyclerView;
    private BottomNavigationView bottomNavigationView;
    private CardView markAttendanceCardView;
    private FloatingActionButton presentFab;
    private FloatingActionButton absentFab;
    private Button updateButton;
    private TextView noPracticeTextView;
    private ImageButton signOut;

    private Boolean isAdmin;
    private String attendance;

    private ManageAttendanceRecyclerViewAdapter manageAttendanceRecyclerViewAdapter;
    private String practiceDate;

    private List<Confirmation> practiceList;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Confirmations");
    private CollectionReference userCollection = db.collection("Users");

    private FirebaseAuth firebaseAuth;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ManagePracticeConfirmationActivity.this, Dashboard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_practice_confirmation);

        Objects.requireNonNull(getSupportActionBar()).hide();

        firebaseAuth = FirebaseAuth.getInstance();
        practiceList = new ArrayList<>();

        recyclerView = findViewById(R.id.manage_attendance_recycler_view);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        markAttendanceCardView = findViewById(R.id.mark_attendance_cardView);
        presentFab = findViewById(R.id.present_fab);
        absentFab = findViewById(R.id.absent_fab);
        updateButton = findViewById(R.id.update_attendance_button);
        noPracticeTextView = findViewById(R.id.no_practice_textView);
        signOut = findViewById(R.id.signOut_manage_attendance);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Intent intent = new Intent(ManagePracticeConfirmationActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        absentFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ManagePracticeConfirmationActivity.this, R.color.absent_button_color)));
        presentFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ManagePracticeConfirmationActivity.this, R.color.present_button_color)));


        bottomNavigationView.setSelectedItemId(R.id.manage_practice_confirmation_button);

        markAttendanceCardView.setVisibility(View.INVISIBLE);

        UserApi userApi = UserApi.getInstance();
        isAdmin = Objects.equals(userApi.getUsername(), "ADMIN");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        attendance = "";

        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        practiceDate = simpleDateFormat.format(today);

        if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
        || Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            recyclerView.setVisibility(View.INVISIBLE);
            noPracticeTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noPracticeTextView.setVisibility(View.INVISIBLE);
            userCollection
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                    String userId = snapshot.getString("userId");
                                    String username = snapshot.getString("username");
                                    SimpleDateFormat docAddressFormat = new SimpleDateFormat("dd_MM_yyyy");
                                    String practiceDateDoc = docAddressFormat.format(today);
                                    String docAddress = userId + "_" + practiceDateDoc;
                                    collectionReference.document(docAddress)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful() &&
                                                            !Objects.equals(snapshot.getString("username"), "ADMIN")) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (!document.exists()) {
                                                            Confirmation confirmation = new Confirmation();
                                                            confirmation.setUserId(userId);
                                                            confirmation.setUsername(username);
                                                            confirmation.setConfirmation("absent");
                                                            confirmation.setPracticeDate(practiceDate);
                                                            confirmation.setRemarkOrReason("Didn't add confirmation");

                                                            collectionReference.document(docAddress).set(confirmation);
                                                        }
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
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: couldn't fetch user collection");
                        }
                    });
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home_button) {
                    Intent intent = new Intent(ManagePracticeConfirmationActivity.this, Dashboard.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (item.getItemId() == R.id.council_button) {
                    Intent intent = new Intent(ManagePracticeConfirmationActivity.this, CouncilActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (item.getItemId() == R.id.attendance_button) {
                    Intent intent = new Intent(ManagePracticeConfirmationActivity.this, BatchmateAttendance.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (item.getItemId() == R.id.manage_practice_confirmation_button) {
                    return true;
                }
                if (item.getItemId() == R.id.works_button) {
                    Intent intent = new Intent(ManagePracticeConfirmationActivity.this, WorksActivity.class);
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
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        practiceDate = simpleDateFormat.format(today);
        collectionReference.whereEqualTo("practiceDate", practiceDate)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                Confirmation confirmation = snapshot.toObject(Confirmation.class);
                                practiceList.add(confirmation);
                            }


                            manageAttendanceRecyclerViewAdapter = new ManageAttendanceRecyclerViewAdapter(practiceList,
                                    ManagePracticeConfirmationActivity.this,
                                    ManagePracticeConfirmationActivity.this);
                            recyclerView.setAdapter(manageAttendanceRecyclerViewAdapter);
                            manageAttendanceRecyclerViewAdapter.notifyDataSetChanged();

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    @Override
    public void OnAttendanceClick(String userId, String username) {
        Log.d(TAG, "OnAttendanceClick: "+ isAdmin);
        UserApi userApi = UserApi.getInstance();
        isAdmin = Objects.equals(userApi.getUsername(), "ADMIN");
        if (isAdmin) {
            markAttendanceCardView.setVisibility(View.VISIBLE);
            presentFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attendance = "present";
                    presentFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ManagePracticeConfirmationActivity.this, R.color.selected_button_color)));
                    absentFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ManagePracticeConfirmationActivity.this, R.color.absent_button_color)));
                }
            });
            absentFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attendance = "absent";
                    absentFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ManagePracticeConfirmationActivity.this, R.color.selected_button_color)));
                    presentFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ManagePracticeConfirmationActivity.this, R.color.present_button_color)));
                }
            });
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Confirmation confirmation = new Confirmation();
                    confirmation.setUserId(userId);
                    confirmation.setUsername(username);
                    confirmation.setConfirmation(attendance);
                    confirmation.setPracticeDate(practiceDate);
                    if (Objects.equals(attendance, "present")) {
                        confirmation.setRemarkOrReason("PRESENT ON PRACTICE");
                    } else if (Objects.equals(attendance, "absent")){
                        confirmation.setRemarkOrReason("ABSENT ON PRACTICE");
                    }
                    SimpleDateFormat documentDateFormat = new SimpleDateFormat("dd_MM_yyyy");
                    Date today = Calendar.getInstance().getTime();
                    String docDate = documentDateFormat.format(today);
                    String docAddress = userId+"_"+docDate;
                    collectionReference.document(docAddress)
                            .set(confirmation)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(ManagePracticeConfirmationActivity.this, "Attendance marked!", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            });
                    attendance = "";
                    absentFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ManagePracticeConfirmationActivity.this, R.color.absent_button_color)));
                    presentFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ManagePracticeConfirmationActivity.this, R.color.present_button_color)));
                    markAttendanceCardView.setVisibility(View.INVISIBLE);
                    manageAttendanceRecyclerViewAdapter.notifyDataSetChanged();
                }
            });
        } else {
            Toast.makeText(ManagePracticeConfirmationActivity.this, "Only admins can mark attendance!", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}