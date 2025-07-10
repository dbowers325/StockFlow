package com.example.stockflow;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private final List<InventoryItem> inventoryItems;
    private final List<InventoryItem> inventoryItemsFull; // This will store the original list
    private final Context context;

    public InventoryAdapter(List<InventoryItem> inventoryItems, Context context) {
        this.inventoryItems = inventoryItems;
        this.context = context;
        this.inventoryItemsFull = new ArrayList<>(inventoryItems);  // Create a copy of the original list
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.inventory_item_layout, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        InventoryItem item = inventoryItems.get(position);

        // Set up item image, name, quantity, and description
        holder.itemName.setText(item.getName());
        holder.itemQuantity.setText("Qty: " + item.getQuantity());
        holder.itemDescription.setText(item.getDescription());

        // Set up the "More Info" button
        holder.moreInfoButton.setOnClickListener(v -> {
            // Pass data to ItemDataActivity
            Intent intent = new Intent(context, ItemDataActivity.class);
            intent.putExtra("item_name", item.getName());
            intent.putExtra("item_quantity", item.getQuantity());
            intent.putExtra("item_description", item.getDescription());
            intent.putExtra("item_id", item.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return inventoryItems.size();
    }

    // Method to filter the inventory list based on search query
    public void filter(String query) {
        List<InventoryItem> filteredList = new ArrayList<>();

        // If query is empty, reset to original data
        if (query.isEmpty()) {
            filteredList.addAll(inventoryItemsFull);
        } else {
            // Loop through the list and add the matching items to the filtered list
            for (InventoryItem item : inventoryItemsFull) {
                if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }

        // Update the inventory items with the filtered list and notify the adapter
        inventoryItems.clear();
        inventoryItems.addAll(filteredList);
        notifyDataSetChanged();  // Notify the adapter that data has been updated
    }

    // ViewHolder class to hold the views
    public static class InventoryViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName, itemQuantity, itemDescription;
        Button moreInfoButton;

        public InventoryViewHolder(View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemName = itemView.findViewById(R.id.item_name);
            itemQuantity = itemView.findViewById(R.id.item_quantity);
            itemDescription = itemView.findViewById(R.id.item_description);
            moreInfoButton = itemView.findViewById(R.id.more_info_button);
        }
    }
}
