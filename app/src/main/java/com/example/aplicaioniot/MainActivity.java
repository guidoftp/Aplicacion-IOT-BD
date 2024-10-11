package com.example.aplicaioniot;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Cargar el fragmento inicial (HomeFragment)
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // Configurar el BottomNavigationView y su listener
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    // Método para manejar la selección del menú de navegación
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
    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    // Método para manejar el cierre de sesión
    private void logoutUser() {
        // Lógica para cerrar sesión
        FirebaseAuth.getInstance().signOut(); // Asegúrate de importar FirebaseAuth
        // Redirigir al usuario a la pantalla de inicio de sesión
        loadFragment(new LoginFragment()); // Opcionalmente, vuelve a cargar el fragmento de login
    }
}









