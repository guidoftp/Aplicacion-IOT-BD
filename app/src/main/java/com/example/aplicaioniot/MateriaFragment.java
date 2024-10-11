package com.example.aplicaioniot;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MateriaFragment extends Fragment implements MateriaAdapter.OnMateriaClickListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Button buttonAddMateria;
    private MateriaAdapter adapter;
    private List<Materia> materias = new ArrayList<>();
    private FirebaseFirestore firestore;
    private String asignaturaId; // ID de la asignatura

    public MateriaFragment() {
        // Requiere un constructor vacío
    }

    public static MateriaFragment newInstance(String asignaturaId) {
        MateriaFragment fragment = new MateriaFragment();
        Bundle args = new Bundle();
        args.putString("asignaturaId", asignaturaId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            asignaturaId = getArguments().getString("asignaturaId");
        }
        firestore = FirebaseFirestore.getInstance();
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_materia, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        buttonAddMateria = view.findViewById(R.id.button_add_materia);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MateriaAdapter(materias, this); // Inicializa el adaptador con el listener
        recyclerView.setAdapter(adapter);

        // Configurar el botón para abrir el fragmento AgregarMateriaFragment
        buttonAddMateria.setOnClickListener(v -> openAgregarMateriaFragment());

        // Cargar materias asociadas a la asignatura
        loadMaterias();

        return view;
    }

    private void loadMaterias() {
        progressBar.setVisibility(View.VISIBLE);
        firestore.collection("materias")
                .whereEqualTo("asignaturaId", asignaturaId) // Filtrar por ID de asignatura
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        materias.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Materia materia = document.toObject(Materia.class);
                            // Añadir la ID del documento a la materia
                            if (document.getId() != null) {
                                materia.setId(document.getId());
                            }
                            materias.add(materia);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), "Error al cargar materias", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openAgregarMateriaFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, new AgregarMateriaFragment(asignaturaId))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onMateriaClick(Materia materia) {
        // Manejar el clic en la materia y navegar al ModificarMateriaFragment
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ModificarMateriaFragment.newInstance(materia.getId()))
                .addToBackStack(null)
                .commit();
    }
}