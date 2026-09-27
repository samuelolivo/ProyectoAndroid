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

import com.example.chat.data.User;
import com.example.chat.databinding.ActivityRegisterBinding;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.atomic.AtomicBoolean;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth fAuth;
    ActivityRegisterBinding bng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       bng = ActivityRegisterBinding.inflate(getLayoutInflater());
       setContentView(bng.getRoot());

       fAuth = FirebaseAuth.getInstance();

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

            fAuth.createUserWithEmailAndPassword(correo, password).addOnCompleteListener(this, task -> {
                if(task.isSuccessful()){
                    String uid = fAuth.getCurrentUser().getUid();
                    User newUser = new User(uid, nombre, "", true);

                    FirebaseDatabase.getInstance().getReference("Users").child(uid)
                            .setValue(newUser)
                            .addOnCompleteListener(dbTask -> {
                                if(dbTask.isSuccessful()){
                                    Toast.makeText(RegisterActivity.this, "Cuenta creada exitosamente",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this, MainPage.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    Toast.makeText(RegisterActivity.this, "Error al guardar los datos del usuario", Toast.LENGTH_SHORT).show();
                                }
                            });
                }else {
                    Toast.makeText(RegisterActivity.this, "Error de registro: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}