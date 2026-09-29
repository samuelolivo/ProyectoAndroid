package com.example.chat.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chat.data.Chat;
import com.example.chat.databinding.RecycleChatViewBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Chat> listaChats;

    public ChatAdapter(List<Chat> listaChats) {
        this.listaChats = listaChats;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecycleChatViewBinding binding = RecycleChatViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ChatViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chatActual = listaChats.get(position);

        holder.binding.txtLastMessage.setText(chatActual.getUltimoMensaje());
        holder.binding.txtTime.setText("Hoy"); // Más adelante daremos formato al timestamp
        holder.binding.txtChatName.setText("Cargando contacto...");

        String miUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String idDelAmigo = "";

        if (chatActual.getUsers() != null) {
            for (String idUsuario : chatActual.getUsers().keySet()) {
                if (!idUsuario.equals(miUid)) {
                    idDelAmigo = idUsuario; // ¡Encontramos el ID del otro!
                    break;
                }
            }
        }

        if (!idDelAmigo.isEmpty()) {
            FirebaseDatabase.getInstance().getReference("Users").child(idDelAmigo)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // OJO: Asegúrate de que en tu base de datos la variable se llame "name"
                                String nombreReal = snapshot.child("name").getValue(String.class);
                                String tiempo = snapshot.child("timestamp").getValue(String.class);
                                if (nombreReal != null) {
                                    holder.binding.txtChatName.setText(nombreReal);
                                }
                                if (tiempo != null) {
                                    holder.binding.txtTime.setText(tiempo);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            holder.binding.txtChatName.setText("Error al cargar");
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return listaChats.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        RecycleChatViewBinding binding;
        public ChatViewHolder(RecycleChatViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}