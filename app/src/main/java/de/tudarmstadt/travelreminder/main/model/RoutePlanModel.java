package de.tudarmstadt.travelreminder.main.model;

import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.maps.model.Bounds;
import com.google.maps.model.EncodedPolyline;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import de.tudarmstadt.travelreminder.main.model.converter.BoundsConverter;
import de.tudarmstadt.travelreminder.main.model.converter.DateConverter;
import de.tudarmstadt.travelreminder.main.model.converter.EncodedPolylineConverter;
import de.tudarmstadt.travelreminder.main.model.converter.TravelModeConverter;


abstract public class RoutePlanModel<L extends LocationModel> implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public Long id;

    private boolean notified;

    @TypeConverters(DateConverter.class)
    private DateTime arrivalTime;

    @TypeConverters(DateConverter.class)
    private DateTime departureTime;

    @TypeConverters(DateConverter.class)
    private DateTime delayedDepartureTime;

    @TypeConverters(TravelModeConverter.class)
    private TravelMode mode;

    @TypeConverters(EncodedPolylineConverter.class)
    private EncodedPolyline polyline;

    @TypeConverters(BoundsConverter.class)
    private Bounds mapBounds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(DateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public DateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(DateTime departureTime) {
        this.departureTime = departureTime;
    }

    public DateTime getDelayedDepartureTime() {
        if (this.delayedDepartureTime == null) return getDepartureTime();
        return delayedDepartureTime;
    }

    public void setDelayedDepartureTime(DateTime delayedDepartureTime) {
        this.delayedDepartureTime = delayedDepartureTime;
    }

    public TravelMode getMode() {
        return mode;
    }

    public void setMode(TravelMode mode) {
        this.mode = mode;
    }

    public Duration getDelay() {
        Duration d = new Duration(this.getDelayedDepartureTime(), this.getDepartureTime());
        d.toPeriod();
        return d;
    }

    public boolean isValid() {
        return this.getOrigin() != null
                && this.getDestination() != null
                && this.getOrigin().isValid()
                && this.getDestination().isValid();
    }

    public EncodedPolyline getPolyline() {
        return polyline;
    }

    public void setPolyline(EncodedPolyline polyline) {
        this.polyline = polyline;
    }

    public Bounds getMapBounds() {
        return mapBounds;
    }

    public void setMapBounds(Bounds mapBounds) {
        this.mapBounds = mapBounds;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    public abstract L getOrigin();

    public abstract void setOrigin(L origin);

    public abstract L getDestination();

    public abstract void setDestination(L destination);

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (this.getId() == null) {
            dest.writeLong(0);
        } else {
            dest.writeLong(this.getId());
        }

        if (this.getArrivalTime() == null) {
            dest.writeLong(0);
        } else {
            dest.writeLong(this.getArrivalTime().getMillis());
        }

        if (this.getDepartureTime() == null) {
            dest.writeLong(0);
        } else {
            dest.writeLong(this.getDepartureTime().getMillis());
        }

        if (this.getDelayedDepartureTime() == null) {
            dest.writeLong(0);
        } else {
            dest.writeLong(this.getDelayedDepartureTime().getMillis());
        }

        if (this.getMode() == null) {
            dest.writeInt(TravelModeConverter.toTravelModeIdentifier(TravelMode.UNKNOWN));
        } else {
            dest.writeInt(TravelModeConverter.toTravelModeIdentifier(getMode()));
        }

        if (getPolyline() == null) {
            dest.writeString(null);
        } else {
            dest.writeString(EncodedPolylineConverter.toDataBaseType(getPolyline()));
        }

        if (getMapBounds() == null) {
            dest.writeString(null);
        } else {
            dest.writeString(BoundsConverter.toDataBaseType(getMapBounds()));
        }
    }
}
