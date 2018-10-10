package de.tudarmstadt.travelreminder.gmaps;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.maps.model.EncodedPolyline;

import org.joda.time.DateTime;

import java.io.Serializable;

import de.tudarmstadt.travelreminder.main.model.RoutePlanModel;
import de.tudarmstadt.travelreminder.main.model.TravelMode;
import de.tudarmstadt.travelreminder.main.model.converter.BoundsConverter;
import de.tudarmstadt.travelreminder.main.model.converter.EncodedPolylineConverter;
import de.tudarmstadt.travelreminder.main.model.converter.TravelModeConverter;


/**
 * An implementation of the <code>{@link RoutePlanModel}</code>.
 *
 * This Model can be saved into the gmaps_route_plan table by the
 * <code>{@link GMapsRoutePlanDao}</code>.
 */
@Entity(tableName = "gmaps_route_plan")
public class GMapsRoutePlanModel extends RoutePlanModel<GMapsLocationModel> {

    /**
     * Origin location.
     */
    @Embedded(prefix = "origin_")
    private GMapsLocationModel origin;

    /**
     * Destination location.
     */
    @Embedded(prefix = "destination_")
    private GMapsLocationModel destination;

    // Overridden methods of the RoutePlanModel
    @Override
    public GMapsLocationModel getOrigin() {
        return this.origin;
    }

    @Override
    public void setOrigin(GMapsLocationModel origin) {
        this.origin = origin;
    }

    @Override
    public GMapsLocationModel getDestination() {
        return this.destination;
    }

    @Override
    public void setDestination(GMapsLocationModel destination) {
        this.destination = destination;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(getOrigin(), flags);
        dest.writeParcelable(getDestination(), flags);
    }

    /**
     * Parcelable.Creator instance to create a GMapsRoutePlanModel with the Parcelable app logic.
     */
    public static final Parcelable.Creator<GMapsRoutePlanModel> CREATOR
            = new Parcelable.Creator<GMapsRoutePlanModel>() {
        /**
         * Creates the GMapsRoutePlanModel
         * @param in Parcel information.
         * @return A instance of the GMapsRoutePlanModel.
         */
        public GMapsRoutePlanModel createFromParcel(Parcel in) {
            Log.d("GMapsRoutePlanModel", "createFromParcel");
            GMapsRoutePlanModel plan = new GMapsRoutePlanModel();
            Long longIn = in.readLong();
            if (longIn > 0) {
                plan.setId(longIn);
            }

            longIn = in.readLong();
            if (longIn > 0) {
                plan.setArrivalTime(new DateTime(longIn));
            }
            longIn = in.readLong();
            if (longIn > 0) {
                plan.setDepartureTime(new DateTime(longIn));
            }
            longIn = in.readLong();
            if (longIn > 0) {
                plan.setDelayedDepartureTime(new DateTime(longIn));
            }

            plan.setMode(TravelModeConverter.toTravelMode(in.readInt()));

            String stringIn = in.readString();
            if (stringIn != null) {
                plan.setPolyline(EncodedPolylineConverter.toObject(stringIn));
            }
            stringIn = in.readString();
            if (stringIn != null) {
                plan.setMapBounds(BoundsConverter.toObject(stringIn));
            }

            plan.setOrigin((GMapsLocationModel) in.readParcelable(GMapsLocationModel.class.getClassLoader()));

            GMapsLocationModel dest = in.readParcelable(GMapsLocationModel.class.getClassLoader());
            Log.d("GMapsRoutePlanModel", dest != null?dest.getName():"null");
            plan.setDestination(dest);
            return plan;
        }

        /**
         * Creates a new Array of GMapsRoutePlanModel instances.
         * @param size Size of the array.
         * @return Returns empty array.
         */
        public GMapsRoutePlanModel[] newArray(int size) {
            return new GMapsRoutePlanModel[size];
        }
    };
}
