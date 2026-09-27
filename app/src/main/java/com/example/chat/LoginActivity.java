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

import java.util.concurrent.atomic.AtomicBoolean;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding bng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bng = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(bng.getRoot());

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
        AtomicBoolean validated = new AtomicBoolean(true);

        android.content.res.ColorStateList errorColor = android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.bgInputsError)
        );
        android.content.res.ColorStateList defaultColor = android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.bgInputs)
        );

        bng.inputCorreo.setBackgroundTintList(defaultColor);
        bng.inputPassword.setBackgroundTintList(defaultColor);


        if(bng.inputCorreo.getText() == null || bng.inputCorreo.getText().toString().isEmpty()){
            bng.inputCorreo.setError("Debe Introducir su correo");
            bng.inputCorreo.setBackgroundTintList(errorColor);
            validated.set(false);
        }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(bng.inputCorreo.getText().toString().trim()).matches()) {
            bng.inputCorreo.setError("Por favor, ingresa un correo válido");
            bng.inputCorreo.setBackgroundTintList(errorColor);
            validated.set(false);
        }


        if(bng.inputPassword.getText() == null || bng.inputPassword.getText().toString().isEmpty()){
            bng.inputPassword.setError("Debe Introducir su Contraseña");
            bng.inputPassword.setBackgroundTintList(errorColor);
            validated.set(false);
        } else if(bng.inputPassword.getText().length() <8){
            bng.inputPassword.setError("La contraseña deberia tener 8 o mas caracteres");
            bng.inputPassword.setBackgroundTintList(errorColor);
            validated.set(false);
        }
//validacion de firebase

        if(validated.get()){
            Intent intent = new Intent(LoginActivity.this, MainPage.class);
            startActivity(intent);
        }
    }
}