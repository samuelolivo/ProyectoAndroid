package com.example.chat.ui.main;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chat.data.model.Chat;
import com.example.chat.databinding.RecycleChatViewBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    public interface OnChatClickListener {
        void onChatClick(Chat chat, String nombreAmigo);
    }

    private List<Chat> listaChats;
    private OnChatClickListener listener;

    public ChatAdapter(List<Chat> listaChats) {
        this.listaChats = listaChats;
    }

    public void setOnChatClickListener(OnChatClickListener listener) {
        this.listener = listener;
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
        String horaFormateada = formatearHora(chatActual.getTimestamp());
        holder.binding.txtTime.setText(horaFormateada);
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
                                    holder.nombreAmigo = nombreReal;
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

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChatClick(chatActual, holder.nombreAmigo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaChats.size();
    }

    private String formatearHora(long timestamp) {
        java.util.Calendar calMensaje = Calendar.getInstance();
        calMensaje.setTimeInMillis(timestamp);

        Calendar calHoy = Calendar.getInstance();

        if (calMensaje.get(Calendar.YEAR) == calHoy.get(Calendar.YEAR) &&
                calMensaje.get(Calendar.DAY_OF_YEAR) == calHoy.get(Calendar.DAY_OF_YEAR)) {

            java.text.SimpleDateFormat formatoHora = new java.text.SimpleDateFormat("hh:mm a", Locale.getDefault());
            return formatoHora.format(new Date(timestamp));
        }
        calHoy.add(Calendar.DAY_OF_YEAR, -1);
        if (calMensaje.get(Calendar.YEAR) == calHoy.get(Calendar.YEAR) &&
                calMensaje.get(Calendar.DAY_OF_YEAR) == calHoy.get(Calendar.DAY_OF_YEAR)) {
            return "Ayer";
        }
        java.text.SimpleDateFormat formatoFecha = new java.text.SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        return formatoFecha.format(new java.util.Date(timestamp));
    }
    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        RecycleChatViewBinding binding;
        String nombreAmigo;
        public ChatViewHolder(RecycleChatViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}