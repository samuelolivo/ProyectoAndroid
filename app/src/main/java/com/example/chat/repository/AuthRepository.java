package com.example.chat.repository;

import android.content.Intent;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.chat.LoginActivity;
import com.example.chat.MainPage;
import com.example.chat.data.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AuthRepository {
    private FirebaseAuth fAuth;
    private DatabaseReference usersRef;

    public AuthRepository() {
        fAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
    }

    public void login(String email, String password, MutableLiveData<String> authResult) {
        fAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        authResult.setValue("SUCCESS");
                    } else {
                        authResult.setValue(task.getException().getMessage());
                    }
                });
    }

    public void register(String nombre, String email, String password, MutableLiveData<String> authResult) {
        fAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = fAuth.getCurrentUser().getUid();
                        // Guardamos al usuario en la base de datos con foto vacía
                        User newUser = new User(uid, nombre, email, "", true);

                        usersRef.child(uid).setValue(newUser)
                                .addOnCompleteListener(dbTask -> {
                                    if (dbTask.isSuccessful()) {
                                        authResult.setValue("SUCCESS");
                                    } else {
                                        authResult.setValue("Error al guardar en base de datos");
                                    }
                                });
                    } else {
                        authResult.setValue(task.getException().getMessage());
                    }
                });
    }
}
