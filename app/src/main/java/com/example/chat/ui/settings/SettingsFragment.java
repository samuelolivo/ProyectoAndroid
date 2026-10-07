package com.example.chat.ui.settings;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chat.databinding.FragmentSettingsBinding;
import com.example.chat.ui.login.LoginActivity;
import com.example.chat.viewModel.MainViewModel;
import com.example.chat.viewModel.SettingsViewModel;
import com.example.chat.viewModel.UserListViewModel;
import com.google.firebase.auth.FirebaseAuth;


public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding bng;
    private String miUid;
    private MainViewModel mainViewModel;
    private UserListViewModel userViewModel;
    private SettingsViewModel settingsViewModel;

    private final ActivityResultLauncher<String> openGallery =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    Toast.makeText(getContext(), "Subiendo imagen...", Toast.LENGTH_SHORT).show();
                    settingsViewModel.subirFotoPerfil(miUid, uri);
                }
            });

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        bng = FragmentSettingsBinding.inflate(inflater, container, false);
        return bng.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

      if(FirebaseAuth.getInstance().getCurrentUser() == null){
          return;
      }
      miUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

      settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserListViewModel.class);
        settingsViewModel.cargarMiPerfil(miUid);

        settingsViewModel.getPerfil().observe(getViewLifecycleOwner(), usuario -> {
            if (usuario != null) {
                if (usuario.getName() != null) {
                    bng.etUserName.setText(usuario.getName());
                }
                if (usuario.getPictureProfile() != null && !usuario.getPictureProfile().isEmpty()) {
                    Glide.with(this).load(usuario.getPictureProfile()).circleCrop().into(bng.imgProfile);
                }
            }
        });

        settingsViewModel.getResultadoFoto().observe(getViewLifecycleOwner(), url -> {
            if (url != null && !url.equals("ERROR")) {
                Toast.makeText(getContext(), "Foto actualizada", Toast.LENGTH_SHORT).show();
                Glide.with(this).load(url).circleCrop().into(bng.imgProfile);
            } else if (url != null) {
                Toast.makeText(getContext(), "Error al subir foto", Toast.LENGTH_SHORT).show();
            }
        });

        settingsViewModel.getResultadoNombre().observe(getViewLifecycleOwner(), exito -> {
            if (exito != null && exito) {
                Toast.makeText(requireContext(), "Nombre actualizado", Toast.LENGTH_SHORT).show();
            }
        });

        settingsViewModel.getResultadoPassword().observe(getViewLifecycleOwner(), mensaje -> {
            if (mensaje != null) {
                if (mensaje.equals("SUCCESS")) {
                    Toast.makeText(getContext(), "Contraseña actualizada con éxito", Toast.LENGTH_SHORT).show();
                    bng.etCurrentPassword.setText("");
                    bng.etNewPassword.setText("");
                    bng.etConfirmPassword.setText("");
                } else {
                    Toast.makeText(getContext(), "Error: " + mensaje, Toast.LENGTH_LONG).show();
                }
            }
        });

        bng.imgProfile.setOnClickListener(v -> openGallery.launch("image/*"));



        bng.btnSaveSettings.setOnClickListener(v -> {
            String nuevoNombre = bng.etUserName.getText().toString().trim();
            String passActual = bng.etCurrentPassword.getText().toString().trim();
            String nuevaPass = bng.etNewPassword.getText().toString().trim();
            String confirmarPass = bng.etConfirmPassword.getText().toString().trim();


            if (!nuevoNombre.isEmpty()) {
                settingsViewModel.actualizarNombre(miUid, nuevoNombre);
            }
            if (!passActual.isEmpty() || !nuevaPass.isEmpty() || !confirmarPass.isEmpty()) {

                if (passActual.isEmpty()) {
                    Toast.makeText(getContext(), "Debes ingresar tu contraseña actual", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (nuevaPass.isEmpty() || confirmarPass.isEmpty()) {
                    Toast.makeText(getContext(), "Debes ingresar y repetir la nueva contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(passActual.length() < 8 || nuevaPass.length() < 8 || confirmarPass.length() < 8){
                    Toast.makeText(getContext(), "La contrasena debe tener 8 o mas caracteres", Toast.LENGTH_SHORT).show();
                }
                if (nuevaPass.equals(confirmarPass)) {
                    Toast.makeText(getContext(), "Revisando...", Toast.LENGTH_SHORT).show();
                    settingsViewModel.actualizarContrasena(passActual, nuevaPass);
                } else {
                    Toast.makeText(getContext(), "Las contraseñas nuevas no coinciden", Toast.LENGTH_SHORT).show();
                }
            }
        });


        bng.btnLogout.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Cerrar Sesión");
            builder.setMessage("¿Estás seguro de que deseas salir de tu cuenta?");

            builder.setPositiveButton("Sí, salir", (dialog, which) -> {
                userViewModel.changeOnlineStatus(miUid, false);
                mainViewModel.cerrarSesion(miUid);

                startActivity(new android.content.Intent(requireActivity(), LoginActivity.class));
                requireActivity().finish();
            });

            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
            builder.show();

        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bng = null;
    }
}