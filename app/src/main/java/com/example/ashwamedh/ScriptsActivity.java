package com.example.ashwamedh;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ashwamedh.adapter.OnScriptClickListener;
import com.example.ashwamedh.adapter.ScriptRecyclerViewAdapter;
import com.example.ashwamedh.model.Script;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class ScriptsActivity extends AppCompatActivity implements OnScriptClickListener {
    private static final int SELECT_PDF_RESULT_CODE = 1;
    private RecyclerView scriptRecyclerView;
    private FloatingActionButton addScriptButton;
    private BottomNavigationView bottomNavigationView;
    private CardView addScriptCardView;
    private ImageView chooseScriptButton;
    private EditText titleEditText;
    private EditText sceneEditText;
    private Button uploadScriptButton;
    private ProgressBar progressBar;

    private ScriptRecyclerViewAdapter scriptRecyclerViewAdapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference scriptCollectionReference = db.collection("Scripts");
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private List<Script> scriptList;

    final Boolean[] cardViewActivated = new Boolean[1];
    private Uri pdfUri;

    //ADD PROGRESSBAR


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (cardViewActivated[0]) {
            Intent intent = new Intent(ScriptsActivity.this, ScriptsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            //code to go to previous activity or to close the application
            Intent intent = new Intent(ScriptsActivity.this, WorksActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scripts);

        Objects.requireNonNull(getSupportActionBar()).hide();

        scriptRecyclerView = findViewById(R.id.script_recycler_view);
        addScriptButton = findViewById(R.id.add_script_button);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        addScriptCardView = findViewById(R.id.add_script_cardView);
        chooseScriptButton = findViewById(R.id.choose_script_button);
        titleEditText = findViewById(R.id.script_title_editText);
        sceneEditText = findViewById(R.id.script_scene_editText);
        uploadScriptButton = findViewById(R.id.upload_script_button);
        progressBar = findViewById(R.id.script_progressBar);

        progressBar.setVisibility(View.INVISIBLE);

        addScriptCardView.setVisibility(View.INVISIBLE);
        cardViewActivated[0] = false;

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("script_uploads");

        scriptList = new ArrayList<>();

        scriptRecyclerView.setHasFixedSize(true);
        scriptRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        cardViewActivated[0] = false;

        addScriptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardViewActivated[0] = true;
                addScriptCardView.setVisibility(View.VISIBLE);
            }
        });

        chooseScriptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, SELECT_PDF_RESULT_CODE);
            }
        });

        uploadScriptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(titleEditText.getText()) &&
                !TextUtils.isEmpty(sceneEditText.getText()) &&
                pdfUri != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    String title = titleEditText.getText().toString().trim();
                    String scenes = sceneEditText.getText().toString().trim();

                    String currentMiliSecond = String.valueOf(System.currentTimeMillis());
                    String storageFilePath = "script_" + title + currentMiliSecond + ".pdf";
                    StorageReference filepath = storageReference
                            .child("scripts")
                            .child(storageFilePath);

                    filepath.putFile(pdfUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                                    while(!uri.isComplete());
                                    String pdfDownloadUrl = uri.getResult().toString();
                                    Script script = new Script();
                                    script.setTitle(title);
                                    script.setScenes(scenes);
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    String dateAdded = simpleDateFormat.format(Calendar.getInstance().getTime());
                                    script.setTimeAdded(dateAdded);
                                    script.setDownloadLink(pdfDownloadUrl);
                                    script.setCurrentTimeMiliSec(currentMiliSecond);

                                    databaseReference.child(Objects.requireNonNull(databaseReference.push().getKey())).setValue(script);

                                    scriptCollectionReference.document(storageFilePath)
                                            .set(script)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    titleEditText.setText("");
                                                    sceneEditText.setText("");
                                                    addScriptCardView.setVisibility(View.INVISIBLE);
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(ScriptsActivity.this,
                                                                    "Script uploaded successfully!"
                                                                    , Toast.LENGTH_SHORT)
                                                            .show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    titleEditText.setText("");
                                                    sceneEditText.setText("");
                                                    addScriptCardView.setVisibility(View.INVISIBLE);
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    titleEditText.setText("");
                                    sceneEditText.setText("");
                                    addScriptCardView.setVisibility(View.INVISIBLE);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(ScriptsActivity.this, "Error: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.works_button);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.works_button) {
                    Intent intent = new Intent(ScriptsActivity.this, WorksActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (item.getItemId() == R.id.home_button) {
                    Intent intent = new Intent(ScriptsActivity.this, Dashboard.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (item.getItemId() == R.id.council_button) {
                    Intent intent = new Intent(ScriptsActivity.this, CouncilActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (item.getItemId() == R.id.attendance_button) {
                    Intent intent = new Intent(ScriptsActivity.this, BatchmateAttendance.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (item.getItemId() == R.id.manage_practice_confirmation_button) {
                    Intent intent = new Intent(ScriptsActivity.this, ManagePracticeConfirmationActivity.class);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PDF_RESULT_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                pdfUri = data.getData();
                Toast.makeText(ScriptsActivity.this, "Script chosen!", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    public void onScriptClick(String title, String miliSeconds) {
        String scriptFilepath = "script_" + title + miliSeconds + ".pdf";
        scriptCollectionReference.document(scriptFilepath)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Script script = documentSnapshot.toObject(Script.class);

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        assert script != null;
                        intent.setData(Uri.parse(script.getDownloadLink()));
                        startActivity(intent);
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                scriptList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Script script = postSnapshot.getValue(Script.class);
                    scriptList.add(script);
                }

                scriptRecyclerViewAdapter = new ScriptRecyclerViewAdapter(scriptList, ScriptsActivity.this,
                        ScriptsActivity.this);
                scriptRecyclerView.setAdapter(scriptRecyclerViewAdapter);
                scriptRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}