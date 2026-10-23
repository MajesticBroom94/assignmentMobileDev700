package com.example.assignmentmobiledev.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignmentmobiledev.Models.Recipe;
import com.example.assignmentmobiledev.R;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    // Pass edit and delete button actions back to fragment.
    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private final List<Recipe> recipes = new ArrayList<>();
    private final OnRecipeClickListener listener;

    // Initialize the adapter with a listener for edit and delete actions.
    public RecipeAdapter(OnRecipeClickListener listener) {
        this.listener = listener;
    }

    // Replace the displayed pantry items with the latest data.
    public void setRecipes(List<Recipe> updatedRecipes) {
        recipes.clear();
        recipes.addAll(updatedRecipes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);

        return new RecipeViewHolder(view);
    }

    // Display ingredient details and assign button click listener.
    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {

        Recipe recipe = recipes.get(position);

        holder.txtName.setText(recipe.getName());

        int count = recipe.getIngredients().size();

        holder.txtIngredients.setText(
                "Consists of " + count + (count == 1 ? " ingredient" : " ingredients")
        );

        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    // Store references to the views used by each pantry item.
    static class RecipeViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        TextView txtIngredients;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtRecipeName);
            txtIngredients = itemView.findViewById(R.id.txtRecipeIngredients);
        }
    }
}
