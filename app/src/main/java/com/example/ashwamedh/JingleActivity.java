package com.example.ashwamedh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ashwamedh.adapter.JingleRecyclerViewAdapter;
import com.example.ashwamedh.adapter.OnJingleClickListener;
import com.example.ashwamedh.model.Jingle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class JingleActivity extends AppCompatActivity implements OnJingleClickListener {
    private RecyclerView jingleRecyclerView;
    private FloatingActionButton addJingleButton;
    private BottomNavigationView bottomNavigationView;
    private CardView addJingleCardView;
    private EditText jingleTitleET;
    private EditText jingleSceneET;
    private EditText jingleLyricsET;
    private Button uploadJingleButton;
    private ProgressBar progressBar;
    private JingleRecyclerViewAdapter jingleRecyclerViewAdapter;

    private List<Jingle> jingleList;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Jingles");

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(JingleActivity.this, WorksActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jingle);

        Objects.requireNonNull(getSupportActionBar()).hide();

        jingleRecyclerView = findViewById(R.id.jingle_recycler_view);
        addJingleButton = findViewById(R.id.add_jingle_button);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        addJingleCardView = findViewById(R.id.add_jingle_cardView);
        jingleTitleET = findViewById(R.id.jingle_title_editText);
        jingleSceneET = findViewById(R.id.jingle_scene_editText);
        jingleLyricsET = findViewById(R.id.jingle_lyrics_editText);
        uploadJingleButton = findViewById(R.id.upload_jingle_button);
        progressBar = findViewById(R.id.jingle_progressBar);

        jingleList = new ArrayList<>();

        jingleRecyclerView.setHasFixedSize(true);
        jingleRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar.setVisibility(View.INVISIBLE);
        addJingleCardView.setVisibility(View.INVISIBLE);

        bottomNavigationView.setSelectedItemId(R.id.works_button);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.works_button) {
                    Intent intent = new Intent(JingleActivity.this, WorksActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (item.getItemId() == R.id.home_button) {
                    Intent intent = new Intent(JingleActivity.this, Dashboard.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (item.getItemId() == R.id.council_button) {
                    Intent intent = new Intent(JingleActivity.this, CouncilActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (item.getItemId() == R.id.attendance_button) {
                    Intent intent = new Intent(JingleActivity.this, BatchmateAttendance.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (item.getItemId() == R.id.manage_practice_confirmation_button) {
                    Intent intent = new Intent(JingleActivity.this, ManagePracticeConfirmationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });

        addJingleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addJingleCardView.setVisibility(View.VISIBLE);
            }
        });

        uploadJingleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String title = jingleTitleET.getText().toString().trim();
                String scene = jingleSceneET.getText().toString().trim();
                String lyrics = jingleLyricsET.getText().toString().trim();

                if (!TextUtils.isEmpty(title) &&
                !TextUtils.isEmpty(scene) &&
                !TextUtils.isEmpty(lyrics)) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String dateAdded = simpleDateFormat.format(Calendar.getInstance().getTime());
                    Jingle jingle = new Jingle(title, scene, lyrics, dateAdded, String.valueOf(System.currentTimeMillis()));
                    collectionReference.document("jingle_" + title + "_" + jingle.getCurrentMilliSeconds())
                            .set(jingle)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    addJingleCardView.setVisibility(View.INVISIBLE);
                                    Toast.makeText(JingleActivity.this, "Jingle added!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(JingleActivity.this, "Couldn't add jingle! " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                    addJingleCardView.setVisibility(View.INVISIBLE);
                                }
                            });

                } else {
                    Toast.makeText(JingleActivity.this, "Fields can not be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        collectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                Jingle jingle = snapshot.toObject(Jingle.class);
                                jingleList.add(jingle);
                            }

                            jingleRecyclerViewAdapter = new JingleRecyclerViewAdapter(JingleActivity.this,
                                    jingleList, JingleActivity.this);
                            jingleRecyclerView.setAdapter(jingleRecyclerViewAdapter);
                            jingleRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(JingleActivity.this, "Couldn't fetch data" + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onJingleClick(String title, String currentMilliSeconds) {
        Intent intent = new Intent(JingleActivity.this, JingleLyricsActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("currentMilli", currentMilliSeconds);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}