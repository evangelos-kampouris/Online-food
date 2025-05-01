package other;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a pair of geographical coordinates (latitude and longitude).
 * Includes a utility method for calculating distances between coordinates.
 */
public class Coordinates implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private double latitude;
    private double longitude;

    public Coordinates() {}

    /**
     * Constructs a coordinate pair with the specified latitude and longitude.
     *
     * @param latitude the latitude component
     * @param longitude the longitude component
     */
    public Coordinates(double Latitude, double Longitude) {
        this.latitude = Latitude;
        this.longitude = Longitude;
    }
    // Haversine formula to calculate distance between two coordinates in kilometers.
    /**
     * Calculates the distance between two geographical points using the Haversine formula.
     *
     * @param lat1 the latitude of the first point
     * @param lon1 the longitude of the first point
     * @param lat2 the latitude of the second point
     * @param lon2 the longitude of the second point
     * @return the distance in kilometers between the two points
     */
    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.pow(Math.sin(dLon / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    @Override
    public String toString() {
        return "Latitude: " + latitude + "," + " longitude " + longitude;
    }

    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}
