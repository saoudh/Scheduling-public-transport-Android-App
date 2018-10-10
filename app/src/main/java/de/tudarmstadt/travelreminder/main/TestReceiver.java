package de.tudarmstadt.travelreminder.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import de.tudarmstadt.travelreminder.gmaps.GMapsRoutePlanModel;
import de.tudarmstadt.travelreminder.main.model.RoutePlanModel;

public class TestReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // assumes WordService is a registered service
        Log.d("TestReceiver", "onHandleIntent");
        if (intent == null) return;
        RoutePlanModel plan = (GMapsRoutePlanModel) intent.getExtras().get(NotificationService.ARG_ROUTE_PLAN);
        Log.d("TestReceiver", String.valueOf(intent.hasExtra(NotificationService.ARG_ROUTE_PLAN)));
        if (!intent.hasExtra(NotificationService.ARG_ROUTE_PLAN)) return;
        Log.d("TestReceiver", plan.getId().toString());

        //Intent intent = new Intent(context, WordService.class);
        //context.startService(intent);
    }
}
