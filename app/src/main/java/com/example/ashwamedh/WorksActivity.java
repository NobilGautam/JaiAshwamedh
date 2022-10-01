package com.example.ashwamedh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class WorksActivity extends AppCompatActivity {
    private CardView scriptCardView;
    private CardView jingleCardView;
    private CardView creationCardView;
    private ImageButton signOut;
    private BottomNavigationView bottomNavigationView;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference scripts = db.collection("Scripts");

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(WorksActivity.this, Dashboard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_works);

        Objects.requireNonNull(getSupportActionBar()).hide();

        firebaseAuth = FirebaseAuth.getInstance();

        scriptCardView = findViewById(R.id.script_cardView);
        jingleCardView = findViewById(R.id.jingle_cardView);
//        creationCardView = findViewById(R.id.creation_cardView);
        signOut = findViewById(R.id.signOut_works);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Intent intent = new Intent(WorksActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.works_button);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.works_button) {
                    return true;
                }
                if (item.getItemId() == R.id.home_button) {
                    Intent intent = new Intent(WorksActivity.this, Dashboard.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (item.getItemId() == R.id.council_button) {
                    Intent intent = new Intent(WorksActivity.this, CouncilActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (item.getItemId() == R.id.attendance_button) {
                    Intent intent = new Intent(WorksActivity.this, BatchmateAttendance.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (item.getItemId() == R.id.manage_practice_confirmation_button) {
                    Intent intent = new Intent(WorksActivity.this, ManagePracticeConfirmationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });

        scriptCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WorksActivity.this, ScriptsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

//        jingleCardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(WorksActivity.this, JingleActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                finish();
//            }
//        });
//
//        creationCardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(WorksActivity.this, CreationActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                finish();
//            }
//        });


    }
}