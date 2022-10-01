package com.example.ashwamedh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

public class JingleLyricsActivity extends AppCompatActivity {
    private CardView jingleCardView;
    private TextView titleTextView;
    private TextView lyricsTextView;
    private BottomNavigationView bottomNavigationView;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Jingles");

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(JingleLyricsActivity.this, JingleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jingle_lyrics);

        Objects.requireNonNull(getSupportActionBar()).hide();

        String title = getIntent().getStringExtra("title");
        String currentMilliSec = getIntent().getStringExtra("currentMilli");

        jingleCardView = findViewById(R.id.jingle_lyrics_cardView);
        titleTextView = findViewById(R.id.lyrics_title_textView);
        lyricsTextView = findViewById(R.id.lyrics_lyrics_textView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        collectionReference.document("jingle_" + title + "_" + currentMilliSec)
                        .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        titleTextView.setText(documentSnapshot.getString("title"));
                                        lyricsTextView.setText(documentSnapshot.getString("lyrics"));
                                    }
                                });

        bottomNavigationView.setSelectedItemId(R.id.works_button);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.works_button) {
                    Intent intent = new Intent(JingleLyricsActivity.this, WorksActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (item.getItemId() == R.id.home_button) {
                    Intent intent = new Intent(JingleLyricsActivity.this, Dashboard.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (item.getItemId() == R.id.council_button) {
                    Intent intent = new Intent(JingleLyricsActivity.this, CouncilActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (item.getItemId() == R.id.attendance_button) {
                    Intent intent = new Intent(JingleLyricsActivity.this, BatchmateAttendance.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (item.getItemId() == R.id.manage_practice_confirmation_button) {
                    Intent intent = new Intent(JingleLyricsActivity.this, ManagePracticeConfirmationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });

    }
}