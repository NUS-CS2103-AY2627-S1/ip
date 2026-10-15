package thomas.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import thomas.ThomasException;
import thomas.place.Place;

/** Loads and saves place records in a separate human-readable file. */
public class PlaceStorage {
    private final File file;

    /** Creates storage for the given place-data file. */
    public PlaceStorage(String path) {
        file = new File(path);
    }

    /** Loads all valid place records, skipping corrupt records individually. */
    public ArrayList<Place> load() {
        ArrayList<Place> places = new ArrayList<>();
        createFileIfMissing();
        try (Scanner scanner = new Scanner(file)) {
            int lineNumber = 0;
            while (scanner.hasNextLine()) {
                lineNumber++;
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) {
                    continue;
                }
                try {
                    places.add(parse(line));
                } catch (Exception e) {
                    System.out.println("!!! Corrupted place data on line " + lineNumber + " skipped.");
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading place storage: " + e.getMessage());
        }
        return places;
    }

    /** Saves all places, replacing the previous place-data file. */
    public void save(ArrayList<Place> places) {
        createFileIfMissing();
        try (FileWriter writer = new FileWriter(file)) {
            for (Place place : places) {
                writer.write(place.toFileFormat() + System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Cannot save places: " + e.getMessage());
        }
    }

    private void createFileIfMissing() {
        if (file.exists()) {
            return;
        }
        try {
            File parent = file.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }
            file.createNewFile();
        } catch (IOException e) {
            System.out.println("Could not create place storage file.");
        }
    }

    private Place parse(String line) throws ThomasException {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length != 8 || !fields[0].equals("P")) {
            throw new ThomasException("Invalid place record.");
        }
        LocalDate date = fields[6].isEmpty() ? null : Place.parseDate(fields[6]);
        String note = fields[7].isEmpty() ? null : fields[7];
        return new Place(fields[1], fields[2], fields[3], Place.parseRating(fields[4]), fields[5], date, note);
    }
}
