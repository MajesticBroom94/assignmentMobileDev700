package com.example.assignmentmobiledev.Adapters;


import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignmentmobiledev.Models.PantryItem;
import com.example.assignmentmobiledev.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.color.MaterialColors;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    // Pass edit and delete button actions back to PantryFragment.
    public interface OnIngredientActionListener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final OnIngredientActionListener listener;

    // Initialize the adapter with a listener for edit and delete actions.
    public PantryAdapter(OnIngredientActionListener listener) {
        this.listener = listener;
    }

    // Replace the displayed pantry items with the latest data.
    public void setItems(List<PantryItem> updatedItems) {
        items.clear();
        items.addAll(updatedItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);

        return new PantryViewHolder(view);
    }

    // Display ingredient details and assign button click listeners.
    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {

        PantryItem item = items.get(position);

        holder.txtName.setText(item.getName());

        holder.txtQuantity.setText(item.getQuantity() + " " + item.getUnit());

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(item));

        holder.btnDelete.setOnClickListener(v -> listener.onDelete(item));

        // Read the expiry warning setting saved by SettingsFragment.
        SharedPreferences preferences = holder.itemView.getContext()
                .getSharedPreferences("app_settings", Context.MODE_PRIVATE);

        boolean warningsEnabled = preferences.getBoolean("expiry_warnings", true);

        // Highlight ingredients nearing expiry and reset the colour otherwise.
        if (warningsEnabled && isExpiringSoon(item)) {
            holder.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.grey_600)
            );
        } else {
            holder.cardView.setCardBackgroundColor(
                    MaterialColors.getColor(
                            holder.cardView,
                            com.google.android.material.R.attr.colorSurface
                    )
            );
        }
    }

    // Check whether an ingredient has expired or expires within seven days.
    private static boolean isExpiringSoon(PantryItem item) {
        Date expiryDate = item.getExpiryDate();

        if (expiryDate == null) {
            return false;
        }

        Calendar today = Calendar.getInstance();
        Calendar expiry = Calendar.getInstance();

        expiry.setTime(expiryDate);

        // Remove the time portion so expiry dates are compared by day.
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        expiry.set(Calendar.HOUR_OF_DAY, 0);
        expiry.set(Calendar.MINUTE, 0);
        expiry.set(Calendar.SECOND, 0);
        expiry.set(Calendar.MILLISECOND, 0);

        long daysRemaining = (expiry.getTimeInMillis()
                - today.getTimeInMillis()) / (24L * 60 * 60 * 1000);

        return daysRemaining <= 7;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Store references to the views used by each pantry item.
    static class PantryViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        TextView txtQuantity;
        ImageButton btnEdit;
        ImageButton btnDelete;
        MaterialCardView cardView;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtIngredientName);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            cardView = itemView.findViewById(R.id.cardPantryItem);
        }
    }
}
