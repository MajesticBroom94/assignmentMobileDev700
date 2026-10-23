package com.example.assignmentmobiledev;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.assignmentmobiledev.Models.PantryItem;
import com.example.assignmentmobiledev.Models.Recipe;
import com.example.assignmentmobiledev.Models.RecipeIngredient;
import com.example.assignmentmobiledev.Repositories.PantryRepository;
import com.example.assignmentmobiledev.Repositories.RecipeRepository;
import com.example.assignmentmobiledev.Utils.RecipeMatcher;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {

    private Recipe recipe;
    private PantryRepository pantryRepository;

    private TextView txtTitle, txtStatus;
    private LinearLayout layoutIngredients, layoutSteps;
    private Button btnMadeRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // set padding to layout
        View root = findViewById(R.id.recipeDetailsRoot);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(
                    bars.left,
                    bars.top,
                    bars.right,
                    bars.bottom
            );

            return insets;
        });


        // check if user is logged in
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            finish();
            return;
        }

        // view elements
        txtTitle = findViewById(R.id.txtRecipeTitle);
        txtStatus = findViewById(R.id.txtRecipeStatus);
        layoutIngredients = findViewById(R.id.layoutIngredients);
        layoutSteps = findViewById(R.id.layoutSteps);
        btnMadeRecipe = findViewById(R.id.btnMadeRecipe);
        Button btnCloseRecipe = findViewById(R.id.btnCloseRecipe);

        // initialize pantry repository
        pantryRepository = new PantryRepository();

        // get recipe id from intent
        String recipeId = getIntent().getStringExtra("recipeId");

        if (recipeId == null) {
            finish();
            return;
        }

        // go back to previous screen
        btnCloseRecipe.setOnClickListener(v -> finish());

        // confirm recipe was made to subtract pantry inventory
        btnMadeRecipe.setOnClickListener(v -> confirmMadeRecipe());

        // initialize recipe repository
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

                    // attach id
                    recipe.setId(document.getId());

                    // display loaded recipe details
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

        // recipe can be made or is missing ingredient
        boolean canMake = getIntent().getBooleanExtra("canMake", false);

        txtTitle.setText(recipe.getName());
        txtStatus.setText(canMake ? "You can make this recipe!" : "You need one more item to make this recipe!");

        // clear layout
        layoutIngredients.removeAllViews();
        layoutSteps.removeAllViews();

        // create ingredient rows
        for (RecipeIngredient ingredient : recipe.getIngredients()) {

            TextView textView = new TextView(this);

            // inline details
            textView.setText(
                    "• " + ingredient.getName() + " - " +
                            ingredient.getQuantity() + " " +
                            ingredient.getUnit()
            );

            textView.setTextSize(16);
            textView.setPadding(0, 8, 0, 8);


            // add each ingredient to layout
            layoutIngredients.addView(textView);
        }

        int stepNumber = 1;

        // create step rows
        for (String step : recipe.getSteps()) {

            TextView textView = new TextView(this);

            textView.setText(stepNumber + ". " + step);
            textView.setTextSize(16);
            textView.setPadding(0, 8, 0, 12);

            // add each step to layout
            layoutSteps.addView(textView);

            stepNumber++;
        }

        // hide the 'made recipe' option button if recipe cannot be made
        btnMadeRecipe.setVisibility(canMake ? View.VISIBLE : View.GONE);
    }


    // confirm dialog
    private void confirmMadeRecipe() {

        new MaterialAlertDialogBuilder(this)
                .setTitle("Recipe completed?")
                .setMessage(
                        "Deduct the required ingredients from your pantry?"
                )
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", (dialog, which) ->
                        subtractPantryIngredients()
                )
                .show();
    }

    // subtract ingredients from pantry
    private void subtractPantryIngredients() {

        btnMadeRecipe.setEnabled(false);

        // get all user pantry items
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

                    // store changes
                    List<PantryItem> updatedItems = new ArrayList<>();

                    // check each recipe ingredient
                    for (RecipeIngredient recipeIngredient : recipe.getIngredients()) {

                        // normalize recipe ingredient name
                        String usedIngredientName = RecipeMatcher.singularizeName(recipeIngredient.getNormalizedName());

                        boolean found = false;

                        // check through pantry items - Find Pantry items that needs updating
                        for (PantryItem pantryItem : allPantryItems) {

                            // normalize pantry item name
                            String pantryItemName = RecipeMatcher.singularizeName(pantryItem.getNormalizedName());

                            // check for match
                            if (!usedIngredientName.equals(pantryItemName)) {
                                continue;
                            }

                            if (!RecipeMatcher.compatibleUnits(pantryItem.getUnit(), recipeIngredient.getUnit())) {
                                continue;
                            }

                            // Converts kg/l to g/ml
                            double recipeQuantity = RecipeMatcher.convertQuantity(recipeIngredient.getQuantity(),recipeIngredient.getUnit());
                            double pantryQuantity = RecipeMatcher.convertQuantity(pantryItem.getQuantity(),pantryItem.getUnit());

                            double newQty = pantryQuantity - recipeQuantity;

                            // if it is negative, don't proceed with update
                            if (newQty < 0) {
                                break;
                            }

                            // Convert the remaining quantity back if it was converted
                            if (pantryItem.getUnit().equalsIgnoreCase("kg")
                                || pantryItem.getUnit().equalsIgnoreCase("l")) {

                                // when 1000ml/g or more, convert unit
                                if (newQty >= 1000) {
                                    newQty /= 1000;
                                }

                                // value below threshold - convert unit
                                else if (pantryItem.getUnit().equalsIgnoreCase("kg")) {
                                    pantryItem.setUnit("g");
                                }

                                else {
                                    pantryItem.setUnit("ml");
                                }
                            }

                            // update pantry item qty
                            pantryItem.setQuantity(newQty);

                            // add to update list
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

                    // update each modified item
                    for (PantryItem item : updatedItems) {
                        saveTasks.add(pantryRepository.save(item));
                    }

                    // wait for all updates to complete, then show result message
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