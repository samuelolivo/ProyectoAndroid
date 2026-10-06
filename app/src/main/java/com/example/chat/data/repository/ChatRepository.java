package com.example.chat.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.chat.data.Chat;
import com.example.chat.data.Message;
import com.example.chat.data.MessageType;
import com.example.chat.util.Callback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRepository {

    private DatabaseReference chatsRef;
    private DatabaseReference usersRef;

    private Query queryMensajesChat;
    private ValueEventListener listenerMensajesChat;

    public ChatRepository() {
        chatsRef = FirebaseDatabase.getInstance().getReference("Chats");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
    }

    private DatabaseReference getMessagesRef(String chatId) {
        return chatsRef
                .child(chatId)
                .child("messages");
    }

    public void escucharMisChats(String miUid, MutableLiveData<List<Chat>> chatsLiveData) {
        chatsRef.orderByChild("users/" + miUid).equalTo(true)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Chat> listaTemporal = new ArrayList<>();
                        for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                            Chat chat = chatSnapshot.getValue(Chat.class);
                            if (chat != null) {
                                listaTemporal.add(chat);
                            }
                        }

                        Collections.sort(listaTemporal, (chat1, chat2) ->
                                Long.compare(chat2.getTimestamp(), chat1.getTimestamp())
                        );
                        chatsLiveData.setValue(listaTemporal);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    public void crearChatPorCorreo(String correoAmigo, String miUid, MutableLiveData<String> resultadoCreacion) {

        usersRef.orderByChild("correo").equalTo(correoAmigo)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnap : snapshot.getChildren()) {
                                String idDelAmigo = userSnap.getKey();

                                if (idDelAmigo.equals(miUid)) {
                                    resultadoCreacion.setValue("No puedes crear un chat contigo mismo");
                                    return;
                                }

                                String idNuevoChat = chatsRef.push().getKey();
                                HashMap<String, Boolean> participantes = new HashMap<>();
                                participantes.put(miUid, true);
                                participantes.put(idDelAmigo, true);

                                Chat nuevoChat = new Chat(idNuevoChat, "", participantes, "",  "Chat iniciado", System.currentTimeMillis());

                                chatsRef.child(idNuevoChat).setValue(nuevoChat).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        resultadoCreacion.setValue("SUCCESS");
                                    } else {
                                        resultadoCreacion.setValue("Error al guardar el chat");
                                    }
                                });
                                return;
                            }
                        } else {
                            resultadoCreacion.setValue("No existe un usuario con ese correo");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        resultadoCreacion.setValue("Error de conexión: " + error.getMessage());
                    }
                });
    }

    public void escucharEstadoConexion(MutableLiveData<Boolean> conexionLiveData) {
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");

        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean connected = snapshot.getValue(Boolean.class);
                if (connected != null) {
                    conexionLiveData.setValue(connected);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // No hacemos nada aquí
            }
        });
    }
    public void sendMessage(String chatId, String userId, String content, MessageType type, Callback<Boolean> callback){
        DatabaseReference chatRef = chatsRef.child(chatId);

        chatRef.get().addOnCompleteListener(chatTask -> {
            if (!chatTask.isSuccessful()) {
                callback.onError(new Exception("REPO: no se pudo leer el chat"));
                return;
            }

            Chat chat = chatTask.getResult().getValue(Chat.class);
            if (chat == null || chat.getUsers() == null) {
                callback.onError(new Exception("REPO: el chat no existe"));
                return;
            }

            String messageId = getMessagesRef(chatId).push().getKey();
            if (messageId == null) {
                callback.onError(new Exception("REPO: Id de mensaje no puede ser null"));
                return;
            }

            HashMap<String, Boolean> readBy = new HashMap<String, Boolean>();
            for (String uid : chat.getUsers().keySet()) {
                readBy.put(uid, uid.equals(userId));
            }

            long timestamp = System.currentTimeMillis();
            Message message = new Message(messageId, chatId, userId, content, type, readBy, false, false, timestamp);

            Map<String, Object> cambios = new HashMap<String, Object>();
            cambios.put("messages/" + messageId, message);
            cambios.put("lastMessageId", messageId);
            cambios.put("ultimoMensaje", content);
            cambios.put("timestamp", timestamp);

            chatRef.updateChildren(cambios)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            callback.onSuccess(true);

                            usersRef.child(userId).get().addOnSuccessListener(senderSnapshot -> {
                                String nombreEmisor = "Alguien";
                                String fotoEmisor;

                                if (senderSnapshot.exists()) {
                                    if (senderSnapshot.child("name").exists()) {
                                        nombreEmisor = senderSnapshot.child("name").getValue(String.class);
                                    }
                                    if (senderSnapshot.child("pictureProfile").exists()) {
                                        fotoEmisor = senderSnapshot.child("pictureProfile").getValue(String.class);
                                    } else {
                                        fotoEmisor = "";
                                    }
                                } else {
                                    fotoEmisor = "";
                                }

                                String tituloNotificacion = nombreEmisor;
                                if (chat.getUsers().size() > 2) {
                                    tituloNotificacion = "Grupo: " + nombreEmisor;
                                }

                                String textoAlerta = (type == MessageType.IMAGE) ? "📷 Imagen" : content;

                                for (String receptorUid : chat.getUsers().keySet()) {
                                     if (!receptorUid.equals(userId)) {

                                    String finalTituloNotificacion = tituloNotificacion;
                                    usersRef.child(receptorUid).child("fcmToken").get()
                                            .addOnSuccessListener(dataSnapshot -> {
                                                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                                                    String tokenDestino = dataSnapshot.getValue(String.class);

                                                    dispararNotificacionHono(tokenDestino, finalTituloNotificacion, textoAlerta, fotoEmisor);
                                                }
                                            });
                                     }
                                }
                            });
                        }
                        else {
                            callback.onError(new Exception("REPO: error al guardar el mensaje" + messageId));
                        }
                    });
        });
    };

    public void listenChatMessages(String chatId, String userId, MutableLiveData<List<Message>> mensajesLiveData){
        stopListeningChatMessages();

        queryMensajesChat = getMessagesRef(chatId).orderByChild("timeStamp");
        listenerMensajesChat = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Message> lista = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Message message = snap.getValue(Message.class);
                    if (message != null) {
                        lista.add(message);
                    }
                }
                mensajesLiveData.setValue(lista);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChatRepository", "escucha de mensajes cancelada: " + error.getMessage());
                mensajesLiveData.setValue(new ArrayList<>());
            }
        };
        queryMensajesChat.addValueEventListener(listenerMensajesChat);
    };

    public void deleteMessage(String chatId, String messageId, Callback<Boolean> callback){
        Map<String, Object> cambios = new HashMap<String, Object>();
        cambios.put("deleted", true);
        cambios.put("content", "");

        getMessagesRef(chatId).child(messageId).updateChildren(cambios)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        callback.onSuccess(true);
                    }
                    else {
                        callback.onError(new Exception("REPO: error al borrar el mensaje" + messageId));
                    }
                });
    };

    public void editMessage(String chatId, String messageId, String content, Callback<Boolean> callback){
        Map<String, Object> cambios = new HashMap<String, Object>();
        cambios.put("content", content);
        cambios.put("edited", true);

        getMessagesRef(chatId).child(messageId).updateChildren(cambios)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        callback.onSuccess(true);
                    }
                    else {
                        callback.onError(new Exception("REPO: error al editar el mensaje" + messageId));
                    }
                });
    };

    public void markAsRead(String chatId, String messageId, String userId, Callback<Boolean> callback){
        getMessagesRef(chatId).child(messageId).child("readBy").child(userId).setValue(true)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        callback.onSuccess(true);
                    }
                    else {
                        callback.onError(new Exception("REPO: error al marcar como leido" + messageId));
                    }
                });
    };

    public void stopListeningChatMessages(){
        if (queryMensajesChat != null && listenerMensajesChat != null) {
            queryMensajesChat.removeEventListener(listenerMensajesChat);
        }
        queryMensajesChat = null;
        listenerMensajesChat = null;
    };

    private void dispararNotificacionHono(String tokenDestino, String titulo, String mensaje, String fotoUrl) {
        new Thread(() -> {
            try {

                String urlServidor = "http://10.0.0.141:3000/notificar";
                URL url = new java.net.URL(urlServidor);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("token", tokenDestino);
                json.put("titulo", titulo);
                json.put("mensaje", mensaje);
                json.put("fotoUrl", fotoUrl != null ? fotoUrl : "");

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = json.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int codigoRespuesta = conn.getResponseCode();
                if (codigoRespuesta == 200 || codigoRespuesta == 201) {
                    android.util.Log.d("API_HONO", "¡Éxito! Hono envió la notificación. Código: " + codigoRespuesta);
                } else {
                    android.util.Log.e("API_HONO", "Hono respondió con error: " + codigoRespuesta);
                }

                conn.disconnect();

            } catch (Exception e) {
                android.util.Log.e("API_HONO", "Error conectando al servidor nativo: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
}