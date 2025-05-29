package com.online.food.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.online.food.R;
import com.online.food.network.NetworkManager;
import other.Coordinates;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    
    /** Default user coordinates - Central Athens where shops are located */
    private static final double DEFAULT_LATITUDE = 37.976557;
    private static final double DEFAULT_LONGITUDE = 23.735942;
    
    private EditText editTextServerIP;
    private EditText editTextServerPort;
    private Button buttonConnect;
    private ProgressBar progressBar;

    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initializeComponents();
        setupBackgroundProcessing();
        setupEventListeners();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanup();
    }
    
    /**
     * Initializes all UI components by finding their references.
     */
    private void initializeComponents() {
        editTextServerIP = findViewById(R.id.editTextServerIP);
        editTextServerPort = findViewById(R.id.editTextServerPort);
        buttonConnect = findViewById(R.id.buttonConnect);
        progressBar = findViewById(R.id.progressBar);
    }
    
    /**
     * Sets up background processing components for network operations.
     */
    private void setupBackgroundProcessing() {
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }
    
    /**
     * Sets up event listeners for UI components.
     */
    private void setupEventListeners() {
        buttonConnect.setOnClickListener(v -> connectToServer());
    }
    
    /**
     * Attempts to connect to the server using the provided IP and port.
     * Validates input, shows loading state, and handles connection result.
     */
    private void connectToServer() {
        final String serverIP = editTextServerIP.getText().toString().trim();
        final String portText = editTextServerPort.getText().toString().trim();
        
        if (!validateInputs(serverIP, portText)) {
            return;
        }
        
        final int serverPort;
        try {
            serverPort = Integer.parseInt(portText);
            if (!isValidPortNumber(serverPort)) {
                showToast(getString(R.string.port_number_range_error));
                return;
            }
        } catch (NumberFormatException e) {
            showToast(getString(R.string.invalid_port_number));
            return;
        }
        
        setLoadingState(true);
        
        final Coordinates userCoordinates = new Coordinates(DEFAULT_LATITUDE, DEFAULT_LONGITUDE);
        
        //Εκτέλεση σύνδεσης σε background thread
        executorService.execute(() -> performConnectionAttempt(serverIP, serverPort, userCoordinates));
    }
    
    /**
     * Validates user inputs for server IP and port.
     * 
     * @param serverIP the server IP address
     * @param portText the port number as string
     * @return true if inputs are valid, false otherwise
     */
    private boolean validateInputs(String serverIP, String portText) {
        if (serverIP.isEmpty() || portText.isEmpty()) {
            showToast(getString(R.string.please_enter_server_ip_port));
            return false;
        }
        return true;
    }
    
    /**
     * Checks if the provided port number is within valid range.
     * 
     * @param port the port number to validate
     * @return true if port is valid, false otherwise
     */
    private boolean isValidPortNumber(int port) {
        return port >= 1 && port <= 65535;
    }
    
    /**
     * Performs the actual connection attempt in a background thread.
     * 
     * @param serverIP the server IP address
     * @param serverPort the server port number
     * @param userCoordinates the user's geographical coordinates
     */
    private void performConnectionAttempt(String serverIP, int serverPort, Coordinates userCoordinates) {
        boolean connectionSuccessful = false;
        try {
            //Προσπάθεια σύνδεσης στον Master server χρησιμοποιώντας το NetworkManager
            connectionSuccessful = NetworkManager.getInstance()
                .establishConnection(serverIP, serverPort, userCoordinates);
        } catch (Exception e) {
            // Log the exception for debugging purposes
            android.util.Log.e(getString(R.string.mainactivity_tag), getString(R.string.connection_failed_log), e);
        }
        
        final boolean finalConnectionSuccessful = connectionSuccessful;
        
        //Ενημέρωση UI στο κύριο thread
        mainHandler.post(() -> handleConnectionResult(finalConnectionSuccessful));
    }
    
    /**
     * Handles the result of the connection attempt on the main UI thread.
     * 
     * @param connectionSuccessful true if connection was successful, false otherwise
     */
    private void handleConnectionResult(boolean connectionSuccessful) {
        setLoadingState(false);
        
        if (connectionSuccessful) {
            showToast(getString(R.string.connected_found_shops));
            navigateToStoreList();
        } else {
            showToast(getString(R.string.connection_failed));
        }
    }
    
    /**
     * Sets the UI loading state by showing/hiding progress bar and updating button.
     * 
     * @param isLoading true to show loading state, false to hide it
     */
    private void setLoadingState(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        buttonConnect.setEnabled(!isLoading);
        buttonConnect.setText(isLoading ? 
            getString(R.string.button_connecting) : 
            getString(R.string.button_connect));
    }
    
    /**
     * Navigates to the store list activity and finishes this activity.
     */
    private void navigateToStoreList() {
        Intent intent = new Intent(this, StoreListActivity.class);
        startActivity(intent);
        finish();   //Τερματισμός του current activity μετά τη μετάβαση
    }
    
    /**
     * Shows a toast message to the user.
     * 
     * @param message the message to display
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Cleanup method to properly release resources.
     */
    private void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        NetworkManager.getInstance().disconnect();  //Αποσύνδεση από το server
    }
} 