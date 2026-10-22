package com.example.assignmentmobiledev.Models;

import java.util.Date;
import java.util.Locale;

public class PantryItem {

    private String id;
    private String name;
    private String normalizedName;
    private double quantity;
    private String unit;
    private Date expiryDate;

    public PantryItem() {
        // Required for Firestore deserialization
    }

    public PantryItem(String name, double quantity, String unit, Date expiryDate) {
        this.name = name;
        this.normalizedName = name.trim().toLowerCase(Locale.ROOT);
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNormalizedName() { return normalizedName; }
    public void setNormalizedName(String normalizedName) {
        this.normalizedName = normalizedName;
    }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() { return unit; }
    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}
