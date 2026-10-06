package com.example.chat.ui.chat;

import android.content.res.ColorStateList;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat.R;
import com.example.chat.data.model.Message;
import com.example.chat.data.model.MessageType;
import com.example.chat.databinding.RecycleMessageViewBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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

        int colorVisto = ContextCompat.getColor(b.getRoot().getContext(),
                fueVisto(mensaje) ? R.color.green : R.color.yellow);

        ConstraintLayout.LayoutParams paramsLytContent = (ConstraintLayout.LayoutParams) b.lytContent.getLayoutParams();
        ConstraintLayout.LayoutParams paramsLytInfo = (ConstraintLayout.LayoutParams) b.lytInfo.getLayoutParams();

        if (esMio(mensaje)) {
            b.bgTailSent.setVisibility(View.VISIBLE);
            b.bgTailRecv.setVisibility(View.GONE);
            b.txtNameRecv.setVisibility(View.GONE);
            b.bgTailSent.setImageTintList(ColorStateList.valueOf(colorBurbuja));
            paramsLytContent.startToStart = ConstraintLayout.LayoutParams.UNSET;
            paramsLytContent.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            paramsLytInfo.startToStart = ConstraintLayout.LayoutParams.UNSET;
            b.vwSeen.setVisibility(View.VISIBLE);
            b.vwSeen.setImageTintList(ColorStateList.valueOf(colorVisto));
        } else {
            b.bgTailSent.setVisibility(View.GONE);
            b.bgTailRecv.setVisibility(View.VISIBLE);
            b.txtNameRecv.setVisibility(View.GONE);
            b.bgTailRecv.setImageTintList(ColorStateList.valueOf(colorBurbuja));
            paramsLytContent.endToEnd = ConstraintLayout.LayoutParams.UNSET;
            paramsLytContent.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            paramsLytInfo.endToEnd = ConstraintLayout.LayoutParams.UNSET;
            b.vwSeen.setVisibility(View.GONE);
        }
        b.bgContent.setImageTintList(ColorStateList.valueOf(colorBurbuja));

        if (mensaje.isDeleted()) {
            b.crdImage.setVisibility(View.GONE);
            b.txtContent.setText(R.string.msgEliminado);
            Glide.with(b.getRoot()).clear(b.imgContent);
        } else if (mensaje.getType() == null || mensaje.getType() == MessageType.TEXT) {
            b.crdImage.setVisibility(View.GONE);
            b.txtContent.setText(mensaje.getContent());
            Glide.with(b.getRoot()).clear(b.imgContent);
        } else {
            b.crdImage.setVisibility(View.VISIBLE);
            b.txtContent.setText("");
            Glide.with(b.getRoot())
                    .load(Base64.decode(mensaje.getContent(), Base64.NO_WRAP))
                    .placeholder(R.drawable.bg_circle_online)
                    .error(R.drawable.bg_circle_online)
                    .into(b.imgContent);
        }

        String hora = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(mensaje.getTimeStamp()));
        if (esMio(mensaje) && mensaje.isEdited()) {
            hora = hora + " · " + b.getRoot().getContext().getString(R.string.msgEditado);
        }
        b.txtTimeSent.setText(hora);
    }

    private boolean esMio(Message mensaje) {
        return miUid != null && miUid.equals(mensaje.getIdUserSender());
    }

    private boolean fueVisto(Message mensaje) {
        HashMap<String, Boolean> readBy = mensaje.getReadBy();
        if (readBy == null) return false;
        for (String uid : readBy.keySet()) {
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