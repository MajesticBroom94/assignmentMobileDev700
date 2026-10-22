package com.example.assignmentmobiledev;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

import com.example.assignmentmobiledev.Models.PantryItem;
import com.example.assignmentmobiledev.Repositories.PantryRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {

    private PantryRepository repository;
    private String editingItemId;
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

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            finish();
            return;
        }

        repository = new PantryRepository();

        // layout views
        editName = findViewById(R.id.editName);
        editQuantity = findViewById(R.id.editQuantity);
        editExpiry = findViewById(R.id.editExpiry);

        layoutName = findViewById(R.id.layoutName);
        layoutQuantity = findViewById(R.id.layoutQuantity);

        spinnerUnit = findViewById(R.id.spinnerUnit);

        Button btnSave = findViewById(R.id.btnSaveIngredient);

        // spinner dropdown values
        ArrayAdapter<CharSequence> unitAdapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.ingredient_units,
                        android.R.layout.simple_spinner_item
                );

        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerUnit.setAdapter(unitAdapter);


        // button actions
        editExpiry.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> validateForm());


        // Updating item already in pantry
        editingItemId = getIntent().getStringExtra("itemId");

        if (editingItemId != null) {

            TextView title = findViewById(R.id.txtFormTitle);
            title.setText("Edit Ingredient");

            editName.setText(getIntent().getStringExtra("name"));

            editQuantity.setText(String.valueOf(
                    getIntent().getDoubleExtra("quantity", 0)
            ));

            String unit = getIntent().getStringExtra("unit");

            ArrayAdapter adapter = (ArrayAdapter) spinnerUnit.getAdapter();

            int position = adapter.getPosition(unit);

            if (position >= 0) {
                spinnerUnit.setSelection(position);
            }

            if (getIntent().hasExtra("expiryDate")) {
                selectedExpiry.setTimeInMillis(
                        getIntent().getLongExtra("expiryDate", 0)
                );

                hasExpiry = true;

                editExpiry.setText(
                        new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                .format(selectedExpiry.getTime())
                );
            }
        }
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

        Date expiryDate = hasExpiry ? selectedExpiry.getTime() : null;

        // item to save instance
        PantryItem item = new PantryItem(name, quantity, unit, expiryDate);

        // attach id if updating
        if (editingItemId != null) {
            item.setId(editingItemId);
        }

        saveIngredient(item);
    }
    private void saveIngredient(PantryItem item) {

        Button btnSave = findViewById(R.id.btnSaveIngredient);
        btnSave.setEnabled(false);

        repository.save(item)
                .addOnSuccessListener(saved -> {
                    Toast.makeText(this,
                            "Ingredient saved",
                            Toast.LENGTH_SHORT).show();

                    finish();
                })
                .addOnFailureListener(e -> {
                    btnSave.setEnabled(true);

                    Toast.makeText(this,
                            "Unable to save ingredient",
                            Toast.LENGTH_LONG).show();
                });
    }
}
