package com.example.chat.ui.chat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chat.data.model.Message;
import com.example.chat.databinding.ActivityChatBinding;
import com.example.chat.viewModel.AuthViewModel;
import com.example.chat.viewModel.ChatViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    public static final String EXTRA_CHAT_ID = "extra_chat_id";
    public static final String EXTRA_NOMBRE = "extra_nombre";

    private static final int MAX_LADO = 800;
    private static final int CALIDAD_JPEG = 65;

    private ActivityChatBinding binding;
    private ChatViewModel viewModel;
    private AuthViewModel authViewModel;
    private ChatAdapter adapter;
    private String miUid;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickImage =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    procesarImagen(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(
                binding.getRoot(),
                (view, insets) -> {
                    int top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
                    int bottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
                    view.setPadding(view.getPaddingLeft(), top, view.getPaddingRight(), bottom);

                    return insets;
                }
        );

        miUid = authViewModel.getCurrentUserId();
        if (miUid == null) {
            finish();
            return;
        }

        String chatId = getIntent().getStringExtra(EXTRA_CHAT_ID);
        String nombre = getIntent().getStringExtra(EXTRA_NOMBRE);
        if (chatId == null) {
            finish();
            return;
        }
        binding.txtChatTitle.setText(nombre);

        binding.recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatAdapter(new ArrayList<>(), miUid);
        binding.recyclerViewMessages.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        viewModel.cargarChat(chatId, miUid);

        viewModel.getMensajes().observe(this, mensajes -> {
            if (mensajes == null) return;
            adapter.actualizarMensajes(mensajes);
            if (!mensajes.isEmpty()) {
                binding.recyclerViewMessages.scrollToPosition(mensajes.size() - 1);
            }
            marcarRecibidosComoLeidos(mensajes);
        });

        viewModel.getResultadoOperacion().observe(this, resultado -> {
            if (resultado == null || "SUCCESS".equals(resultado)) return;
            Toast.makeText(this, resultado, Toast.LENGTH_SHORT).show();
        });

        binding.btnSend.setOnClickListener(v -> enviar());
        binding.btnCamera.setOnClickListener(v -> pickImage.launch(
                new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build()));
        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void enviar() {
        String texto = binding.mtLnMessage.getText().toString().trim();
        if (texto.isEmpty()) return;
        viewModel.enviarMensaje(texto);
        binding.mtLnMessage.setText("");
    }

    private void procesarImagen(Uri uri) {
        try {
            byte[] datos = comprimirImagen(uri);
            viewModel.enviarImagen(Base64.encodeToString(datos, Base64.NO_WRAP));
        } catch (IOException e) {
            Toast.makeText(this, "No se pudo leer la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] comprimirImagen(Uri uri) throws IOException {
        BitmapFactory.Options opciones = new BitmapFactory.Options();
        opciones.inJustDecodeBounds = true;
        try (InputStream entrada = getContentResolver().openInputStream(uri)) {
            BitmapFactory.decodeStream(entrada, null, opciones);
        }

        int muestreo = 1;
        int ladoMayor = Math.max(opciones.outWidth, opciones.outHeight);
        while (ladoMayor / muestreo > MAX_LADO) {
            muestreo *= 2;
        }

        BitmapFactory.Options reales = new BitmapFactory.Options();
        reales.inSampleSize = muestreo;
        Bitmap bitmap;
        try (InputStream entrada = getContentResolver().openInputStream(uri)) {
            bitmap = BitmapFactory.decodeStream(entrada, null, reales);
        }
        if (bitmap == null) throw new IOException("decodeStream devolvió null");

        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, CALIDAD_JPEG, salida);
        byte[] datos = salida.toByteArray();

        bitmap.recycle();
        salida.close();
        return datos;
    }

    private void marcarRecibidosComoLeidos(List<Message> mensajes) {
        for (Message mensaje : mensajes) {
            Map<String, Boolean> readBy = mensaje.getReadBy();
            if (readBy != null && Boolean.TRUE.equals(readBy.get(miUid))) continue;
            viewModel.marcarComoLeido(mensaje.getIdMessage());
        }
    }
}