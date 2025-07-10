package com.example.stockflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ItemDataActivity extends AppCompatActivity {

    private TextView itemName, itemQuantity, itemDescription;
    private int currentQuantity;
    private String itemId;
    private InventoryDatabaseHelper inventoryDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_data);

        inventoryDatabaseHelper = new InventoryDatabaseHelper(this);  // Initialize database helper

        // Back Button functionality
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());

        // Initialize views
        itemName = findViewById(R.id.item_name);
        itemQuantity = findViewById(R.id.item_quantity);
        itemDescription = findViewById(R.id.item_description);

        Button decrementButton = findViewById(R.id.decrement_button);
        Button incrementButton = findViewById(R.id.increment_button);
        Button deleteButton = findViewById(R.id.delete_button);

        // Get the data passed via the Intent
        Intent intent = getIntent();
        String name = intent.getStringExtra("item_name");
        int quantity = intent.getIntExtra("item_quantity", 0);
        String description = intent.getStringExtra("item_description");
        itemId = intent.getStringExtra("item_id");

        // Set the data in the views
        itemName.setText(name);
        currentQuantity = quantity;
        itemQuantity.setText("Quantity: " + currentQuantity);
        itemDescription.setText(description);

        // Decrement button functionality
        decrementButton.setOnClickListener(v -> {
            if (currentQuantity > 0) {
                currentQuantity--;
                itemQuantity.setText("Quantity: " + currentQuantity);
                updateItemQuantityInDatabase(); // Update database when quantity is changed
            } else {
                Toast.makeText(this, "Quantity cannot be less than 0", Toast.LENGTH_SHORT).show();
            }
        });

        // Increment button functionality
        incrementButton.setOnClickListener(v -> {
            currentQuantity++;
            itemQuantity.setText("Quantity: " + currentQuantity);
            updateItemQuantityInDatabase(); // Update database when quantity is changed
        });

        // Delete button functionality
        deleteButton.setOnClickListener(v -> {
            deleteItemFromDatabase(); // Delete the item from the database
        });
    }

    // Update the item quantity in the database
    private void updateItemQuantityInDatabase() {
        boolean isUpdated = inventoryDatabaseHelper.updateItemQuantity(itemId, currentQuantity);
        if (isUpdated) {
            Toast.makeText(this, "Quantity updated successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to update quantity", Toast.LENGTH_SHORT).show();
        }
    }

    // Delete the item from the database
    private void deleteItemFromDatabase() {
        boolean isDeleted = inventoryDatabaseHelper.deleteItem(itemId);
        if (isDeleted) {
            Toast.makeText(this, "Item deleted successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity and return to the inventory screen
        } else {
            Toast.makeText(this, "Failed to delete item", Toast.LENGTH_SHORT).show();
        }
    }
}
