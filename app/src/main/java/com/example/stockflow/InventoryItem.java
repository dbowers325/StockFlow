package com.example.stockflow;

public class InventoryItem {

    private String id;
    private final String imageUrl;
    private final String name;
    private final int quantity;
    private final String description;

    // Constructor for creating new inventory items (without an ID)
    public InventoryItem(String imageUrl, String name, int quantity, String description) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.quantity = quantity;
        this.description = description;
    }

    // Constructor for retrieving inventory items from the database (with an ID)
    public InventoryItem(String id, String imageUrl, String name, int quantity, String description) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.quantity = quantity;
        this.description = description;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDescription() {
        return description;
    }
}
