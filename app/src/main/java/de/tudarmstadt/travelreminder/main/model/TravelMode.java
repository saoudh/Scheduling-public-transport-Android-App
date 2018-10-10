package de.tudarmstadt.travelreminder.main.model;

import android.support.annotation.StringRes;

import de.tudarmstadt.travelreminder.R;

/**
 * Enumeration of the transportation mode.
 */
public enum TravelMode {
    // Valid Values
    DRIVING, WALKING, BICYCLING, TRANSIT, UNKNOWN;

    @StringRes
    public int getTextValueId() {
        switch (this) {
            case DRIVING:
                return R.string.driving;
            case BICYCLING:
                return R.string.bicycling;
            case TRANSIT:
                return R.string.transit;
            case WALKING:
                return R.string.walking;
            case UNKNOWN:
            default:
                return R.string.unknown;
        }
    }
}
