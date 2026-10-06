package com.example.chat.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.chat.data.model.Chat;
import com.example.chat.data.repository.AuthRepository;
import com.example.chat.data.repository.ChatRepository;
import com.example.chat.data.repository.UserRepository;

import java.util.List;

public class MainViewModel extends ViewModel {

    private ChatRepository repository;
    private UserRepository userRepository;
    private AuthRepository authRepository;
    private MutableLiveData<List<Chat>> chatsLiveData;
    private MutableLiveData<String> resultadoCreacionChat;

    private MutableLiveData<Boolean> conexionLiveData;
    private MutableLiveData<Boolean> logout;

    public MainViewModel() {
        repository = new ChatRepository();
        userRepository = new UserRepository();
        authRepository = new AuthRepository();
        chatsLiveData = new MutableLiveData<>();
        logout = new MutableLiveData<>();
        resultadoCreacionChat = new MutableLiveData<>();
        conexionLiveData = new MutableLiveData<>();
    }

    public LiveData<List<Chat>> getChatsLiveData() {
        return chatsLiveData;
    }

    public LiveData<String> getResultadoCreacionChat() {
        return resultadoCreacionChat;
    }

    public void cargarMisChats(String miUid) {
        repository.escucharMisChats(miUid, chatsLiveData);
    }

    public void iniciarNuevoChat(String correoAmigo, String miUid) {
        repository.crearChatPorCorreo(correoAmigo, miUid, resultadoCreacionChat);
    }

    public void cerrarSesion(String uid) {
        authRepository.cerrarSesion(uid, logout);
    }

    public LiveData<Boolean> getConexionLiveData(){
        return conexionLiveData;
    }

    public void verificarConexion() {
        repository.escucharEstadoConexion(conexionLiveData);
    }
}