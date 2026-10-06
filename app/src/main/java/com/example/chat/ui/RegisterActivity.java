package com.example.chat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.chat.R;
import com.example.chat.databinding.ActivityRegisterBinding;
import com.example.chat.viewModel.AuthViewModel;

import java.util.concurrent.atomic.AtomicBoolean;

public class RegisterActivity extends AppCompatActivity {
    private AuthViewModel fAuth;
    ActivityRegisterBinding bng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       bng = ActivityRegisterBinding.inflate(getLayoutInflater());
       setContentView(bng.getRoot());

        fAuth = new ViewModelProvider(this).get(AuthViewModel.class);

        // Observamos el resultado de la autenticación
        fAuth.getAuthResult().observe(this, result -> {
            if (result != null) {
                bng.progressBarRegister.setVisibility(View.GONE);
                bng.btnFormRegister.setEnabled(true);
                bng.btnFormRegister.setText(R.string.btnToLogin);
                if (result.equals("SUCCESS")) {
                    Toast.makeText(this, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainPage.class));
                    finish();
                } else {
                    Toast.makeText(this, "Error: " + result, Toast.LENGTH_LONG).show();
                }
            }
        });

       bng.btnBackToLogin.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
               startActivity(intent);
           }
       });

       bng.btnFormRegister.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               validateForm();
           }
       });
    }

    private void validateForm() {
        AtomicBoolean validated = new AtomicBoolean(true);

        android.content.res.ColorStateList errorColor = android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.bgInputsError)
        );
        android.content.res.ColorStateList defaultColor = android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.bgInputs)
        );

        bng.inputNombre.setBackgroundTintList(defaultColor);
        bng.inputCorreoReg.setBackgroundTintList(defaultColor);
        bng.inputPasswordReg.setBackgroundTintList(defaultColor);
        bng.inputConfirmPassword.setBackgroundTintList(defaultColor);

        if(bng.inputNombre.getText() == null || bng.inputNombre.getText().toString().isEmpty()){
            bng.inputNombre.setError("Debe Introducir su nombre");
            bng.inputNombre.setBackgroundTintList(errorColor);
            validated.set(false);
        }

        if(bng.inputCorreoReg.getText() == null || bng.inputCorreoReg.getText().toString().isEmpty()){
            bng.inputCorreoReg.setError("Debe Introducir su correo");
            bng.inputCorreoReg.setBackgroundTintList(errorColor);
            validated.set(false);
        }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(bng.inputCorreoReg.getText().toString().trim()).matches()) {
            bng.inputCorreoReg.setError("Por favor, ingresa un correo válido");
            bng.inputCorreoReg.setBackgroundTintList(errorColor);
            validated.set(false);
        }

        if(bng.inputPasswordReg.getText() == null || bng.inputPasswordReg.getText().toString().isEmpty()){
            bng.inputPasswordReg.setError("Debe Introducir su contrasena");
            bng.inputPasswordReg.setBackgroundTintList(errorColor);
            validated.set(false);
        }else if(bng.inputConfirmPassword.getText() == null || bng.inputConfirmPassword.getText().toString().isEmpty()) {
            bng.inputConfirmPassword.setError("Debe Introducir la confirmacion de contrasena");
            bng.inputConfirmPassword.setBackgroundTintList(errorColor);
            validated.set(false);
        }else if(bng.inputConfirmPassword.getText().length()<8 || bng.inputPasswordReg.getText().length()<8 ){
            bng.inputConfirmPassword.setError("La contrasena debe tener minimo 8 caracteres");
            bng.inputConfirmPassword.setBackgroundTintList(errorColor);
            validated.set(false);
        }else if(!bng.inputPasswordReg.getText().toString().equals( bng.inputConfirmPassword.getText().toString())){
            Toast.makeText(this, "Las Contrasenas deben ser iguales", Toast.LENGTH_SHORT).show();
            bng.inputPasswordReg.setBackgroundTintList(errorColor);
            bng.inputConfirmPassword.setBackgroundTintList(errorColor);
            validated.set(false);
        }

        if(validated.get()){
            String nombre= bng.inputNombre.getText().toString().trim();
            String correo = bng.inputCorreoReg.getText().toString().trim();
            String password = bng.inputPasswordReg.getText().toString().trim();


            bng.btnFormRegister.setText("");
            bng.btnFormRegister.setEnabled(false);
            bng.progressBarRegister.setVisibility(View.VISIBLE);
            fAuth.register(nombre, correo, password);
        }
    }
}