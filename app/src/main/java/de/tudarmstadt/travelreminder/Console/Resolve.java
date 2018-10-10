package de.tudarmstadt.travelreminder.Console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import de.tudarmstadt.travelreminder.gmaps.GMapsLocationModel;
import de.tudarmstadt.travelreminder.gmaps.GMapsRepository;

/**
 * Testing the resolve with a terminal app.
 */
public class Resolve {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Eingabe:");
        String input = br.readLine();
        GMapsRepository service = new GMapsRepository("AIzaSyANe77Vq3WWF4qvruftXxO-2kH_-2h2yGA");

        while (!input.isEmpty()) {
            GMapsLocationModel l = new GMapsLocationModel();
            l.setName(input);
            service.resolve(l);
            printLocation(l);
            System.out.println("---------");
            System.out.print("Eingabe:");
            input = br.readLine();
        }
    }

    private static void printLocation(GMapsLocationModel locationModel) {
        System.out.println("PlaceID: " + locationModel.getPlaceId());
        System.out.println("Name: " + locationModel.getName());
        if (locationModel.getPosition() == null) return;
        System.out.println("Lat: " + locationModel.getPosition().lat);
        System.out.println("Lng: " + locationModel.getPosition().lng);
    }
}
