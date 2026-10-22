package com.example.assignmentmobiledev;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.assignmentmobiledev.Adapters.PantryAdapter;
import com.example.assignmentmobiledev.Models.PantryItem;
import com.example.assignmentmobiledev.Repositories.PantryRepository;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PantryFragment extends Fragment {

    private PantryAdapter adapter;
    private PantryRepository repository;
    private ListenerRegistration pantryListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pantry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerPantry);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new PantryAdapter(
                new PantryAdapter.OnIngredientActionListener() {
                    @Override
                    public void onEdit(PantryItem item) {

                        Intent intent = new Intent(
                                requireContext(), AddEditIngredientActivity.class
                        );

                        intent.putExtra("itemId", item.getId());
                        intent.putExtra("name", item.getName());
                        intent.putExtra("quantity", item.getQuantity());
                        intent.putExtra("unit", item.getUnit());

                        if (item.getExpiryDate() != null) {
                            intent.putExtra("expiryDate", item.getExpiryDate().getTime());
                        }

                        startActivity(intent);
                    }

                    @Override
                    public void onDelete(PantryItem item) {

                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Delete Ingredient")
                                .setMessage("Are you sure you want to delete "
                                        + item.getName() + "?")
                                .setNegativeButton("Cancel", null)
                                .setPositiveButton("Delete", (dialog, which) -> {

                                    if (repository == null) {
                                        return;
                                    }

                                    repository.delete(item.getId())
                                            .addOnSuccessListener(unused ->
                                                    Toast.makeText(requireContext(),
                                                            "Ingredient deleted",
                                                            Toast.LENGTH_SHORT).show()
                                            )
                                            .addOnFailureListener(e ->
                                                    Toast.makeText(requireContext(),
                                                            "Unable to delete ingredient",
                                                            Toast.LENGTH_LONG).show()
                                            );
                                })
                                .show();
                    }
                }
        );

        recyclerView.setAdapter(adapter);

        // add ingredient button action
        Button addIngredientBtn = view.findViewById(R.id.btnAddIngredient);

        addIngredientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), AddEditIngredientActivity.class);
                startActivity(intent);
            }
        });

        // Authenticate before loading pantry data
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            loadPantry();
        } else {
            auth.signInAnonymously()
                    .addOnSuccessListener(result -> loadPantry())
                    .addOnFailureListener(e ->
                            Toast.makeText(requireContext(),
                                    "Authentication failed",
                                    Toast.LENGTH_LONG).show()
                    );
        }
    }

    @Override
    public void onDestroyView() {
        if (pantryListener != null) {
            pantryListener.remove();
            pantryListener = null;
        }

        super.onDestroyView();
    }

    private void loadPantry() {

        repository = new PantryRepository();

        pantryListener = repository.listen(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Pantry", "Error loading items", error);
                    return;
                }

                if (snapshots == null) {
                    return;
                }

                List<PantryItem> items = new ArrayList<>();

                for (DocumentSnapshot document : snapshots.getDocuments()) {
                    // Convert document to PantryItem
                    PantryItem item = document.toObject(PantryItem.class);

                    if (item != null) {
                        item.setId(document.getId());
                        items.add(item);
                    }
                }

                // Update adapter with new items
                adapter.setItems(items);
            }
        });
    }
}