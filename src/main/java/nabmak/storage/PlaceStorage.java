package nabmak.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import nabmak.place.Place;

/**
 * Handles loading and saving places to a storage file.
 */
public class PlaceStorage {
    private final String filePath;

    /**
     * Creates a place storage using the specified file path.
     *
     * @param filePath path of the file used to store places
     */
    public PlaceStorage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves the given places to the storage file.
     *
     * @param places places to save
     */
    public void save(List<Place> places) {
        try {
            File file = new File(this.filePath);
            File parent = file.getParentFile();

            if (parent != null) {
                parent.mkdirs();
            }

            FileWriter writer = new FileWriter(file);

            for (Place place : places) {
                writer.write(placeToStr(place));
                writer.write(System.lineSeparator());
            }

            writer.close();
        } catch (IOException e) {
            System.out.println("Couldnt save places");
        }
    }

    /**
     * Loads all saved places from the storage file.
     *
     * @return places loaded from the file
     */
    public List<Place> load() {
        List<Place> places = new ArrayList<>();
        File file = new File(this.filePath);

        if (!file.exists()) {
            return places;
        }

        try {
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                Place place = strToPlace(line);

                if (place != null) {
                    places.add(place);
                }
            }

            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("Couldnt load places.");
        }

        return places;
    }

    /**
     * Converts a place into String format to store in file.
     *
     * @param place place to convert
     * @return string format of place
     */
    private String placeToStr(Place place) {
        return place.getName() + " | " + place.getDetails();
    }

    /**
     * Converts a stored line into a Place object.
     *
     * @param str stored place
     * @return place constructed from the stored line, or null if the line is invalid
     */
    private Place strToPlace(String str) {
        String[] data = str.split(" \\| ", 2);

        if (data.length < 2) {
            return null;
        }

        String name = data[0];
        String details = data[1];

        if (name.isEmpty() || details.isEmpty()) {
            return null;
        }

        return new Place(name, details);
    }
}
