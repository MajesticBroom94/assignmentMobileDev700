package com.example.assignmentmobiledev;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.materialswitch.MaterialSwitch;


public class SettingsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        // toggle
        MaterialSwitch switchWarnings = view.findViewById(R.id.switchExpiryWarnings);

        // Initialize shared preferences
        SharedPreferences preferences = requireContext()
                .getSharedPreferences("app_settings", Context.MODE_PRIVATE);

        // Set switch state based on shared preferences
        switchWarnings.setChecked(
                preferences.getBoolean("expiry_warnings", true)
        );

        // Save current switch state to shared preferences
        switchWarnings.setOnCheckedChangeListener((button, isChecked) -> {
            preferences.edit()
                    .putBoolean("expiry_warnings", isChecked)
                    .apply();
        });
    }
}