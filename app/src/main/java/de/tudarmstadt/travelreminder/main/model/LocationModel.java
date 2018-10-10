package de.tudarmstadt.travelreminder.main.model;


import android.arch.persistence.room.Embedded;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.maps.model.LatLng;

import java.io.Serializable;

abstract public class LocationModel implements Parcelable {
    private String name;
    @Embedded(prefix = "position")
    private LatLng position;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public boolean isValid() {
        return this.name != null && this.name.length() > 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getName());
        if (getPosition() == null) {
            dest.writeDouble(0);
            dest.writeDouble(0);
        } else {
            dest.writeDouble(getPosition().lat);
            dest.writeDouble(getPosition().lng);
        }
    }
}
