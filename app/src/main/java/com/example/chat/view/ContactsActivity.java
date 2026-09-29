package com.example.chat.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chat.R;
import com.example.chat.adapters.UserAdapter;
import com.example.chat.databinding.ActivityContactsBinding;
import com.example.chat.viewModel.UserListViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {

    private ActivityContactsBinding binding;
    private UserListViewModel viewModel;
    private UserAdapter adapter;
    private String miUid;
    private boolean navSincronizando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        miUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        binding.recyclerViewContacts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(new ArrayList<>());
        binding.recyclerViewContacts.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(UserListViewModel.class);
        viewModel.getUserList().observe(this, usuarios -> adapter.actualizarLista(usuarios));

        viewModel.loadAllUsers(miUid);

        binding.bottomNav.setSelectedItemId(R.id.nav_contactos);
        binding.bottomNav.setOnItemSelectedListener(item -> {
            if (navSincronizando) return true;
            if (item.getItemId() == R.id.nav_chats) {
                finish();
            }
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        navSincronizando = true;
        binding.bottomNav.setSelectedItemId(R.id.nav_contactos);
        navSincronizando = false;
    }
}
