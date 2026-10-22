package com.example.assignmentmobiledev.Models;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;

public class Recipe {

    private String id;
    private String name;
    private List<RecipeIngredient> ingredients = new ArrayList<>();
    private List<String> steps = new ArrayList<>();

    public Recipe() {
    }

    public Recipe(String name, List<RecipeIngredient> ingredients, List<String> steps) {
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    @Exclude
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

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }
}
