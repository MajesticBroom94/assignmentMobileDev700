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
import android.widget.Button;

import com.example.assignmentmobiledev.Adapters.PantryAdapter;
import com.example.assignmentmobiledev.Models.PantryItem;

import java.util.ArrayList;
import java.util.List;

public class PantryFragment extends Fragment {

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

        PantryAdapter adapter = new PantryAdapter(
                new PantryAdapter.OnIngredientActionListener() {
                    @Override
                    public void onEdit(PantryItem item) {
                        // Implement later
                    }

                    @Override
                    public void onDelete(PantryItem item) {
                        // Implement later
                    }
                }
        );

        recyclerView.setAdapter(adapter);

        List<PantryItem> sampleItems = new ArrayList<>();

        sampleItems.add(new PantryItem("Tomato", 5, "pcs", null));
        sampleItems.add(new PantryItem("Milk", 500, "ml", null));
        sampleItems.add(new PantryItem("Egg", 6, "pcs", null));

        adapter.setItems(sampleItems);


        // add ingredient button action
        Button addIngredientBtn = view.findViewById(R.id.btnAddIngredient);

        addIngredientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), AddEditIngredientActivity.class);
                startActivity(intent);
            }
        });
    }
}