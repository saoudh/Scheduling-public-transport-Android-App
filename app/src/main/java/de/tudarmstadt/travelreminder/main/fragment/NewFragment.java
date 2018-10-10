package de.tudarmstadt.travelreminder.main.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import de.tudarmstadt.travelreminder.R;
import de.tudarmstadt.travelreminder.main.Factory;
import de.tudarmstadt.travelreminder.main.MainActivity;
import de.tudarmstadt.travelreminder.main.adapter.LocationModelAdapter;
import de.tudarmstadt.travelreminder.main.model.LocationModel;
import de.tudarmstadt.travelreminder.main.model.RoutePlanModel;
import de.tudarmstadt.travelreminder.main.model.TravelMode;
import de.tudarmstadt.travelreminder.main.model.converter.DateConverter;
import de.tudarmstadt.travelreminder.main.widget.DateTimePicker;
import de.tudarmstadt.travelreminder.main.widget.LocationSelector;


/**
 * Fragment to edit or create an RoutePlanModel.
 *
 * @arg RoutePlanModel {@link NewFragment}.ARG_ROUTE_PLAN The RoutePlan to edit or an empty route plan.
 * @arg FactoryInterface {@link MainActivity}.ARG_FACTORY The factory instance to create and fetch data.
 */


public class NewFragment extends Fragment implements OnRoutePlanModelUpdatedListener, MapFragment.MapReadyListener {

    /**
     * The tag of this fragment for the FragmentManager.
     */
    public static final String TAG = "NEW_FRAGMENT";

    /**
     * The tag of the MapFragment for the FragmentManager.
     */
    public static final String TAG_MAP = "MAP_FRAGMENT";

    /**
     * The argument key of the RoutePlanModel
     */
    public static final String ARG_ROUTE_PLAN = "route_plan";

    /**
     * The RoutePlanModel to edit or create.
     */
    RoutePlanModel plan;

    /**
     * The factory to receive special implemented Objects.
     */
    Factory factory;

    // Fragment Lifecycle

    /**
     * Called when the fragment is created.
     * @param savedInstanceState Saved instance.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("NewFragment", "onCreate called");
        // Use new RoutePlanModel if none is given.
        if (getRoutePlanModel() == null) {
            this.getArguments().putParcelable(ARG_ROUTE_PLAN, factory.createRoutePlanModel());
        }
    }

    /**
     * Called when the fragment should create its View.
     * @param inflater LayoutInflater to inflate the fragment layout.
     * @param container Parent view.
     * @param savedInstanceState Saved bundle.
     * @return The fragment view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("NewFragment", "onCreateView called");
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.new_fragment, container, false);
    }

    /**
     * Called when the activity that holds this fragment is created.
     * The view is complete and the context is ready.
     * @param savedInstanceState The saved instance from a previous state.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("NewFragment", "onActivityCreated called");
        // Setup and view the MapFragment
        this.setupMapFragment();



        this.setupModeSelector();
        this.setupOriginEditText();
        this.setupDestinationEditText();
        this.setupDateTimePicker();

        // @TODO verbessern
        new UpdateRoutePlanModelTask(this).execute(plan);
    }

    /**
     * Called if the fragment is be shown again or shown for the first time.
     * Setting the BackButton.
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d("NewFragment", "onResume called");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Helper methods

    /**
     * Returns the current RoutePlanModel set by the arguments.
     * @return The current RoutePlanModel
     */
    private RoutePlanModel getRoutePlanModel() {
        if (this.plan == null) {
            this.plan = (RoutePlanModel) getArguments().get(ARG_ROUTE_PLAN);
        }
        return this.plan;
    }

    private Factory getFactory() {
        if (this.factory == null) {
            this.factory = (Factory) getArguments().get(MainActivity.ARG_FACTORY);
        }
        return this.factory;
    }



    // MapFragment related methods

    /**
     * Adds the MapFragment to the view.
     */
    private void setupMapFragment() {
        MapFragment mf = this.getCurrentMapFragment();
        if (mf == null) {
            mf = new MapFragment();
            mf.setOnMapReadyListener(this);
            getChildFragmentManager().beginTransaction()
                    .add(R.id.new_fragment_maps_fragment_container, mf, TAG_MAP)
                    .commit();
            getChildFragmentManager().executePendingTransactions();
        }
    }

    /**
     * Returns the current MapFragment registered to the ChildFragmentManger.
     * @return The current MapFragment.
     */
    private MapFragment getCurrentMapFragment() {
        return (MapFragment) getChildFragmentManager()
                .findFragmentByTag(TAG_MAP);
    }

    @Override
    public void onMapReady() {
        // Draw the current RoutePlanModel
        getCurrentMapFragment().draw((RoutePlanModel) getArguments().get(ARG_ROUTE_PLAN));
    }


    // TravelMode related methods

