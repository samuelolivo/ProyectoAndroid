package com.example.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chat.databinding.ActivityMainPageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.chat.databinding.ActivityMainPageBinding;
public class MainPage extends AppCompatActivity {
private ActivityMainPageBinding bng;
     DatabaseReference userRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bng = ActivityMainPageBinding.inflate(getLayoutInflater());
        setContentView(bng.getRoot());
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        userRef.child("online").setValue(true);
        userRef.child("online").onDisconnect().setValue(false);

        bng.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
}