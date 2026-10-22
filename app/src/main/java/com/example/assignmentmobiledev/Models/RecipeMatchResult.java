package com.example.assignmentmobiledev.Models;

import java.util.List;

public class RecipeMatchResult {

    private final List<Recipe> canMake;
    private final List<Recipe> almostThere;

    public RecipeMatchResult(List<Recipe> canMake,
                             List<Recipe> almostThere) {
        this.canMake = canMake;
        this.almostThere = almostThere;
    }

    public List<Recipe> getCanMake() {
        return canMake;
    }

    public List<Recipe> getAlmostThere() {
        return almostThere;
    }
}