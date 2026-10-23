package com.example.assignmentmobiledev.Utils;

import com.example.assignmentmobiledev.Models.PantryItem;
import com.example.assignmentmobiledev.Models.Recipe;
import com.example.assignmentmobiledev.Models.RecipeIngredient;
import com.example.assignmentmobiledev.Models.RecipeMatchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class RecipeMatcher {

    public static RecipeMatchResult getMatchingRecipes(List<PantryItem> pantry, List<Recipe> recipes) {

        List<Recipe> canMake = new ArrayList<>();
        List<Recipe> almostThere = new ArrayList<>();

        for (Recipe recipe : recipes) {

            int matchedIngredientCount = 0;
            int missingIngredientCount = 0;
            int recipeIngredientsCount = recipe.getIngredients().size();

            for (RecipeIngredient requiredIngredient : recipe.getIngredients()) {

                boolean ingredientExists = false;
                boolean quantitySufficient = false;

                String requiredIngredientName = singularizeName(requiredIngredient.getNormalizedName());

                for (PantryItem availableIngredient : pantry) {

                    String availableIngredientName = singularizeName(availableIngredient.getNormalizedName());

                    // Check ingredient name
                    if (!requiredIngredientName.equals(availableIngredientName)) {
                        continue;
                    }

                    // Check unit compatibility
                    if (!compatibleUnits(availableIngredient.getUnit(), requiredIngredient.getUnit())) {
                        continue;
                    }

                    ingredientExists = true;

                    double availableQuantity = convertQuantity(
                            availableIngredient.getQuantity(),
                            availableIngredient.getUnit()
                    );

                    double requiredQuantity = convertQuantity(
                            requiredIngredient.getQuantity(),
                            requiredIngredient.getUnit()
                    );

                    if (availableQuantity >= requiredQuantity) {
                        quantitySufficient = true;
                        matchedIngredientCount++;
                    }

                    break;
                }

                // Count only entirely missing ingredients.
                if (!ingredientExists) {
                    missingIngredientCount++;
                }

                // A recipe cannot qualify for either list if an
                // existing ingredient has insufficient quantity or is missing 2 or more items.
                if (ingredientExists && !quantitySufficient || missingIngredientCount >= 2) {
                    break;
                }
            }

            // Every required ingredient is available
            if (matchedIngredientCount == recipeIngredientsCount) {
                canMake.add(recipe);
            }

            // Exactly one ingredient is missing entirely;
            // all remaining ingredients have sufficient quantities.
            else if (missingIngredientCount == 1 &&
                    matchedIngredientCount == recipeIngredientsCount - 1) {

                almostThere.add(recipe);
            }
        }

        return new RecipeMatchResult(canMake, almostThere);
    }

    public static String singularizeName(String ingredientName) {

        ingredientName = ingredientName.trim().toLowerCase(Locale.ROOT);

        if (ingredientName.endsWith("toes")) {
            return ingredientName.substring(0, ingredientName.length() - 2);
        }

        if (ingredientName.endsWith("ies")) {
            return ingredientName.substring(0, ingredientName.length() - 3) + "y";
        }

        if (ingredientName.endsWith("s") && !ingredientName.endsWith("ss")) {
            return ingredientName.substring(0, ingredientName.length() - 1);
        }

        return ingredientName;
    }

    public static boolean compatibleUnits(String firstUnit, String secondUnit) {

        firstUnit = firstUnit.toLowerCase(Locale.ROOT);
        secondUnit = secondUnit.toLowerCase(Locale.ROOT);

        if (firstUnit.equals(secondUnit)) {
            return true;
        }

        if ((firstUnit.equals("g") || firstUnit.equals("kg")) &&
                (secondUnit.equals("g") || secondUnit.equals("kg"))) {
            return true;
        }

        return (firstUnit.equals("ml") || firstUnit.equals("l")) &&
                (secondUnit.equals("ml") || secondUnit.equals("l"));
    }

    public static double convertQuantity(double quantity, String unit) {

        unit = unit.toLowerCase(Locale.ROOT);

        if (unit.equals("kg") || unit.equals("l")) {
            return quantity * 1000;
        }

        return quantity;
    }
}
