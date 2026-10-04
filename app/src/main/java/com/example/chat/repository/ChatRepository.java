package com.example.chat.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.chat.data.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatRepository {

    private DatabaseReference chatsRef;
    private DatabaseReference usersRef;

    public ChatRepository() {
        chatsRef = FirebaseDatabase.getInstance().getReference("Chats");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
    }

    public void escucharMisChats(String miUid, MutableLiveData<List<Chat>> chatsLiveData) {
        chatsRef.orderByChild("users/" + miUid).equalTo(true)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Chat> listaTemporal = new ArrayList<>();
                        for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                            Chat chat = chatSnapshot.getValue(Chat.class);
                            if (chat != null) {
                                listaTemporal.add(chat);
                            }
                        }
                        chatsLiveData.setValue(listaTemporal);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    public void crearChatPorCorreo(String correoAmigo, String miUid, MutableLiveData<String> resultadoCreacion) {

        Log.d("correo amigo", correoAmigo);
        usersRef.orderByChild("correo").equalTo(correoAmigo)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnap : snapshot.getChildren()) {
                                String idDelAmigo = userSnap.getKey();

                                if (idDelAmigo.equals(miUid)) {
                                    resultadoCreacion.setValue("No puedes crear un chat contigo mismo");
                                    return;
                                }

                                String idNuevoChat = chatsRef.push().getKey();
                                HashMap<String, Boolean> participantes = new HashMap<>();
                                participantes.put(miUid, true);
                                participantes.put(idDelAmigo, true);

                                Chat nuevoChat = new Chat(idNuevoChat, participantes, "Chat iniciado", System.currentTimeMillis());

                                chatsRef.child(idNuevoChat).setValue(nuevoChat).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        resultadoCreacion.setValue("SUCCESS");
                                    } else {
                                        resultadoCreacion.setValue("Error al guardar el chat");
                                    }
                                });
                                return;
                            }
                        } else {
                            resultadoCreacion.setValue("No existe un usuario con ese correo");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        resultadoCreacion.setValue("Error de conexión: " + error.getMessage());
                    }
                });
    }



    // Agrega esto en tu ChatRepository.java
    public void escucharEstadoConexion(MutableLiveData<Boolean> conexionLiveData) {
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");

        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean connected = snapshot.getValue(Boolean.class);
                if (connected != null) {
                    conexionLiveData.setValue(connected);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // No hacemos nada aquí
            }
        });
    }
}