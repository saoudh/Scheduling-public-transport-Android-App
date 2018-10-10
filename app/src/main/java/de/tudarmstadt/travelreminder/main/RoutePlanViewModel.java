package de.tudarmstadt.travelreminder.main;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Intent;
import android.os.Build;
import android.support.v7.util.SortedList;
import android.util.Log;

import org.joda.time.DateTime;

import de.tudarmstadt.travelreminder.gmaps.GMapsRoutePlanModel;
import de.tudarmstadt.travelreminder.main.model.RoutePlanModel;

import static android.content.Context.ALARM_SERVICE;

/**
 * RoutePlanViewModel to interact with the storage.
 *
 * @param <P> RoutePlanModel implementation.
 */
public abstract class RoutePlanViewModel<P extends RoutePlanModel> extends AndroidViewModel {
    /**
     * Indicates if a instance of the ViewModel is resolved. May an instance have to load
     * external data in an AsyncTask if the resolved parameter is set to false.
     */
    protected Boolean resolved = true;

    /**
     * Sorted List of the current loaded RoutePlans.
     */
    private SortedList<P> plans;

    /**
     * RoutePlanList change listener for external propose.
     */
    private OnRoutePlanListChangedListener<P> sortedListListener;


    public RoutePlanViewModel(Application application, Class<P> PlanClass) {
        super(application);
        // CallbackFunctions of changes of the sorted RoutePlanModel list.
        ListCallback callback = new ListCallback();
        this.plans = new SortedList<>(
                PlanClass,
                callback
        );
    }

    // Abstract Methods Implemented by each service type.

    /**
     * Deletes an RoutePlan
     * <p>
     * This method should call an AsyncTask and on success it should delete the item from the list
     * <code>SortedList.remove(plan)</code>
     *
     * @param plan Plan to delete.
     */
    public abstract void deleteRoutePlan(P plan);

    /**
     * Saves a given RoutePlan.
     * <p>
     * This method should call an AsyncTask and on success it should add the item to the list
     * <code>SortedList.add(plan)</code>
     *
     * @param plan Plan to save.
     */
    public abstract void saveRoutePlan(P plan);

    // Implemented Methods
    /**
     * Checks if an instance is finished with loading external data.
     *
     * @return True id the ViewModel is resolved.
     */
    public boolean isResolved() {
        return this.resolved;
    }

    /**
     * Resolves this ViewModel.
     * <p>
     * It will call the <code>RoutePlanViewModel.OnRoutePlanListChangedListener.onResolved()</code>
     * method.
     *
     * @see RoutePlanViewModel.OnRoutePlanListChangedListener
     */
    protected void resolve() {
        this.resolved = true;
        if (sortedListListener != null)
            sortedListListener.onResolved(this, this.plans);
    }

    /**
     * Returns the current loaded RoutePlans.
     *
     * @return Current loaded RoutePlans.
     */
    public SortedList<P> getRoutePlans() {
        return this.plans;
    }

    /**
     * Sets the RoutePlanListChangedListener.
     *
     * @param listener The RoutePlanListChangedListener.
     */
    public void setOnRoutePlanListChangedListener(RoutePlanViewModel.OnRoutePlanListChangedListener<P> listener) {
        this.sortedListListener = listener;
        if (this.isResolved()) listener.onResolved(this, this.plans);
    }


    // Interfaces

    /**
     * Interface that listens if an item has changed in the RoutePlanList.
     *
     * @param <P> An RoutePlanModel class.
     */
    public interface OnRoutePlanListChangedListener<P extends RoutePlanModel> {
        /**
         * Is called if an item is added to the list.
         *
         * @param position Position of the new item.
         * @param count    Count of added items.
         */
        void onInserted(int position, int count);

        /**
         * Is called if an item is removed from the list.
         *
         * @param position Position of the removed item.
         * @param count    Count of removed items.
         */
        void onRemoved(int position, int count);

        /**
         * Called if an item is moved from position a to position b.
         *
         * @param fromPosition Position a.
         * @param toPosition   Position b.
         */
        void onMoved(int fromPosition, int toPosition);

        /**
         * Called if an item changed.
         *
         * @param position Position of the changed item.
         * @param count    Count of items that changed.
         */
        void onChanged(int position, int count);

        /**
         * Called if the ViewModel is ready with its first fetch.
         *
         * @param plans SortedList of the RoutePlans.
         */
        void onResolved(RoutePlanViewModel<P> viewModel, SortedList<P> plans);
    }


    // Classes

    /**
     * Listens to the SortedList callbacks.
     */
    private class ListCallback extends SortedList.Callback<P> {
        @Override
        public int compare(P routePlan1, P routePlan2) {
            if (routePlan1.getArrivalTime() == null && routePlan2.getArrivalTime() == null) {
                return 0;
            }

            if (routePlan1.getArrivalTime() == null) {
                return -1;
            }

            return routePlan1.getArrivalTime().compareTo(routePlan2.getArrivalTime());
        }

        @Override
        public boolean areContentsTheSame(P t1, P t2) {
            return this.compare(t1, t2) == 0;
        }

        @Override
        public boolean areItemsTheSame(P t1, P t2) {
            return t1.getId().equals(t2.getId());
        }

        @Override
        public void onInserted(int position, int count) {
            if (sortedListListener != null) sortedListListener.onInserted(position, count);

            RoutePlanModel plan = getRoutePlans().get(position);
            if (plan.isNotified()) return;
            AlarmManager alarmManager =
                (AlarmManager) RoutePlanViewModel.this.getApplication().getSystemService(ALARM_SERVICE);

            Intent alarmIntent = new Intent(
                    RoutePlanViewModel.this.getApplication(),
                    NotificationService.class
            );
            alarmIntent.putExtra(NotificationService.ARG_ROUTE_PLAN, plan.getId());
            PendingIntent intent = PendingIntent.getService(
                    RoutePlanViewModel.this.getApplication(),
                    plan.getId().hashCode(),
                    alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        plan.getDelayedDepartureTime().getMillis(),
                        intent
                );
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        plan.getDelayedDepartureTime().getMillis(),
                        intent
                );
            } else {
                alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        plan.getDelayedDepartureTime().getMillis(),
                        intent
                );
            }

        }

        @Override
        public void onRemoved(int position, int count) {
            if (sortedListListener != null) sortedListListener.onRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            if (sortedListListener != null) sortedListListener.onMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count) {
            if (sortedListListener != null) sortedListListener.onChanged(position, count);
        }
    }
}
