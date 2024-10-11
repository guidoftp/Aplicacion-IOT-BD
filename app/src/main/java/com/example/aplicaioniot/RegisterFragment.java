package com.example.aplicaioniot;

import android.content.Intent; // Asegúrate de importar Intent
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterFragment extends Fragment {

    private EditText etUsername, etEmail, etPassword, etRepeatPassword;
    private Button btnRegister, btnBackToLogin;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Enlazar vistas
        etUsername = view.findViewById(R.id.etUsername);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        etRepeatPassword = view.findViewById(R.id.etRepeatPassword);
        btnRegister = view.findViewById(R.id.btnRegister);
        btnBackToLogin = view.findViewById(R.id.btnBackToLogin1);

        // Evento de registro
        btnRegister.setOnClickListener(v -> registerUser());

        // Botón para volver a la pantalla de login
        btnBackToLogin.setOnClickListener(v -> {
            // Iniciar la actividad de Login
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            // Opcional: finalizar la actividad de registro si no deseas volver
            requireActivity().finish();
        });

        return view;
    }

    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String repeatPassword = etRepeatPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Ingrese su nombre de usuario");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Ingrese su email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Ingrese su contraseña");
            return;
        }

        if (!password.equals(repeatPassword)) {
            etRepeatPassword.setError("Las contraseñas no coinciden");
            return;
        }

        // Registrar el usuario con Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // El registro fue exitoso, puedes mostrar un mensaje
                            Toast.makeText(getActivity(), "Usuario registrado: " + email, Toast.LENGTH_SHORT).show();
                            // Iniciar la actividad de inicio después de registrarse
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                            requireActivity().finish(); // Finalizar la actividad de registro
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error al registrar usuario: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}



