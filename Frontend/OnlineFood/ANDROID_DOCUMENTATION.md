# Online Food Ordering Android Application Documentation

## Application Overview

The **Online Food Ordering Android Application** is a client-side mobile app that connects to a distributed Java server system for food delivery services. The app allows users to:

- Connect to a distributed food ordering server
- Browse nearby restaurants within a 5km radius
- Filter restaurants by price, rating, and food category
- View restaurant details and product catalogs
- Add products to a shopping cart
- Rate restaurants
- Complete purchases

### Technical Specifications
- **Platform**: Android (API Level 24+)
- **Language**: Java
- **UI Framework**: Material Design Components
- **Network Protocol**: Java Object Serialization over TCP sockets
- **Threading**: ExecutorService for background operations
- **Image Management**: Glide library with caching

---

## Architecture & Design Patterns

### 1. **Singleton Pattern**
- `NetworkManager` implements singleton pattern for centralized network operations
- Ensures single connection point to the distributed server system

### 2. **Observer Pattern**
- Activities implement listener interfaces for adapter callbacks
- Real-time UI updates based on data changes

### 3. **Adapter Pattern**
- RecyclerView adapters (`StoreAdapter`, `ProductAdapter`, `CartAdapter`)
- Bridge between data models and UI components

### 4. **Repository Pattern**
- `NetworkManager` acts as a repository for server communication
- Local caching of shop data for improved performance

---

## Project Structure

```
app/src/main/java/
├── com/online/food/
│   ├── activities/           # UI Activities
│   │   ├── MainActivity.java
│   │   ├── StoreListActivity.java
│   │   ├── StoreDetailActivity.java
│   │   └── CartActivity.java
│   ├── adapters/            # RecyclerView Adapters
│   │   ├── StoreAdapter.java
│   │   ├── ProductAdapter.java
│   │   └── CartAdapter.java
│   ├── network/             # Network Layer
│   │   └── NetworkManager.java
│   └── utils/               # Utility Classes
│       └── StoreImageManager.java
├── DTO/                     # Data Transfer Objects
│   ├── SearchRequestDTO.java
│   ├── BuyRequestDTO.java
│   ├── RateStoreRequestDTO.java
│   └── ReducerResultDTO.java
├── Responses/               # Response Objects
│   └── ResponseDTO.java
├── Filtering/               # Filter Classes
│   ├── Filtering.java
│   ├── FilterCords.java
│   ├── FilterPrice.java
│   ├── FilterRating.java
│   └── FilterFoodCategory.java
├── Inventory/               # Inventory Management
│   ├── Inventory.java
│   ├── InventoryItem.java
│   ├── InventoryCart.java
│   ├── ShopInventory.java
│   └── ShopInventoryItem.java
└── other/                   # Domain Models
    ├── Shop.java
    ├── Product.java
    ├── Coordinates.java
    ├── Price.java
    ├── Rating.java
    ├── ProductCategory.java
    └── StoreCategories.java
```

---

## Core Components

### 1. **Activities**

#### **MainActivity**
- **Purpose**: Server connection and authentication
- **Features**:
  - Server IP/Port input validation (1-65535)
  - Connection establishment with distributed server
  - Automatic 5km radius search on connection
  - Loading states and error handling
  - TCP socket timeout management (10 seconds)

#### **StoreListActivity**
- **Purpose**: Restaurant browsing and filtering
- **Features**:
  - Display list of nearby restaurants
  - Multi-criteria filtering (price, rating, food category)
  - Pull-to-refresh functionality
  - Search execution with filter combinations

#### **StoreDetailActivity**
- **Purpose**: Restaurant details and product catalog
- **Features**:
  - Restaurant information display
  - Product catalog browsing
  - Add to cart functionality
  - Restaurant rating submission with radio buttons
  - Dynamic image loading with Glide

#### **CartActivity**
- **Purpose**: Shopping cart management and checkout
- **Features**:
  - Cart item display and management
  - Total cost calculation
  - Item removal functionality
  - Purchase processing

### 2. **Adapters**

#### **StoreAdapter**
- Displays restaurant list with ratings, prices, and categories
- Handles click events for navigation to restaurant details

#### **ProductAdapter**
- Shows product catalog with prices and categories
- Manages quantity selection for cart additions
- Real-time quantity updates

#### **CartAdapter**
- Displays cart items with quantities and prices
- Handles item removal with confirmation
- Calculates and displays subtotals

