package com.example.aplicaioniot;

public class Asignatura {
    private String id; // Cambia esto si ya tienes un ID en Firestore
    private String name;
    private String description;
    private int rating;

    // Constructor sin argumentos (necesario para Firestore)
    public Asignatura() {
    }

    // Constructor con argumentos
    public Asignatura(String id, String name, String description, int rating) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.rating = rating;
    }

    // Getters y setters
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
}







