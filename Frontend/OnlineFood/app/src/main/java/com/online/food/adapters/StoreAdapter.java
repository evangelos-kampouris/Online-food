package com.online.food.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.online.food.R;
import com.online.food.utils.StoreImageManager;

import other.Shop;
import other.Price;

import java.util.List;

/**
 * RecyclerView adapter for displaying a list of food delivery stores.
 *
 */
public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreViewHolder> {

    /** Cross-fade animation duration for image transitions */
    private static final int IMAGE_CROSSFADE_DURATION_MS = 300;
    private List<Shop> stores; //Λίστα καταστημάτων
    private final OnStoreClickListener listener;

    /**
     * Interface for handling store item click events.
     */
    public interface OnStoreClickListener {
        void onStoreClick(Shop store);
    }

    /**
     * Creates a new StoreAdapter with the provided data and listener.
     * 
     * @param stores list of shops to display
     * @param listener callback for handling store clicks
     */
    public StoreAdapter(List<Shop> stores, OnStoreClickListener listener) {
        this.stores = stores;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_store, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        Shop store = stores.get(position);
        holder.bind(store); //Σύνδεση δεδομένων καταστήματος με UI components
    }

    @Override
    public int getItemCount() {
        return stores != null ? stores.size() : 0;
    }

    
    /**
     * ViewHolder class for store list items.
     * Handles the binding of shop data to UI components.
     */
    class StoreViewHolder extends RecyclerView.ViewHolder {
        
        // UI Components
        private final ImageView imageViewStoreLogo;
        private final TextView textViewStoreName;
        private final TextView textViewStoreCategory;
        private final RatingBar ratingBarStore;
        private final TextView textViewStorePrice;

        /**
         * Creates a new StoreViewHolder and sets up UI components.
         * 
         * @param itemView the view for this holder
         */
        public StoreViewHolder(@NonNull View itemView) {
            super(itemView);
            
            // Initialize UI components
            imageViewStoreLogo = itemView.findViewById(R.id.imageViewStoreLogo);
            textViewStoreName = itemView.findViewById(R.id.textViewStoreName);
            textViewStoreCategory = itemView.findViewById(R.id.textViewStoreCategory);
            ratingBarStore = itemView.findViewById(R.id.ratingBarStore);
            textViewStorePrice = itemView.findViewById(R.id.textViewStorePrice);

            // Set up click listener
            setupClickListener();
        }

        public void bind(Shop store) {
            if (store == null) {
                return;
            }
            
            bindBasicInfo(store);
            bindRating(store);
            bindPriceCategory(store);
            bindStoreImage(store);
        }

        private void setupClickListener() {
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null && stores != null) {
                    listener.onStoreClick(stores.get(position));
                }
            });
        }

        private void bindBasicInfo(Shop store) {
            // Set store name
            String storeName = store.getName();
            textViewStoreName.setText(storeName != null ? storeName : itemView.getContext().getString(R.string.unknown_store));
            
            //Set store category
            if (store.getStoreCategory() != null) {
                textViewStoreCategory.setText(store.getStoreCategory().getName());
            } else {
                textViewStoreCategory.setText(itemView.getContext().getString(R.string.restaurant));
            }
        }
        
        /**
         * Binds store rating information.
         * 
         * @param store the shop to bind
         */
        private void bindRating(Shop store) {
            if (store.getRating() != null) {
                ratingBarStore.setRating(store.getRating().getValue());  //Set rate
                ratingBarStore.setVisibility(View.VISIBLE);
            } else {
                ratingBarStore.setVisibility(View.GONE);
            }
        }
        
        /**
         * Binds store price category display.
         * 
         * @param store the shop to bind
         */
        private void bindPriceCategory(Shop store) {
            String priceSymbol = getPriceSymbol(store.getPrice());
            textViewStorePrice.setText(priceSymbol);
        }
        
        /**
         * Binds store image using Glide with proper error handling.
         * 
         * @param store the shop to bind
         */
        private void bindStoreImage(Shop store) {
            String imageUrl = StoreImageManager.getStoreImageUrl(store.getName());
            
            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .apply(createImageRequestOptions())
                    .transition(DrawableTransitionOptions.withCrossFade(IMAGE_CROSSFADE_DURATION_MS))
                    .into(imageViewStoreLogo);
        }

        
        /**
         * Converts price enum to display symbol.
         * 
         * @param price the price category
         * @return price symbol string ($, $$, $$$)
         */
        private String getPriceSymbol(Price price) {
            if (price == null) {
                return itemView.getContext().getString(R.string.price_symbol_low); // Default fallback
            }
            
            switch (price) {
                case LOW:
                    return itemView.getContext().getString(R.string.price_symbol_low);
                case MEDIUM:
                    return itemView.getContext().getString(R.string.price_symbol_medium);
                case HIGH:
                    return itemView.getContext().getString(R.string.price_symbol_high);
                default:
                    return itemView.getContext().getString(R.string.price_symbol_low);
            }
        }
        
        /**
         * Creates RequestOptions for Glide image loading.
         * 
         * @return configured RequestOptions
         */
        private RequestOptions createImageRequestOptions() {
            return new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.ic_store_placeholder) // Loading placeholder
                    .error(R.drawable.ic_store_placeholder); // Error fallback
        }
    }
} 