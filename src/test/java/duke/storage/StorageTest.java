package duke.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import duke.exception.DukeException;
import duke.place.Place;
import duke.place.PlaceList;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.Task;
import duke.task.TaskList;
import duke.task.ToDo;

/**
 * Unit tests for the {@link Storage} class.
 */
public class StorageTest {

    @TempDir
    Path tempDir;

    @Test
    public void load_nonExistentFile_returnsEmptyList() throws DukeException {
        File file = tempDir.resolve("nonexistent.txt").toFile();
        Storage storage = new Storage(file.getAbsolutePath());
        ArrayList<Task> tasks = storage.load();
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void load_validFileWithAllTaskTypes_success() throws Exception {
        File file = tempDir.resolve("tasks.txt").toFile();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("T | 1 | read book\n");
            writer.write("D | 0 | return book | 2026-09-01T18:00\n");
            writer.write("E | 1 | orientation | 2026-09-02T09:00 | 2026-09-02T17:00\n");
            writer.write("corrupted line that should be skipped\n");
        }

        Storage storage = new Storage(file.getAbsolutePath());
        ArrayList<Task> tasks = storage.load();
        assertEquals(3, tasks.size());

        assertEquals("T", tasks.get(0).getTaskIcon());
        assertEquals("X", tasks.get(0).getStatusIcon());
        assertEquals("read book", tasks.get(0).toString());

        assertEquals("D", tasks.get(1).getTaskIcon());
        assertEquals(" ", tasks.get(1).getStatusIcon());

        assertEquals("E", tasks.get(2).getTaskIcon());
        assertEquals("X", tasks.get(2).getStatusIcon());
    }

    @Test
    public void saveAndLoad_taskListRoundTrip_success() throws Exception {
        File file = tempDir.resolve("nested/dir/tasks.txt").toFile();
        Storage storage = new Storage(file.getAbsolutePath());

        TaskList taskList = new TaskList();
        taskList.addTask(new ToDo("buy groceries"));
        Deadline deadline = new Deadline("submit project", "2026-10-15 2359");
        deadline.markAsDone();
        taskList.addTask(deadline);
        taskList.addTask(new Event("hackathon", "2026-11-01 0900", "2026-11-02 1800"));

        storage.save(taskList);
        assertTrue(file.exists());

        ArrayList<Task> reloadedTasks = storage.load();
        assertEquals(3, reloadedTasks.size());
        assertEquals("buy groceries", reloadedTasks.get(0).toString());
        assertEquals("X", reloadedTasks.get(1).getStatusIcon());
        assertEquals("E", reloadedTasks.get(2).getTaskIcon());
    }

    @Test
    public void constructor_emptyFilePath_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Storage("   "));
    }

    @Test
    public void save_nullTaskList_throwsAssertionError() {
        Storage storage = new Storage("test.txt");
        assertThrows(AssertionError.class, () -> storage.save((TaskList) null));
    }

    @Test
    public void loadPlaces_nonExistentFile_returnsEmptyList() throws DukeException {
        File file = tempDir.resolve("nonexistent_places.txt").toFile();
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString(), file.getAbsolutePath());
        PlaceList loaded = storage.loadPlaces();
        assertEquals(0, loaded.size());
    }

    @Test
    public void saveAndLoad_placeListRoundTrip_success() throws Exception {
        File file = tempDir.resolve("places.txt").toFile();
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString(), file.getAbsolutePath());

        PlaceList placeList = new PlaceList();
        placeList.addPlace(new Place("Marina Bay Sands", "SkyPark view"));
        placeList.addPlace(new Place("Sentosa"));

        storage.savePlaces(placeList);
        assertTrue(file.exists());

        PlaceList reloaded = storage.loadPlaces();
        assertEquals(2, reloaded.size());
        assertEquals("Marina Bay Sands", reloaded.getPlace(0).getName());
        assertEquals("SkyPark view", reloaded.getPlace(0).getDetails());
        assertEquals("Sentosa", reloaded.getPlace(1).getName());
        assertEquals("", reloaded.getPlace(1).getDetails());
    }

    @Test
    public void load_directoryPath_exceptionThrown() {
        Storage storage = new Storage(tempDir.toAbsolutePath().toString());
        DukeException ex = assertThrows(DukeException.class, storage::load);
        assertTrue(ex.getMessage().contains("is a directory"));
    }

    @Test
    public void save_directoryPath_exceptionThrown() {
        Storage storage = new Storage(tempDir.toAbsolutePath().toString());
        DukeException ex = assertThrows(DukeException.class, () -> storage.save(new TaskList()));
        assertTrue(ex.getMessage().contains("directory path"));
    }

    @Test
    public void loadPlaces_directoryPath_exceptionThrown() {
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString(), tempDir.toAbsolutePath().toString());
        DukeException ex = assertThrows(DukeException.class, storage::loadPlaces);
        assertTrue(ex.getMessage().contains("is a directory"));
    }

    @Test
    public void savePlaces_directoryPath_exceptionThrown() {
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString(), tempDir.toAbsolutePath().toString());
        DukeException ex = assertThrows(DukeException.class, () -> storage.savePlaces(new PlaceList()));
        assertTrue(ex.getMessage().contains("directory path"));
    }

    @Test
    public void loadPlaces_corruptedFileAndEmptyLines_skipsCorrupted() throws Exception {
        File file = tempDir.resolve("corrupted_places.txt").toFile();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("\n");
            writer.write("   \n");
            writer.write("Place Only\n");
            writer.write("Place With | Details\n");
            writer.write(" | missing name\n");
        }
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString(), file.getAbsolutePath());
        PlaceList loaded = storage.loadPlaces();
        assertEquals(2, loaded.size());
        assertEquals("Place Only", loaded.getPlace(0).getName());
        assertEquals("Place With", loaded.getPlace(1).getName());
        assertEquals("Details", loaded.getPlace(1).getDetails());
    }

    @Test
    public void setPlaceList_nullArgument_throwsAssertionError() {
        Storage storage = new Storage("tasks.txt", "places.txt");
        assertThrows(AssertionError.class, () -> storage.setPlaceList(null));
        assertThrows(AssertionError.class, () -> storage.savePlaces(null));
        assertThrows(AssertionError.class, () -> storage.save((java.util.ArrayList<Task>) null));
    }
}
