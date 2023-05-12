package com.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import com.chatapp.databinding.ActivityUpdateBinding;
import com.chatapp.models.User;
import com.chatapp.utilities.Constants;
import com.chatapp.utilities.PreferenceManager;

import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.SetOptions;


public class UpdateActivity extends AppCompatActivity {

    private ActivityUpdateBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        database = FirebaseFirestore.getInstance();
        getUserDetails();
        setListeners();

        binding.buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(),MainActivity.class)));
    }

    private void getUserDetails() {
        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        binding.textName.setText(user.name);
                        binding.textEmail.setText(user.email);
                        binding.textImage.setText(user.image);
                        binding.textToken.setText(user.token);
                        binding.textId.setText(user.id);
                        // update image here if necessary
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(UpdateActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void updateUser() {
        String name = binding.textName.getText().toString().trim();
        String email = binding.textEmail.getText().toString().trim();
        String image = binding.textImage.getText().toString().trim();
        String token = binding.textToken.getText().toString().trim();
        String id = binding.textId.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }

        // update user object with new data
        User user = new User();
        user.name = name;
        user.email = email;
        user.image = image;
        user.token = token;
        user.id = id;

        binding.buttonUpdate.setEnabled(false);

        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .set(user, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    preferenceManager.putString(Constants.KEY_NAME, name);
                    preferenceManager.putString(Constants.KEY_EMAIL, email);
                    preferenceManager.putString(Constants.KEY_IMAGE, image);
                    preferenceManager.putString(Constants.KEY_FCM_TOKEN,token);
                    preferenceManager.putString(Constants.KEY_USER_ID,id);
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    binding.buttonUpdate.setEnabled(true);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    binding.buttonUpdate.setEnabled(true);
                });
    }
}
