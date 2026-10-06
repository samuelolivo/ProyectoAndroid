package com.example.chat.data.repository;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.chat.data.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private final FirebaseDatabase db;
    private final FirebaseStorage storage;

    public UserRepository() {
        storage = FirebaseStorage.getInstance();
        db = FirebaseDatabase.getInstance();
    }

    public void observeAllUsers(String userId, MutableLiveData<List<User>> usuariosLiveData) {
        db.getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> listaUsuarios = new ArrayList<>();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    User usuario = snap.getValue(User.class);
                    if (usuario != null && usuario.getIdUser() != null) {
                        if (!usuario.getIdUser().equals(userId)) {
                            listaUsuarios.add(usuario);
                        }
                    }
                }
                usuariosLiveData.postValue(listaUsuarios);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                usuariosLiveData.postValue(new ArrayList<>());
            }
        });
    }
    public void cargarMiPerfil(String uid, MutableLiveData<User> perfilLiveData) {
        DatabaseReference userRef = db.getReference("Users").child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User miUsuario = snapshot.getValue(User.class);
                perfilLiveData.setValue(miUsuario);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                perfilLiveData.setValue(null);
            }
        });
    }
    public void updateOnlineStatus(String uid, boolean isOnline) {
        db.getReference("Users").child(uid).child("online").setValue(isOnline);
        db.getReference("Users").child(uid).child("online").onDisconnect().setValue(false);;
    }


    public void actualizarNombre(String uid, String nuevoNombre, MutableLiveData<Boolean> resultadoUpdate) {
        DatabaseReference userRef = db.getReference("Users").child(uid);
        userRef.child("name").setValue(nuevoNombre).addOnCompleteListener(task -> {
            resultadoUpdate.setValue(task.isSuccessful());
        });
    }

    public void subirFotoPerfil(String uid, Uri imageUri, MutableLiveData<String> resultadoFoto) {

        StorageReference fileRef = storage.getReference().child("profile_images").child(uid + ".jpg");

        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();

                db.getReference("Users").child(uid).child("foto").setValue(imageUrl);
                resultadoFoto.setValue(imageUrl);
            });
        }).addOnFailureListener(e -> {
            resultadoFoto.setValue("ERROR");
        });
    }


    public void guardarTokenFCM(String uid, String token) {
        db.getReference("Users").child(uid).child("fcmToken").setValue(token);
    }

    public void actualizarTokenFCM(String uid) {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                       return;
                    }

                    if (task.getResult() != null) {
                        String token = task.getResult();

                        // Intentamos guardarlo en la base de datos
                        db.getReference("Users").child(uid).child("fcmToken").setValue(token);

                    }
                });
    }
}
