package com.example.assignmentmobiledev;

import android.os.Bundle;

import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.assignmentmobiledev.Seeder.RecipeSeeder;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // layout top bar padding
       View root = findViewById(R.id.mainActivityRoot);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(
                    v.getPaddingLeft(),
                    bars.top,
                    v.getPaddingRight(),
                    v.getPaddingBottom()
            );

            return insets;
        });

        // firebase instance
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // user is signed in
        if (auth.getCurrentUser() != null) {
            initializeApp(savedInstanceState);
        }

        // sign in the user, then run the app
        else {
            auth.signInAnonymously()
                    .addOnSuccessListener(result -> {
                        initializeApp(savedInstanceState);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Authentication failed", Toast.LENGTH_LONG).show();
                    });
        }
    }


    private void initializeApp(Bundle savedInstanceState) {

        // seeds recipes (if needed)
        RecipeSeeder.seedRecipes();

        // bottom navigation menu
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        // fragment navigation on button press
        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_pantry) {
                showFragment(new PantryFragment());
                return true;
            }

            if (id == R.id.nav_recipes) {
                showFragment(new RecipesFragment());
                return true;
            }

            if (id == R.id.nav_settings) {
                showFragment(new SettingsFragment());
                return true;
            }

            return false;
        });

        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_pantry);
        }
    }

    // fragment display helper
    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }
}