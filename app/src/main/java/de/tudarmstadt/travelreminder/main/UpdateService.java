package de.tudarmstadt.travelreminder.main;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.util.SortedList;
import android.util.Log;

import org.joda.time.DateTime;

import de.tudarmstadt.travelreminder.gmaps.GMapsLocationModel;
import de.tudarmstadt.travelreminder.gmaps.GMapsRoutePlanModel;
import de.tudarmstadt.travelreminder.main.model.RoutePlanModel;

/**
 * An {@link android.app.IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */

public class UpdateService extends Service implements RoutePlanViewModel.OnRoutePlanListChangedListener {

    public static final long INTERVAL = 1000 * 60 * 2; // 2 Minutes
    private Factory factory;

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Log.d("UpdateService", "onStartCommand");
        // Fetch the factory from the intent.
        if (intent != null)
            this.factory = intent.getParcelableExtra(MainActivity.ARG_FACTORY);
        if (this.factory == null) {
            return Service.START_NOT_STICKY;
        }
        Log.d("UpdateService", "onStartCommand#ready");
        factory.createRoutePlanViewModel(getApplication())
                .setOnRoutePlanListChangedListener(UpdateService.this);
        // If the service get killed, restart the service.
        return Service.START_STICKY;
    }

    @Override
    public void onResolved(RoutePlanViewModel viewModel, SortedList plans) {
        Log.d("UpdateService", "RoutePlanViewModel.onResolved");
        Log.i("UpdateService", String.format("%d RoutePlanModels to update", plans.size()));
        
        for (int i = 0; i < plans.size(); i++) {
            this.update((RoutePlanModel) plans.get(i), viewModel);
        }
    }

    private void update(RoutePlanModel plan, RoutePlanViewModel viewModel) {
        if (plan.isNotified()) return;
        DateTime currentDepartureTime = plan.getDelayedDepartureTime();
        this.factory.getRepository().update(plan);
        if (currentDepartureTime.equals(plan.getDelayedDepartureTime())) return;
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);

        Intent alarmIntent = new Intent(
                this,
                NotificationService.class
        );
        alarmIntent.putExtra(NotificationService.ARG_ROUTE_PLAN, plan.getId());
        PendingIntent intent = PendingIntent.getService(
                getApplicationContext(),
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

        viewModel.saveRoutePlan(plan);
    }

    // Do Nothing
    @Override
    public void onInserted(int position, int count) {}

    @Override
    public void onRemoved(int position, int count) {}

    @Override
    public void onMoved(int fromPosition, int toPosition) {}

    @Override
    public void onChanged(int position, int count) {}


    @Override
    public IBinder onBind(final Intent intent) {
        // Do nothing
        return null;
    }
}
