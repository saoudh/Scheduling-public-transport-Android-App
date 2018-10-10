package de.tudarmstadt.travelreminder.main;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;

import de.tudarmstadt.travelreminder.R;
import de.tudarmstadt.travelreminder.gmaps.GMapsFactory;
import de.tudarmstadt.travelreminder.gmaps.GMapsLocationModel;
import de.tudarmstadt.travelreminder.gmaps.GMapsRoutePlanModel;
import de.tudarmstadt.travelreminder.main.fragment.DetailFragment;
import de.tudarmstadt.travelreminder.main.fragment.HomeFragment;
import de.tudarmstadt.travelreminder.main.model.RoutePlanModel;

/**
 * The application main activity.
 */
public class MainActivity extends AppCompatActivity implements OnSuccessListener<Location> {
    public static final int REQUEST_LOCATION_ID = 1;
    public static final String ARG_FACTORY = "factory";
    private static final String BC_SEND_ALARM = "BC_SEND_ALARM";
    GMapsFactory factory;
    private FusedLocationProviderClient mFusedLocationClient;
    private ArrayList<PendingIntent> intentArray;
    private AlarmManager mgrAlarm;
    private BroadcastReceiver alarmReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        String apiKey;
        try {
            apiKey = getPackageManager()
                .getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA)
                .metaData
                .getString("com.google.android.geo.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            // Close app with error
            AlertDialog.Builder builder = new AlertDialog.Builder( this );
            builder
                    .setMessage( "There was an error: " + e.getMessage() )
                    .setCancelable( false )
                    .setNeutralButton( "Ok.", new DialogInterface.OnClickListener()
                    {
                        public void onClick ( DialogInterface dialog, int which )
                        {
                            MainActivity.this.finish();
                        }
                    } );
            AlertDialog error = builder.create();
            error.show();
            return;
        }


        // Create factory
        this.factory = new GMapsFactory(apiKey);
        addUpdateService();
       // Fetch last known location on startup.
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        },
                        REQUEST_LOCATION_ID
                );
            }
        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, this);
        }

        // Set supported toolbar
        this.setSupportActionBar((Toolbar) this.findViewById(R.id.toolbar));


        // Setup HomeFragment
        HomeFragment home = (HomeFragment) getSupportFragmentManager().findFragmentByTag(HomeFragment.TAG);
        if (home == null) {
            // home fragment not added, add it to the activity.
            home = new HomeFragment();
            Bundle args = new Bundle();
            args.putParcelable(ARG_FACTORY, this.factory);
            home.setArguments(args);
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter_left, R.anim.exit_right, R.anim.enter_right, R.anim.exit_left)
                    .replace(R.id.fragment_container, home, HomeFragment.TAG)
                    .commit();
        }

        // Setup Database inspector button.
        /*Button button = (Button) this.findViewById(R.id.inspectDB);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent dbmanager = new Intent(MainActivity.this, AndroidDatabaseManager.class);
                startActivity(dbmanager);
            }
        });
*/
        if (getIntent().getAction() != null
            && getIntent().getAction().equals(NotificationService.ACTION_NOTIFICATION_CLICKED)
            && getIntent().hasExtra(NotificationService.ARG_ROUTE_PLAN)
        ) {
            NotificationManager notifyManager =
                    (NotificationManager) getApplicationContext()
                            .getSystemService(Context.NOTIFICATION_SERVICE);
            notifyManager.cancelAll();
            GMapsRoutePlanModel plan = getIntent().getParcelableExtra(NotificationService.ARG_ROUTE_PLAN);
            plan.setNotified(true);
            factory.createRoutePlanViewModel(this).saveRoutePlan(plan);
            Fragment f = new DetailFragment();
            Bundle args = new Bundle();
            args.putParcelable(MainActivity.ARG_FACTORY, this.factory);
            args.putParcelable(DetailFragment.ARG_ROUTE_PLAN, plan);
            f.setArguments(args);
            this.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_right, R.anim.exit_left, R.anim.enter_left, R.anim.exit_right)
                    .addToBackStack(DetailFragment.TAG)
                    .replace(R.id.fragment_container, f, DetailFragment.TAG)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MAIN", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MAIN", "onPause");
        //this.unregisterReceiver(this.alarmReceiver);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_ID) {
            boolean granted = false;
            for  (int i = 0; i < permissions.length; i++) {
                granted = granted || grantResults[i] == PackageManager.PERMISSION_GRANTED;
            }

            if (granted)
                mFusedLocationClient.getLastLocation().addOnSuccessListener(this, this);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onSuccess(Location location) {
        if (location == null) return;
        this.factory.setCurrentLocation(location);
    }

    private void addUpdateService() {
        Intent updateIntent = new Intent(this.getApplicationContext(), UpdateService.class);
        updateIntent.putExtra(MainActivity.ARG_FACTORY, this.factory);
        PendingIntent intent = PendingIntent.getService(
            this.getApplicationContext(),
            10,
            updateIntent,
            PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_ONE_SHOT
        );

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            DateTime.now().getMillis(),
            UpdateService.INTERVAL,
            intent
        );
    }
}