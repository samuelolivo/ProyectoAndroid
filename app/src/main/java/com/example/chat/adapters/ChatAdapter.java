package com.example.chat.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chat.data.Chat;
import com.example.chat.databinding.RecycleChatViewBinding;
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

        // Colocamos los datos básicos que sí tenemos en la clase Chat
        holder.binding.txtLastMessage.setText(chatActual.getUltimoMensaje());
        holder.binding.txtTime.setText("Hoy"); // Más adelante daremos formato al timestamp

        // Como solo tenemos el ID del usuario, ponemos esto temporalmente
        holder.binding.txtChatName.setText("Cargando contacto...");

        // (En el siguiente paso agregaremos aquí las 3 líneas de código
        // para que Firebase busque el nombre real usando el ID)
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