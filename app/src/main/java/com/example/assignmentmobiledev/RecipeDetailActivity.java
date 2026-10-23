package com.example.assignmentmobiledev;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.assignmentmobiledev.Models.PantryItem;
import com.example.assignmentmobiledev.Models.Recipe;
import com.example.assignmentmobiledev.Models.RecipeIngredient;
import com.example.assignmentmobiledev.Repositories.PantryRepository;
import com.example.assignmentmobiledev.Repositories.RecipeRepository;
import com.example.assignmentmobiledev.Utils.RecipeMatcher;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {

    private Recipe recipe;
    private PantryRepository pantryRepository;

    private TextView txtTitle, txtStatus;
    private LinearLayout layoutIngredients, layoutSteps;
    private Button btnMadeRecipe, btnCloseRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe_detail);


        txtTitle = findViewById(R.id.txtRecipeTitle);
        txtStatus = findViewById(R.id.txtRecipeStatus);
        layoutIngredients = findViewById(R.id.layoutIngredients);
        layoutSteps = findViewById(R.id.layoutSteps);
        btnMadeRecipe = findViewById(R.id.btnMadeRecipe);
        btnCloseRecipe = findViewById(R.id.btnCloseRecipe);

        pantryRepository = new PantryRepository();

        String recipeId = getIntent().getStringExtra("recipeId");

        if (recipeId == null) {
            finish();
            return;
        }

        // go back to previous screen
        btnCloseRecipe.setOnClickListener(v -> finish());

        // confirm recipe was made to subtract pantry inventory
        btnMadeRecipe.setOnClickListener(v -> confirmMadeRecipe());

        RecipeRepository recipeRepository = new RecipeRepository();

        // load recipe details from db
        recipeRepository.getById(recipeId)
                .addOnSuccessListener(document -> {

                    recipe = document.toObject(Recipe.class);

                    // not found
                    if (recipe == null) {
                        Toast.makeText(this,
                                "Recipe not found",
                                Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    recipe.setId(document.getId());

                    displayRecipe();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Unable to load recipe",
                            Toast.LENGTH_LONG).show();
                    finish();
                });
    }

    private void displayRecipe() {

        boolean canMake = getIntent().getBooleanExtra("canMake", false);

        txtTitle.setText(recipe.getName());
        txtStatus.setText(canMake ? "You can make this recipe!" : "You need one more item to make this recipe!");

        layoutIngredients.removeAllViews();
        layoutSteps.removeAllViews();

        for (RecipeIngredient ingredient : recipe.getIngredients()) {

            TextView textView = new TextView(this);

            textView.setText(
                    "• " + ingredient.getName() + " - " +
                            ingredient.getQuantity() + " " +
                            ingredient.getUnit()
            );

            textView.setTextSize(16);
            textView.setPadding(0, 8, 0, 8);

            layoutIngredients.addView(textView);
        }

        int stepNumber = 1;

        for (String step : recipe.getSteps()) {

            TextView textView = new TextView(this);

            textView.setText(stepNumber + ". " + step);
            textView.setTextSize(16);
            textView.setPadding(0, 8, 0, 12);

            layoutSteps.addView(textView);

            stepNumber++;
        }

        btnMadeRecipe.setVisibility(canMake ? View.VISIBLE : View.GONE);
    }


    private void confirmMadeRecipe() {

        new MaterialAlertDialogBuilder(this)
                .setTitle("Recipe completed?")
                .setMessage(
                        "Deduct the required ingredients from your pantry?"
                )
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", (dialog, which) ->
                        deductPantryIngredients()
                )
                .show();
    }

    private void deductPantryIngredients() {

        btnMadeRecipe.setEnabled(false);

        pantryRepository.getAll()
                .addOnSuccessListener(querySnapshot -> {

                    List<PantryItem> allPantryItems = new ArrayList<>();

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        PantryItem item = document.toObject(PantryItem.class);

                        if (item != null) {
                            item.setId(document.getId());
                            allPantryItems.add(item);
                        }
                    }

                    // Find Pantry items that needs updating
                    List<PantryItem> updatedItems = new ArrayList<>();

                    for (RecipeIngredient recipeIngredient : recipe.getIngredients()) {

                        String usedIngredientName = RecipeMatcher.singularizeName(recipeIngredient.getNormalizedName());

                        boolean found = false;

                        for (PantryItem pantryItem : allPantryItems) {

                            String pantryItemName = RecipeMatcher.singularizeName(pantryItem.getNormalizedName());

                            if (!usedIngredientName.equals(pantryItemName)) {
                                continue;
                            }

                            if (!RecipeMatcher.compatibleUnits(pantryItem.getUnit(), recipeIngredient.getUnit())) {
                                continue;
                            }

                            // Convert kg/l to g/ml
                            double recipeQuantity = RecipeMatcher.convertQuantity(recipeIngredient.getQuantity(),recipeIngredient.getUnit());
                            double pantryQuantity = RecipeMatcher.convertQuantity(pantryItem.getQuantity(),pantryItem.getUnit());

                            double newQty = pantryQuantity - recipeQuantity;

                            if (newQty < 0) {
                                break;
                            }

                            // Convert the remaining quantity back if necessary
                            if (pantryItem.getUnit().equalsIgnoreCase("kg")
                                    || pantryItem.getUnit().equalsIgnoreCase("l")) {

                                if (newQty >= 1000) {
                                    newQty /= 1000;
                                }

                                else if (pantryItem.getUnit().equalsIgnoreCase("kg")) {
                                    pantryItem.setUnit("g");
                                }

                                else {
                                    pantryItem.setUnit("ml");
                                }
                            }

                            pantryItem.setQuantity(newQty);

                            if (!updatedItems.contains(pantryItem)) {
                                updatedItems.add(pantryItem);
                            }

                            found = true;
                            break;
                        }

                        if (!found) {
                            Toast.makeText(this,
                                    "Insufficient ingredients",
                                    Toast.LENGTH_LONG).show();

                            btnMadeRecipe.setEnabled(true);
                            return;
                        }
                    }

                    // Save only after all ingredients were successfully checked
                    List<Task<Void>> saveTasks = new ArrayList<>();

                    for (PantryItem item : updatedItems) {
                        saveTasks.add(pantryRepository.save(item));
                    }

                    Tasks.whenAll(saveTasks)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this,
                                        "Pantry updated successfully",
                                        Toast.LENGTH_SHORT).show();

                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this,
                                        "Unable to update pantry",
                                        Toast.LENGTH_LONG).show();

                                btnMadeRecipe.setEnabled(true);
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Unable to load pantry",
                            Toast.LENGTH_LONG).show();

                    btnMadeRecipe.setEnabled(true);
                });
    }
}