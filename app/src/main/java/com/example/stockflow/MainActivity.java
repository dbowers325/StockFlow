package com.example.stockflow;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText searchInventoryBar;
    private InventoryDatabaseHelper inventoryDatabaseHelper;

    private static final int SMS_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inventoryDatabaseHelper = new InventoryDatabaseHelper(this);

        // Initialize the search bar and button
        searchInventoryBar = findViewById(R.id.searchInventoryBar);
        Button searchButton = findViewById(R.id.searchButton);

        // Search button click listener
        searchButton.setOnClickListener(v -> {
            String searchQuery = searchInventoryBar.getText().toString().trim();
            if (!searchQuery.isEmpty()) {
                List<InventoryItem> searchResults = inventoryDatabaseHelper.searchItems(searchQuery);

                if (!searchResults.isEmpty()) {
                    // If search results found, navigate to the first item in the list
                    InventoryItem item = searchResults.get(0);
                    Intent intent = new Intent(MainActivity.this, ItemDataActivity.class);
                    intent.putExtra("item_name", item.getName());
                    intent.putExtra("item_quantity", item.getQuantity());
                    intent.putExtra("item_description", item.getDescription());
                    intent.putExtra("item_id", item.getId());
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "No items found matching your search.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Please enter a search term.", Toast.LENGTH_SHORT).show();
            }
        });

        // View inventory button
        Button viewInventoryButton = findViewById(R.id.view_inventory_button);
        viewInventoryButton.setOnClickListener(v -> {
            // Start the InventoryActivity
            Intent intent = new Intent(MainActivity.this, InventoryActivity.class);
            startActivity(intent);
        });

        // Add inventory button
        Button addInventoryButton = findViewById(R.id.addInventoryButton);
        addInventoryButton.setOnClickListener(v -> {
            // Start the AddInventoryActivity
            Intent intent = new Intent(MainActivity.this, AddInventoryActivity.class);
            startActivity(intent);
        });

        // Check if SMS permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
        }
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with app functionality
            } else {
                // Permission denied, show a message
                Toast.makeText(this, "Permission denied, cannot send SMS notifications.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Function to send low inventory SMS notifications
    private void sendLowInventorySMS(String productName, int quantity) {
        String phoneNumber = "1234567890"; // Replace with actual phone number
        String message = "Low stock alert: Only " + quantity + " units left of " + productName;

        // Send SMS using SmsManager
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);

        // Show a confirmation toast
        Toast.makeText(this, "Low inventory SMS sent!", Toast.LENGTH_SHORT).show();
    }

    // Function to update item and send SMS if necessary
    public void updateInventoryQuantity(String itemId, int newQuantity) {
        // Update the item quantity in the database
        boolean updated = inventoryDatabaseHelper.updateItemQuantity(itemId, newQuantity);

        // Check if the update was successful and the quantity is below 10
        if (updated && newQuantity < 10) {
            // Get the item details
            InventoryItem item = inventoryDatabaseHelper.getItemById(itemId);
            if (item != null) {
                // Send low inventory SMS
                sendLowInventorySMS(item.getName(), newQuantity);
            }
        }
    }
}
