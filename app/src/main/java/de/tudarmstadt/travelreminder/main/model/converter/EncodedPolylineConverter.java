package de.tudarmstadt.travelreminder.main.model.converter;

import android.arch.persistence.room.TypeConverter;

import com.google.maps.model.EncodedPolyline;


/**
 * Converts the EncodedPolyline from and to an Database usable format.
 */
public class EncodedPolylineConverter {

    /**
     * Converts the database value to a valid EncodedPolyline value.
     * @param s The database value.
     * @return The equivalent EncodedPolyline.
     */
    @TypeConverter
    public static EncodedPolyline toObject(String s) {
        if (s == null || s.length() == 0) return null;
        return new EncodedPolyline(s);
    }

    /**
     * Converts the EncodedPolyline to an database usable value.
     * @param polyline The EncodedPolyline to convert.
     * @return The equivalent database value.
     */
    @TypeConverter
    public static String toDataBaseType(EncodedPolyline polyline) {
        if (polyline == null) return null;
        return polyline.getEncodedPath();
    }
}
