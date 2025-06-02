package com.online.food.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.online.food.R;
import com.online.food.adapters.CartAdapter;
import com.online.food.network.NetworkManager;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Inventory.InventoryCart;
import other.Shop;
/*
is the cart(checkout) layout
 */
public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartItemRemovedListener {

    private RecyclerView recyclerViewCartItems;
    private TextView textViewCartStoreName;
    private TextView textViewSubtotal;
    private Button buttonCheckout;
    private ProgressBar progressBar;
    private TextView textViewEmptyCart;
    
    private Shop currentStore;  //Το κατάστημα από το οποίο γίνεται η παραγγελία
    private CartAdapter cartAdapter;
    
    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        
        //Λήψη ονόματος καταστήματος από intent και φόρτωση από NetworkManager
        String shopName = getIntent().getStringExtra(getString(R.string.shop_name_key));
        if (shopName == null) {
            Toast.makeText(this, getString(R.string.error_loading_cart_data), Toast.LENGTH_SHORT).show();
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
        getSupportActionBar().setTitle(getString(R.string.cart));
        
        // Initialize UI components
        recyclerViewCartItems = findViewById(R.id.recyclerViewCartItems);
        textViewCartStoreName = findViewById(R.id.textViewCartStoreName);
        textViewSubtotal = findViewById(R.id.textViewSubtotal);
        buttonCheckout = findViewById(R.id.buttonCheckout);
        progressBar = findViewById(R.id.progressBar);
        textViewEmptyCart = findViewById(R.id.textViewEmptyCart);
        
        // Initialize executors for background tasks
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        
        // Set store name
        textViewCartStoreName.setText(currentStore.getName());
        
        //Λήψη καλαθιού από το NetworkManager
        InventoryCart cart = NetworkManager.getInstance().getCart();
        
        // Set up RecyclerView
        recyclerViewCartItems.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(cart, this);
        recyclerViewCartItems.setAdapter(cartAdapter);
        
        //Ενημέρωση συνολικού κόστους
        updateSubtotal();
        
        // Set up checkout button
        buttonCheckout.setOnClickListener(v -> checkout());
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
    
    private void updateSubtotal() {
        InventoryCart cart = NetworkManager.getInstance().getCart();
        float subtotal = cart.getCost();
        textViewSubtotal.setText(getString(R.string.total_amount, subtotal));
        
        //Εμφάνιση/απόκρυψη μηνύματος άδειου καλαθιού
        if (cart.getInventory().isEmpty()) {
            recyclerViewCartItems.setVisibility(View.GONE);
            textViewEmptyCart.setVisibility(View.VISIBLE);
            buttonCheckout.setEnabled(false);
        } else {
            recyclerViewCartItems.setVisibility(View.VISIBLE);
            textViewEmptyCart.setVisibility(View.GONE);
            buttonCheckout.setEnabled(true);
        }
    }
    
    private void checkout() {
        InventoryCart cart = NetworkManager.getInstance().getCart();
        
        if (cart.getInventory().isEmpty()) {
            Toast.makeText(this, getString(R.string.cart_is_empty), Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show loading state
        progressBar.setVisibility(View.VISIBLE);
        buttonCheckout.setEnabled(false);
        
        //Εκτέλεση αγοράς σε background thread
        executorService.execute(() -> {
            boolean success = NetworkManager.getInstance().performPurchase(currentStore);
            
            //Ενημέρωση UI στο κύριο thread
            mainHandler.post(() -> {
                progressBar.setVisibility(View.GONE);
                
                if (success) {
                    Toast.makeText(CartActivity.this, getString(R.string.purchase_successful), Toast.LENGTH_LONG).show();
                    //Το καλάθι εκκαθαρίζεται αυτόματα από το NetworkManager
                    //Κλείσιμο activity και επιστροφή στο store detail
                    finish();
                } else {
                    Toast.makeText(CartActivity.this, getString(R.string.purchase_failed), Toast.LENGTH_LONG).show();
                    buttonCheckout.setEnabled(true);
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

    @Override
    public void onCartItemRemoved(String productName) {
        //Αφαίρεση προϊόντος από το καλάθι στο NetworkManager
        NetworkManager.getInstance().removeFromCart(productName);
        
        //Ενημέρωση συνολικού κόστους χρησιμοποιώντας τον υπολογισμό του adapter
        float subtotal = cartAdapter.getCartTotal();
        textViewSubtotal.setText(getString(R.string.total_amount, subtotal));
        
        //Εμφάνιση/απόκρυψη μηνύματος άδειου καλαθιού
        if (cartAdapter.getItemCount() == 0) {
            recyclerViewCartItems.setVisibility(View.GONE);
            textViewEmptyCart.setVisibility(View.VISIBLE);
            buttonCheckout.setEnabled(false);
        } else {
            recyclerViewCartItems.setVisibility(View.VISIBLE);
            textViewEmptyCart.setVisibility(View.GONE);
            buttonCheckout.setEnabled(true);
        }
    }
} 