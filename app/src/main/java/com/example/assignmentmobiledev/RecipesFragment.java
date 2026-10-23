package com.example.assignmentmobiledev;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignmentmobiledev.Adapters.RecipeAdapter;
import com.example.assignmentmobiledev.Models.PantryItem;
import com.example.assignmentmobiledev.Models.Recipe;
import com.example.assignmentmobiledev.Models.RecipeMatchResult;
import com.example.assignmentmobiledev.Repositories.PantryRepository;
import com.example.assignmentmobiledev.Repositories.RecipeRepository;
import com.example.assignmentmobiledev.Utils.RecipeMatcher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class RecipesFragment extends Fragment {

    private RecipeAdapter canMakeAdapter, almostThereAdapter;

    private ProgressBar progressRecipes;
    private View layoutRecipeResults;
    private TextView txtNoMatches, txtNoAlmostThere;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressRecipes = view.findViewById(R.id.progressRecipes);
        layoutRecipeResults = view.findViewById(R.id.layoutRecipeResults);
        txtNoMatches = view.findViewById(R.id.txtNoMatches);
        txtNoAlmostThere = view.findViewById(R.id.txtNoAlmostThere);

        // recycler views linked to layout
        // two recycler views on fragment
        RecyclerView recyclerCanMake = view.findViewById(R.id.recyclerCanMake);
        RecyclerView recyclerAlmostThere = view.findViewById(R.id.recyclerAlmostThere);

        recyclerCanMake.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerAlmostThere.setLayoutManager(new LinearLayoutManager(requireContext()));


        // Handle click/select actions through the adapter listener
        canMakeAdapter = new RecipeAdapter(new RecipeAdapter.OnRecipeClickListener() {
            @Override
            public void onRecipeClick(Recipe recipe) {
                openRecipe(recipe, true);
            }
        });

        almostThereAdapter = new RecipeAdapter(new RecipeAdapter.OnRecipeClickListener() {
            @Override
            public void onRecipeClick(Recipe recipe) {
                openRecipe(recipe, false);
            }
        });

        // set the adapters to the recycler views
        recyclerCanMake.setAdapter(canMakeAdapter);
        recyclerAlmostThere.setAdapter(almostThereAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Refresh results when returning to the Recipes screen.
        if (getView() != null) {
            loadRecipes();
        }
    }

    private void loadRecipes() {
        progressRecipes.setVisibility(View.VISIBLE);
        layoutRecipeResults.setVisibility(View.GONE);
        fetchRecipeData();
    }


    private void fetchRecipeData() {

        PantryRepository pantryRepository = new PantryRepository();
        RecipeRepository recipeRepository = new RecipeRepository();

        // retrieve all pantry items
        pantryRepository.getAll()
                .addOnSuccessListener(pantrySnapshot -> {

                    if (getView() == null) return;

                    // store all pantry items
                    List<PantryItem> pantryItems = new ArrayList<>();

                    for (DocumentSnapshot document : pantrySnapshot.getDocuments()) {

                        // convert document to pantry item
                        PantryItem item = document.toObject(PantryItem.class);

                        // attach the document id to the item
                        if (item != null) {
                            item.setId(document.getId());
                            pantryItems.add(item);
                        }
                    }

                    // retrieve all recipe items
                    recipeRepository.getAll()
                            .addOnSuccessListener(recipeSnapshot -> {

                                if (getView() == null) return;

                                // store all recipe items
                                List<Recipe> recipes = new ArrayList<>();

                                for (DocumentSnapshot document : recipeSnapshot.getDocuments()) {

                                    Recipe recipe = document.toObject(Recipe.class);

                                    if (recipe != null) {
                                        recipe.setId(document.getId());
                                        recipes.add(recipe);
                                    }
                                }

                                // Recipe matcher will return two lists for recipes that:
                                // 1 - Matched exactly
                                // 2 - Has exactly one item missing to make recipe
                                RecipeMatchResult result =
                                        RecipeMatcher.getMatchingRecipes(pantryItems, recipes);

                                // Set the adapter data to inflate the recycler views
                                canMakeAdapter.setRecipes(result.getCanMake());
                                almostThereAdapter.setRecipes(result.getAlmostThere());

                                // Set the visibility of the text views and progress bar
                                txtNoMatches.setVisibility(
                                        result.getCanMake().isEmpty() ?
                                        View.VISIBLE : View.GONE
                                );

                                txtNoAlmostThere.setVisibility(
                                        result.getAlmostThere().isEmpty()
                                                ? View.VISIBLE : View.GONE
                                );

                                progressRecipes.setVisibility(View.GONE);
                                layoutRecipeResults.setVisibility(View.VISIBLE);
                            })
                            .addOnFailureListener(e -> showError());
                })
                .addOnFailureListener(e -> showError());
    }

    // database error messages
    private void showError() {
        if (getView() == null) return;

        progressRecipes.setVisibility(View.GONE);

        Toast.makeText(requireContext(), "Unable to load recipes", Toast.LENGTH_LONG).show();
    }

    // intent to open recipe detail screen
    private void openRecipe(Recipe recipe, boolean canMake) {

        Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);

        intent.putExtra("recipeId", recipe.getId());
        intent.putExtra("canMake", canMake);

        startActivity(intent);
    }
}