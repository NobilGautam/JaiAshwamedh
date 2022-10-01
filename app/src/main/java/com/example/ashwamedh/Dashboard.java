package com.example.ashwamedh;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ashwamedh.model.Confirmation;
import com.example.ashwamedh.util.UserApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class Dashboard extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private static TextView currentAttendanceTextView;
    private FloatingActionButton presentButton;
    private FloatingActionButton absentButton;
    private EditText remarkEditText;
    private Button updateConfirmationButton;
    private ImageButton signOut;
    private TextView intimationTextView;
    private TextView greetingNameTextView;
    private ImageView profilePicture;

    private String practiceDay;
    private String practiceDate;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Confirmations");
    private CollectionReference profileCollectionReference = db.collection("Profile Pictures");


    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private static FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Objects.requireNonNull(getSupportActionBar()).hide();


        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };
        currentUser = firebaseAuth.getCurrentUser();

        currentAttendanceTextView = findViewById(R.id.current_attendance_textView);
        presentButton = findViewById(R.id.present_fab);
        absentButton = findViewById(R.id.absent_fab);
        remarkEditText = findViewById(R.id.remark_edit_text);
        updateConfirmationButton = findViewById(R.id.update_confirmation_button);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home_button);
        signOut = findViewById(R.id.signOut_dashboard);
        greetingNameTextView = findViewById(R.id.greeting_name_textview);
        intimationTextView = findViewById(R.id.practice_intimation_textview);
        profilePicture = findViewById(R.id.userImageView);

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, EditProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        final String[] imageUrl = {""};
        profileCollectionReference.whereEqualTo("userId", UserApi.getInstance().getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()){
                            for (QueryDocumentSnapshot snapshot: queryDocumentSnapshots) {
                                imageUrl[0] = snapshot.getString("imagePath");
                                if (!Objects.equals(imageUrl[0], "")) {
                                    Picasso.get().load(imageUrl[0]).into(profilePicture);
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Intent intent = new Intent(Dashboard.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        absentButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(Dashboard.this, R.color.absent_button_color)));
        presentButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(Dashboard.this, R.color.present_button_color)));

        UserApi userApi = UserApi.getInstance();
        greetingNameTextView.setText(userApi.getUsername());

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        practiceDate = simpleDateFormat.format(tomorrow);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case Calendar.MONDAY: practiceDay = "MONDAY"; break;
            case Calendar.TUESDAY: practiceDay = "TUESDAY"; break;
            case Calendar.WEDNESDAY: practiceDay = "WEDNESDAY"; break;
            case Calendar.THURSDAY: practiceDay = "THURSDAY"; break;
            case Calendar.FRIDAY: practiceDay = "FRIDAY"; break;
            case Calendar.SATURDAY: practiceDay = "SATURDAY";
            calendar.add(Calendar.DAY_OF_YEAR, 2);
            tomorrow = calendar.getTime();
            practiceDate = simpleDateFormat.format(tomorrow);
            practiceDay = "MONDAY";
            break;
            case Calendar.SUNDAY: practiceDay = "SUNDAY";
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                tomorrow = calendar.getTime();
                practiceDate = simpleDateFormat.format(tomorrow);
                practiceDay = "MONDAY";
            break;
        }

        intimationTextView.setText("Add your confirmation for the next practice at " + practiceDate + ", " + practiceDay);

        updateAttendance(collectionReference);


        final String[] confirmation = {""};
        presentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmation[0] = "present";
                presentButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(Dashboard.this, R.color.selected_button_color)));
                absentButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(Dashboard.this, R.color.absent_button_color)));
            }
        });
        absentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmation[0] = "absent";
                absentButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(Dashboard.this, R.color.selected_button_color)));
                presentButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(Dashboard.this, R.color.present_button_color)));
            }
        });
        
        //submitting confirmation
        Date finalTomorrow = tomorrow;
        updateConfirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (confirmation[0] == "") {
                    Toast.makeText(Dashboard.this, "Confirm your presence first", Toast.LENGTH_SHORT)
                            .show();
                } else if (confirmation[0] == "absent" && TextUtils.isEmpty(remarkEditText.getText().toString())) {
                    Toast.makeText(Dashboard.this, "Mention the reason for your absence", Toast.LENGTH_SHORT)
                            .show();
                } else if(confirmation[0] == "absent" && !TextUtils.isEmpty(remarkEditText.getText().toString())) {
                    UserApi userApi = UserApi.getInstance();
                    String userId = userApi.getUserId();
                    String username = userApi.getUsername();
                    Log.d("DashboardConf", "onClick: " + username);
                    Confirmation currentConfirmation = new Confirmation();
                    currentConfirmation.setUsername(username);
                    currentConfirmation.setConfirmation("absent");
                    currentConfirmation.setUserId(userId);
                    currentConfirmation.setPracticeDate(practiceDate);
                    currentConfirmation.setRemarkOrReason(remarkEditText.getText().toString().trim());
                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd_MM_yyyy");
                    String date = simpleDateFormat1.format(finalTomorrow);
                    String docAddress = userId+"_"+date;
                    Log.d("docAddress", "onClick: " + docAddress);
                    collectionReference.document(docAddress)
                            .set(currentConfirmation)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(Dashboard.this, "Thankyou for confirming your presence", Toast.LENGTH_SHORT)
                                            .show();
                                    confirmation[0] = "";
                                    absentButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(Dashboard.this, R.color.absent_button_color)));
                                    remarkEditText.setText("");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Dashboard.this, "Couldn't update attendance", Toast.LENGTH_SHORT)
                                            .show();
                                    confirmation[0] = "";
                                    absentButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(Dashboard.this, R.color.absent_button_color)));
                                    remarkEditText.setText("");
                                }
                            });
                } else if (confirmation[0] == "present") {
                    UserApi userApi = UserApi.getInstance();
                    String userId = userApi.getUserId();
                    String username = userApi.getUsername();
                    Confirmation currentConfirmation = new Confirmation();
                    currentConfirmation.setConfirmation("present");
                    currentConfirmation.setUsername(username);
                    currentConfirmation.setUserId(userId);
                    currentConfirmation.setPracticeDate(practiceDate);
                    currentConfirmation.setRemarkOrReason(remarkEditText.getText().toString().trim());
                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd_MM_yyyy");
                    String date = simpleDateFormat1.format(finalTomorrow);
                    String docAddress = userId+"_"+date;
                    Log.d("docAddress", "onClick: " + docAddress);
                    collectionReference.document(docAddress)
                            .set(currentConfirmation)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(Dashboard.this, "Thankyou for confirming your presence", Toast.LENGTH_SHORT)
                                            .show();
                                    confirmation[0] = "";
                                    presentButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(Dashboard.this, R.color.present_button_color)));
                                    remarkEditText.setText("");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Dashboard.this, "Couldn't update attendance", Toast.LENGTH_SHORT)
                                            .show();
                                    confirmation[0] = "";
                                    presentButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(Dashboard.this, R.color.present_button_color)));
                                    remarkEditText.setText("");
                                }
                            });
                }

                updateAttendance(collectionReference);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home_button) {
                    return true;
                }
                if (item.getItemId() == R.id.council_button) {
                    Intent intent = new Intent(Dashboard.this, CouncilActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (item.getItemId() == R.id.works_button) {
                    Intent intent = new Intent(Dashboard.this, WorksActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (item.getItemId() == R.id.attendance_button) {
                    Intent intent = new Intent(Dashboard.this, BatchmateAttendance.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (item.getItemId() == R.id.manage_practice_confirmation_button) {
                    Intent intent = new Intent(Dashboard.this, ManagePracticeConfirmationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });


    }

    private static void updateAttendance(CollectionReference collectionReference) {
        int[] daysPresent = {0};
        int[] totalDays = {0};
        //Counting no. of days present
        collectionReference.whereEqualTo("userId", currentUser.getUid())
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                assert value != null;
                                if (!value.isEmpty()) {
                                    for (QueryDocumentSnapshot snapshot : value) {
                                        if (Objects.equals(snapshot.getString("confirmation"), "present")) {
                                            daysPresent[0]++;
                                        }
                                        totalDays[0]++;
                                    }

                                    currentAttendanceTextView.setText("Your current attendance: " + daysPresent[0] + "/" + totalDays[0]);
                                }
                            }
                        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(authStateListener);
        currentUser = firebaseAuth.getCurrentUser();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("dashboard_act", "onResume: called");
        final String[] imageUrl = {""};
        profileCollectionReference.whereEqualTo("userId", UserApi.getInstance().getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()){
                            for (QueryDocumentSnapshot snapshot: queryDocumentSnapshots) {
                                imageUrl[0] = snapshot.getString("imagePath");
                                if (!Objects.equals(imageUrl[0], "")) {
                                    Picasso.get().load(imageUrl[0]).into(profilePicture);
                                }
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