package com.example.chat.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.chat.data.Message;
import com.example.chat.data.MessageType;
import com.example.chat.repository.ChatRepository;
import com.example.chat.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel implements Callback<Boolean> {

    private final ChatRepository repository;
    private final MutableLiveData<List<Message>> mensajes = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> resultadoOperacion = new MutableLiveData<>();

    private String chatId;
    private String miUid;

    public ChatViewModel() {
        repository = new ChatRepository();
    }

    public LiveData<List<Message>> getMensajes() {
        return mensajes;
    }

    public LiveData<String> getResultadoOperacion() {
        return resultadoOperacion;
    }

    public void cargarChat(String chatId, String miUid) {
        this.chatId = chatId;
        this.miUid = miUid;
        repository.listenChatMessages(chatId, mensajes);
    }

    public void enviarMensaje(String contenido) {
        repository.sendMessage(chatId, miUid, contenido, MessageType.TEXT, this);
    }

    public void enviarImagen(byte[] datos) {
        repository.enviarImagen(chatId, miUid, datos, this);
    }

    public void editarMensaje(String messageId, String contenido) {
        repository.editMessage(chatId, messageId, contenido, this);
    }

    public void eliminarMensaje(String messageId) {
        repository.deleteMessage(chatId, messageId, this);
    }

    public void marcarComoLeido(String messageId) {
        repository.markAsRead(chatId, messageId, miUid, this);
    }

    public String getMiUid() {
        return miUid;
    }

    @Override
    public void onSuccess(Boolean result) {
        resultadoOperacion.setValue("SUCCESS");
    }

    @Override
    public void onError(Exception e) {
        resultadoOperacion.setValue(e.getMessage());
    }
}