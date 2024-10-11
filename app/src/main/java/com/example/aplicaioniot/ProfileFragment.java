package com.example.aplicaioniot;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private TextView tvEmail;
    private EditText etUsername, etNewEmail;
    private Button btnUpdateProfile, btnLogout;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Inicializar Firebase Auth y Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Enlazar vistas
        tvEmail = view.findViewById(R.id.tvEmail);
        etUsername = view.findViewById(R.id.etUsername);
        etNewEmail = view.findViewById(R.id.etNewEmail); // Agregar EditText para el nuevo email
        btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Obtener usuario actual y mostrar su email
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            tvEmail.setText("Email: " + user.getEmail());
            // Supongamos que el nombre de usuario se almacena en el nombre
            etUsername.setText(user.getDisplayName());
        }

        // Evento para actualizar el perfil
        btnUpdateProfile.setOnClickListener(v -> updateProfile());

        // Evento para cerrar sesión
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(getActivity(), "Sesión cerrada", Toast.LENGTH_SHORT).show();
            ((MainActivity) requireActivity()).loadFragment(new LoginFragment());
        });

        return view;
    }

    private void updateProfile() {
        String username = etUsername.getText().toString().trim();
        String newEmail = etNewEmail.getText().toString().trim(); // Obtener nuevo email
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            // Validar entradas
            if (TextUtils.isEmpty(username)) {
                etUsername.setError("Ingrese un nombre de usuario");
                return;
            }
            if (!TextUtils.isEmpty(newEmail)) {
                // Actualizar correo electrónico en Firebase Authentication
                user.updateEmail(newEmail).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Correo actualizado, ahora actualizamos en Firestore
                        updateUserInFirestore(user.getUid(), username, newEmail);
                    } else {
                        Toast.makeText(getActivity(), "Error al actualizar el email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Si el nuevo email no es proporcionado, solo actualiza el nombre de usuario
                updateUserInFirestore(user.getUid(), username, user.getEmail());
            }
        } else {
            Toast.makeText(getActivity(), "Error al actualizar el perfil", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserInFirestore(String userId, String username, String email) {
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("username", username);
        userUpdates.put("email", email);

        // Actualizar los datos en Firestore
        db.collection("users").document(userId)
                .set(userUpdates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error al actualizar el perfil: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

