package com.example.chat.ui.contacts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chat.databinding.FragmentContactsBinding;
import com.example.chat.viewModel.UserListViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ContactsFragment extends Fragment {

    private FragmentContactsBinding binding;
    private UserListViewModel viewModel;
    private ContactsAdapter adapter;
    private String miUid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentContactsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return;
        }
        miUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        binding.recyclerViewContacts.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ContactsAdapter(new ArrayList<>());
        binding.recyclerViewContacts.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(UserListViewModel.class);
        viewModel.getUserList().observe(getViewLifecycleOwner(), usuarios -> adapter.actualizarLista(usuarios));

        viewModel.loadAllUsers(miUid);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
