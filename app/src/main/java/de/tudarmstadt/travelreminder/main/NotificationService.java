package de.tudarmstadt.travelreminder.main;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.joda.time.DateTime;

import java.util.Random;

import de.tudarmstadt.travelreminder.R;
import de.tudarmstadt.travelreminder.gmaps.GMapsDatabase;
import de.tudarmstadt.travelreminder.gmaps.GMapsFactory;
import de.tudarmstadt.travelreminder.gmaps.GMapsRoutePlanModel;
import de.tudarmstadt.travelreminder.main.model.RoutePlanModel;
import de.tudarmstadt.travelreminder.main.model.converter.DateConverter;


public class NotificationService extends Service {
    public static final String ARG_ROUTE_PLAN = "ROUTE_PLAN";
    public static final String ACTION_NOTIFICATION_CLICKED = "notify_clicked";

    private MediaPlayer mp;

    //private static int NOTIFICATION_ID = 1;
    //Notification notification;
    //MediaPlayer mp;
    //private NotificationManager notificationManager;
    //private PendingIntent pendingIntent;

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent == null || !intent.hasExtra(ARG_ROUTE_PLAN))
            return Service.START_STICKY_COMPATIBILITY;

        final GMapsDatabase db =  GMapsDatabase.getDatabase(getApplicationContext());


        new AsyncTask<Void, Void, GMapsRoutePlanModel>() {

            @Override
            protected GMapsRoutePlanModel doInBackground(Void... params) {
                return db.RoutePlanModelDao()
                        .getRoutePlan(intent.getLongExtra(ARG_ROUTE_PLAN, -1));
            }

            @Override
            protected void onPostExecute(final GMapsRoutePlanModel plan) {
                super.onPostExecute(plan);
                if (plan == null) return;
                if (plan.isNotified()) return;
                plan.setNotified(true);
                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

                Intent clickIntent = new Intent(getApplicationContext(), MainActivity.class);
                clickIntent.setAction(ACTION_NOTIFICATION_CLICKED);
                clickIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                clickIntent.putExtra(
                        NotificationService.ARG_ROUTE_PLAN,
                        plan
                );

                PendingIntent pIntent = PendingIntent.getActivity(
                        getApplicationContext(),
                        1,
                        clickIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

                NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(getApplicationContext());
                notifyBuilder.setContentIntent(pIntent)
                        .setLights(0xFFFFA500, 800, 1000)
                        .setPriority(9)
                        .setSound(soundUri)
                        .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                        .setAutoCancel(true)
                        .setContentTitle(plan.getDestination().getName())
                        .setContentText(getString(R.string.time_to_departure))
                        .setSubText(DateConverter.format(getApplicationContext(), plan.getDelayedDepartureTime()))
                        .setTicker(getText(R.string.time_to_departure))
                        .setSmallIcon(R.mipmap.notification)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.notification));

                Notification notify = notifyBuilder.build();

                NotificationManager notifyManager =
                        (NotificationManager) getApplicationContext()
                                .getSystemService(Context.NOTIFICATION_SERVICE);

                notifyManager.notify(1, notify);

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        db.RoutePlanModelDao().addRoutePlan(plan);
                        return null;
                    }
                }.execute();

            }
        }.execute();

        return Service.START_STICKY_COMPATIBILITY;

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // DoNothing
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }
}