### 3. **NetworkManager (Singleton)**
- **Core Responsibilities**:
  - TCP socket connection management
  - Object serialization/deserialization
  - Request/response handling
  - Local data caching
  - Background thread management

---

## Network Communication - TCP Sockets

### **Connection Architecture**
The app communicates with a distributed server system consisting of:
- **Master Server** (Port 9000): Main coordination server
- **Reducer Server** (Port 8080): Request aggregation
- **Worker Servers** (Ports 9001-9003): Data processing nodes

### **TCP Communication Protocol Implementation**
The `NetworkManager` class implements TCP socket communication with the following actual features:

#### **Basic Socket Configuration**
```java
private static final String DEFAULT_SERVER_IP = "10.0.2.2";  // Android emulator localhost
private static final int DEFAULT_MASTER_PORT = 9000;
private static final int SOCKET_TIMEOUT_MS = 10000;          // 10 second timeout

// Socket creation with timeout
Socket socket = new Socket(serverIP, serverPort);
socket.setSoTimeout(SOCKET_TIMEOUT_MS);
```

#### **Object Serialization Implementation**
```java
// Establish object streams for communication
ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

// Send request and receive response
out.writeObject(requestDTO);
out.flush();
Object response = in.readObject();
```

### **Actual TCP Request Types Implemented**

#### **1. Search Requests**
```java
// Implementation in NetworkManager.performSearch()
SearchRequestDTO searchRequestDTO = new SearchRequestDTO(filters);
// Creates new socket for each search operation
Socket searchSocket = new Socket(DEFAULT_SERVER_IP, DEFAULT_MASTER_PORT);
searchSocket.setSoTimeout(SOCKET_TIMEOUT_MS);
```

#### **2. Purchase Requests**  
```java
// Implementation in NetworkManager.performPurchase()
BuyRequestDTO buyRequestDTO = new BuyRequestDTO(selectedShop, cart);
// Uses separate socket connection for purchase
Socket purchaseSocket = createPurchaseSocket();
```

#### **3. Rating Requests**
```java
// Implementation in NetworkManager.performRating()
RateStoreRequestDTO rateStoreRequestDTO = new RateStoreRequestDTO(storeName, rating);
// Uses generic sendAndReceiveRequest method with fresh connection
Object response = sendAndReceiveRequest(rateStoreRequestDTO);
```

### **Connection Management Strategy**
- **Fresh Connections**: Each operation creates a new socket connection
- **No Connection Pooling**: Connections are closed after each request
- **Timeout Protection**: 10-second socket timeout on all operations
- **Resource Cleanup**: Explicit closing of streams and sockets in finally blocks

### **Error Handling Implementation**
```java
// Basic exception handling pattern used throughout
try {
    // Socket operations
} catch (Exception e) {
    Log.e(TAG, "Error message", e);
    return false;
} finally {
    // Cleanup resources
    if (out != null) out.close();
    if (in != null) in.close();
    if (socket != null) socket.close();
}
```

---

## Threading Architecture

### **Actual Threading Implementation**

**Actual Pattern**: Simple background execution with main thread callbacks for UI updates.

The Android app uses a threading model implemented in the activities:

#### **Main Components Used**
```java
// In MainActivity and other activities
private ExecutorService executorService;
private Handler mainHandler;

// Initialization in onCreate()
executorService = Executors.newSingleThreadExecutor();
mainHandler = new Handler(Looper.getMainLooper());
```

#### **Background Network Operations**
```java
// Pattern used in MainActivity.connectToServer()
executorService.execute(() -> {
    // Network operation in background thread
    boolean connectionSuccessful = NetworkManager.getInstance()
        .establishConnection(serverIP, serverPort, userCoordinates);
    
    // Switch back to main thread for UI updates
    mainHandler.post(() -> handleConnectionResult(connectionSuccessful));
});
```

#### **Threading in StoreDetailActivity**
```java
// Pattern for rating submission
executorService.execute(() -> {
    boolean success = NetworkManager.getInstance().performRating(storeName, rating);
    
    mainHandler.post(() -> {
        if (success) {
            Toast.makeText(this, getString(R.string.rating_submitted_successfully), Toast.LENGTH_SHORT).show();
            // Update UI with new data
        }
    });
});
```

### **Lifecycle Management**
```java
@Override
protected void onDestroy() {
    super.onDestroy();
    executorService.shutdown();
}
```

