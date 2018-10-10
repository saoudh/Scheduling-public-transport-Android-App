package de.tudarmstadt.travelreminder.main.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import de.tudarmstadt.travelreminder.main.model.LocationModel;

/**
 * An AutoCompleteAdapter to fetch a list of <code>{@link LocationModel}</code> by a given String.
 *
 * @param <L> The LocationModel class.
 */
public abstract class LocationModelAdapter<L extends LocationModel>
        extends ArrayAdapter<L>
        implements Filterable {

    /**
     * Save the filter, so it would not build twice.
     */
    private Filter filter;

    /**
     * The current autocomplete list.
     */
    private ArrayList<L> locations;

    /**
     * Default constructor.
     * @param context The current context.
     */
    public LocationModelAdapter(Context context) {
        // Use Googles simple list item.
        super(context, android.R.layout.simple_expandable_list_item_1, android.R.id.text1);
        this.locations = new ArrayList<>();
    }

    /**
     * Sets the current list of locations.
     * @param locations List of locations.
     */
    public void setLocations(ArrayList<L> locations) {
        this.locations = locations;
    }

    /**
     * Creates a filter, that autocomplete the location.
     * @return The Filter instance.
     */
    protected abstract Filter createFilter();


    // Overridden methods of the ArrayAdapter
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return this.locations.size();
    }

    @Override
    public L getItem(int position) {
        if (position >= this.locations.size()) return null;
        return this.locations.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = super.getView(position, convertView, parent);
        LocationModel location = this.getItem(position);
        TextView tv = (TextView) row.findViewById(android.R.id.text1);
        tv.setText(location.getName());

        return row;
    }

    // Overridden methods of the Filterable interface.
    @Override
    public Filter getFilter() {
        if (this.filter != null) return this.filter;
        this.filter = this.createFilter();
        return this.filter;
    }
}
