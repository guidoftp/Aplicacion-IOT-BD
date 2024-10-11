package com.example.aplicaioniot;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class AgregarAsignaturaFragment extends Fragment {

    private EditText editTextName; // EditText para el nombre de la asignatura
    private EditText editTextDescription; // EditText para la descripción
    private EditText editTextNameToDelete; // EditText para el nombre de la asignatura a eliminar
    private EditText editTextRating; // EditText para el rating
    private EditText editTextNameToModify; // EditText para el nombre de la asignatura a modificar
    private Button buttonSave; // Botón para guardar
    private Button buttonDelete; // Botón para eliminar
    private Button buttonModify; // Botón para modificar

    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agregar_asignatura, container, false);

        // Inicializar EditTexts y botones
        editTextName = view.findViewById(R.id.editTextName);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        editTextNameToDelete = view.findViewById(R.id.editTextNameToDelete);
        editTextRating = view.findViewById(R.id.editTextRating);
        editTextNameToModify = view.findViewById(R.id.editTextNameToModify); // Nuevo EditText para el nombre de la asignatura a modificar
        buttonSave = view.findViewById(R.id.buttonSave);
        buttonDelete = view.findViewById(R.id.buttonDelete);
        buttonModify = view.findViewById(R.id.buttonModify); // Inicializar el botón de modificar

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Configurar listeners para los botones
        buttonSave.setOnClickListener(v -> saveAsignatura());
        buttonDelete.setOnClickListener(v -> deleteAsignatura());
        buttonModify.setOnClickListener(v -> modifyAsignatura()); // Listener para el botón de modificar

        // Crear colección "asignaturas" si no existe
        createAsignaturasCollectionIfNotExists();

        return view;
    }

    private void saveAsignatura() {
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String ratingString = editTextRating.getText().toString().trim();

        // Validar entradas
        if (name.isEmpty() || description.isEmpty() || ratingString.isEmpty()) {
            Toast.makeText(getActivity(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int rating;
        try {
            rating = Integer.parseInt(ratingString);
            if (rating < 0 || rating > 3) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "El rating debe ser un número entre 0 y 3", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear una nueva asignatura con un ID automático
        Asignatura asignatura = new Asignatura("", name, description, rating);

        // Usar 'add()' para agregar el documento
        db.collection("asignaturas").add(asignatura)
                .addOnSuccessListener(documentReference -> {
                    String generatedId = documentReference.getId();
                    Toast.makeText(getActivity(), "Asignatura agregada con ID: " + generatedId, Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error al agregar asignatura", Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteAsignatura() {
        String nameToDelete = editTextNameToDelete.getText().toString().trim();

        if (nameToDelete.isEmpty()) {
            Toast.makeText(getActivity(), "Por favor ingresa el nombre de la asignatura a eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("asignaturas")
                .whereEqualTo("name", nameToDelete)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            Toast.makeText(getActivity(), "No se encontró ninguna asignatura con ese nombre", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String asignaturaId = document.getId();
                            deleteMateriasByAsignaturaId(asignaturaId);
                            db.collection("asignaturas").document(asignaturaId).delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getActivity(), "Asignatura eliminada", Toast.LENGTH_SHORT).show();
                                        clearFields();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getActivity(), "Error al eliminar asignatura", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error al buscar asignaturas", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error al realizar la búsqueda", Toast.LENGTH_SHORT).show();
                });
    }

    private void modifyAsignatura() {
        String nameToModify = editTextNameToModify.getText().toString().trim(); // Obtener el nombre de la asignatura a modificar
        String newDescription = editTextDescription.getText().toString().trim();
        String newRatingString = editTextRating.getText().toString().trim();

        // Validar entradas
        if (nameToModify.isEmpty() || newDescription.isEmpty() || newRatingString.isEmpty()) {
            Toast.makeText(getActivity(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int newRating;
        try {
            newRating = Integer.parseInt(newRatingString);
            if (newRating < 0 || newRating > 3) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "El rating debe ser un número entre 0 y 3", Toast.LENGTH_SHORT).show();
            return;
        }

        // Buscar la asignatura a modificar
        db.collection("asignaturas")
                .whereEqualTo("name", nameToModify)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            Toast.makeText(getActivity(), "No se encontró ninguna asignatura con ese nombre", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // Modificar cada asignatura encontrada
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String asignaturaId = document.getId(); // Obtener el ID de la asignatura
                            // Crear un nuevo objeto Asignatura con los nuevos valores
                            Asignatura updatedAsignatura = new Asignatura(asignaturaId, nameToModify, newDescription, newRating);

                            // Actualizar el documento en Firestore
                            db.collection("asignaturas").document(asignaturaId).set(updatedAsignatura)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getActivity(), "Asignatura modificada", Toast.LENGTH_SHORT).show();
                                        clearFields();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getActivity(), "Error al modificar asignatura", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error al buscar asignaturas", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error al realizar la búsqueda", Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteMateriasByAsignaturaId(String asignaturaId) {
        db.collection("materias")
                .whereEqualTo("asignaturaId", asignaturaId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("materias").document(document.getId()).delete()
                                    .addOnSuccessListener(aVoid -> {
                                        // Materia eliminada exitosamente
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getActivity(), "Error al eliminar materia: " + document.getId(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error al buscar materias asociadas", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error al realizar la búsqueda de materias", Toast.LENGTH_SHORT).show();
                });
    }

    private void createAsignaturasCollectionIfNotExists() {
        db.collection("asignaturas").limit(1).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Asignatura asignatura = new Asignatura("", "", "", 0);
                        db.collection("asignaturas").add(asignatura)
                                .addOnSuccessListener(documentReference -> {
                                    // Se ha creado la colección con un documento vacío
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getActivity(), "Error al crear la colección de asignaturas", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error al acceder a la colección de asignaturas", Toast.LENGTH_SHORT).show();
                });
    }

    private void clearFields() {
        editTextName.setText("");
        editTextDescription.setText("");
        editTextNameToDelete.setText("");
        editTextRating.setText("");
        editTextNameToModify.setText(""); // Limpiar el campo de nombre a modificar
    }
}