    /**
     * Setup the mode Selector with a TabSelectedListener.
     * It will set the selected tab and the TravelMode of the RoutePlanModel if necessary.
     */
    public void setupModeSelector() {
        TabLayout tl = (TabLayout) this.getView().findViewById(R.id.new_fragment_mode);
        tl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setTravelModeIntoPlan(tab.getPosition());
            }

            // Do Nothing
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        RoutePlanModel plan = (RoutePlanModel) getArguments().get(ARG_ROUTE_PLAN);
        if (plan.getMode() == null) {
            setTravelModeIntoPlan(tl.getSelectedTabPosition());
            return;
        }

        switch (plan.getMode()) {
            case BICYCLING:
                tl.getTabAt(1).select();
                break;
            case WALKING:
                tl.getTabAt(2).select();
                break;
            case TRANSIT:
                tl.getTabAt(3).select();
                break;
            case DRIVING:
            case UNKNOWN:
            default:
                tl.getTabAt(0).select();
                break;
        }
    }

    /**
     * Sets the TravelMode to the RoutePlanModel by the position of the selected tab.
     * @param position The selected tab position.
     */
    private void setTravelModeIntoPlan(int position) {
        RoutePlanModel plan = getRoutePlanModel();
        switch (position) {
            case 0:
                plan.setMode(TravelMode.DRIVING);
                break;
            case 1:
                plan.setMode(TravelMode.BICYCLING);
                break;
            case 2:
                plan.setMode(TravelMode.WALKING);
                break;
            case 3:
                plan.setMode(TravelMode.TRANSIT);
                break;
            default:
                plan.setMode(TravelMode.UNKNOWN);
                break;
        }
        dataChanges();
        // @TODO Verbessern!
        new UpdateRoutePlanModelTask(this).execute(plan);
    }

    // Origin location related Methods
    /**
     * Setup the origin location Selector.
     * It will set the text of the LocationSelector to the text of the origin from the RoutePlanModel.
     */
    public void setupOriginEditText() {
        // The AutoCompleteEditTextView
        final LocationSelector et =
                (LocationSelector) this.getView().findViewById(R.id.new_fragment_origin);
        // ProgressBar indicator that is displayed, if the AutoComplete
        ProgressBar pb =
                (ProgressBar) this.getView().findViewById(R.id.new_fragment_origin_progress);

        // Create the autocomplete adapter
        LocationModelAdapter adapter = getFactory().createLocationModelAdapter(this.getContext());

        // add everything to the location selector
        et.setAdapter(adapter);
        et.setProgressBar(pb);
        et.setFactory(getFactory());
        et.setOnLocationSelectedListner(new LocationSelector.OnLocationSelectedListener() {
            @Override
            public void onSelected(LocationSelector selector, LocationModel location) {
                getRoutePlanModel().setOrigin(location);
                dataChanges();
                clearError(R.id.new_fragment_origin_layout);
                // @TODO: Verbessern
                new UpdateRoutePlanModelTask(NewFragment.this).execute(getRoutePlanModel());
            }
        });

        if (getRoutePlanModel().getOrigin() != null) {
            et.setText(getRoutePlanModel().getOrigin().getName());
        }
    }


    // Destination related methods
    /**
     * Setup the destination location Selector.
     * It will set the text of the LocationSelector to the text of the destination from the
     * RoutePlanModel.
     */
    public void setupDestinationEditText() {
        // The AutoCompleteEditTextView
        final LocationSelector et =
                (LocationSelector) this.getView().findViewById(R.id.new_fragment_destination);
        // ProgressBar indicator that is displayed, if the AutoComplete
        ProgressBar pb =
                (ProgressBar) this.getView().findViewById(R.id.new_fragment_destination_progress);

        // Create the autocomplete adapter
        LocationModelAdapter adapter = getFactory().createLocationModelAdapter(this.getContext());

        // add everything to the location selector
        et.setAdapter(adapter);
        et.setProgressBar(pb);
        et.setFactory(getFactory());
        et.setOnLocationSelectedListner(new LocationSelector.OnLocationSelectedListener() {
            @Override
            public void onSelected(LocationSelector selector, LocationModel location) {
                plan.setDestination(location);
                dataChanges();
                clearError(R.id.new_fragment_destination_layout);
                new UpdateRoutePlanModelTask(NewFragment.this).execute(plan);
            }
        });
        if (getRoutePlanModel().getDestination() != null) {
            et.setText(getRoutePlanModel().getDestination().getName());
        }
    }

    // Arrival time Setup

    /**
     * Setup the arrival time selector.
     * It will set the text of the selector to the text of the arrival time from the
     * RoutePlanModel.
     */
    public void setupDateTimePicker() {
        DateTimePicker dtp = (DateTimePicker) this.getView().findViewById(R.id.new_fragment_arrival_time);
        dtp.setOnDateTimeSetListener(new DateTimePicker.OnDateTimeSetListener() {
            @Override
            public void onDateTimeSet(DateTime dateTime) {
                plan.setArrivalTime(dateTime);
                dataChanges();
                clearError(R.id.new_fragment_arrival_time_layout);
                new UpdateRoutePlanModelTask(NewFragment.this).execute(plan);
            }
        });
        if (plan.getArrivalTime() != null) {
            dtp.setDateTime(plan.getArrivalTime());
        }
    }

    // Setup the ActionBar

    /**
     * Setup the options menu.
     * @param menu Parent menu.
     * @param inflater Menu inflater.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.new_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Called if an action bar is clicked
     * @param item The item that was clicked
     * @return True if handled.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.new_fragment_save) {
            if (!validate()) return false;
            final View progressbar = this.getView().findViewById(R.id.new_fragment_save_progress_bar);
            progressbar.setVisibility(View.VISIBLE);
            new UpdateRoutePlanModelTask(new OnRoutePlanModelUpdatedListener() {
                @Override
                public void onRoutePlanModelUpdated(boolean success) {
                    progressbar.setVisibility(View.GONE);
                    if (!success) return;
                    factory.createRoutePlanViewModel(NewFragment.this.getActivity()).saveRoutePlan(plan);
                    getActivity().getSupportFragmentManager().popBackStack(TAG, 1);

                }
            }).execute(plan);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Validates the current RoutePlanModel, sets error messages if necessary and returns the valid
     * status.
     * @return True if the current RoutePlanModel is valid.
     */
    private boolean validate() {
        boolean valid = true;

        if (plan.getOrigin() == null) {
            setError(R.id.new_fragment_origin_layout, R.string.missing_origin);
            valid = false;
        }


        if (plan.getDestination() == null) {
            setError(R.id.new_fragment_destination_layout, R.string.missing_destination);
            valid = false;
        }

        if (plan.getArrivalTime() == null) {
            setError(R.id.new_fragment_arrival_time_layout, R.string.missing_arrival_time);
            valid = false;
        }

        return valid && plan.isValid();
    }

    /**
     * Sets the error message to a TextInputLayout element.
     * @param id Id of the TextInputLayout.
     * @param msg Message to add.
     */
    private void setError(@IdRes int id, @Nullable @StringRes int msg) {
        ((TextInputLayout) this.getView().findViewById(id)).setError(getString(msg));
    }

    /**
     * Removes the error message from a TextInputLayout element.
     * @param id Id of the TextInputLayout.
     */
    private void clearError(@IdRes int id) {
        if (getView() == null) return;
        ((TextInputLayout) this.getView().findViewById(id)).setError(null);
    }


    @Override
    public void onRoutePlanModelUpdated(boolean success) {
        if (!success || getView() == null) return;
        getCurrentMapFragment().draw(plan);

        if (plan.getDepartureTime() != null) {
            String dep = DateConverter.format(this.getContext(), plan.getDepartureTime());
            ((TextView) getView().findViewById(R.id.new_fragment_departure_time)).setText(dep);
            getView().findViewById(R.id.new_fragment_departure_time_container).setVisibility(View.VISIBLE);
        }

        if (plan.getDelayedDepartureTime() != null && plan.getDelay().isLongerThan(Duration.ZERO)) {
            String dep = DateConverter.format(plan.getDelay());
            ((TextView) getView().findViewById(R.id.new_fragment_delayed_departure_time)).setText(
                String.format("+%s", dep)
            );
        } else {
            ((TextView) getView().findViewById(R.id.new_fragment_delayed_departure_time)).setText("");
        }
    }

    private void dataChanges() {
        plan.setNotified(false);
        plan.setMapBounds(null);
        plan.setPolyline(null);
        plan.setDepartureTime(null);
        plan.setDelayedDepartureTime(null);
        if (getView() == null) return;
        View v = getView().findViewById(R.id.new_fragment_departure_time_container);
        if (v != null)
            v.setVisibility(View.INVISIBLE);
    }

    // Classes
    /**
     * Updates a given RoutePlanModel with the Repository.
     * @TODO replace with AsyncTaskLoader?
     */
    private class UpdateRoutePlanModelTask extends AsyncTask<RoutePlanModel, Void, Boolean> {

        OnRoutePlanModelUpdatedListener listener;

        UpdateRoutePlanModelTask(OnRoutePlanModelUpdatedListener listener) {
            this.listener = listener;
        }

        @Override
        protected Boolean doInBackground(RoutePlanModel... params) {
            if (params.length == 0) return Boolean.FALSE;
            getFactory().getRepository().update(params[0]);
            if (params[0].getDelayedDepartureTime() != null && params[0].getDelayedDepartureTime().equals(params[0].getDepartureTime()))
                getFactory().getRepository().update(params[0]);
            return Boolean.TRUE;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (listener != null)
                listener.onRoutePlanModelUpdated(success);
        }
    }
}
