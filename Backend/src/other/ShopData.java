package other;

import java.io.Serializable;

/**
 * Thread-safe wrapper for a Shop and its two version counters:
 *  - clientVersion:   incremented when data clients care about changes
 *  - managerVersion:  incremented when manager-only data changes
 */
public class ShopData implements Serializable {
    private Shop shop;
    private long clientVersion;
    private long managerVersion;

    public ShopData(Shop shop) {
        this.shop = shop;
        this.clientVersion = 0;
        this.managerVersion = 0;
    }

    /** Returns a (shallow) copy of the Shop object. */
    public synchronized Shop getShop() {
        return shop;
    }

    /**
     * Called when a client-visible field changes.
     * Increments the clientVersion counter.
     */
    public synchronized void applyClientUpdate(Shop newShopState) {
        this.shop = newShopState;
        this.clientVersion++;
    }

    /**
     * Called when a manager-only field changes (e.g. revenue).
     * Increments the managerVersion counter, but leaves clientVersion alone.
     */
    public synchronized void applyManagerUpdate(Shop newShopState) {
        this.shop = newShopState;
        this.managerVersion++;
    }

    /** The version clients should use when polling. */
    public synchronized long getClientVersion() {
        return clientVersion;
    }

    /** The version managers should use when polling or syncing. */
    public synchronized long getManagerVersion() {
        return managerVersion;
    }
}