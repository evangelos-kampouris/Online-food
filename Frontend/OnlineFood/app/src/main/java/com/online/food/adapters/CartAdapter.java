package com.online.food.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.online.food.R;

import Inventory.InventoryCart;
import Inventory.InventoryItem;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<InventoryItem> cartItems;
    private OnCartItemRemovedListener listener;

    public interface OnCartItemRemovedListener {
        void onCartItemRemoved(String productName);
    }

    public CartAdapter(InventoryCart cart, OnCartItemRemovedListener listener) {
        this.listener = listener;
        this.cartItems = new ArrayList<>();
        
        // Extract cart items from InventoryCart
        if (cart != null && cart.getInventory() != null) {
            cartItems.addAll(cart.getInventory().values());
        }
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        InventoryItem cartItem = cartItems.get(position);
        holder.bind(cartItem);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }
    
    public float getCartTotal() {
        float total = 0;
        for (InventoryItem item : cartItems) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        return total;
    }
    
    public void removeItem(int position) {
        if (position >= 0 && position < cartItems.size()) {
            InventoryItem removedItem = cartItems.remove(position);
            notifyItemRemoved(position);
            
            if (listener != null) {
                listener.onCartItemRemoved(removedItem.getProduct().getName());
            }
        }
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewCartProductName;
        private final TextView textViewCartQuantity;
        private final TextView textViewCartPrice;
        private final ImageButton imageButtonRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCartProductName = itemView.findViewById(R.id.textViewCartProductName);
            textViewCartQuantity = itemView.findViewById(R.id.textViewCartQuantity);
            textViewCartPrice = itemView.findViewById(R.id.textViewCartPrice);
            imageButtonRemove = itemView.findViewById(R.id.imageButtonRemove);
        }

        public void bind(InventoryItem cartItem) {
            textViewCartProductName.setText(cartItem.getProduct().getName());
            textViewCartQuantity.setText(String.format("x%d", cartItem.getQuantity()));
            float itemCost = cartItem.getProduct().getPrice() * cartItem.getQuantity();
            textViewCartPrice.setText(String.format("$%.2f", itemCost));
            
            imageButtonRemove.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    removeItem(position);
                }
            });
        }
    }
} 