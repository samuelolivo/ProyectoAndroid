package com.example.chat.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.chat.repository.AuthRepository;

public class AuthViewModel extends ViewModel {

    private AuthRepository repository;
    private MutableLiveData<String> authResult;


    public AuthViewModel(){
        repository = new AuthRepository();
        authResult = new MutableLiveData<>();
    }

    public LiveData<String> getAuthResult(){
        return authResult;
    }

    public void login (String email, String pass){
        repository.login( email, pass, authResult);
    }
    public void register (String nombre, String email, String pass){
        repository.register(nombre, email, pass, authResult);
    }
}
