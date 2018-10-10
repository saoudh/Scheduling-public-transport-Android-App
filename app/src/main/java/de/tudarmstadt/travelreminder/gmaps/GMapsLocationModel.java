package de.tudarmstadt.travelreminder.gmaps;

import android.arch.persistence.room.Ignore;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.maps.model.LatLng;

import org.joda.time.DateTime;

import java.io.Serializable;

import de.tudarmstadt.travelreminder.main.model.LocationModel;
import de.tudarmstadt.travelreminder.main.model.converter.BoundsConverter;
import de.tudarmstadt.travelreminder.main.model.converter.EncodedPolylineConverter;
import de.tudarmstadt.travelreminder.main.model.converter.TravelModeConverter;


/**
 * An Google Maps implementation of the LocationModel
 */
public class GMapsLocationModel extends LocationModel  {
    /**
     * The given Place ID to identify this Location by the google maps services.
     */
    private String placeId;

    /**
     * Sets the Google Maps place id.
     * @return The Google Maps place id.
     */
    public String getPlaceId() {
        return placeId;
    }

    /**
     * Returns the Google Maps place id.
     * @param placeId Null if not set | The Google Maps place id.
     */
    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.d("GMapsLocationModel", "writeToParcel");
        super.writeToParcel(dest, flags);
        dest.writeString(getPlaceId());
    }

    /**
     * Parcelable.Creator instance to create a GMapsLocationModel with the Parcelable app logic.
     */
    public static final Parcelable.Creator<GMapsLocationModel> CREATOR
            = new Parcelable.Creator<GMapsLocationModel>() {
        /**
         * Creates the GMapsLocationModel
         * @param in Parcel information.
         * @return A instance of the GMapsRoutePlanModel.
         */
        public GMapsLocationModel createFromParcel(Parcel in) {
            GMapsLocationModel location = new GMapsLocationModel();

            location.setName(in.readString());

            Double lat = in.readDouble();
            Double lng = in.readDouble();

            if (lat != 0 && lng != 0) {
                LatLng position = new LatLng(
                        lat,
                        lng
                );
                location.setPosition(position);
            }

            location.setPlaceId(in.readString());
            return location;
        }

        /**
         * Creates a new Array of GMapsLocationModel instances.
         * @param size Size of the array.
         * @return Returns empty array.
         */
        public GMapsLocationModel[] newArray(int size) {
            return new GMapsLocationModel[size];
        }
    };
}
