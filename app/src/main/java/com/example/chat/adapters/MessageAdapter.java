package com.example.chat.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat.R;
import com.example.chat.data.Message;
import com.example.chat.data.MessageType;
import com.example.chat.databinding.RecycleMessageViewBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> listaMensajes;
    private String miUid;

    public MessageAdapter(List<Message> listaMensajes, String miUid) {
        this.listaMensajes = listaMensajes;
        this.miUid = miUid;
    }

    public void actualizarMensajes(List<Message> nuevaLista) {
        this.listaMensajes = nuevaLista;
        notifyDataSetChanged();
    }

    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecycleMessageViewBinding binding = RecycleMessageViewBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new MessageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message mensaje = listaMensajes.get(position);
        RecycleMessageViewBinding b = holder.binding;
        int colorBurbuja = ContextCompat.getColor(b.getRoot().getContext(),
                esMio(mensaje) ? R.color.primary : R.color.hintColorInputs);

        if (esMio(mensaje)) {
            b.bgTailSent.setVisibility(View.VISIBLE);
            b.bgTailRecv.setVisibility(View.GONE);
            b.lytNameRecv.setVisibility(View.GONE);
            b.bgTailSent.setImageTintList(ColorStateList.valueOf(colorBurbuja));
        } else {
            b.bgTailSent.setVisibility(View.GONE);
            b.bgTailRecv.setVisibility(View.VISIBLE);
            b.lytNameRecv.setVisibility(View.GONE);
            b.bgTailSent.setImageTintList(ColorStateList.valueOf(colorBurbuja));
        }
        b.bgContent.setImageTintList(ColorStateList.valueOf(colorBurbuja));

        if (mensaje.isDeleted()) {
            b.lytImage.setVisibility(View.GONE);
            b.txtContent.setText(R.string.msgEliminado);
            Glide.with(b.getRoot()).clear(b.imgContent);
        } else if (mensaje.getType() == null || mensaje.getType() == MessageType.TEXT) {
            b.lytImage.setVisibility(View.GONE);
            b.txtContent.setText(mensaje.getContent());
            Glide.with(b.getRoot()).clear(b.imgContent);
        } else {
            b.lytImage.setVisibility(View.VISIBLE);
            b.txtContent.setText("");
            Glide.with(b.getRoot())
                    .load(mensaje.getContent())
                    .placeholder(R.drawable.bg_circle_online)
                    .error(R.drawable.bg_circle_online)
                    .into(b.imgContent);
        }

        String hora = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(mensaje.getTimeStamp()));
        if (esMio(mensaje) && mensaje.isEdited()) {
            hora = hora + " · " + b.getRoot().getContext().getString(R.string.msgEditado);
        }
        b.txtTimeSent.setText(hora);

        b.vwSeen.setVisibility(fueVisto(mensaje) ? View.VISIBLE : View.INVISIBLE);
    }

    private boolean esMio(Message mensaje) {
        return miUid != null && miUid.equals(mensaje.getIdUserSender());
    }

    private boolean fueVisto(Message mensaje) {
        HashMap<String, Boolean> readBy = mensaje.getReadBy();
        if (readBy == null) return false;
        for (String uid : readBy.keySet()) {
            if (uid.equals(miUid)) continue;
            if (!Boolean.TRUE.equals(readBy.get(uid))) return false;
        }
        return !readBy.isEmpty();
    }

    @Override
    public int getItemCount() {
        return listaMensajes != null ? listaMensajes.size() : 0;
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        RecycleMessageViewBinding binding;

        public MessageViewHolder(RecycleMessageViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}