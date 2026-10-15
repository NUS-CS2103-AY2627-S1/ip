package thomas.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import thomas.place.Place;

class PlaceStorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void saveAndLoad_place_preservesAllFields() throws Exception {
        PlaceStorage storage = new PlaceStorage(temporaryDirectory.resolve("places.txt").toString());
        ArrayList<Place> places = new ArrayList<>();
        places.add(new Place("Sushi", "Restaurant", "Town", 4, "12.50", Place.parseDate("2026-09-08"), "Nice"));

        storage.save(places);
        ArrayList<Place> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("Sushi", loaded.get(0).getName());
        assertEquals("2026-09-08", loaded.get(0).getVisitedDate().toString());
        assertEquals("12.50", loaded.get(0).getPrice());
    }

    @Test
    void load_missingAndMalformedFile_createsFileAndSkipsInvalidRecords() throws Exception {
        Path file = temporaryDirectory.resolve("places.txt");
        PlaceStorage storage = new PlaceStorage(file.toString());
        assertTrue(storage.load().isEmpty());
        assertTrue(Files.exists(file));
        Files.writeString(file, "invalid\nP | Cafe | Cafe | Town | 5 | 0.00 |  | \n");

        assertEquals(1, storage.load().size());
    }
}
