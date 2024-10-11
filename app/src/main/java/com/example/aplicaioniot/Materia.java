package com.example.aplicaioniot;

import com.google.firebase.firestore.DocumentId;

public class Materia {

    @DocumentId
    private String id;
    private String name;
    private String asignaturaId;
    private String description; // Descripción de la materia
    private int rating; // Calificación de la materia

    public Materia() {}

    // Constructor completo
    public Materia(String id, String name, String asignaturaId, String description, int rating) {
        this.id = id;
        this.name = name;
        this.asignaturaId = asignaturaId;
        this.description = description;
        this.rating = rating;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAsignaturaId() {
        return asignaturaId;
    }

    public void setAsignaturaId(String asignaturaId) {
        this.asignaturaId = asignaturaId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Materia{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", asignaturaId='" + asignaturaId + '\'' +
                ", description='" + description + '\'' +
                ", rating=" + rating +
                '}';
    }
}