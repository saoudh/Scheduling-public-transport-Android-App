package de.tudarmstadt.travelreminder.main.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.tudarmstadt.travelreminder.R;
import de.tudarmstadt.travelreminder.main.Factory;
import de.tudarmstadt.travelreminder.main.MainActivity;
import de.tudarmstadt.travelreminder.main.model.RoutePlanModel;
import de.tudarmstadt.travelreminder.main.model.converter.DateConverter;


/**
 * The DetailFragment displays the details of an single RoutePlanModel.
 *
 * @arg RoutePlanModel {@link DetailFragment}.ARG_ROUTE_PLAN The RoutePlan to edit or an empty route plan.
 * @arg FactoryInterface {@link MainActivity}.ARG_FACTORY The factory instance to create and fetch data.
 */
public class DetailFragment extends Fragment implements MapFragment.MapReadyListener {
    /**
     * The tag of this fragment for the FragmentManager.
     */
    public static final String TAG = "DETAIL_FRAGMENT";

    public static final String TAG_MAP = "MAP";

    /**
     * The argument key of the RoutePlanModel.
     */
    public static final String ARG_ROUTE_PLAN = "route_plan";

    // Fragment Lifecycle

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.detail_fragment, container, false);
        final RoutePlanModel plan = (RoutePlanModel) getArguments().get(ARG_ROUTE_PLAN);

        MapFragment mf = (MapFragment) getChildFragmentManager()
                .findFragmentByTag(TAG_MAP);

        if (mf == null) {
            mf = new MapFragment();
            mf.setOnMapReadyListener(this);
            getChildFragmentManager().beginTransaction()
                    .add(R.id.detail_fragment_maps_fragment_container, mf, TAG_MAP)
                    .commit();
            getChildFragmentManager().executePendingTransactions();
        }


        TextView tm = (TextView) rootView.findViewById(R.id.detail_fragment_travel_mode);
        tm.setText(getString(plan.getMode().getTextValueId()));

        TextView o = (TextView) rootView.findViewById(R.id.detail_fragment_origin);
        o.setText(plan.getOrigin().getName());

        TextView d = (TextView) rootView.findViewById(R.id.detail_fragment_destination);
        d.setText(plan.getDestination().getName());

        TextView at = (TextView) rootView.findViewById(R.id.detail_fragment_arrival_time);
        at.setText(DateConverter.format(this.getContext(), plan.getArrivalTime()));

        TextView dt = (TextView) rootView.findViewById(R.id.detail_fragment_departure_time);
        dt.setText(DateConverter.format(this.getContext(), plan.getDepartureTime()));

        TextView ddt = (TextView) rootView.findViewById(R.id.detail_fragment_delayed_departure_time);
        ddt.setText(DateConverter.format(plan.getDelay()));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MapFragment mf = (MapFragment) getChildFragmentManager().findFragmentByTag(TAG_MAP);
        mf.setOnMapReadyListener(this);

    }

    // ActionBar setup
    /**
     * Setup the options menu.
     * @param menu Parent menu.
     * @param inflater Menu inflater.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_fragment, menu);
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

        if (id == R.id.detail_fragment_edit) {
            Fragment f = new NewFragment();
            Bundle args = new Bundle();
            args.putParcelable(MainActivity.ARG_FACTORY, getArguments().getParcelable(MainActivity.ARG_FACTORY));
            args.putParcelable(
                NewFragment.ARG_ROUTE_PLAN,
                getArguments().getParcelable(ARG_ROUTE_PLAN)
            );
            f.setArguments(args);

            this.getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_right, R.anim.exit_left, R.anim.enter_left, R.anim.exit_right)
                    .addToBackStack(NewFragment.TAG)
                    .replace(R.id.fragment_container, f, NewFragment.TAG)
                    .commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady() {
        MapFragment mf = (MapFragment) getChildFragmentManager().findFragmentByTag(TAG_MAP);
        mf.draw((RoutePlanModel) getArguments().get(ARG_ROUTE_PLAN));
    }
}
