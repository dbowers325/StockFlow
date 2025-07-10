package com.example.stockflow;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class InventoryDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_NAME = "inventory";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_IMAGE_URI = "image_uri";

    private final Context context; // Context to send SMS

    public InventoryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context; // Set context to access SmsManager for SMS notifications
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_QUANTITY + " INTEGER, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_IMAGE_URI + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            String dropTable = "DROP TABLE IF EXISTS " + TABLE_NAME;
            db.execSQL(dropTable);
            onCreate(db);
        }
    }

    // Insert new item
    public long insertItem(InventoryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_QUANTITY, item.getQuantity());
        values.put(COLUMN_DESCRIPTION, item.getDescription());
        values.put(COLUMN_IMAGE_URI, item.getImageUrl());
        return db.insert(TABLE_NAME, null, values);
    }

    // Update item quantity and send SMS notification if quantity is below 10
    public boolean updateItemQuantity(String itemId, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUANTITY, newQuantity);
        int rowsAffected = db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{itemId});

        if (rowsAffected > 0 && newQuantity < 10) {
            InventoryItem item = getItemById(itemId);  // Retrieve item details
            sendLowInventorySMS(item.getName(), newQuantity);  // Send SMS if quantity is below 10
        }

        return rowsAffected > 0;
    }

    // Delete item
    public boolean deleteItem(String itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{itemId});
        return rowsDeleted > 0;
    }

    // Retrieve all items
    public List<InventoryItem> getAllItems() {
        List<InventoryItem> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
                int quantityIndex = cursor.getColumnIndex(COLUMN_QUANTITY);
                int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
                int imageUriIndex = cursor.getColumnIndex(COLUMN_IMAGE_URI);

                // Ensure column indices are valid
                if (idIndex != -1 && nameIndex != -1 &&
                        quantityIndex != -1 && descriptionIndex != -1 &&
                        imageUriIndex != -1) {

                    String id = cursor.getString(idIndex);
                    String name = cursor.getString(nameIndex);
                    int quantity = cursor.getInt(quantityIndex);
                    String description = cursor.getString(descriptionIndex);
                    String imageUri = cursor.getString(imageUriIndex);

                    InventoryItem item = new InventoryItem(id, imageUri, name, quantity, description);
                    itemList.add(item);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return itemList;
    }

    // Search items by name
    public List<InventoryItem> searchItems(String query) {
        List<InventoryItem> filteredItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_NAME + " LIKE ?";
        String[] selectionArgs = {"%" + query + "%"};

        Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
                int quantityIndex = cursor.getColumnIndex(COLUMN_QUANTITY);
                int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
                int imageUriIndex = cursor.getColumnIndex(COLUMN_IMAGE_URI);

                if (idIndex != -1 && nameIndex != -1 && quantityIndex != -1 &&
                        descriptionIndex != -1 && imageUriIndex != -1) {
                    String id = cursor.getString(idIndex);
                    String name = cursor.getString(nameIndex);
                    int quantity = cursor.getInt(quantityIndex);
                    String description = cursor.getString(descriptionIndex);
                    String imageUri = cursor.getString(imageUriIndex);

                    InventoryItem item = new InventoryItem(id, imageUri, name, quantity, description);
                    filteredItems.add(item);
                } else {
                    Log.e("Database", "One or more columns not found in the cursor.");
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return filteredItems;
    }

    // Function to retrieve an item by its ID
    InventoryItem getItemById(String itemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{itemId});

        if (cursor.moveToFirst()) {
            // Ensure column indices are valid
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
            int quantityIndex = cursor.getColumnIndex(COLUMN_QUANTITY);
            int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
            int imageUriIndex = cursor.getColumnIndex(COLUMN_IMAGE_URI);

            // Check if the column indices are valid (≥ 0)
            if (idIndex >= 0 && nameIndex >= 0 && quantityIndex >= 0 && descriptionIndex >= 0 && imageUriIndex >= 0) {
                String id = cursor.getString(idIndex);
                String name = cursor.getString(nameIndex);
                int quantity = cursor.getInt(quantityIndex);
                String description = cursor.getString(descriptionIndex);
                String imageUri = cursor.getString(imageUriIndex);

                cursor.close();
                return new InventoryItem(id, imageUri, name, quantity, description);
            } else {
                // Log an error if any column index is invalid
                Log.e("Database", "Invalid column index found in the cursor.");
            }
        }

        // Close cursor if it's not null
        cursor.close();

        return null; // Return null if no item was found or columns are invalid
    }

    // Function to send SMS when inventory is low
    private void sendLowInventorySMS(String productName, int quantity) {
        String phoneNumber = "1234567890"; // Replace with actual phone number
        String message = "Low stock alert: Only " + quantity + " units left of " + productName;

        // Send SMS using SmsManager
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);

        // Show a confirmation toast
        Toast.makeText(context, "Low inventory SMS sent!", Toast.LENGTH_SHORT).show();
    }
}
