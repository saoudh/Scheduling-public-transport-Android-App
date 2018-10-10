package de.tudarmstadt.travelreminder.main.model.converter;

import android.arch.persistence.room.TypeConverter;

import de.tudarmstadt.travelreminder.main.model.TravelMode;

/**
 * Converts the TravelMode from and to an Database usable format.
 */
public class TravelModeConverter {
    /**
     * Converts the database value to a valid TravelMode value.
     * @param mode The database value.
     * @return The equivalent TravelMode.
     */
    @TypeConverter
    public static TravelMode toTravelMode(int mode) {
        switch (mode) {
            case 0:
                return TravelMode.BICYCLING;
            case 1:
                return TravelMode.DRIVING;
            case 2:
                return TravelMode.TRANSIT;
            case 3:
                return TravelMode.WALKING;
            case -1:
            default:
                return TravelMode.UNKNOWN;
        }
    }

    /**
     * Converts the TravelMode to an database usable value.
     * @param mode The TravelMode to convert.
     * @return The equivalent database value.
     */
    @TypeConverter
    public static int toTravelModeIdentifier(TravelMode mode) {
        if (mode == null) return -1;
        switch (mode) {
            case BICYCLING:
                return 0;
            case DRIVING:
                return 1;
            case TRANSIT:
                return 2;
            case WALKING:
                return 3;
            case UNKNOWN:
            default:
                return -1;
        }
    }
}
