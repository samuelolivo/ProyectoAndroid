package com.example.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chat.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.atomic.AtomicBoolean;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    ActivityLoginBinding bng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bng = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(bng.getRoot());
        fAuth = FirebaseAuth.getInstance();

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

            // 4. Le pedimos a Firebase que verifique si el usuario existe
            fAuth.signInWithEmailAndPassword(correo, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // ¡Exito! Las credenciales son correctas
                            Toast.makeText(LoginActivity.this, "¡Inicio de sesión exitoso!", Toast.LENGTH_SHORT).show();

                            // Viajamos a la página principal
                            Intent intent = new Intent(LoginActivity.this, MainPage.class);
                            startActivity(intent);

                            // Cerramos esta pantalla para que no puedan volver atrás con el botón del celular
                            finish();
                        } else {
                            // Fracaso: Correo no existe o contraseña incorrecta
                            Toast.makeText(LoginActivity.this, "Error al iniciar sesión. Revisa tus datos.", Toast.LENGTH_LONG).show();
                        }
                    });
        }
}