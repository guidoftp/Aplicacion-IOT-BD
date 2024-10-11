package com.example.aplicaioniot;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance(); // Asegúrate de inicializar FirebaseAuth aquí

        // Enlazar vistas
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnRegister = view.findViewById(R.id.btnRegister);
        progressBar = view.findViewById(R.id.progressBar);

        // Evento de inicio de sesión
        btnLogin.setOnClickListener(v -> loginUser());

        // Evento de registro
        btnRegister.setOnClickListener(v -> {
            // Cambiar a la pantalla de registro
            ((MainActivity) requireActivity()).loadFragment(new RegisterFragment());
        });

        return view;
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validación de entradas
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Ingrese su email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Ingrese su contraseña");
            return;
        }

        // Mostrar ProgressBar mientras se intenta iniciar sesión
        progressBar.setVisibility(View.VISIBLE);

        // Iniciar sesión con Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE); // Ocultar ProgressBar

                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                        // Navegar a la pantalla principal
                        ((MainActivity) requireActivity()).loadFragment(new HomeFragment());
                    } else {
                        Toast.makeText(getActivity(), "Error en inicio de sesión: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}







