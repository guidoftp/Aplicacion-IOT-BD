package com.example.aplicaioniot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private AsignaturaAdapter asignaturaAdapter;
    private List<Asignatura> asignaturaList;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private Button buttonAddAsignatura; // Botón para agregar asignatura

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Inicializar vistas
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        buttonAddAsignatura = view.findViewById(R.id.button_add_asignatura); // Inicializar botón
        asignaturaList = new ArrayList<>();

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        asignaturaAdapter = new AsignaturaAdapter(asignaturaList, asignatura -> {
            // Manejo de clics en la asignatura
            openMateriaFragment(asignatura);
        });
        recyclerView.setAdapter(asignaturaAdapter);

        // Cargar datos de Firestore
        loadAsignaturas();

        // Configurar el botón para agregar asignaturas
        buttonAddAsignatura.setOnClickListener(v -> openAgregarAsignaturaFragment());

        return view;
    }

    private void loadAsignaturas() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("asignaturas")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        progressBar.setVisibility(View.GONE);
                        if (error != null) {
                            Toast.makeText(getActivity(), "Error al cargar datos", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        asignaturaList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            Asignatura asignatura = doc.toObject(Asignatura.class);
                            asignatura.setId(doc.getId());
                            asignaturaList.add(asignatura);
                        }
                        asignaturaAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void openMateriaFragment(Asignatura asignatura) {
        MateriaFragment materiaFragment = MateriaFragment.newInstance(asignatura.getId());
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, materiaFragment)
                .addToBackStack(null)
                .commit();
    }

    private void openAgregarAsignaturaFragment() {
        AgregarAsignaturaFragment agregarAsignaturaFragment = new AgregarAsignaturaFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, agregarAsignaturaFragment)
                .addToBackStack(null)
                .commit();
    }
}






