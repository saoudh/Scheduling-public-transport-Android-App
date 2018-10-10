package de.tudarmstadt.travelreminder.gmaps;

import android.content.Context;
import android.widget.Filter;

import java.util.ArrayList;

import de.tudarmstadt.travelreminder.main.adapter.LocationModelAdapter;

/**
 * An Google Maps implementation of the <code>{@link LocationModelAdapter}</code>
 */
class GMapsLocationModelAdapter extends LocationModelAdapter<GMapsLocationModel> {
    /**
     * Stored GMapsRepository
     */
    private GMapsRepository repo;

    /**
     * Default Constructor.
     * @param context The current Context.
     * @param repo An instance of the <code>{@link GMapsRepository}</code>.
     */
    GMapsLocationModelAdapter(Context context, GMapsRepository repo) {
        super(context);
        this.repo = repo;
    }

    // Overridden methods of the {@link LocationModelAdapter}
    @Override
    protected Filter createFilter() {
        return new GMapsLocationFilter();
    }

    /**
     * Filter class, that autocomplete the given String to a valid <code>{@link GMapsLocationModel}</code>
     */
    private class GMapsLocationFilter extends Filter {
        // Overridden Methods of the Filter
        @Override
        protected FilterResults performFiltering(CharSequence constraints) {
            FilterResults results = new FilterResults();
            if (constraints == null) return null;
            ArrayList<GMapsLocationModel> locations = repo.autoComplete(constraints.toString());
            results.values = locations;
            results.count = locations.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {
            if (results != null && results.count > 0) {
                setLocations((ArrayList<GMapsLocationModel>) results.values);
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
