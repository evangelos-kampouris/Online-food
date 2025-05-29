package com.online.food.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.online.food.R;
import com.online.food.adapters.StoreAdapter;
import com.online.food.network.NetworkManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import other.Shop;
import other.Coordinates;
import other.Price;
import other.ProductCategory;
import other.Rating;
import Filtering.Filtering;
import Filtering.FilterCords;
import Filtering.FilterPrice;
import Filtering.FilterRating;
import Filtering.FilterFoodCategory;

/**
 * Activity for displaying and filtering food stores.
 * Handles search filters, store list display, and navigation to store details.
 */
public class StoreListActivity extends AppCompatActivity implements StoreAdapter.OnStoreClickListener {

    private RecyclerView recyclerViewStores;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView textViewNoStores;
    private Chip chipPriceFilter, chipRatingFilter, chipFoodCategoryFilter, chipClearFilters;
    private FloatingActionButton fabFilterSearch;
    
    private ExecutorService executorService;
    private Handler mainHandler;
    
    private List<Shop> shops = new ArrayList<>();
    private List<Filtering> activeFilters = new ArrayList<>();
    private StoreAdapter storeAdapter;
    
    // Default location coordinates (Central Athens)
    private static final double DEFAULT_LATITUDE = 37.976557;
    private static final double DEFAULT_LONGITUDE = 23.735942;
    private final Coordinates userLocation = new Coordinates(DEFAULT_LATITUDE, DEFAULT_LONGITUDE);
    
