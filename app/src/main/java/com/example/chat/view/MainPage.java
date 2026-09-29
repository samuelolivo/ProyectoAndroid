package com.example.chat.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chat.Adapters.ChatAdapter;
import com.example.chat.data.Chat;
import com.example.chat.databinding.ActivityMainPageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainPage extends AppCompatActivity {
private ActivityMainPageBinding bng;
     private DatabaseReference userRef;
     private String uidActualUser;
    private ChatAdapter adapter;
    private List<Chat> listaChats;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bng = ActivityMainPageBinding.inflate(getLayoutInflater());
        setContentView(bng.getRoot());

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Si por algún motivo no hay sesión, lo devolvemos al login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        uidActualUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
//manejo de online
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(uidActualUser);
        userRef.child("online").setValue(true);
        userRef.child("online").onDisconnect().setValue(false);
//recyclerView
        bng.recyclerViewChats.setLayoutManager(new LinearLayoutManager(this));
        listaChats = new ArrayList<>();
        adapter = new ChatAdapter(listaChats);
        bng.recyclerViewChats.setAdapter(adapter);

        cargarChatsDesdeFirebase();
//
//        bng.btnLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LogOut();
//            }
//        });

        bng.btnNuevoChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainPage.this, "Abrir lista de contactos...", Toast.LENGTH_SHORT).show();
                LogOut();
            }
        });
    }

    //funcion para cerrar sesion
    private void LogOut() {
        userRef.child("online").setValue(false);
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainPage.this, LoginActivity.class);

        startActivity(intent);

    }

    private void cargarChatsDesdeFirebase() {
        DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference("Chats");

        // Magia de Firebase: "Búscame solo los chats donde mi ID tenga el valor true"
        chatsRef.orderByChild("users/" + uidActualUser).equalTo(true)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listaChats.clear(); // Limpiamos la lista vieja para evitar duplicados en la pantalla

                        // Recorremos todos los chats que Firebase encontró
                        for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                            Chat chat = chatSnapshot.getValue(Chat.class);
                            if (chat != null) {
                                listaChats.add(chat);
                            }
                        }

                        // Le avisamos al Adaptador que la lista cambió para que repinte la pantalla
                        adapter.notifyDataSetChanged();
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainPage.this, "Error al cargar chats: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}