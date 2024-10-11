package com.example.aplicaioniot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnGuest;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnGuest = findViewById(R.id.btnGuest);
        progressBar = findViewById(R.id.progressBar);

        // Evento de inicio de sesión
        btnLogin.setOnClickListener(v -> loginUser());

        // Evento para entrar como invitado
        btnGuest.setOnClickListener(v -> {
            // Navegar a MainActivity sin autenticación
            navigateToMainActivity();
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Ingrese su email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Ingrese su contraseña");
            return;
        }

        progressBar.setVisibility(View.VISIBLE); // Mostrar ProgressBar

        // Iniciar sesión con Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE); // Ocultar ProgressBar

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(LoginActivity.this, "Bienvenido: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                            navigateToMainActivity();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Error al iniciar sesión. Verifique sus credenciales.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Cerrar LoginActivity
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Comprobar si el usuario ya está autenticado al iniciar la aplicación
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            navigateToMainActivity(); // Si el usuario ya está logueado, ir directamente a MainActivity
        }
    }
}




