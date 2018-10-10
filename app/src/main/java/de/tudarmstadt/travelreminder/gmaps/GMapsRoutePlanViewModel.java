package de.tudarmstadt.travelreminder.gmaps;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import de.tudarmstadt.travelreminder.main.RoutePlanViewModel;

/**
 * An implementation of the RoutePlanViewModel to handle GMaps RoutePlans.
 */
public class GMapsRoutePlanViewModel
        extends RoutePlanViewModel<GMapsRoutePlanModel> {

    /**
     * An instance of the database.
     */
    private GMapsDatabase db;

    /**
     * Default Constructor
     *
     * @param application The current application.
     */
    public GMapsRoutePlanViewModel(Application application) {
        super(application, GMapsRoutePlanModel.class);
        this.db = GMapsDatabase.getDatabase(application);
        this.resolved = false;
        new FetchTask().execute();
    }

    // Overwritten Methods
    @Override
    public void deleteRoutePlan(GMapsRoutePlanModel plan) {
        new DeleteTask().execute(plan);
    }

    @Override
    public void saveRoutePlan(GMapsRoutePlanModel plan) {
        new SaveTask().execute(plan);
    }

    // AsyncTasks

    /**
     * AsyncTask for deletions.
     */
    private class DeleteTask extends AsyncTask<GMapsRoutePlanModel, Void, GMapsRoutePlanModel[]> {
        @Override
        protected GMapsRoutePlanModel[] doInBackground(GMapsRoutePlanModel... plans) {
            db.RoutePlanModelDao().deleteRoutePlan(plans);
            return plans;
        }

        @Override
        protected void onPostExecute(GMapsRoutePlanModel[] result) {
            super.onPostExecute(result);
            getRoutePlans().beginBatchedUpdates();
            for (GMapsRoutePlanModel item : result) {
                getRoutePlans().remove(item);
            }
            getRoutePlans().endBatchedUpdates();
        }
    }

    /**
     * AsyncTask for saves.
     */
    private class SaveTask extends AsyncTask<GMapsRoutePlanModel, Void, GMapsRoutePlanModel[]> {
        @Override
        protected GMapsRoutePlanModel[] doInBackground(GMapsRoutePlanModel... plans) {
            for (GMapsRoutePlanModel plan : plans) {
                Long id = db.RoutePlanModelDao().addRoutePlan(plan);
                plan.setId(id);
            }
            return plans;
        }

        @Override
        protected void onPostExecute(GMapsRoutePlanModel[] result) {
            super.onPostExecute(result);
            getRoutePlans().beginBatchedUpdates();
            getRoutePlans().addAll(result);
            getRoutePlans().endBatchedUpdates();
        }
    }


    /**
     * AsyncTask for initial fetch.
     */
    private class FetchTask extends AsyncTask<Void, Void, List<GMapsRoutePlanModel>> {
        @Override
        protected List<GMapsRoutePlanModel> doInBackground(Void... args) {
            return db.RoutePlanModelDao().getAllRoutePlans();
        }

        @Override
        protected void onPostExecute(List<GMapsRoutePlanModel> plans) {
            getRoutePlans().beginBatchedUpdates();
            getRoutePlans().addAll(plans);
            getRoutePlans().endBatchedUpdates();
            resolve();
        }
    }
}
