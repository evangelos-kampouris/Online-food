package other;

import java.io.Serial;
import java.io.Serializable;

public class Coordinates implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private double latitude;
    private double longitude;

    public Coordinates(double Latitude, double Longitude) {
        this.latitude = Latitude;
        this.longitude = Longitude;
    }

    @Override
    public String toString() {
        return "Latitude: " + latitude + "," + " longitude " + longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}