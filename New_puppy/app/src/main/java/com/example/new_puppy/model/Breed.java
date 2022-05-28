package com.example.new_puppy.model;

public class Breed {
    int id;
    String breed;

    public Breed() {
    }

    public Breed(int id, String breed) {
        this.id = id;
        this.breed = breed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }
}
