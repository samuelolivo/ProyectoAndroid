package com.example.chat.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.chat.data.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private final FirebaseDatabase db;

    public UserRepository() {
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
}