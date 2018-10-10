package de.tudarmstadt.travelreminder.main;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import de.tudarmstadt.travelreminder.main.adapter.LocationModelAdapter;
import de.tudarmstadt.travelreminder.main.adapter.RoutePlanModelListAdapter;
import de.tudarmstadt.travelreminder.main.model.LocationModel;
import de.tudarmstadt.travelreminder.main.model.RoutePlanModel;

/**
 * Factory interface that generate all necessary objects.
 *
 * @param <P>  An implementation of <code>RoutePlanModel<L></code>
 * @param <L>  An implementation of <code>LocationModel</code>
 * @param <VM> An implementation of <code>RoutePlanViewModel<P></code>
 * @param <LA> An implementation of <code>LocationModelAdapter<L></code>
 * @param <RA> An implementation of <code>RoutePlanModelListAdapter<P, VM></code>
 */
public abstract class Factory<
        P extends RoutePlanModel<L>,
        L extends LocationModel,
        VM extends RoutePlanViewModel<P>,
        LA extends LocationModelAdapter<L>,
        RA extends RoutePlanModelListAdapter<P, VM>,
        R extends Repository<P, L>
        > implements Parcelable {


    Location currentLocation;

    /**
     * Creates a new RoutePlanModel.
     *
     * @return The new RoutePlanModel.
     */
    public abstract P createRoutePlanModel();

    public abstract L createLocationModel();

    /**
     * @param fragment
     * @return
     */
    public abstract VM createRoutePlanViewModel(Fragment fragment);

    public abstract VM createRoutePlanViewModel(Application application);

    public abstract VM createRoutePlanViewModel(FragmentActivity activity);

    public abstract LA createLocationModelAdapter(Context context);

    public abstract RA createRoutePlanModelListAdapter(VM viewModel);

    public abstract R getRepository();


    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(getCurrentLocation(), flags);
    }
}
