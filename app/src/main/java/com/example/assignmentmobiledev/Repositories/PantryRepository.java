package com.example.assignmentmobiledev.Repositories;

import com.example.assignmentmobiledev.Models.PantryItem;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

public class PantryRepository {

    private final CollectionReference pantry;

    public PantryRepository() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        pantry = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("pantryItems");
    }

    public Task<Void> save(PantryItem item) {
        DocumentReference document;

        if (item.getId() == null || item.getId().isEmpty()) {
            document = pantry.document();
        }
        else {
            document = pantry.document(item.getId());
        }

        item.setId(document.getId());
        return document.set(item);
    }

    public Task<Void> delete(String itemId) {
        return pantry.document(itemId).delete();
    }

    public Task<QuerySnapshot> findByName(String normalizedName) {
        return pantry.whereEqualTo("normalizedName", normalizedName).get();
    }

    public Task<QuerySnapshot> getAll() {
        return pantry.get();
    }

    public ListenerRegistration listen(EventListener<QuerySnapshot> listener) {
        return pantry.addSnapshotListener(listener);
    }
}
