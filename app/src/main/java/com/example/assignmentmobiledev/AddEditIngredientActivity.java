package com.example.assignmentmobiledev;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {

    private TextInputEditText editName, editQuantity, editExpiry;
    private TextInputLayout layoutName, layoutQuantity;
    private Spinner spinnerUnit;

    private final Calendar selectedExpiry = Calendar.getInstance();
    private boolean hasExpiry = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_ingredient);

        editName = findViewById(R.id.editName);
        editQuantity = findViewById(R.id.editQuantity);
        editExpiry = findViewById(R.id.editExpiry);

        layoutName = findViewById(R.id.layoutName);
        layoutQuantity = findViewById(R.id.layoutQuantity);

        spinnerUnit = findViewById(R.id.spinnerUnit);

        Button btnSave = findViewById(R.id.btnSaveIngredient);

        ArrayAdapter<CharSequence> unitAdapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.ingredient_units,
                        android.R.layout.simple_spinner_item
                );

        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerUnit.setAdapter(unitAdapter);

        editExpiry.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> validateForm());
    }

    private void showDatePicker() {
        new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    selectedExpiry.set(year, month, day, 0, 0, 0);
                    selectedExpiry.set(Calendar.MILLISECOND, 0);
                    hasExpiry = true;

                    SimpleDateFormat format =
                            new SimpleDateFormat(
                                    "dd/MM/yyyy",
                                    Locale.getDefault()
                            );

                    editExpiry.setText(format.format(selectedExpiry.getTime()));
                },
                selectedExpiry.get(Calendar.YEAR),
                selectedExpiry.get(Calendar.MONTH),
                selectedExpiry.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void validateForm() {
        String name = String.valueOf(editName.getText()).trim();
        String quantityText = String.valueOf(editQuantity.getText()).trim();

        layoutName.setError(null);
        layoutQuantity.setError(null);

        if (name.isEmpty()) {
            layoutName.setError("Ingredient name is required");
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        }
        catch (NumberFormatException e) {
            layoutQuantity.setError("Enter a valid quantity");
            return;
        }

        if (!Double.isFinite(quantity) || quantity <= 0) {
            layoutQuantity.setError("Quantity must be greater than zero");
            return;
        }

        String unit = spinnerUnit.getSelectedItem().toString();

        // Form is valid.
        // Firestore save/update will be implemented next.
    }



}
