package com.example.aplicaioniot;

import android.annotation.SuppressLint;
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

import com.google.firebase.firestore.FirebaseFirestore;

public class ModificarMateriaFragment extends Fragment {

    private EditText editTextName;
    private EditText editTextDescription;
    private EditText editTextRating; // Agregado para la calificación
    private Button buttonSave;
    private Button buttonDelete; // Botón de eliminar agregado
    private ProgressBar progressBar;

    private FirebaseFirestore firestore;
    private String materiaId;

    public ModificarMateriaFragment() {
        // Constructor vacío
    }

    public static ModificarMateriaFragment newInstance(String materiaId) {
        ModificarMateriaFragment fragment = new ModificarMateriaFragment();
        Bundle args = new Bundle();
        args.putString("materiaId", materiaId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            materiaId = getArguments().getString("materiaId");
        }
        firestore = FirebaseFirestore.getInstance();
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_modificar_materia, container, false);
        editTextName = view.findViewById(R.id.editTextName);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        editTextRating = view.findViewById(R.id.editTextRating); // Inicializar calificación
        buttonSave = view.findViewById(R.id.buttonSave);
        buttonDelete = view.findViewById(R.id.buttonDelete); // Inicializar el botón de eliminar
        progressBar = view.findViewById(R.id.progressBar);

        // Cargar datos de la materia si el ID es válido
        if (materiaId != null) {
            loadMateria();
        } else {
            Toast.makeText(getActivity(), "ID de materia no válido", Toast.LENGTH_SHORT).show();
        }

        // Manejar el clic del botón Guardar
        buttonSave.setOnClickListener(v -> saveMateria());

        // Manejar el clic del botón Eliminar
        buttonDelete.setOnClickListener(v -> deleteMateria());

        return view;
    }

    private void loadMateria() {
        progressBar.setVisibility(View.VISIBLE);
        firestore.collection("materias").document(materiaId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Materia materia = documentSnapshot.toObject(Materia.class);
                        if (materia != null) {
                            editTextName.setText(materia.getName());
                            editTextDescription.setText(materia.getDescription()); // Cargar descripción
                            editTextRating.setText(String.valueOf(materia.getRating())); // Cargar calificación
                        }
                    } else {
                        Toast.makeText(getActivity(), "Materia no encontrada", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error al cargar materia: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void saveMateria() {
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String ratingStr = editTextRating.getText().toString().trim(); // Obtener calificación

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(ratingStr)) {
            Toast.makeText(getActivity(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int rating;
        try {
            rating = Integer.parseInt(ratingStr); // Convertir a entero
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Calificación inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        // Aquí hacemos un update para solo modificar los campos especificados
        firestore.collection("materias").document(materiaId)
                .update("name", name, "description", description, "rating", rating)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Materia actualizada correctamente", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager().popBackStack(); // Volver al fragment anterior
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error al actualizar materia: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void deleteMateria() {
        if (materiaId != null) {
            progressBar.setVisibility(View.VISIBLE);
            firestore.collection("materias").document(materiaId).delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getActivity(), "Materia eliminada correctamente", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        if (getActivity() != null) {
                            getActivity().getSupportFragmentManager().popBackStack(); // Volver al fragment anterior
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Error al eliminar materia: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    });
        } else {
            Toast.makeText(getActivity(), "ID de materia no válido", Toast.LENGTH_SHORT).show();
        }
    }
}

