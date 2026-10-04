package com.example.chat.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.chat.data.User;
import com.example.chat.repository.AuthRepository;
import com.example.chat.repository.UserRepository;

public class SettingsViewModel extends ViewModel {

    private AuthRepository authRepository;
    private UserRepository userRepository;
//    private MutableLiveData<String> authResult;
    private MutableLiveData<User> perfil;
    private MutableLiveData<String> resultadoPassword;



    public SettingsViewModel(){
        authRepository = new AuthRepository();
        userRepository = new UserRepository();
        perfil = new MutableLiveData<>();
        resultadoPassword = new MutableLiveData<>();
    }

    public LiveData<User> getPerfil(){
        return perfil;
    }

    public LiveData<String> getResultadoPassword(){
        return resultadoPassword;
    }

    public void cargarMiPerfil(String uid) {
        userRepository.cargarMiPerfil(uid, perfil);
    }

    public void actualizarContrasena(String passwordActual, String passwordNew) {
        if(passwordNew.length() > 0){
            authRepository.actualizarContrasena(passwordActual, passwordNew, resultadoPassword);
        }else {
            resultadoPassword.setValue("La nueva contraseña debe tener al menos 8 caracteres");
        }
    }
}
