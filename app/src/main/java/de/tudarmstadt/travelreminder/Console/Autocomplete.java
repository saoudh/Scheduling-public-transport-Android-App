package de.tudarmstadt.travelreminder.Console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import de.tudarmstadt.travelreminder.gmaps.GMapsLocationModel;
import de.tudarmstadt.travelreminder.gmaps.GMapsRepository;

/**
 * Testing the Autocomplete with a terminal app.
 */
public class Autocomplete {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Eingabe:");
        String input = br.readLine();
        GMapsRepository service = new GMapsRepository("AIzaSyANe77Vq3WWF4qvruftXxO-2kH_-2h2yGA");

        List<GMapsLocationModel> locations;
        while (!input.isEmpty()) {
            locations = service.autoComplete(input);
            printLocations(locations);
            System.out.println("---------");
            System.out.print("Eingabe:");
            input = br.readLine();
        }
    }

    public static void printLocations(List<GMapsLocationModel> locations) {
        System.out.printf("%d Found\n", locations.size());
        for (int i = 0; i < locations.size(); i++) {
            System.out.printf("%d: %s\n", i, locations.get(i).getName());
        }
    }
}
