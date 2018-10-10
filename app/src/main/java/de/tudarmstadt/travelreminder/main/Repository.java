package de.tudarmstadt.travelreminder.main;

import android.location.Location;

import org.joda.time.DateTime;

import java.util.ArrayList;

import de.tudarmstadt.travelreminder.main.model.LocationModel;
import de.tudarmstadt.travelreminder.main.model.RoutePlanModel;
import de.tudarmstadt.travelreminder.main.model.TravelMode;


/**
 * Repository is an abstract class of a service to resolve and calculate route plans. May an
 * implementation will call external services.
 * <p>
 * An implementation should initialize a route plan, update a route plan departure time and resolve
 * a given location. As additional method it should be able to autocomplete a given address to an
 * valid location.
 * </p>
 * <p>
 * An implementation may be an singleton.
 *
 * @param <P> A <p>RoutePlanModel</p> implementation class.
 * @param <L> A <p>LocationModel</p> implementation class.
 * @since 1.0
 */
public abstract class Repository<P extends RoutePlanModel, L extends LocationModel> {

    private Location currentLocation;

    /**
     * Updates the departure time of the given route plan. If possible it should consider the
     * traffic and other parameters.
     *
     * @param plan The rout plan to be updated.
     */
    public abstract void update(P plan);

    /**
     * Resolves a location to a valid service location object.
     *
     * @param location The location to be resolved.
     */
    public abstract void resolve(L location);

    /**
     * Autocomplete a given string to a list of valid possible locations.
     *
     * @param address The address string.
     * @return The list of valid possible locations.
     */
    public abstract ArrayList<L> autoComplete(String address);

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }
}
