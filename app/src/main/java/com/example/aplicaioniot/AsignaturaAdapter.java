package com.example.aplicaioniot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AsignaturaAdapter extends RecyclerView.Adapter<AsignaturaAdapter.AsignaturaViewHolder> {

    private List<Asignatura> asignaturaList;
    private OnAsignaturaClickListener listener;

    // Constructor
    public AsignaturaAdapter(List<Asignatura> asignaturaList, OnAsignaturaClickListener listener) {
        this.asignaturaList = asignaturaList != null ? asignaturaList : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public AsignaturaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.asignatura_item, parent, false);
        return new AsignaturaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AsignaturaViewHolder holder, int position) {
        Asignatura asignatura = asignaturaList.get(position);
        holder.textViewName.setText(asignatura.getName());
        holder.textViewDescription.setText(asignatura.getDescription());
        holder.textViewRating.setText(String.valueOf(asignatura.getRating()));

        // Manejo de clics en el elemento
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAsignaturaClick(asignatura); // Pasar la asignatura completa
            }
        });
    }

    @Override
    public int getItemCount() {
        return asignaturaList.size();
    }

    // Método para actualizar la lista de asignaturas
    public void updateAsignaturas(List<Asignatura> newAsignaturas) {
        this.asignaturaList.clear();
        if (newAsignaturas != null) {
            this.asignaturaList.addAll(newAsignaturas);
        }
        notifyDataSetChanged(); // Notificar que los datos han cambiado
    }

    // Clase ViewHolder
    public static class AsignaturaViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public TextView textViewDescription;
        public TextView textViewRating;

        public AsignaturaViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDescription = itemView.findViewById(R.id.textViewDescription); // Referencia correcta
            textViewRating = itemView.findViewById(R.id.textViewRating); // Agregada la referencia
        }
    }

    // Interfaz para manejar clics en asignaturas
    public interface OnAsignaturaClickListener {
        void onAsignaturaClick(Asignatura asignatura); // Pasar la asignatura completa
    }
}




