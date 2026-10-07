package com.example.chat.ui.conversation;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chat.data.model.Chat;
import com.example.chat.databinding.FragmentChatsBinding;
import com.example.chat.ui.chat.ChatActivity;
import com.example.chat.viewModel.AuthViewModel;
import com.example.chat.viewModel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class ConversationFragment extends Fragment {

    private FragmentChatsBinding binding;
    private ConversationAdapter adapter;
    private List<Chat> listaChats;
    private MainViewModel viewModel;
    private AuthViewModel authViewModel;
    private String miUid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        miUid = authViewModel.getCurrentUserId();
        if (miUid == null) return;

        listaChats = new ArrayList<>();
        adapter = new ConversationAdapter(listaChats);
        binding.recyclerViewChats.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewChats.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        viewModel.getChatsLiveData().observe(getViewLifecycleOwner(), chats -> {
            listaChats.clear();
            if (chats != null) {
                listaChats.addAll(chats);
            }
            adapter.notifyDataSetChanged();
        });

        viewModel.getResultadoCreacionChat().observe(getViewLifecycleOwner(), resultado -> {
            if (resultado != null) {
                if (resultado.equals("SUCCESS")) {
                    Toast.makeText(requireContext(), "¡Chat creado exitosamente!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), resultado, Toast.LENGTH_LONG).show();
                }
            }
        });

        viewModel.cargarMisChats(miUid);

        adapter.setOnChatClickListener((chat, nombreAmigo) -> {
            Intent intent = new Intent(requireContext(), ChatActivity.class);
            intent.putExtra(ChatActivity.EXTRA_CHAT_ID, chat.getIdChat());
            intent.putExtra(ChatActivity.EXTRA_NOMBRE, nombreAmigo);
            startActivity(intent);
        });

        binding.btnNuevoChat.setOnClickListener(v -> mostrarDialogoBuscarCorreo());
    }

    private void mostrarDialogoBuscarCorreo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Nuevo Chat");
        builder.setMessage("Ingresa el correo del usuario:");

        final android.widget.EditText inputCorreo = new android.widget.EditText(requireContext());
        inputCorreo.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(inputCorreo);

        builder.setPositiveButton("Buscar", (dialog, which) -> {
            String correoBuscado = inputCorreo.getText().toString().trim().toLowerCase();
            if (!correoBuscado.isEmpty()) {
                viewModel.iniciarNuevoChat(correoBuscado, miUid);
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
