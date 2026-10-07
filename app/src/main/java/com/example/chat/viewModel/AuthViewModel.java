package com.example.chat.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.chat.data.repository.AuthRepository;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends ViewModel {

    private AuthRepository repository;
    private MutableLiveData<String> authResult;
    private MutableLiveData<Boolean> logoutLiveData;

    public AuthViewModel(){
        repository = new AuthRepository();
        authResult = new MutableLiveData<>();
        logoutLiveData = new MutableLiveData<>();
    }

    public LiveData<String> getAuthResult(){
        return authResult;
    }
    public LiveData<Boolean> getLogoutLiveData() {
        return logoutLiveData;
    }
    public void login (String email, String pass){
        repository.login( email, pass, authResult);
    }
    public void register (String nombre, String email, String pass){
        repository.register(nombre, email, pass, authResult);
    }
    public void logout(String miUid) {
        repository.logout(miUid, logoutLiveData);
    }
    public String getCurrentUserId(){
        return repository.getCurrentUserId();
    }
}
