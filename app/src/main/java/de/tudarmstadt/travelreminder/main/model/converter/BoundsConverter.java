package de.tudarmstadt.travelreminder.main.model.converter;

import android.arch.persistence.room.TypeConverter;

import com.google.maps.model.Bounds;
import com.google.maps.model.LatLng;

/**
 * Converts the Bounds from and to an Database usable format.
 */
public class BoundsConverter {

    /**
     * Converts the database value to a valid Bounds value.
     * @param s The database value.
     * @return The equivalent Bounds.
     */
    @TypeConverter
    public static Bounds toObject(String s) {
        if (s == null || s.length() == 0) return null;
        String[] parts = s.split(":");
        if (parts.length != 4) return null;
        Bounds b = new Bounds();
        b.northeast = new LatLng(
                Double.valueOf(parts[0]),
                Double.valueOf(parts[1])
        );
        b.southwest = new LatLng(
                Double.valueOf(parts[2]),
                Double.valueOf(parts[3])
        );
        return b;
    }

    /**
     * Converts the Bounds to an database usable value.
     * @param bounds The Bounds to convert.
     * @return The equivalent database value.
     */
    @TypeConverter
    public static String toDataBaseType(Bounds bounds) {
        if (bounds == null) return null;
        StringBuilder sb = new StringBuilder();
        sb.append(bounds.northeast.lat);
        sb.append(':');
        sb.append(bounds.northeast.lng);
        sb.append(':');
        sb.append(bounds.southwest.lng);
        sb.append(':');
        sb.append(bounds.southwest.lng);
        return sb.toString();
    }
}
