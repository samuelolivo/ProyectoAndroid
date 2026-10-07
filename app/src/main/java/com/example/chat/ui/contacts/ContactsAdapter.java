package com.example.chat.ui.contacts;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.R;
import com.example.chat.data.model.User;
import com.example.chat.databinding.RecycleUserViewBinding;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.UserViewHolder> {

    private List<User> listaUsuarios;

    public ContactsAdapter(List<User> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    public void actualizarLista(List<User> nuevaLista) {
        this.listaUsuarios = nuevaLista;
        this.notifyDataSetChanged();
    }

    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecycleUserViewBinding binding = RecycleUserViewBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User usuario = listaUsuarios.get(position);
        holder.binding.imgProfileUser.setVisibility(ViewGroup.GONE);

        holder.binding.txtUserName.setText(usuario.getName());
        holder.binding.txtEmail.setText(usuario.getCorreo());

        int color = usuario.isOnline() ? Color.GREEN : Color.GRAY;
        String status = holder.binding.txtOnlineStatus.getContext()
                .getString(usuario.isOnline() ? R.string.statusOnline : R.string.statusOffline);

        holder.binding.txtOnlineStatus.setText(status);
        holder.binding.txtOnlineStatus.setTextColor(color);
        holder.binding.viewOnlineStatus.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    @Override
    public int getItemCount() {
        return listaUsuarios != null ? listaUsuarios.size() : 0;
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        RecycleUserViewBinding binding;

        UserViewHolder(RecycleUserViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
