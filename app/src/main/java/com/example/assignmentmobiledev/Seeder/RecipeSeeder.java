package com.example.assignmentmobiledev.Seeder;

import android.util.Log;

import com.example.assignmentmobiledev.Models.Recipe;
import com.example.assignmentmobiledev.Models.RecipeIngredient;
import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;

import java.util.List;
import java.util.Locale;

public class RecipeSeeder {

    public static void seedRecipes() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Recipe> recipes = createRecipes();

        // comparing data with database
        for (int i = 0; i < recipes.size(); i++) {

            // recipe format: recipe001...
            String recipeId = String.format(Locale.ROOT, "recipe%03d", i + 1);

            // locate manually created recipe in list
            Recipe recipe = recipes.get(i);

            // set document id to retrieve recipe
            DocumentReference document = db.collection("recipes").document(recipeId);

            // check if recipe with id exists
            document.get().addOnSuccessListener(snapshot -> {

                        if (!snapshot.exists()) {
                            // add it if it doesn't exist
                            document.set(recipe)
                                    .addOnSuccessListener(unused ->
                                            Log.d("RecipeSeeder",
                                                    "Created: " + recipe.getName())
                                    )
                                    .addOnFailureListener(e ->
                                            Log.e("RecipeSeeder",
                                                    "Failed to save recipe", e)
                                    );
                        }
                    })
                    .addOnFailureListener(e ->
                            Log.e("RecipeSeeder",
                                    "Failed to check recipe", e)
                    );
        }
    }


    // data to seed
    private static List<Recipe> createRecipes() {

        List<Recipe> recipes = new ArrayList<>();

        // 1. Scrambled Eggs
        recipes.add(new Recipe("Scrambled Eggs",
                List.of(
                        new RecipeIngredient("Egg", 2, "pcs"),
                        new RecipeIngredient("Milk", 30, "ml"),
                        new RecipeIngredient("Butter", 10, "g")
                ),
                List.of(
                        "Crack the eggs into a bowl.",
                        "Add milk and whisk until combined.",
                        "Melt butter in a frying pan.",
                        "Add the egg mixture and stir gently until set."
                )));

        // 2. Cheese Sandwich
        recipes.add(new Recipe("Cheese Sandwich",
                List.of(
                        new RecipeIngredient("Bread", 2, "pcs"),
                        new RecipeIngredient("Cheese", 40, "g"),
                        new RecipeIngredient("Butter", 10, "g")
                ),
                List.of(
                        "Spread butter onto the bread.",
                        "Place cheese between the two slices.",
                        "Serve immediately or toast until golden."
                )));

        // 3. Turkish Pasta
        recipes.add(new Recipe("Turkish Pasta",
                List.of(
                        new RecipeIngredient("Pasta", 400, "g"),
                        new RecipeIngredient("Yoghurt", 150, "g"),
                        new RecipeIngredient("Garlic", 1, "pcs"),
                        new RecipeIngredient("Butter", 30, "g"),
                        new RecipeIngredient("Cayenne Pepper", 1, "g"),
                        new RecipeIngredient("Salt", 2, "g")
                ),
                List.of(
                        "Boil the pasta in salted water until al dente, then drain.",
                        "Peel and crush the garlic, then mix it into the yoghurt with a pinch of salt.",
                        "Melt the butter in a pan over medium heat.",
                        "Stir in the cayenne pepper and cook for 30 seconds, then remove from the heat.",
                        "Serve the pasta topped with the garlic yoghurt"
                )));

        // 4. Red Lentil Soup
        recipes.add(new Recipe("Red Lentil Soup",
                List.of(
                        new RecipeIngredient("Red Lentils", 100, "g"),
                        new RecipeIngredient("Onion", 1, "pcs"),
                        new RecipeIngredient("Carrot", 1, "pcs"),
                        new RecipeIngredient("Oil", 15, "ml"),
                        new RecipeIngredient("Salt", 2, "g")
                ),
                List.of(
                        "Peel and finely chop the onion and carrot.",
                        "Heat the oil in a pot and cook the onion and carrot until soft.",
                        "Rinse the lentils and add them to the pot.",
                        "Pour in about 600 ml of water and add the salt.",
                        "Simmer for 20 minutes until the lentils break down.",
                        "Blend until smooth if desired and serve hot."
                )));

        // 5. Mashed Potatoes
        recipes.add(new Recipe("Mashed Potatoes",
                List.of(
                        new RecipeIngredient("Potato", 3, "pcs"),
                        new RecipeIngredient("Milk", 100, "ml"),
                        new RecipeIngredient("Butter", 20, "g"),
                        new RecipeIngredient("Salt", 2, "g")
                ),
                List.of(
                        "Peel and chop the potatoes.",
                        "Boil until soft.",
                        "Drain the potatoes and mash them.",
                        "Add milk, butter and salt.",
                        "Mix until smooth."
                )));

        // 6. French Toast
        recipes.add(new Recipe("French Toast",
                List.of(
                        new RecipeIngredient("Bread", 2, "pcs"),
                        new RecipeIngredient("Egg", 1, "pcs"),
                        new RecipeIngredient("Milk", 50, "ml"),
                        new RecipeIngredient("Butter", 10, "g")
                ),
                List.of(
                        "Whisk the egg and milk together.",
                        "Dip both sides of the bread into the mixture.",
                        "Melt butter in a frying pan.",
                        "Cook each side until golden brown."
                )));

        // 7. Apple Cinnamon Oatmeal
        recipes.add(new Recipe("Apple Cinnamon Oatmeal",
                List.of(
                        new RecipeIngredient("Oats", 60, "g"),
                        new RecipeIngredient("Milk", 200, "ml"),
                        new RecipeIngredient("Apple", 1, "pcs"),
                        new RecipeIngredient("Cinnamon", 1, "g"),
                        new RecipeIngredient("Honey", 15, "g")
                ),
                List.of(
                        "Core the apple and cut it into small cubes.",
                        "Combine the oats and milk in a small pot.",
                        "Cook over medium heat, stirring, until thick and creamy.",
                        "Stir in the apple and cinnamon and cook for 2 more minutes.",
                        "Serve drizzled with honey."
                )));

        // 8. Egg Sandwich
        recipes.add(new Recipe("Egg Sandwich",
                List.of(
                        new RecipeIngredient("Bread", 2, "pcs"),
                        new RecipeIngredient("Egg", 2, "pcs"),
                        new RecipeIngredient("Butter", 10, "g")
                ),
                List.of(
                        "Boil the eggs until firm.",
                        "Peel and slice the eggs.",
                        "Spread butter onto the bread.",
                        "Place the sliced eggs between the bread.",
                        "Serve."
                )));

        // 9. Tomato and Cheese Toast
        recipes.add(new Recipe("Tomato and Cheese Toast",
                List.of(
                        new RecipeIngredient("Bread", 2, "pcs"),
                        new RecipeIngredient("Tomato", 1, "pcs"),
                        new RecipeIngredient("Cheese", 40, "g")
                ),
                List.of(
                        "Slice the tomato.",
                        "Place tomato and cheese on the bread.",
                        "Toast until the cheese melts.",
                        "Serve warm."
                )));

        // 10. Buttered Rice
        recipes.add(new Recipe("Buttered Rice",
                List.of(
                        new RecipeIngredient("Rice", 150, "g"),
                        new RecipeIngredient("Butter", 15, "g"),
                        new RecipeIngredient("Salt", 2, "g")
                ),
                List.of(
                        "Cook the rice in water until tender.",
                        "Drain any excess water if necessary.",
                        "Stir in the butter and salt.",
                        "Serve warm."
                )));

        // 11. Tomato Rice
        recipes.add(new Recipe("Tomato Rice",
                List.of(
                        new RecipeIngredient("Rice", 150, "g"),
                        new RecipeIngredient("Tomato", 2, "pcs"),
                        new RecipeIngredient("Oil", 15, "ml"),
                        new RecipeIngredient("Salt", 2, "g")
                ),
                List.of(
                        "Cook the rice until tender.",
                        "Chop the tomatoes.",
                        "Heat the oil and cook the tomatoes until soft.",
                        "Add the cooked rice and salt.",
                        "Stir together and serve."
                )));

        // 12. Cheese Omelette
        recipes.add(new Recipe("Cheese Omelette",
                List.of(
                        new RecipeIngredient("Egg", 2, "pcs"),
                        new RecipeIngredient("Cheese", 40, "g"),
                        new RecipeIngredient("Butter", 10, "g")
                ),
                List.of(
                        "Beat the eggs in a bowl.",
                        "Melt butter in a frying pan.",
                        "Pour in the eggs and cook gently.",
                        "Add cheese and fold the omelette.",
                        "Cook until the egg is set."
                )));

        // 13. Tomato Omelette
        recipes.add(new Recipe("Tomato Omelette",
                List.of(
                        new RecipeIngredient("Egg", 2, "pcs"),
                        new RecipeIngredient("Tomato", 1, "pcs"),
                        new RecipeIngredient("Oil", 10, "ml"),
                        new RecipeIngredient("Salt", 1, "g")
                ),
                List.of(
                        "Chop the tomato into small pieces.",
                        "Beat the eggs with the salt.",
                        "Heat oil in a frying pan.",
                        "Add the tomato and cook briefly.",
                        "Pour in the eggs and cook until set."
                )));

        // 14. Simple Pancakes
        recipes.add(new Recipe("Simple Pancakes",
                List.of(
                        new RecipeIngredient("Flour", 150, "g"),
                        new RecipeIngredient("Egg", 1, "pcs"),
                        new RecipeIngredient("Milk", 200, "ml"),
                        new RecipeIngredient("Oil", 15, "ml")
                ),
                List.of(
                        "Combine the flour, egg and milk.",
                        "Whisk until a smooth batter forms.",
                        "Heat a small amount of oil in a pan.",
                        "Pour in some batter and spread it thinly.",
                        "Cook both sides until golden.",
                        "Repeat with the remaining batter."
                )));

        // 15. Banana Pancakes
        recipes.add(new Recipe("Banana Pancakes",
                List.of(
                        new RecipeIngredient("Banana", 1, "pcs"),
                        new RecipeIngredient("Egg", 2, "pcs"),
                        new RecipeIngredient("Oil", 10, "ml")
                ),
                List.of(
                        "Mash the banana in a bowl.",
                        "Add the eggs and mix thoroughly.",
                        "Heat oil in a non-stick pan.",
                        "Pour small portions of batter into the pan.",
                        "Cook both sides until firm and golden."
                )));

        // 16. Banana Milkshake
        recipes.add(new Recipe("Banana Milkshake",
                List.of(
                        new RecipeIngredient("Banana", 1, "pcs"),
                        new RecipeIngredient("Milk", 150, "ml"),
                        new RecipeIngredient("Ice Cream", 300, "ml")
                ),
                List.of(
                        "Peel and chop the banana.",
                        "Place banana, milk and ice cream into a blender.",
                        "Blend until smooth and creamy.",
                        "Pour into a glass and serve."
                )));

        // 17. Fried Potatoes
        recipes.add(new Recipe("Fried Potatoes",
                List.of(
                        new RecipeIngredient("Potato", 3, "pcs"),
                        new RecipeIngredient("Oil", 30, "ml"),
                        new RecipeIngredient("Salt", 2, "g")
                ),
                List.of(
                        "Wash and cut the potatoes into small pieces.",
                        "Heat the oil in a frying pan.",
                        "Cook the potatoes until tender and golden.",
                        "Season with salt and serve."
                )));

        // 18. Garlic Bread
        recipes.add(new Recipe("Garlic Bread",
                List.of(
                        new RecipeIngredient("Bread", 2, "pcs"),
                        new RecipeIngredient("Garlic", 2, "pcs"),
                        new RecipeIngredient("Butter", 20, "g")
                ),
                List.of(
                        "Peel and finely chop the garlic.",
                        "Mix the garlic with softened butter.",
                        "Spread the garlic butter onto the bread.",
                        "Toast until golden."
                )));

        // 19. Cheese Pasta
        recipes.add(new Recipe("Cheese Pasta",
                List.of(
                        new RecipeIngredient("Pasta", 150, "g"),
                        new RecipeIngredient("Cheese", 60, "g"),
                        new RecipeIngredient("Milk", 100, "ml"),
                        new RecipeIngredient("Butter", 15, "g")
                ),
                List.of(
                        "Boil the pasta in water until tender.",
                        "Drain the pasta.",
                        "Melt butter in a saucepan.",
                        "Add milk and cheese, stirring until melted.",
                        "Combine the sauce with the pasta.",
                        "Serve warm."
                )));

        // 20. Tomato Pasta
        recipes.add(new Recipe("Tomato Pasta",
                List.of(
                        new RecipeIngredient("Pasta", 150, "g"),
                        new RecipeIngredient("Tomato", 3, "pcs"),
                        new RecipeIngredient("Oil", 15, "ml"),
                        new RecipeIngredient("Salt", 2, "g"),
                        new RecipeIngredient("Garlic", 1, "pcs")
                ),
                List.of(
                        "Boil the pasta until tender.",
                        "Chop the tomatoes and garlic.",
                        "Heat oil in a pan.",
                        "Cook the garlic and tomatoes until soft.",
                        "Add salt and the cooked pasta.",
                        "Mix thoroughly and serve."
                )));

        return recipes;
    }
}

