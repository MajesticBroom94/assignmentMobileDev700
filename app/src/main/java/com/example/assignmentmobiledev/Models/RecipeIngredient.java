package com.example.assignmentmobiledev.Models;


import java.util.Locale;

public class RecipeIngredient {

    private String name;
    private String normalizedName;
    private double quantity;
    private String unit;

    public RecipeIngredient() {
    }

    public RecipeIngredient(String name, double quantity, String unit) {
        this.name = name;
        this.normalizedName = name.trim().toLowerCase(Locale.ROOT);
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNormalizedName() {
        return normalizedName;
    }

    public void setNormalizedName(String normalizedName) {
        this.normalizedName = normalizedName;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
