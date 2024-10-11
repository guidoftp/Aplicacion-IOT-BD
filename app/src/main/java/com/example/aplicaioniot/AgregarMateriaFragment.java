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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgregarMateriaFragment extends Fragment implements MateriaAdapter.OnMateriaClickListener {

    private EditText editTextMateriaName;
    private EditText editTextMateriaDescription;
    private EditText editTextMateriaRating;
    private Button buttonAddMateria;
    private RecyclerView recyclerViewMaterias;
    private MateriaAdapter materiaAdapter;
    private List<Materia> materiasList = new ArrayList<>();
    private ProgressBar progressBar;
    private TextView textViewStatus;

    private FirebaseFirestore db;
    private String asignaturaId;

    public AgregarMateriaFragment(String asignaturaId) {
        this.asignaturaId = asignaturaId;
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agregar_materia, container, false);

        db = FirebaseFirestore.getInstance();

        editTextMateriaName = view.findViewById(R.id.editTextMateriaName);
        editTextMateriaDescription = view.findViewById(R.id.editTextMateriaDescription);
        editTextMateriaRating = view.findViewById(R.id.editTextMateriaRating);
        buttonAddMateria = view.findViewById(R.id.buttonAddMateria);
        recyclerViewMaterias = view.findViewById(R.id.recyclerViewMaterias);
        progressBar = view.findViewById(R.id.progressBar);
        textViewStatus = view.findViewById(R.id.textViewStatus);

        // Configurar RecyclerView
        recyclerViewMaterias.setLayoutManager(new LinearLayoutManager(getContext()));
        materiaAdapter = new MateriaAdapter(materiasList, this); // Aquí pasamos el listener
        recyclerViewMaterias.setAdapter(materiaAdapter);

        createMateriasCollectionIfNotExists();

        buttonAddMateria.setOnClickListener(v -> addMateria());

        loadMaterias();

        return view;
    }

    private void addMateria() {
        String materiaName = editTextMateriaName.getText().toString().trim();
        String materiaDescription = editTextMateriaDescription.getText().toString().trim();
        String ratingStr = editTextMateriaRating.getText().toString().trim();

        if (TextUtils.isEmpty(materiaName) || TextUtils.isEmpty(materiaDescription) || TextUtils.isEmpty(ratingStr)) {
            Toast.makeText(getActivity(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int materiaRating;
        try {
            materiaRating = Integer.parseInt(ratingStr); // Convertir a entero
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Calificación inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        textViewStatus.setVisibility(View.GONE);

        Map<String, Object> materiaData = new HashMap<>();
        materiaData.put("name", materiaName);
        materiaData.put("description", materiaDescription);
        materiaData.put("rating", materiaRating);
        materiaData.put("asignaturaId", asignaturaId);

        // Agregar la materia a Firestore y obtener el ID del nuevo documento
        db.collection("materias").add(materiaData)
                .addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Materia agregada", Toast.LENGTH_SHORT).show();

                    // Crear una nueva instancia de Materia con el ID generado
                    Materia newMateria = new Materia(documentReference.getId(), materiaName, asignaturaId, materiaDescription, materiaRating);
                    materiasList.add(newMateria); // Añadir la nueva materia a la lista
                    materiaAdapter.notifyItemInserted(materiasList.size() - 1); // Notificar al adaptador de la nueva materia

                    // Limpiar los campos
                    editTextMateriaName.setText("");
                    editTextMateriaDescription.setText("");
                    editTextMateriaRating.setText("");
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Error al agregar materia", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadMaterias() {
        progressBar.setVisibility(View.VISIBLE);
        textViewStatus.setVisibility(View.GONE);

        db.collection("materias")
                .whereEqualTo("asignaturaId", asignaturaId)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        materiasList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String name = document.getString("name");
                            String description = document.getString("description");
                            int rating = document.getLong("rating").intValue();
                            materiasList.add(new Materia(id, name, asignaturaId, description, rating));
                        }
                        materiaAdapter.notifyDataSetChanged();
                    } else {
                        textViewStatus.setText("Error al cargar materias");
                        textViewStatus.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    textViewStatus.setText("Error al cargar materias");
                    textViewStatus.setVisibility(View.VISIBLE);
                });
    }

    private void createMateriasCollectionIfNotExists() {
        db.collection("materias").limit(1).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Map<String, Object> materiaData = new HashMap<>();
                        materiaData.put("name", "Ejemplo");
                        materiaData.put("description", "Descripción de ejemplo");
                        materiaData.put("rating", 0);
                        materiaData.put("asignaturaId", asignaturaId);
                        db.collection("materias").add(materiaData)
                                .addOnSuccessListener(documentReference -> {
                                    loadMaterias();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getActivity(), "Error al crear la colección de materias", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        loadMaterias();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error al verificar la colección de materias", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onMateriaClick(Materia materia) {
        // Manejar el clic en la materia si es necesario
        // Aquí puedes implementar la lógica para manejar el clic en una materia
    }
}