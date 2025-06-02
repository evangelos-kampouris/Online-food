package com.online.food.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.online.food.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import other.Product;
import other.Shop;
import Inventory.InventoryItem;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> products;
    private Map<String, Integer> productQuantities;
    private Context context;
    private Shop currentShop;  // Reference to shop for stock information

    public ProductAdapter(List<Product> products, Context context, Shop shop) {
        this.products = products;
        this.productQuantities = new HashMap<>();
        this.context = context;
        this.currentShop = shop;
        
        // Initialize quantities to 0
        for (Product product : products) {
            productQuantities.put(product.getName(), 0);
        }
    }

    @NonNull
    @Override //used by the recycler
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override //used by the recycler
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        int quantity = productQuantities.getOrDefault(product.getName(), 0);
        holder.bind(product, quantity);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public Map<String, Integer> getProductQuantities() {
        // Return only products with quantities > 0
        Map<String, Integer> result = new HashMap<>();
        for (Map.Entry<String, Integer> entry : productQuantities.entrySet()) {
            if (entry.getValue() > 0) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    /**
     * Resets all product quantities to 0 and refreshes the UI
     */
    public void resetQuantities() {
        for (String productName : productQuantities.keySet()) {
            productQuantities.put(productName, 0);
        }
        notifyDataSetChanged();
    }

    /**
     * Updates the shop reference to get latest stock information
     */
    public void updateShop(Shop shop) {
        this.currentShop = shop;
        notifyDataSetChanged(); // Refresh to show updated stock
    }

    /**
     * Gets the available stock for a product from the shop's inventory
     */
    private int getAvailableStock(String productName) {
        if (currentShop == null || currentShop.getCatalog() == null) {
            return 0;
        }
        
        Map<String, InventoryItem> inventory = currentShop.getCatalog().getInventory();
        if (inventory.containsKey(productName)) {
            return inventory.get(productName).getQuantity();
        }
        
        return 0;
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewProductName;
        private final TextView textViewProductType;
        private final TextView textViewProductPrice;
        private final TextView textViewAvailableAmount;
        private final TextView textViewQuantity;
        private final Button buttonDecrease;
        private final Button buttonIncrease;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewProductType = itemView.findViewById(R.id.textViewProductType);
            textViewProductPrice = itemView.findViewById(R.id.textViewProductPrice);
            textViewAvailableAmount = itemView.findViewById(R.id.textViewAvailableAmount);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            buttonDecrease = itemView.findViewById(R.id.buttonDecrease);
            buttonIncrease = itemView.findViewById(R.id.buttonIncrease);
        }

        public void bind(Product product, int quantity) {
            textViewProductName.setText(product.getName());
            
            // Use correct Java implementation methods
            if (product.getFoodCategory() != null) {
                textViewProductType.setText(product.getFoodCategory().getName());
            }
            
            textViewProductPrice.setText(String.format("$%.2f", product.getPrice()));
            
            // Display actual stock quantity
            int availableStock = getAvailableStock(product.getName());
            if (availableStock <= 0) {
                textViewAvailableAmount.setText(context.getString(R.string.out_of_stock));
                textViewAvailableAmount.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
            } else {
                textViewAvailableAmount.setText(context.getString(R.string.stock_available, availableStock));
                textViewAvailableAmount.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            }
            
            textViewQuantity.setText(String.valueOf(quantity));
            
            // Update button states based on quantity and stock
            buttonDecrease.setEnabled(quantity > 0);
            buttonIncrease.setEnabled(availableStock > 0 && quantity < availableStock);
            
            // Set click listeners for quantity adjustment
            buttonDecrease.setOnClickListener(v -> {
                int currentQuantity = productQuantities.get(product.getName());
                if (currentQuantity > 0) {
                    currentQuantity--;
                    productQuantities.put(product.getName(), currentQuantity);
                    textViewQuantity.setText(String.valueOf(currentQuantity));
                    buttonDecrease.setEnabled(currentQuantity > 0);
                    buttonIncrease.setEnabled(availableStock > 0 && currentQuantity < availableStock);
                }
            });
            
            buttonIncrease.setOnClickListener(v -> {
                int currentQuantity = productQuantities.get(product.getName());
                int stock = getAvailableStock(product.getName());
                if (stock > 0 && currentQuantity < stock) {
                    currentQuantity++;
                    productQuantities.put(product.getName(), currentQuantity);
                    textViewQuantity.setText(String.valueOf(currentQuantity));
                    buttonDecrease.setEnabled(true);
                    buttonIncrease.setEnabled(currentQuantity < stock);
                }
            });
        }
    }
} 