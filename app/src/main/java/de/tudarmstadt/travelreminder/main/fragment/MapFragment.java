package de.tudarmstadt.travelreminder.main.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.model.Bounds;
import com.google.maps.model.EncodedPolyline;

import de.tudarmstadt.travelreminder.main.MainActivity;
import de.tudarmstadt.travelreminder.main.model.RoutePlanModel;

public class MapFragment extends com.google.android.gms.maps.SupportMapFragment implements OnMapReadyCallback, OnSuccessListener<Location> {
    GoogleMap map;
    MapReadyListener mapReadyListener;
    boolean cameraMoved = false;

    public MapFragment() {
        super();
        this.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        if (this.mapReadyListener != null)
            mapReadyListener.onMapReady();

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    MainActivity.REQUEST_LOCATION_ID
            );
            return;
        }
        locationGranted();
    }

    @RequiresPermission(
        anyOf = {"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}
    )
    private void locationGranted() {
        LocationServices.getFusedLocationProviderClient(getActivity())
                .getLastLocation()
                .addOnSuccessListener(this);
        map.setMyLocationEnabled(true);
    }

    public void draw(RoutePlanModel plan) {
        if (this.map == null) return;
        this.map.clear();

        if (plan.getOrigin() != null && plan.getOrigin().getPosition() != null) {
            this.addMarker(
                    plan.getOrigin().getName(),
                    plan.getOrigin().getPosition()
            );
        }

        if (plan.getDestination() != null && plan.getDestination().getPosition() != null) {
            this.addMarker(
                    plan.getDestination().getName(),
                    plan.getDestination().getPosition()
            );
        }
        if (plan.getPolyline() != null) {
            drawPolyline(plan.getPolyline());
        }

        if (plan.getMapBounds() == null) return;
        cameraMoved = true;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(
                convert(plan.getMapBounds()),
                5
        );
        try {
            this.map.moveCamera(cu);
            this.map.setLatLngBoundsForCameraTarget(convert(plan.getMapBounds()));
        } catch (Exception e) {}

    }

    private void addMarker(String title, com.google.maps.model.LatLng pos) {
        if (this.map == null) return;
        MarkerOptions mo = new MarkerOptions();
        mo.title(title);
        mo.position(convert(pos));
        this.map.addMarker(mo);
    }

    private void drawPolyline(EncodedPolyline polyline) {
        if (this.map == null) return;
        PolylineOptions po = new PolylineOptions();
        po.color(0xff000000);
        po.width(5);

        for (com.google.maps.model.LatLng pos : polyline.decodePath()) {
            po.add(this.convert(pos));
        }

        this.map.addPolyline(po);
    }


    private LatLng convert(com.google.maps.model.LatLng pos) {
        return new LatLng(
                pos.lat,
                pos.lng
        );
    }

    private LatLngBounds convert(Bounds bounds) {
        return new LatLngBounds(
                convert(bounds.southwest),
                convert(bounds.northeast)
        );
    }


    public void setOnMapReadyListener(MapReadyListener mapReadyListener) {
        this.mapReadyListener = mapReadyListener;
    }


    @Override
    public void onSuccess(Location location) {
        if (location == null) Log.d("TEST", "location null");
        if (cameraMoved || map == null || location == null) return;
        LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLng(pos));
    }

    public interface MapReadyListener {
        void onMapReady();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MainActivity.REQUEST_LOCATION_ID: {
                boolean granted = false;
                for  (int i = 0; i < permissions.length; i++) {
                    granted = granted || grantResults[i] == PackageManager.PERMISSION_GRANTED;
                }

                if (granted)
                    locationGranted();
            }
        }
    }
}
