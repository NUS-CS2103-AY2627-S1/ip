package kaykay.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import kaykay.model.ApplicationData;
import kaykay.model.Deadline;
import kaykay.model.Event;
import kaykay.model.Place;
import kaykay.model.PlaceList;
import kaykay.model.TaskList;
import kaykay.model.Todo;

/** Tests task persistence, special-character escaping, and malformed data handling. */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    /** Checks that all task types and their statuses survive a save/load cycle. */
    @Test
    void saveDataThenLoadData_taskRecords_preservesTaskData() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("nested/kaykay.txt").toString());
        Todo todo = new Todo("pipe | slash \\");
        todo.mark();
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 12, 25, 18, 30));
        Event event = new Event("project meeting", LocalDateTime.of(2026, 12, 26, 14, 0),
                LocalDateTime.of(2026, 12, 26, 16, 0));
        TaskList tasks = new TaskList();
        tasks.add(todo);
        tasks.add(deadline);
        tasks.add(event);

        ApplicationData data = new ApplicationData(tasks);
        storage.saveData(data);
        TaskList loadedTasks = storage.loadData().getTasks();

        assertEquals(3, loadedTasks.size());
        assertEquals(todo.toString(), loadedTasks.getTask(0).toString());
        assertEquals(todo.toFileFormat(), loadedTasks.getTask(0).toFileFormat());
        assertEquals(deadline.toString(), loadedTasks.getTask(1).toString());
        assertEquals(event.toString(), loadedTasks.getTask(2).toString());
    }

    /** Checks missing files and malformed task records. */
    @Test
    void loadData_fileState_returnsExpectedResult() throws IOException {
        Storage missingFileStorage = new Storage(temporaryDirectory.resolve("missing.txt").toString());
        ApplicationData missingData = missingFileStorage.loadData();
        assertEquals(0, missingData.getTasks().size());
        assertEquals(0, missingData.getPlaces().size());

        Path malformedFile = temporaryDirectory.resolve("malformed.txt");
        Files.writeString(malformedFile, "Q | 2 | broken task" + System.lineSeparator());
        Storage malformedStorage = new Storage(malformedFile.toString());
        assertThrows(IOException.class, malformedStorage::loadData);
    }

    /** Checks semantic validation of task data that is structurally complete. */
    @Test
    void loadData_invalidSemanticRecords_throwsError() throws IOException {
        Path dataFile = temporaryDirectory.resolve("invalid-semantic-data.txt");
        String[] invalidContents = {
            "T | 0 |   ",
            "E | 0 | meeting | 26 12 2026 14:00 | 26 12 2026 14:00",
            "T | 0 | bad\\q escape"
        };

        for (String invalidContent : invalidContents) {
            Files.writeString(dataFile, invalidContent + System.lineSeparator());
            Storage storage = new Storage(dataFile.toString());
            assertThrows(IOException.class, storage::loadData);
        }
    }

    /** Checks that duplicates from older save files remain loadable. */
    @Test
    void loadData_duplicateLegacyRecords_preservesAllRecords() throws IOException {
        Path dataFile = temporaryDirectory.resolve("legacy-duplicates.txt");
        Files.writeString(dataFile, "T | 0 | duplicate" + System.lineSeparator()
                + "T | 1 | duplicate" + System.lineSeparator()
                + "P | Cafe | restaurant | Kent Ridge |  | 4 | good" + System.lineSeparator()
                + "P | Cafe | restaurant | Kent Ridge |  | 4 | good" + System.lineSeparator());

        ApplicationData loadedData = new Storage(dataFile.toString()).loadData();

        assertEquals(2, loadedData.getTasks().size());
        assertEquals(2, loadedData.getPlaces().size());
    }

    /**
     * Checks that existing numeric dates load with readable output and save unchanged.
     */
    @Test
    void loadDataThenSaveData_existingDates_preservesStorageFormat() throws IOException {
        Path dataFile = temporaryDirectory.resolve("existing-dates.txt");
        String originalContents = "D | 1 | report | 25 12 2026 18:30" + System.lineSeparator()
                + "E | 0 | meeting | 26 12 2026 14:00 | 26 12 2026 16:00" + System.lineSeparator();
        Files.writeString(dataFile, originalContents);
        Storage storage = new Storage(dataFile.toString());

        ApplicationData loadedData = storage.loadData();

        assertEquals("[D][X] report (by: 25 Dec 2026, 18:30)", loadedData.getTasks().getTask(0).toString());
        assertEquals("[E][ ] meeting (from: 26 Dec 2026, 14:00 to: 26 Dec 2026, 16:00)",
                loadedData.getTasks().getTask(1).toString());
        storage.saveData(loadedData);
        assertEquals(originalContents, Files.readString(dataFile));
    }

    /** Checks that a failed load prevents the damaged file from being overwritten. */
    @Test
    void saveData_afterFailedLoad_preservesExistingFile() throws IOException {
        Path dataFile = temporaryDirectory.resolve("protected-data.txt");
        String invalidData = "Q | 2 | broken task" + System.lineSeparator();
        Files.writeString(dataFile, invalidData);
        Storage storage = new Storage(dataFile.toString());
        assertThrows(IOException.class, storage::loadData);

        assertThrows(IOException.class, () ->
                storage.saveData(new ApplicationData(new TaskList(new Todo("replacement")))));
        assertEquals(invalidData, Files.readString(dataFile));
    }

    /** Checks that task and place records survive a combined save/load cycle. */
    @Test
    void saveDataThenLoadData_mixedRecords_preservesTasksAndPlaces() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("kaykay.txt").toString());
        TaskList tasks = new TaskList(new Todo("visit again"));
        Place place = new Place("Burnt | Ends", "restaurant", "Dempsey",
                LocalDate.of(2026, 9, 10), 5, "Great \\ brisket");
        ApplicationData data = new ApplicationData(tasks, new PlaceList(place));

        storage.saveData(data);
        ApplicationData loadedData = storage.loadData();

        assertEquals(1, loadedData.getTasks().size());
        assertEquals("[T][ ] visit again", loadedData.getTasks().getTask(0).toString());
        assertEquals(1, loadedData.getPlaces().size());
        assertEquals(place.toString(), loadedData.getPlaces().getPlace(0).toString());
        assertEquals(place.toFileFormat(), loadedData.getPlaces().getPlace(0).toFileFormat());
    }

    /** Checks escaping of every character with special meaning in the file format. */
    @Test
    void escape_specialCharacters_returnsStorageSafeText() {
        assertEquals("line\\nreturn\\rpipe\\|slash\\\\",
                Storage.escape("line\nreturn\rpipe|slash\\"));
    }

    /** Checks that non-ASCII text survives explicit UTF-8 persistence. */
    @Test
    void saveDataThenLoadData_unicodeText_preservesText() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("unicode.txt").toString());
        Todo todo = new Todo("买牛奶 at the café");

        storage.saveData(new ApplicationData(new TaskList(todo)));

        assertEquals(todo.toString(), storage.loadData().getTasks().getTask(0).toString());
    }
}
