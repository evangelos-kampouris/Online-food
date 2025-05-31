package com.online.food.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.online.food.R;
import com.online.food.adapters.ProductAdapter;
import com.online.food.network.NetworkManager;
import com.online.food.utils.StoreImageManager;

import other.Shop;
import other.Product;
import Inventory.InventoryCart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StoreDetailActivity extends AppCompatActivity {

    private ImageView imageViewStoreLogo;
    private TextView textViewCategory;
    private TextView textViewPrice;
    private RatingBar ratingBarStore;
    private TextView textViewRating;
    private RecyclerView recyclerViewProducts;
    private FloatingActionButton fabViewCart;
    private Button buttonRateStore;
    
    private Shop currentStore;  //Το τρέχον κατάστημα που εμφανίζεται
    private ProductAdapter productAdapter;
    private List<Product> products = new ArrayList<>(); //Λίστα προϊόντων του καταστήματος
    
    private ExecutorService executorService;
    private Handler mainHandler;

    @SuppressLint("StringFormatMatches")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);
        
        // Initialize executors
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        
        //Λήψη ονόματος καταστήματος από το intent
        String shopName = getIntent().getStringExtra(getString(R.string.shop_name_key));
        if (shopName == null) {
            Toast.makeText(this, getString(R.string.error_loading_store_details), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        //Λήψη καταστήματος από το NetworkManager cache
        Map<String, Shop> shops = NetworkManager.getInstance().getShops();
        currentStore = shops.get(shopName);
        
        if (currentStore == null) {
            Toast.makeText(this, getString(R.string.store_not_found), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(currentStore.getName());
        
        // Initialize UI components
        imageViewStoreLogo = findViewById(R.id.imageViewStoreLogo);
        textViewCategory = findViewById(R.id.textViewCategory);
        textViewPrice = findViewById(R.id.textViewPrice);
        ratingBarStore = findViewById(R.id.ratingBarStore);
        textViewRating = findViewById(R.id.textViewRating);
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        fabViewCart = findViewById(R.id.fabViewCart);
        buttonRateStore = findViewById(R.id.buttonRateStore);
        
        //Εμφάνιση πληροφοριών καταστήματος
        displayStoreInfo();
        
        //Λήψη προϊόντων από τον κατάλογο του καταστήματος
        if (currentStore.getCatalog() != null) {
            products = currentStore.getCatalog().getAllProducts();
            
            //=== DEBUGGING: Καταγραφή πληροφοριών καταλόγου ===
            Log.d(getString(R.string.store_detail_tag), getString(R.string.catalog_debug_start));
            Log.d(getString(R.string.store_detail_tag), getString(R.string.store_info, currentStore.getName()));
            Log.d(getString(R.string.store_detail_tag), getString(R.string.catalog_object_info, currentStore.getCatalog()));
            Log.d(getString(R.string.store_detail_tag), getString(R.string.products_list_size, (products != null ? String.valueOf(products.size()) : "null")));
            
            if (products != null && !products.isEmpty()) {
                Log.d(getString(R.string.store_detail_tag), getString(R.string.products_found_label));
                
                for (int i = 0; i < Math.min(products.size(), 3); i++) {
                    Product product = products.get(i);
                    Log.d(getString(R.string.store_detail_tag), getString(R.string.product_details,
                        (i+1), product.getName(), product.getFoodCategory(), product.getPrice()));
                }
            } else {
                Log.w(getString(R.string.store_detail_tag), getString(R.string.no_products_found_catalog));
            }
            
            if (currentStore.getCatalog().getInventory() != null) {
                Log.d(getString(R.string.store_detail_tag), getString(R.string.inventory_size_info, currentStore.getCatalog().getInventory().size()));
                Log.d(getString(R.string.store_detail_tag), getString(R.string.inventory_contents_info, currentStore.getCatalog().getInventory().keySet()));
            } else {
                Log.w(getString(R.string.store_detail_tag), getString(R.string.catalog_inventory_null));
            }
            
            Log.d(getString(R.string.store_detail_tag), getString(R.string.catalog_debug_end));
            
        } else {
            //log για debugging
            Log.w(getString(R.string.store_detail_tag), getString(R.string.store_catalog_null, currentStore.getName()));
            products = new ArrayList<>();
        }
        
        //Εμφάνιση μηνύματος αν δεν υπάρχουν προϊόντα
        if (products.isEmpty()) {
            Toast.makeText(this, getString(R.string.no_products_available), Toast.LENGTH_LONG).show();
        }
        
        // Set up products RecyclerView
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(products, this, currentStore);
        recyclerViewProducts.setAdapter(productAdapter);
        
        // Set up click listeners
        fabViewCart.setOnClickListener(v -> openCart());
        buttonRateStore.setOnClickListener(v -> showRatingDialog());
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        // Reset product quantities when returning from cart (after purchase)
        // Check if cart is empty (indicates successful purchase)
        InventoryCart cart = NetworkManager.getInstance().getCart();
        if (cart.getInventory().isEmpty() && productAdapter != null) {
            productAdapter.resetQuantities();
            
            // Update shop data to reflect current stock levels
            Map<String, Shop> shops = NetworkManager.getInstance().getShops();
            Shop updatedShop = shops.get(currentStore.getName());
            if (updatedShop != null) {
                currentStore = updatedShop;
                productAdapter.updateShop(currentStore);
                Log.d(getString(R.string.store_detail_tag), "Updated shop stock information after purchase");
            }
        }
    }
    
    private void displayStoreInfo() {
        if (currentStore.getStoreCategory() != null) {
            textViewCategory.setText(currentStore.getStoreCategory().getName());
        }
        
        //Ορισμός κατηγορίας τιμών
        String priceText = "";
        if (currentStore.getPrice() != null) {
            switch (currentStore.getPrice()) {
                case LOW:
                    priceText = getString(R.string.price_symbol_low);
                    break;
                case MEDIUM:
                    priceText = getString(R.string.price_symbol_medium);
                    break;
                case HIGH:
                    priceText = getString(R.string.price_symbol_high);
                    break;
            }
        }
        textViewPrice.setText(priceText);
        
        //Ορισμός βαθμολογίας
        if (currentStore.getRating() != null) {
            ratingBarStore.setRating(currentStore.getRating().getValue());
            textViewRating.setText(String.format("%.1f", currentStore.getRating().getValue()));
        }
        
        //Φόρτωση εικόνας καταστήματος χρησιμοποιώντας το StoreImageManager
        String imageUrl = StoreImageManager.getStoreImageUrl(currentStore.getName());
        
        Glide.with(this)
                .load(imageUrl)
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.ic_store_placeholder) // Loading placeholder
                        .error(R.drawable.ic_store_placeholder)) // Error fallback
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .into(imageViewStoreLogo);
    }
    
    private void openCart() {
        //Λήψη επιλεγμένων προϊόντων με ποσότητες > 0
        Map<String, Integer> selectedProducts = productAdapter.getProductQuantities();
        
        if (selectedProducts.isEmpty()) {
            Toast.makeText(this, getString(R.string.please_select_at_least_one_product), Toast.LENGTH_SHORT).show();
            return;
        }
        
        //Προσθήκη προϊόντων στο καλάθι του NetworkManager
        for (Map.Entry<String, Integer> entry : selectedProducts.entrySet()) {
            String productName = entry.getKey();
            int quantity = entry.getValue();
            
            //Εύρεση προϊόντος με βάση το όνομα
            for (Product product : products) {
                if (product.getName().equals(productName)) {
                    NetworkManager.getInstance().addToCart(product, quantity);
                    break;
                }
            }
        }
        
        //Εκκίνηση cart activity
        Intent intent = new Intent(this, CartActivity.class);
        intent.putExtra(getString(R.string.shop_name_key), currentStore.getName());
        startActivity(intent);
    }
    
    private void showRatingDialog() {
        final String[] ratingOptions = {
            "1 Star", "1.5 Stars", "2 Stars", "2.5 Stars", 
            "3 Stars", "3.5 Stars", "4 Stars", "4.5 Stars", "5 Stars"
        };
        final float[] ratingValues = {1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f};
        
        // Track the selected rating index (-1 means no selection)
        final int[] selectedRatingIndex = {-1};
        
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.rate_store))
                .setSingleChoiceItems(ratingOptions, -1, (dialog, which) -> {
                    // Update the selected rating index when user clicks a radio button
                    selectedRatingIndex[0] = which;
                })
                .setPositiveButton(getString(R.string.submit_rating), (dialog, which) -> {
                    // Submit rating only if user has made a selection
                    if (selectedRatingIndex[0] != -1) {
                        float selectedRating = ratingValues[selectedRatingIndex[0]];
                        rateStore(selectedRating);
                    }
                    dialog.dismiss();
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                .show();
    }
    
    private void rateStore(float rating) {
        executorService.execute(() -> {
            //Υποβολή βαθμολογίας στον server μέσω NetworkManager
            boolean success = NetworkManager.getInstance().performRating(currentStore.getName(), rating);
            
            mainHandler.post(() -> {
                if (success) {
                    Toast.makeText(this, getString(R.string.rating_submitted_successfully), Toast.LENGTH_SHORT).show();
                    
                    //Ενημέρωση NetworkManager
                    Map<String, Shop> shops = NetworkManager.getInstance().getShops();
                    Shop updatedStore = shops.get(currentStore.getName());
                    if (updatedStore != null) {
                        currentStore = updatedStore;
                        displayStoreInfo(); // Refresh the display
                    }
                } else {
                    Toast.makeText(this, getString(R.string.rating_submission_failed), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}