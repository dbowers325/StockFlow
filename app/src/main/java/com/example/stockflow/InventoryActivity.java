package com.example.stockflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.stockflow.InventoryDatabaseHelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InventoryActivity extends AppCompatActivity {

    private InventoryDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Show the back button
        }

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns in the grid

        // Initialize the database helper
        dbHelper = new InventoryDatabaseHelper(this);

        // Get all inventory items from the database
        List<InventoryItem> inventoryItems = dbHelper.getAllItems();

        if (inventoryItems.isEmpty()) {
            Toast.makeText(this, "No inventory items found", Toast.LENGTH_SHORT).show();
        }

        // Set the adapter for the RecyclerView
        InventoryAdapter adapter = new InventoryAdapter(inventoryItems, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item clicks
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Go back to the previous screen
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();  // Go back to the previous screen
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the inventory when the activity is resumed
        updateInventory();
    }

    // Method to update the RecyclerView when inventory data changes
    private void updateInventory() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        List<InventoryItem> inventoryItems = dbHelper.getAllItems();

        InventoryAdapter adapter = new InventoryAdapter(inventoryItems, this);
        recyclerView.setAdapter(adapter);
    }
}