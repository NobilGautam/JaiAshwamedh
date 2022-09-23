package com.example.ashwamedh;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashwamedh.model.ProfilePicture;
import com.example.ashwamedh.util.UserApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

    private static final int GALLERY_CODE = 1;
    private static final String TAG = "EDIT_PROFILE";
    private TextView updateProfilePictureTextView;
    private ImageView profileImageView;
    private TextView emailTextView;
    private TextView nameTextView;
    private Button changePasswordButton;
    private CardView changePasswordCardView;
    private EditText enterPasswordEditText;
    private EditText confirmPasswordEditText;
    private Button updatePasswordButton;
    private Button saveChangesButton;
    private BottomNavigationView bottomNavigationView;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private StorageReference storageReference;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Profile Pictures");

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Objects.requireNonNull(getSupportActionBar()).hide();

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        updateProfilePictureTextView = findViewById(R.id.edit_profile_picture_button);
        profileImageView = findViewById(R.id.profile_picture_imageView);
        emailTextView = findViewById(R.id.email_textView_profile);
        nameTextView = findViewById(R.id.name_textView_profile);
        changePasswordButton = findViewById(R.id.change_password_button);
        changePasswordCardView = findViewById(R.id.change_password_cardView);
        enterPasswordEditText = findViewById(R.id.new_password_editText);
        confirmPasswordEditText = findViewById(R.id.confirm_password_editText);
        updatePasswordButton = findViewById(R.id.update_password_button);
        saveChangesButton = findViewById(R.id.save_changes_button);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        changePasswordCardView.setVisibility(View.INVISIBLE);

        bottomNavigationView.setSelectedItemId(R.id.home_button);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home_button) {
                    Intent intent = new Intent(EditProfileActivity.this, Dashboard.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (item.getItemId() == R.id.council_button) {
                    Intent intent = new Intent(EditProfileActivity.this, CouncilActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (item.getItemId() == R.id.attendance_button) {
                    Intent intent = new Intent(EditProfileActivity.this, BatchmateAttendance.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (item.getItemId() == R.id.manage_practice_confirmation_button) {
                    Intent intent = new Intent(EditProfileActivity.this, ManagePracticeConfirmationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });

        final String[] imageUrl = {""};
        collectionReference.whereEqualTo("userId", UserApi.getInstance().getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                imageUrl[0] = snapshot.getString("imagePath");
                                if (!Objects.equals(imageUrl[0], "")) {
                                    Picasso.get().load(imageUrl[0]).into(profileImageView);
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

        updateProfilePictureTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_CODE);
            }
        });

        emailTextView.setText(UserApi.getInstance().getUserEmailId());
        nameTextView.setText(UserApi.getInstance().getUsername());

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePasswordCardView.setVisibility(View.VISIBLE);
            }
        });

        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPassword = enterPasswordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(EditProfileActivity.this, "Passwords don't match!", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    //Updating password
                    AlertDialog.Builder alertDialogBox = new AlertDialog.Builder(EditProfileActivity.this);

                    alertDialogBox.setTitle("Enter Old Password");
                    alertDialogBox.setCancelable(true);
                    final EditText passwordEditText = new EditText(EditProfileActivity.this);
                    alertDialogBox.setView(passwordEditText);
                    alertDialogBox.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String password = passwordEditText.getText().toString().trim();
                            String email = firebaseUser.getEmail();
                            assert email != null;
                            AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                            firebaseUser.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            firebaseUser.updatePassword(newPassword)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toast.makeText(EditProfileActivity.this,
                                                                            "Password changed successfully",
                                                                            Toast.LENGTH_SHORT)
                                                                    .show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(EditProfileActivity.this,
                                                                            "Error in changing password: " + e.getMessage(),
                                                                            Toast.LENGTH_SHORT)
                                                                    .show();
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: couldn't reauthenticate");
                                        }
                                    });
                        }
                    });
                    alertDialogBox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialogBox.show();

                    enterPasswordEditText.setText("");
                    confirmPasswordEditText.setText("");

                    changePasswordCardView.setVisibility(View.INVISIBLE);

                }
            }
        });

        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + imageUri.toString());
                if (imageUri != null) {
                    Log.d(TAG, "onClick: inside save changes");
                    StorageReference filepath = storageReference
                            .child("profile_pictures")
                            .child("profileImg_" + UserApi.getInstance().getUserId());

                    Log.d(TAG, "onClick: filepath set");

                    filepath.putFile(imageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    filepath.getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    Log.d(TAG, "onSuccess: inside putfile method");
                                                    ProfilePicture profilePicture = new ProfilePicture();
                                                    profilePicture.setUserId(UserApi.getInstance().getUserId());
                                                    profilePicture.setImagePath(uri.toString());
                                                    collectionReference.document(UserApi.getInstance().getUserId() + "_profileImg")
                                                            .set(profilePicture)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Toast.makeText(EditProfileActivity.this,
                                                                                    "Saved Successfully!", Toast.LENGTH_SHORT)
                                                                            .show();
                                                                    Intent intent = new Intent(EditProfileActivity.this, Dashboard.class);
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.d(TAG, "onCollectionFailure: " + e.getMessage());
                                                                }
                                                            });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "onFailure: get download url " + e.getMessage());
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: put file " + e.getMessage());
                                }
                            });
                }


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            assert data != null;
            if (data.getData() != null) {
                imageUri = data.getData();
                Log.d(TAG, imageUri.toString());
                profileImageView.setImageURI(imageUri);
            }
        }
        Log.d(TAG, "onActivityResult: " + imageUri.toString());
    }
}