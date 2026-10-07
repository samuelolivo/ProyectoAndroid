package com.example.chat.ui.main;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.chat.R;
import com.example.chat.databinding.ActivityMainBinding;
import com.example.chat.ui.contacts.ContactsFragment;
import com.example.chat.ui.conversation.ConversationFragment;
import com.example.chat.ui.login.LoginActivity;
import com.example.chat.ui.settings.SettingsFragment;
import com.example.chat.viewModel.AuthViewModel;
import com.example.chat.viewModel.MainViewModel;
import com.example.chat.viewModel.UserListViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private AuthViewModel authViewModel;
    private UserListViewModel userViewModel;
    private String miUid;
    private boolean navSincronizando;
    private AlertDialog dialogConexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(
                binding.getRoot(),
                (view, insets) -> {
                    int top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
                    int bottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
                    view.setPadding(view.getPaddingLeft(), top, view.getPaddingRight(), bottom);

                    return insets;
                }
        );

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        miUid = authViewModel.getCurrentUserId();

        if (miUid == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserListViewModel.class);
        userViewModel.changeOnlineStatus(miUid, true);

        // Pedir permiso de notificaciones para Android 13 o superior
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
        userViewModel.registrarTokenNotificaciones(miUid);

        authViewModel.getLogoutLiveData().observe(this, cerrado -> {
            if (cerrado != null && cerrado) {
                Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        viewModel.getConexionLiveData().observe(this, hayInternet -> {
            if (!hayInternet) {
                mostrarDialogoSinInternet();
            } else {
                userViewModel.changeOnlineStatus(miUid, true);
                ocultarDialogoSinInternet();
            }
        });

        viewModel.verificarConexion();

        binding.bottomNav.setOnItemSelectedListener(item -> {
            if (navSincronizando) return true;
            int id = item.getItemId();

            if (id == R.id.nav_chats) {
                replaceFragment(new ConversationFragment());
                return true;
            } else if (id == R.id.nav_contactos) {
                replaceFragment(new ContactsFragment());
                return true;
            } else if (id == R.id.nav_ajustes) {
                replaceFragment(new SettingsFragment());
                return true;
            }

            return false;
        });

        // Mostrar fragmento por defecto según intent o chats
        if (savedInstanceState == null) {
            boolean abrirAjustes = getIntent().getBooleanExtra("abrir_ajustes", false);
            boolean abrirContactos = getIntent().getBooleanExtra("abrir_contactos", false);
            if (abrirAjustes) {
                binding.bottomNav.setSelectedItemId(R.id.nav_ajustes);
            } else if (abrirContactos) {
                binding.bottomNav.setSelectedItemId(R.id.nav_contactos);
            } else {
                binding.bottomNav.setSelectedItemId(R.id.nav_chats);
            }
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            if (intent.getBooleanExtra("abrir_ajustes", false)) {
                binding.bottomNav.setSelectedItemId(R.id.nav_ajustes);
            } else if (intent.getBooleanExtra("abrir_contactos", false)) {
                binding.bottomNav.setSelectedItemId(R.id.nav_contactos);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        navSincronizando = true;
        int selectedId = binding.bottomNav.getSelectedItemId();
        if (selectedId != R.id.nav_chats && selectedId != R.id.nav_contactos && selectedId != R.id.nav_ajustes) {
            binding.bottomNav.setSelectedItemId(R.id.nav_chats);
        }
        navSincronizando = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (miUid != null && userViewModel != null) {
            userViewModel.changeOnlineStatus(miUid, false);
        }
    }

    private void mostrarDialogoSinInternet() {
        if (isFinishing() || isDestroyed()) return;

        if (dialogConexion == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Sin conexión");
            builder.setMessage("Buscando red... La aplicación se reconectará automáticamente.");
            builder.setCancelable(false);

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