---

## Data Models

### **Core Entities**

#### **Shop (other/Shop.java)**

#### **Product (other/Product.java)**

#### **InventoryCart (Inventory/InventoryCart.java)**

#### **Support Classes**
- **Coordinates**: Latitude/longitude for geographic positioning
- **Rating**: Store rating with float value
- **Price**: Enum for LOW/MEDIUM/HIGH price categories
- **ProductCategory**: Enum for food categories
- **StoreCategories**: Enum for store types

### **Data Transfer Objects (DTO Package)**
- **SearchRequestDTO**: Contains list of filters for search operations
- **BuyRequestDTO**: Contains shop and cart for purchase operations  
- **RateStoreRequestDTO**: Contains store name and rating value
- **ReducerResultDTO**: Contains search results from distributed system
- **ResponseDTO**: Generic response wrapper with success/data/message

### **Inventory System (Inventory Package)**
- **Inventory**: Base class for inventory management
- **InventoryItem**: Individual inventory item with product and quantity
- **InventoryCart**: Shopping cart extending base inventory
- **ShopInventory**: Store's product catalog
- **ShopInventoryItem**: Individual store inventory item

### **Filtering System (Filtering Package)**
The filtering implementation includes:
- **FilterCords**: Geographic radius filtering (5km radius used)
- **FilterPrice**, **FilterRating**, **FilterFoodCategory**: Additional filter types

---

## User Interface

### **Design Principles**
- **Material Design**: Modern Android UI guidelines
- **Responsive Layout**: Adapts to different screen sizes
- **Accessibility**: Proper content descriptions and navigation
- **Loading States**: Progress indicators for network operations

### **Key UI Components**
- **RecyclerViews**: Efficient list displays
- **SwipeRefreshLayout**: Pull-to-refresh functionality
- **Chips**: Filter selection and display
- **FloatingActionButton**: Primary actions (search, cart)
- **ProgressBar**: Loading state indicators
- **Radio Button Dialogs**: Intuitive single-choice selections for ratings

### **Navigation Flow**
```
MainActivity (Connection)
    ↓
StoreListActivity (Restaurant List)
    ↓
StoreDetailActivity (Restaurant Details)
    ↓
CartActivity (Shopping Cart)
```

---

## Features & Functionality

### **1. Server Connection**
- IP/Port validation with range checking (1-65535)
- Connection timeout handling (10 seconds)
- Automatic retry mechanisms
- Error messaging for connection failures

### **2. Restaurant Discovery**
- Automatic 5km radius search on connection
- Real-time filtering with multiple criteria
- Pull-to-refresh for updated data
- Empty state handling

### **3. Advanced Filtering**
- **Price Filters**: Cheap ($), Medium ($$), Expensive ($$$)
- **Rating Filters**: 1-5 stars with half-star increments
- **Category Filters**: All food categories (Pizza, Burger, Sushi, etc.)
- **Multi-selection**: Combine multiple filters
- **Filter Persistence**: Maintains selections during session

### **4. Product Catalog**
- Dynamic product loading from server
- Category-based organization
- Stock availability display
- Quantity selection with validation

### **5. Shopping Cart**
- Real-time total calculation
- Item quantity management
- Remove item functionality
- Empty cart state handling

### **6. Rating System**
- 1-5 star rating submission with radio button selection
- Intuitive single-choice rating dialog interface
- Real-time rating updates
- Server synchronization
- Local cache updates

### **7. Image Management**
- Dynamic restaurant image loading
- Placeholder and error handling
- Smooth transitions with Glide
- Optimized caching

---

## Setup & Installation

### **Prerequisites**
- Android Studio Meerkat
- Android SDK API Level 24+
- Java 8+ development environment
- Running distributed server system

### **Installation Steps**

1. **Open in Android Studio**
   - Import project
   - Sync Gradle files
   - Resolve dependencies

2. **Server Configuration**
   - Ensure distributed server is running
   - Configure server IP in MainActivity
   - Default ports: Master(9000), Reducer(8080), Workers(9001-9003)

3. **Build & Run**

### **Configuration**
- **Server IP**: Configurable in app (default: 10.0.2.2 for emulator)
- **Timeouts**: 10 seconds for socket operations
- **Search Radius**: 5km default for initial search
- **Image Loading**: Glide with placeholder and error handling
- **Threading**: Optimized for automatic resource management