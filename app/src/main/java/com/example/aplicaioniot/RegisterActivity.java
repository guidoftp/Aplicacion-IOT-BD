package com.example.aplicaioniot;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnRegister, btnBackToLogin;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar Firebase Auth y Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Enlazar vistas
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnBackToLogin = findViewById(R.id.btnBackToLogin1);

        // Evento de registro
        btnRegister.setOnClickListener(v -> registerUser());

        // Evento de volver a la pantalla de inicio de sesión
        btnBackToLogin.setOnClickListener(v -> finish());
    }

    private void registerUser() {
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

        // Crear usuario con Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkAndCreateUserCollection(user);
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Error en registro: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void checkAndCreateUserCollection(FirebaseUser user) {
        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot snapshot = task.getResult();
                boolean isCollectionEmpty = snapshot.isEmpty();

                // Crear un objeto de usuario para Firestore
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("email", user.getEmail());
                userMap.put("uid", user.getUid());

                // Agregar el usuario a la colección "users" en Firestore
                db.collection("users").document(user.getUid()).set(userMap)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(RegisterActivity.this, "Usuario registrado en Firestore", Toast.LENGTH_SHORT).show();

                            // Si es la primera vez, creamos una colección o configuraciones adicionales aquí si es necesario
                            if (isCollectionEmpty) {
                                // Añadir configuraciones adicionales aquí si es necesario
                            }

                            // Redirigir al usuario a LoginActivity después del registro
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(RegisterActivity.this, "Error al guardar los datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            } else {
                Toast.makeText(this, "Error al verificar la colección: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}


