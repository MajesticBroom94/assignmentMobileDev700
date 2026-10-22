package com.example.assignmentmobiledev.Repositories;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class RecipeRepository {

    private final CollectionReference recipes;

    public RecipeRepository() {
        recipes = FirebaseFirestore.getInstance()
                .collection("recipes");
    }

    public Task<QuerySnapshot> getAll() {
        return recipes.get();
    }

    public Task<DocumentSnapshot> getById(String recipeId) {
        return recipes.document(recipeId).get();
    }

}
