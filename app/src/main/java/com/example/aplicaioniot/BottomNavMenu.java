package com.example.aplicaioniot;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class BottomNavMenu extends AppCompatActivity {

    // Firebase Authentication para manejar el cierre de sesión
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav_menu);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Cargar el fragmento inicial (HomeFragment)
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // Configurar el BottomNavigationView y su listener
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    // Listener para los ítems del BottomNavigationView
    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        // Manejar la navegación entre fragmentos usando if-else
        if (item.getItemId() == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        } else if (item.getItemId() == R.id.nav_profile) {
            selectedFragment = new ProfileFragment();
        } else if (item.getItemId() == R.id.nav_logout) {
            logoutUser();
            return true; // Salir de la función aquí para evitar cargar un fragmento
        }

        // Cargar el fragmento seleccionado
        if (selectedFragment != null) {
            loadFragment(selectedFragment);
        }

        return true;
    }

    // Método para cargar el fragmento
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    // Método para manejar el cierre de sesión
    private void logoutUser() {
        mAuth.signOut(); // Cerrar sesión con Firebase Auth
        // Redirigir al usuario a la pantalla de inicio de sesión
        Intent intent = new Intent(BottomNavMenu.this, LoginActivity.class); // Asume que tienes una LoginActivity
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Cerrar la actividad actual
    }
}












