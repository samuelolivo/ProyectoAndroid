package com.example.chat.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.chat.R;
import com.example.chat.databinding.ActivityLoginBinding;
import com.example.chat.ui.main.MainActivity;
import com.example.chat.ui.register.RegisterActivity;
import com.example.chat.viewModel.AuthViewModel;

public class LoginActivity extends AppCompatActivity {
    AuthViewModel authViewModel;
    ActivityLoginBinding bng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bng = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(bng.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.getAuthResult().observe(this, r -> {
            if(r != null){
                bng.progressBarLogin.setVisibility(View.GONE);
                bng.btnFormLogin.setEnabled(true);
                bng.btnFormLogin.setText(R.string.btnToLogin);

                if (r.equals("SUCCESS")) {
                    Toast.makeText(this, "¡Inicio exitoso!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Error: " + r, Toast.LENGTH_LONG).show();
                }
            }
        });

        bng.btnFormToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);

                startActivity(intent);
            }
        });

        bng.btnFormLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateForm();
            }
        });


    }
    @Override
    protected void onStart() {
        super.onStart();
        if (authViewModel.getCurrentUserId() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void validateForm(){
        // Sacamos el texto que escribió el usuario
        String correo = bng.inputCorreo.getText().toString().trim();
        String password = bng.inputPassword.getText().toString().trim();

        // Validaciones súper básicas para que no envíen campos vacíos
        if (correo.isEmpty()) {
            bng.inputCorreo.setError("Debe introducir su correo");
            return; // Detiene el código aquí si está vacío
        }

        if (password.isEmpty()) {
            bng.inputPassword.setError("Debe introducir su contraseña");
            return;
        }

        bng.btnFormLogin.setText("");
        bng.btnFormLogin.setEnabled(false);
        bng.progressBarLogin.setVisibility(View.VISIBLE);

        authViewModel.login(correo, password);

    }
}