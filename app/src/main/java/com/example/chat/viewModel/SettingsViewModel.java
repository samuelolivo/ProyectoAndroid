package com.example.chat.viewModel;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.chat.data.User;
import com.example.chat.data.repository.AuthRepository;
import com.example.chat.data.repository.UserRepository;

public class SettingsViewModel extends ViewModel {

    private AuthRepository authRepository;
    private UserRepository userRepository;
//    private MutableLiveData<String> authResult;
    private MutableLiveData<User> perfil;
    private MutableLiveData<String> resultadoPassword;
    private MutableLiveData<String> resultadoFoto;
    private MutableLiveData<Boolean> resultadoNombre;



    public SettingsViewModel(){
        authRepository = new AuthRepository();
        userRepository = new UserRepository();
        perfil = new MutableLiveData<>();
        resultadoPassword = new MutableLiveData<>();
        resultadoFoto = new MutableLiveData<>();
        resultadoNombre = new MutableLiveData<>();
    }

    public LiveData<User> getPerfil(){
        return perfil;
    }

    public LiveData<String> getResultadoPassword(){
        return resultadoPassword;
    }

    public LiveData<String> getResultadoFoto() {
        return resultadoFoto;
    }

    public LiveData<Boolean> getResultadoNombre() {
        return resultadoNombre;
    }
    public void cargarMiPerfil(String uid) {
        userRepository.cargarMiPerfil(uid, perfil);
    }



    public void subirFotoPerfil(String uid, Uri imageUri) {
        userRepository.subirFotoPerfil(uid, imageUri, resultadoFoto);
    }
    public void actualizarNombre(String uid, String nombre) {
        userRepository.actualizarNombre(uid, nombre, resultadoNombre);
    }
    public void actualizarContrasena(String passwordActual, String passwordNew) {
        if(passwordNew.length() > 0){
            authRepository.actualizarContrasena(passwordActual, passwordNew, resultadoPassword);
        }else {
            resultadoPassword.setValue("La nueva contraseña debe tener al menos 8 caracteres");
        }
    }
}
