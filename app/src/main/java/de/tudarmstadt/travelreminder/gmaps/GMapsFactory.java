package de.tudarmstadt.travelreminder.gmaps;


import android.app.Application;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.util.Locale;

import de.tudarmstadt.travelreminder.main.Factory;
import de.tudarmstadt.travelreminder.main.adapter.RoutePlanModelListAdapter;

/**
 * Google Maps implementation of the FactoryInterface
 */
public class GMapsFactory extends Factory<
        GMapsRoutePlanModel,
        GMapsLocationModel,
        GMapsRoutePlanViewModel,
        GMapsLocationModelAdapter,
        RoutePlanModelListAdapter<GMapsRoutePlanModel, GMapsRoutePlanViewModel>,
        GMapsRepository
        > {

    /**
     * Parcelable.Creator instance to create a GMapsFactory with the Parcelable app logic.
     */
    public static final Parcelable.Creator<GMapsFactory> CREATOR
            = new Parcelable.Creator<GMapsFactory>() {
        /**
         * Creates the GMapsFactory
         * @param in Parcel information.
         * @return A instance of the GMapsFactory.
         */
        public GMapsFactory createFromParcel(Parcel in) {
            // API KEY FIRST!
            GMapsFactory factory = new GMapsFactory(in.readString());
            factory.setCurrentLocation((Location) in.readParcelable(Location.class.getClassLoader()));
            return factory;
        }

        /**
         * Creates a new Array of GMapsFactory instances.
         * @param size Size of the array.
         * @return Returns empty array.
         */
        public GMapsFactory[] newArray(int size) {
            return new GMapsFactory[size];
        }
    };

    private String apiKey;

    /**
     * A instance of the GMapsRepository.
     */
    private GMapsRepository repo;

    public GMapsFactory(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public GMapsRoutePlanModel createRoutePlanModel() {
        return new GMapsRoutePlanModel();
    }

    @Override
    public GMapsLocationModel createLocationModel() {
        return new GMapsLocationModel();
    }

    @Override
    public GMapsRoutePlanViewModel createRoutePlanViewModel(Application application) {
        return new GMapsRoutePlanViewModel(application);
    }

    @Override
    public GMapsRoutePlanViewModel createRoutePlanViewModel(Fragment fragment) {
        return ViewModelProviders.of(fragment).get(GMapsRoutePlanViewModel.class);
    }

    @Override
    public GMapsRoutePlanViewModel createRoutePlanViewModel(FragmentActivity activity) {
        return ViewModelProviders.of(activity).get(GMapsRoutePlanViewModel.class);
    }

    @Override
    public GMapsLocationModelAdapter createLocationModelAdapter(Context context) {
        return new GMapsLocationModelAdapter(context, this.getRepository());
    }

    @Override
    public RoutePlanModelListAdapter<GMapsRoutePlanModel, GMapsRoutePlanViewModel> createRoutePlanModelListAdapter(GMapsRoutePlanViewModel vm) {
        return new RoutePlanModelListAdapter<>(vm);
    }

    @Override
    public GMapsRepository getRepository() {
        if (repo == null) {
            repo = new GMapsRepository(
                this.apiKey,
                Locale.getDefault().getLanguage()
            );
        }
        if (this.getCurrentLocation() != null) {
            repo.setCurrentLocation(this.getCurrentLocation());
        }
        return repo;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.apiKey);
        super.writeToParcel(dest, flags);
    }
}
