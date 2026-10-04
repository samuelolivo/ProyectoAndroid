package com.example.chat.view;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chat.R;
import com.example.chat.databinding.FragmentSettingsBinding;
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



        bng.btnSaveSettings.setOnClickListener(v -> {
            String nuevoNombre = bng.etUserName.getText().toString().trim();
            String passActual = bng.etCurrentPassword.getText().toString().trim();
            String nuevaPass = bng.etNewPassword.getText().toString().trim();
            String confirmarPass = bng.etConfirmPassword.getText().toString().trim();

            if (!passActual.isEmpty() || !nuevaPass.isEmpty() || !confirmarPass.isEmpty()) {

                if (passActual.isEmpty()) {
                    Toast.makeText(getContext(), "Debes ingresar tu contraseña actual", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (nuevaPass.isEmpty() || confirmarPass.isEmpty()) {
                    Toast.makeText(getContext(), "Debes ingresar y repetir la nueva contraseña", Toast.LENGTH_SHORT).show();
                    return;
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
                mainViewModel.cerrarSesion(miUid);
                userViewModel.changeOnlineStatus(miUid, false);

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