package com.example.chat.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chat.adapters.ChatAdapter;
import com.example.chat.R;
import com.example.chat.data.Chat;
import com.example.chat.databinding.ActivityMainPageBinding;
import com.example.chat.viewModel.MainViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainPage extends AppCompatActivity {

    private ActivityMainPageBinding bng;
    private ChatAdapter adapter;
    private List<Chat> listaChats;
    private MainViewModel viewModel;
    private String miUid;
    private boolean navSincronizando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bng = ActivityMainPageBinding.inflate(getLayoutInflater());
        setContentView(bng.getRoot());

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        miUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference("Users").child(miUid).child("online").setValue(true);
        FirebaseDatabase.getInstance().getReference("Users").child(miUid).child("online").onDisconnect().setValue(false);

        listaChats = new ArrayList<>();
        adapter = new ChatAdapter(listaChats);
        bng.recyclerViewChats.setLayoutManager(new LinearLayoutManager(this));
        bng.recyclerViewChats.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        viewModel.getChatsLiveData().observe(this, chats -> {
            listaChats.clear();
            if (chats != null) {
                listaChats.addAll(chats);
            }
            adapter.notifyDataSetChanged();
        });

        viewModel.getResultadoCreacionChat().observe(this, resultado -> {
            if (resultado != null) {
                if (resultado.equals("SUCCESS")) {
                    Toast.makeText(this, "¡Chat creado exitosamente!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, resultado, Toast.LENGTH_LONG).show();
                }
            }
        });

        viewModel.getLogoutLiveData().observe(this, cerrado -> {
            if (cerrado != null && cerrado) {
                Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainPage.this, LoginActivity.class));
                finish();
            }
        });

        bng.bottomNav.setOnItemSelectedListener(item -> {
            if (navSincronizando) return true;
            int id = item.getItemId();

            if (id == R.id.nav_chats) {
                return true;

            } else if (id == R.id.nav_contactos) {
                startActivity(new Intent(MainPage.this, ContactsActivity.class));
                return true;

            } else if (id == R.id.nav_ajustes) {
                // Aquí abrirás tu pantalla de ajustes en el futuro
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainPage.this);
                builder.setTitle("Cerrar Sesión");
                builder.setMessage("¿Estás seguro de que deseas salir de tu cuenta?");

                builder.setPositiveButton("Sí, salir", (dialog, which) -> {
                    // Le pedimos al ViewModel que haga el trabajo
                    viewModel.cerrarSesion(miUid);
                });

                builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
                builder.show();

                return true;
            }

            return false;
        });

        viewModel.cargarMisChats(miUid);

        bng.btnNuevoChat.setOnClickListener(v -> mostrarDialogoBuscarCorreo());
    }

    private void mostrarDialogoBuscarCorreo() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Nuevo Chat");
        builder.setMessage("Ingresa el correo del usuario:");

        final android.widget.EditText inputCorreo = new android.widget.EditText(this);
        inputCorreo.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(inputCorreo);

        builder.setPositiveButton("Buscar", (dialog, which) -> {
            String correoBuscado = inputCorreo.getText().toString().trim().toLowerCase()    ;
            if (!correoBuscado.isEmpty()) {
                viewModel.iniciarNuevoChat(correoBuscado, miUid);
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navSincronizando = true;
        bng.bottomNav.setSelectedItemId(R.id.nav_chats);
        navSincronizando = false;
    }
}