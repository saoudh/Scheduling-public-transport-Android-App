package de.tudarmstadt.travelreminder.gmaps;

import android.location.Location;
import android.util.Log;

import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.PlaceDetailsRequest;
import com.google.maps.PlacesApi;
import com.google.maps.QueryAutocompleteRequest;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AutocompletePrediction;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.TrafficModel;

import java.io.IOException;
import java.util.ArrayList;

import de.tudarmstadt.travelreminder.main.Repository;
import de.tudarmstadt.travelreminder.main.model.TravelMode;

/**
 * Gmaps is an implementation of the <code>ServiceInterface</code> for the Google Maps API.
 * <p>
 * It initializes a <code>GmapsPlan</code> with the given <code>GmapsLocation</code> instances and
 * calculates the departure time.
 * </p>
 * <p>
 * To communicate with the google apis it is necessary to create an api key and enable the
 * Services "Google Maps Directions API", "Google Places API Web Service" and
 * "Google Maps Geocoding API".
 * <a href="https://console.developers.google.com">Google API Console</a>
 * </p>
 *
 * @author Alexander Kopp
 * @see Repository
 * @since 1.0
 */
public class GMapsRepository extends Repository<GMapsRoutePlanModel, GMapsLocationModel> {

    private static String DEFAULT_LANGUAGE = "de";

    private GeoApiContext context;
    private String language;

    /**
     * Constructs the google maps api service with the default language "German".
     *
     * @param key The google api key.
     */
    public GMapsRepository(String key) {
        this(key, DEFAULT_LANGUAGE);
    }

    /**
     * Constructs the google maps api service.
     *
     * @param key      The google api key.
     * @param language A valid language code.
     */
    GMapsRepository(String key, String language) {
        // Create the api context.
        this.context = new GeoApiContext();
        this.context.setApiKey(key);
        this.language = language;
    }

    @Override
    public void update(GMapsRoutePlanModel plan) {
        if (!plan.isValid()) return;

        if (
            plan.getArrivalTime() != null &&
            (plan.getArrivalTime().isBeforeNow() || plan.getArrivalTime().isEqualNow())
        ) {
            return;
        }

        resolve(plan.getOrigin());
        resolve(plan.getDestination());

        DirectionsApiRequest request = DirectionsApi.getDirections(
                this.context,
                "place_id:" + plan.getOrigin().getPlaceId(),
                "place_id:" + plan.getDestination().getPlaceId()
        );
        request.language(this.language);
        request.mode(toGMapsMode(plan.getMode()));

        // Problem DepTime in Past!

        if (plan.getDepartureTime() != null) {
            request.departureTime(plan.getDepartureTime());
            request.trafficModel(TrafficModel.PESSIMISTIC);
        } else if (plan.getArrivalTime() != null){
            request.arrivalTime(plan.getArrivalTime());
        }

        DirectionsResult result;

        result = request.awaitIgnoreError();

        if (result == null || result.routes.length == 0) {
            return;
        }

        plan.setPolyline(result.routes[0].overviewPolyline);
        plan.setMapBounds(result.routes[0].bounds);

        if (plan.getArrivalTime() == null) return;

        if (plan.getDepartureTime() == null && result.routes[0].legs[0].duration != null) {
            plan.setDepartureTime(
                plan.getArrivalTime().minusSeconds((int) result.routes[0].legs[0].duration.inSeconds)
            );
        }

        if (result.routes[0].legs[0].durationInTraffic != null) {
            plan.setDelayedDepartureTime(
                plan.getArrivalTime().minusSeconds((int) result.routes[0].legs[0].durationInTraffic.inSeconds)
            );
        }
    }

    /**
     * Converts a TravelMode to the google enum TravelMode.
     * @param mode The TravelMode to convert.
     * @return Returns the google maps equivalent value.
     */
    private com.google.maps.model.TravelMode toGMapsMode(TravelMode mode) {
        switch (mode) {
            case BICYCLING:
                return com.google.maps.model.TravelMode.BICYCLING;
            case DRIVING:
                return com.google.maps.model.TravelMode.DRIVING;
            case TRANSIT:
                return com.google.maps.model.TravelMode.TRANSIT;
            case WALKING:
                return com.google.maps.model.TravelMode.WALKING;
            case UNKNOWN:
            default:
                return com.google.maps.model.TravelMode.UNKNOWN;
        }
    }

    /**
     * Resolves a location with the google maps places api.
     *
     * @param location The location to be resolved.
     */
    private void resolveWithPlaces(GMapsLocationModel location) {
        PlaceDetailsRequest request = PlacesApi.placeDetails(this.context, location.getPlaceId());
        request.language(this.language);
        PlaceDetails result = request.awaitIgnoreError();
        if (result != null) {
            location.setPosition(result.geometry.location);
            location.setName(result.formattedAddress);
        }
    }

    /**
     * Resolves a location with the google maps gecoding api.
     *
     * @param location The location to be resolved.
     */
    private void resolveWithGeocoding(GMapsLocationModel location) {
        GeocodingApiRequest request = GeocodingApi.geocode(context, location.getName());
        request.language(this.language);
        GeocodingResult[] result = request.awaitIgnoreError();
        if (result != null && result.length > 0) {
            location.setPlaceId(result[0].placeId);
            location.setName(result[0].formattedAddress);
            location.setPosition(result[0].geometry.location);
        }
    }


    @Override
    public void resolve(GMapsLocationModel location) {
        if (!location.isValid()
            || (location.getPlaceId() != null && location.getPosition() != null)) {
            return;
        }

        if (location.getPlaceId() != null) {
            this.resolveWithPlaces(location);
        } else {
            this.resolveWithGeocoding(location);
        }
    }

    @Override
    public ArrayList<GMapsLocationModel> autoComplete(String address) {
        QueryAutocompleteRequest request = PlacesApi.queryAutocomplete(context, address);
        // Set language
        request.language(this.language);

        // Add current location if available
        if (getCurrentLocation() != null) {
            request.location(new LatLng(
                getCurrentLocation().getLatitude(),
                getCurrentLocation().getLongitude()
            ));

            request.radius(200 * 1000); // Radius = 2km
        }

        AutocompletePrediction[] result;
        result = request.awaitIgnoreError();

        ArrayList<GMapsLocationModel> locations = new ArrayList<>();
        if (result != null) {
            for (AutocompletePrediction res : result) {
                GMapsLocationModel location = new GMapsLocationModel();
                location.setPlaceId(res.placeId);
                location.setName(res.description);
                this.resolve(location);
                locations.add(location);
            }
        }
        return locations;
    }
}
