package com.example.stockflow;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.stockflow.InventoryDatabaseHelper;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;
import java.util.Objects;

public class AddInventoryActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText itemNameEditText, itemQuantityEditText, itemDescriptionEditText;
    private ImageView itemImageView;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_inventory);

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Initialize the views
        itemNameEditText = findViewById(R.id.item_name);
        itemQuantityEditText = findViewById(R.id.item_quantity);
        itemDescriptionEditText = findViewById(R.id.item_description);
        itemImageView = findViewById(R.id.item_image);
        Button selectImageButton = findViewById(R.id.select_image_button);
        Button addButton = findViewById(R.id.add_button);

        // Set up the image selection button
        selectImageButton.setOnClickListener(v -> openFileChooser());

        // Set up the add button to save the new item
        addButton.setOnClickListener(v -> addNewItem());
    }

    // Open file chooser to select an image
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle the back button press (on toolbar)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Go back to the previous screen
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Handle the result of the image selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            // Display the selected image in the ImageView
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                itemImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Add new item to the inventory
    private void addNewItem() {
        // Get the values from the EditText fields
        String itemName = itemNameEditText.getText().toString().trim();
        String itemQuantityString = itemQuantityEditText.getText().toString().trim();
        String itemDescription = itemDescriptionEditText.getText().toString().trim();

        // Validate input fields
        if (itemName.isEmpty() || itemQuantityString.isEmpty() || itemDescription.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int itemQuantity = Integer.parseInt(itemQuantityString);

        // Check if the image URI is null and set a default if necessary
        String imageUriString = null;
        if (selectedImageUri != null) {
            imageUriString = selectedImageUri.toString();
        } else {
            imageUriString = "default_image_uri";  // Default image URI
        }

        // Create an InventoryItem object
        InventoryItem newItem = new InventoryItem(imageUriString, itemName, itemQuantity, itemDescription);


        try (InventoryDatabaseHelper dbHelper = new InventoryDatabaseHelper(this)) {
            long result = dbHelper.insertItem(newItem);

            if (result > 0) {
                Toast.makeText(this, "Item Added: " + itemName, Toast.LENGTH_SHORT).show();
                finish();  // Close the AddInventoryActivity
            } else {
                Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // Handle any exceptions during database operation
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}