    // Multi-selection filter storage
    private List<Price> selectedPriceFilters = new ArrayList<>();   //Φίλτρα τιμών που έχει επιλέξει ο χρήστης
    private List<Rating> selectedRatingFilters = new ArrayList<>(); //Φίλτρα βαθμολογίας που έχει επιλέξει ο χρήστης
    private List<ProductCategory> selectedFoodCategoryFilters = new ArrayList<>(); //Φίλτρα κατηγοριών φαγητού που έχει επιλέξει ο χρήστης

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);
        
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // Initialize UI components
        recyclerViewStores = findViewById(R.id.recyclerViewStores);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        progressBar = findViewById(R.id.progressBar);
        textViewNoStores = findViewById(R.id.textViewNoStores);
        chipPriceFilter = findViewById(R.id.chipPriceFilter);
        chipRatingFilter = findViewById(R.id.chipRatingFilter);
        chipFoodCategoryFilter = findViewById(R.id.chipFoodCategoryFilter);
        chipClearFilters = findViewById(R.id.chipClearFilters);
        fabFilterSearch = findViewById(R.id.fabFilterSearch);
        
        // Setup RecyclerView
        recyclerViewStores.setLayoutManager(new LinearLayoutManager(this));
        storeAdapter = new StoreAdapter(shops, this);
        recyclerViewStores.setAdapter(storeAdapter);
        
        // Setup SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this::refreshStores);
        
        // Initialize background task handling
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        
        // Setup event listeners
        setupFilterListeners();
        fabFilterSearch.setOnClickListener(v -> performSearch());
        
        // Initialize UI state
        updateClearFiltersVisibility();
        updateChipText();
        
        //Load shops from NetworkManager
        loadInitialShops();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
    
    /**
     * Setup click listeners for filter chips
     */
    private void setupFilterListeners() {
        chipPriceFilter.setOnClickListener(v -> showPriceFilterDialog());
        chipRatingFilter.setOnClickListener(v -> showRatingFilterDialog());
        chipFoodCategoryFilter.setOnClickListener(v -> showFoodCategoryFilterDialog());
        chipClearFilters.setOnClickListener(v -> clearFilters());
    }
    
    /**
     * Load shops from NetworkManager cache (populated in MainActivity)
     */
    private void loadInitialShops() {
        Map<String, Shop> networkShops = NetworkManager.getInstance().getShops();
        shops.clear();
        shops.addAll(networkShops.values());
        
        progressBar.setVisibility(View.GONE);
        
        if (shops.isEmpty()) {
            textViewNoStores.setVisibility(View.VISIBLE);
            recyclerViewStores.setVisibility(View.GONE);
        } else {
            textViewNoStores.setVisibility(View.GONE);
            recyclerViewStores.setVisibility(View.VISIBLE);
        }
        
        storeAdapter.notifyDataSetChanged();
    }
    
    /**
     * Refresh store list with current filters
     */
    private void refreshStores() {
        executorService.execute(() -> {
            try {
                //Δημιουργία λίστας φίλτρων με τοποθεσία + τρέχοντα φίλτρα
                List<Filtering> refreshFilters = new ArrayList<>();
                
                //Πάντα να συμπεριλαμβάνεται το φίλτρο 5km τοποθεσίας
                FilterCords locationFilter = new FilterCords(5.0f, userLocation);
                refreshFilters.add(locationFilter);
                
                //Προσθήκη ενεργών φίλτρων
                if (selectedPriceFilters.size() > 0) {
                    for (Price price : selectedPriceFilters) {
                        refreshFilters.add(new FilterPrice(price));
                    }
                }
                
                if (selectedRatingFilters.size() > 0) {
                    for (Rating rating : selectedRatingFilters) {
                        refreshFilters.add(new FilterRating(rating));
                    }
                }
                
                if (selectedFoodCategoryFilters.size() > 0) {
                    for (ProductCategory category : selectedFoodCategoryFilters) {
                        refreshFilters.add(new FilterFoodCategory(category));
                    }
                }
                
                //Εκτέλεση αναζήτησης
                List<Shop> searchResults = NetworkManager.getInstance().performSearch(refreshFilters);
                
                mainHandler.post(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    
                    if (searchResults != null && !searchResults.isEmpty()) {
                        shops.clear();
                        shops.addAll(searchResults);
                        storeAdapter.notifyDataSetChanged();
                        
                        recyclerViewStores.setVisibility(View.VISIBLE);
                        textViewNoStores.setVisibility(View.GONE);
                        
                        Toast.makeText(this, getString(R.string.refreshed_found_stores, shops.size()), Toast.LENGTH_SHORT).show();
                    } else {
                        shops.clear();
                        storeAdapter.notifyDataSetChanged();
                        
                        recyclerViewStores.setVisibility(View.GONE);
                        textViewNoStores.setVisibility(View.VISIBLE);
                        
                        Toast.makeText(this, getString(R.string.no_stores_found), Toast.LENGTH_SHORT).show();
                    }
                });
                
            } catch (Exception e) {
                mainHandler.post(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(this, getString(R.string.refresh_failed, e.getMessage()), Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    /**
     * Show price filter selection dialog
     */
    private void showPriceFilterDialog() {
        final String[] priceOptions = {
            getString(R.string.price_cheap), 
            getString(R.string.price_medium), 
            getString(R.string.price_expensive)
        };
        final Price[] priceValues = {Price.LOW, Price.MEDIUM, Price.HIGH};
        
        //Δημιουργία array για τις επιλογές
        boolean[] checkedItems = new boolean[priceOptions.length];
        for (int i = 0; i < priceValues.length; i++) {
            checkedItems[i] = selectedPriceFilters.contains(priceValues[i]);
        }
        
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.filter_by_price))
                .setMultiChoiceItems(priceOptions, checkedItems, 
                        (dialog, which, isChecked) -> {
                            if (isChecked) {
                                if (!selectedPriceFilters.contains(priceValues[which])) {
                                    selectedPriceFilters.add(priceValues[which]);
                                }
                            } else {
                                selectedPriceFilters.remove(priceValues[which]);
                            }
                            
                            chipPriceFilter.setChecked(selectedPriceFilters.size() > 0);
                            updateClearFiltersVisibility();
                        })
                .setPositiveButton(getString(R.string.select), (dialog, which) -> {
                    updateChipText();
                    dialog.dismiss();
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                .show();
    }
    
    /**
     * Show rating filter selection dialog
     */
    private void showRatingFilterDialog() {
        final String[] ratingOptions = {
            getString(R.string.rating_1_star), getString(R.string.rating_1_5_stars), 
            getString(R.string.rating_2_stars), getString(R.string.rating_2_5_stars),
            getString(R.string.rating_3_stars), getString(R.string.rating_3_5_stars), 
            getString(R.string.rating_4_stars), getString(R.string.rating_4_5_stars), 
            getString(R.string.rating_5_stars)
        };
        final Rating[] ratingValues = {
            Rating.ONE_STAR, Rating.ONE_HALF_STAR, Rating.TWO_STARS, Rating.TWO_HALF_STARS,
            Rating.THREE_STARS, Rating.THREE_HALF_STARS, Rating.FOUR_STARS, Rating.FOUR_HALF_STARS, Rating.FIVE_STARS
        };
        
        //Δημιουργία array για βαθμολογίες
        boolean[] checkedItems = new boolean[ratingOptions.length];
        for (int i = 0; i < ratingValues.length; i++) {
            checkedItems[i] = selectedRatingFilters.contains(ratingValues[i]);
        }
        
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.filter_by_rating))
                .setMultiChoiceItems(ratingOptions, checkedItems, 
                        (dialog, which, isChecked) -> {
                            if (isChecked) {
                                if (!selectedRatingFilters.contains(ratingValues[which])) {
                                    selectedRatingFilters.add(ratingValues[which]);
                                }
                            } else {
                                selectedRatingFilters.remove(ratingValues[which]);
                            }
                            
                            chipRatingFilter.setChecked(selectedRatingFilters.size() > 0);
                            updateClearFiltersVisibility();
                        })
                .setPositiveButton(getString(R.string.select), (dialog, which) -> {
                    updateChipText();
                    dialog.dismiss();
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                .show();
    }
    
    /**
     * Show food category filter selection dialog
     */
    private void showFoodCategoryFilterDialog() {
        ProductCategory[] categories = ProductCategory.values();
        String[] categoryOptions = new String[categories.length];
        for (int i = 0; i < categories.length; i++) {
            categoryOptions[i] = categories[i].getName();
        }
        
        //Δημιουργία array για κατηγορίες φαγητού
        boolean[] checkedItems = new boolean[categoryOptions.length];
        for (int i = 0; i < categories.length; i++) {
            checkedItems[i] = selectedFoodCategoryFilters.contains(categories[i]);
        }
        
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.filter_by_food_category))
                .setMultiChoiceItems(categoryOptions, checkedItems, 
                        (dialog, which, isChecked) -> {
                            if (isChecked) {
                                if (!selectedFoodCategoryFilters.contains(categories[which])) {
                                    selectedFoodCategoryFilters.add(categories[which]);
                                }
                            } else {
                                selectedFoodCategoryFilters.remove(categories[which]);
                            }
                            
                            chipFoodCategoryFilter.setChecked(selectedFoodCategoryFilters.size() > 0);
                            updateClearFiltersVisibility();
                        })
                .setPositiveButton(getString(R.string.select), (dialog, which) -> {
                    updateChipText();
                    dialog.dismiss();
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                .show();
    }
    
    /**
     * Execute search with selected filters
     */
    private void performSearch() {
        // Εμφανιση loading state
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewStores.setVisibility(View.GONE);
        textViewNoStores.setVisibility(View.GONE);
        
        //Δημιουργία λίστας φίλτρων
        activeFilters.clear();
        
        //Φίλτρο 5km τοποθεσίας
        FilterCords locationFilter = new FilterCords(5.0f, userLocation);
        activeFilters.add(locationFilter);
        
        //Προσθήκη φίλτρων απο εχει επιλεξει ο χρηστης
        if (selectedPriceFilters.size() > 0) {
            for (Price price : selectedPriceFilters) {
                activeFilters.add(new FilterPrice(price));
            }
        }
        
        if (selectedRatingFilters.size() > 0) {
            for (Rating rating : selectedRatingFilters) {
                activeFilters.add(new FilterRating(rating));
            }
        }
        
        if (selectedFoodCategoryFilters.size() > 0) {
            for (ProductCategory category : selectedFoodCategoryFilters) {
                activeFilters.add(new FilterFoodCategory(category));
            }
        }
        
        //Εκτέλεση αναζήτησης στο background
        executorService.execute(() -> {
            try {
                List<Shop> searchResults = NetworkManager.getInstance().performSearch(activeFilters);
                
                mainHandler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    
                    if (searchResults != null && !searchResults.isEmpty()) {
                        shops.clear();
                        shops.addAll(searchResults);
                        storeAdapter.notifyDataSetChanged();
                        
                        recyclerViewStores.setVisibility(View.VISIBLE);
                        textViewNoStores.setVisibility(View.GONE);
                        
                        Toast.makeText(this, getString(R.string.found_stores, shops.size()), Toast.LENGTH_SHORT).show();
                    } else {
                        shops.clear();
                        storeAdapter.notifyDataSetChanged();
                        
                        recyclerViewStores.setVisibility(View.GONE);
                        textViewNoStores.setVisibility(View.VISIBLE);
                        
                        Toast.makeText(this, getString(R.string.no_stores_found_with_filters), Toast.LENGTH_SHORT).show();
                    }
                });
                
            } catch (Exception e) {
                mainHandler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, getString(R.string.search_failed, e.getMessage()), Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    /**
     * Clear all filters and show all nearby stores
     */
    private void clearFilters() {
        selectedPriceFilters.clear();
        selectedRatingFilters.clear();
        selectedFoodCategoryFilters.clear();
        chipPriceFilter.setChecked(false);
        chipRatingFilter.setChecked(false);
        chipFoodCategoryFilter.setChecked(false);
        chipClearFilters.setChecked(false);
        
        updateChipText();
        updateClearFiltersVisibility();
        
        Toast.makeText(this, getString(R.string.filters_cleared), Toast.LENGTH_SHORT).show();
        
        performSearchWithoutFilters();
    }
    
    /**
     * Search with location filter only (no other filters)
     */
    private void performSearchWithoutFilters() {
        // Εμφανιση loading state
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewStores.setVisibility(View.GONE);
        textViewNoStores.setVisibility(View.GONE);
        
        executorService.execute(() -> {
            try {
                //Μόνο φίλτρο τοποθεσίας
                List<Filtering> noFilters = new ArrayList<>();
                FilterCords locationFilter = new FilterCords(5.0f, userLocation);
                noFilters.add(locationFilter);
                
                List<Shop> searchResults = NetworkManager.getInstance().performSearch(noFilters);
                
                mainHandler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    
                    if (searchResults != null && !searchResults.isEmpty()) {
                        shops.clear();
                        shops.addAll(searchResults);
                        storeAdapter.notifyDataSetChanged();
                        
                        recyclerViewStores.setVisibility(View.VISIBLE);
                        textViewNoStores.setVisibility(View.GONE);
                        
                        Toast.makeText(this, getString(R.string.showing_all_nearby_stores, shops.size()), Toast.LENGTH_SHORT).show();
                    } else {
                        shops.clear();
                        storeAdapter.notifyDataSetChanged();
                        
                        recyclerViewStores.setVisibility(View.GONE);
                        textViewNoStores.setVisibility(View.VISIBLE);
                        
                        Toast.makeText(this, getString(R.string.no_stores_found_nearby), Toast.LENGTH_SHORT).show();
                    }
                });
                
            } catch (Exception e) {
                mainHandler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, getString(R.string.search_failed, e.getMessage()), Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    /**
     * Show/hide clear filters button based on active filters
     */
    private void updateClearFiltersVisibility() {
        chipClearFilters.setVisibility(selectedPriceFilters.size() > 0 || selectedRatingFilters.size() > 0 || selectedFoodCategoryFilters.size() > 0 ? View.VISIBLE : View.GONE);
    }
    
    /**
     * Update chip text to show number of selected filters
     */
    private void updateChipText() {
        //Update chip τιμών
        if (selectedPriceFilters.size() > 0) {
            chipPriceFilter.setText(getString(R.string.price_filter_with_count, selectedPriceFilters.size()));
        } else {
            chipPriceFilter.setText(getString(R.string.price_filter));
        }
        
        //Update chip βαθμολογίας
        if (selectedRatingFilters.size() > 0) {
            chipRatingFilter.setText(getString(R.string.rating_filter_with_count, selectedRatingFilters.size()));
        } else {
            chipRatingFilter.setText(getString(R.string.rating_filter));
        }
        
        //Update chip κατηγορίας φαγητού
        if (selectedFoodCategoryFilters.size() > 0) {
            chipFoodCategoryFilter.setText(getString(R.string.food_category_filter_with_count, selectedFoodCategoryFilters.size()));
        } else {
            chipFoodCategoryFilter.setText(getString(R.string.food_category_filter));
        }
    }
    
    @Override
    public void onStoreClick(Shop store) {
        Intent intent = new Intent(this, StoreDetailActivity.class);
        intent.putExtra("shop_name", store.getName()); //Πέρασμα ονόματος καταστήματος στο detail activity
        startActivity(intent);
    }
} 