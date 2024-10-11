package com.example.aplicaioniot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MateriaAdapter extends RecyclerView.Adapter<MateriaAdapter.MateriaViewHolder> {

    private List<Materia> materiasList;
    private OnMateriaClickListener listener; // Agregar el listener

    // Constructor que acepta el listener
    public MateriaAdapter(List<Materia> materiasList, OnMateriaClickListener listener) {
        this.materiasList = materiasList;
        this.listener = listener; // Asignar el listener
    }

    @NonNull
    @Override
    public MateriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_materia, parent, false);
        return new MateriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MateriaViewHolder holder, int position) {
        Materia materia = materiasList.get(position);

        if (materia != null) {
            holder.textViewName.setText(materia.getName() != null ? materia.getName() : "Nombre no disponible");
            holder.textViewDescription.setText(materia.getDescription() != null ? materia.getDescription() : "Descripción no disponible");
            holder.textViewRating.setText(String.valueOf(materia.getRating()));

            // Manejar clic en la materia
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMateriaClick(materia); // Llamar al método del listener
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return materiasList != null ? materiasList.size() : 0;
    }

    // Interfaz para manejar el clic en la materia
    public interface OnMateriaClickListener {
        void onMateriaClick(Materia materia); // Método a implementar en el fragmento
    }

    static class MateriaViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewDescription;
        TextView textViewRating;

        public MateriaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewMateriaName);
            textViewDescription = itemView.findViewById(R.id.textViewMateriaDescription);
            textViewRating = itemView.findViewById(R.id.textViewMateriaRating);
        }
    }
}
