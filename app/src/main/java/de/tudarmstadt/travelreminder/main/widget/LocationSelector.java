package de.tudarmstadt.travelreminder.main.widget;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;

import de.tudarmstadt.travelreminder.main.Factory;
import de.tudarmstadt.travelreminder.main.model.LocationModel;


public class LocationSelector extends DelayAutoCompleteTextView
        implements AdapterView.OnItemClickListener, View.OnFocusChangeListener {

    private Factory factory;
    private OnLocationSelectedListener onLocationSelectedListner;

    private boolean ignoreFocusChange = false;

    public LocationSelector(Context context) {
        this(context, null);
    }

    public LocationSelector(Context context, AttributeSet attrs) {
        this(context, attrs, android.support.design.R.attr.autoCompleteTextViewStyle);
    }

    public LocationSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOnItemClickListener(this);
        this.setOnFocusChangeListener(this);
    }


    public Factory getFactory() {
        return factory;
    }

    public void setFactory(Factory factory) {
        this.factory = factory;
    }

    public OnLocationSelectedListener getOnLocationSelectedListner() {
        return onLocationSelectedListner;
    }

    public void setOnLocationSelectedListner(OnLocationSelectedListener onLocationSelectedListner) {
        this.onLocationSelectedListner = onLocationSelectedListner;
    }

    // Listener Methods
    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
        LocationModel location = (LocationModel) adapter.getItemAtPosition(position);
        this.setText(location.getName());
        this.ignoreFocusChange = true;
        this.clearFocus();
        new ResolveLocationTask().execute(location);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus || this.getFactory() == null || this.ignoreFocusChange) {
            this.ignoreFocusChange = false;
            return;
        }
        LocationModel location = factory.createLocationModel();
        location.setName(this.getText().toString());
        new ResolveLocationTask().execute(location);
    }

    public interface OnLocationSelectedListener {
        void onSelected(LocationSelector selector, LocationModel location);
    }


    private class ResolveLocationTask extends AsyncTask<LocationModel, Void, LocationModel> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (getProgressBar() != null)
                getProgressBar().setVisibility(VISIBLE);
        }

        @Override
        protected LocationModel doInBackground(LocationModel... params) {
            if (params.length == 0)
                return null;
            factory.getRepository().resolve(params[0]);
            return params[0];
        }

        @Override
        protected void onPostExecute(LocationModel locationModel) {
            super.onPostExecute(locationModel);

            if (getProgressBar() != null)
                getProgressBar().setVisibility(GONE);

            setText(locationModel.getName());

            if (onLocationSelectedListner != null) {
                onLocationSelectedListner.onSelected(LocationSelector.this, locationModel);
            }
        }
    }
}
