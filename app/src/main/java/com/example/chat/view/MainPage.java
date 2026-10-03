package com.example.chat.view;

import android.app.AlertDialog;
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
import com.example.chat.viewModel.UserListViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MainPage extends AppCompatActivity {

    private ActivityMainPageBinding bng;
    private ChatAdapter adapter;
    private List<Chat> listaChats;
    private MainViewModel viewModel;
    private UserListViewModel UserviewModel;
    private String miUid;
    private boolean navSincronizando;
    private AlertDialog dialogConexion;

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

        listaChats = new ArrayList<>();
        adapter = new ChatAdapter(listaChats);
        bng.recyclerViewChats.setLayoutManager(new LinearLayoutManager(this));
        bng.recyclerViewChats.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        UserviewModel = new ViewModelProvider(this).get(UserListViewModel.class);
        UserviewModel.changeOnlineStatus(miUid, true);

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

        viewModel.getConexionLiveData().observe(this, hayInternet -> {
            if (!hayInternet) {
//                UserviewModel.changeOnlineStatus(miUid, false);
                mostrarDialogoSinInternet();
            } else {
                UserviewModel.changeOnlineStatus(miUid, true);
                ocultarDialogoSinInternet();
            }
        });

        viewModel.verificarConexion();

        bng.bottomNav.setOnItemSelectedListener(item -> {
            if (navSincronizando) return true;
            int id = item.getItemId();

            if (id == R.id.nav_chats) {
                return true;

            } else if (id == R.id.nav_contactos) {
                startActivity(new Intent(MainPage.this, ContactsActivity.class));
                return true;

            } else if (id == R.id.nav_ajustes) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainPage.this);
                builder.setTitle("Cerrar Sesión");
                builder.setMessage("¿Estás seguro de que deseas salir de tu cuenta?");

                builder.setPositiveButton("Sí, salir", (dialog, which) -> {
                    viewModel.cerrarSesion(miUid);
                    UserviewModel.changeOnlineStatus(miUid, false);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nuevo Chat");
        builder.setMessage("Ingresa el correo del usuario:");

        final android.widget.EditText inputCorreo = new android.widget.EditText(this);
        inputCorreo.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(inputCorreo);

        builder.setPositiveButton("Buscar", (dialog, which) -> {
            String correoBuscado = inputCorreo.getText().toString().trim().toLowerCase();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cuando la pantalla se destruye, quitamos el estado online
        if (miUid != null && UserviewModel != null) {
            UserviewModel.changeOnlineStatus(miUid, false);
        }
    }

    // DIÁLOGO BÁSICO SIN COMPLICACIONES
    private void mostrarDialogoSinInternet() {
        if (isFinishing() || isDestroyed()) return;

        if (dialogConexion == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Sin conexión");
            builder.setMessage("Buscando red... La aplicación se reconectará automáticamente.");
            builder.setCancelable(false); // No deja tocar nada más hasta que vuelva el internet

            dialogConexion = builder.create();
        }

        if (!dialogConexion.isShowing()) {
            dialogConexion.show();
        }
    }

    private void ocultarDialogoSinInternet() {
        if (dialogConexion != null && dialogConexion.isShowing()) {
            dialogConexion.dismiss();
            Toast.makeText(this, "¡Conectado!", Toast.LENGTH_SHORT).show();
        }
    }
}