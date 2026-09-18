package friday.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import friday.model.Deadline;
import friday.model.Event;
import friday.model.TaskList;
import friday.model.ToDo;

/**
 * Tests persistence, missing-file handling, and corrupted-record recovery.
 */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    /**
     * Verifies that all task types and completion states survive a round trip.
     */
    @Test
    void saveThenLoad_roundTripsAllTaskTypesAndCompletionStatus() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("nested/friday.txt").toString());
        ToDo todo = new ToDo("read | book");
        todo.markAsDone();
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 6, 6));
        Event event = new Event("project meeting", LocalDateTime.of(2019, 8, 6, 14, 0),
                LocalDateTime.of(2019, 8, 6, 16, 0));
        TaskList original = new TaskList(List.of(todo, deadline, event));

        storage.save(original);
        Storage.LoadResult loaded = storage.load();

        assertEquals(3, loaded.getTasks().size());
        assertTrue(loaded.getWarnings().isEmpty());
        assertEquals("[T][X] read | book", loaded.getTasks().get(0).toString());
        assertEquals("[D][ ] return book (by: Jun 06 2019)", loaded.getTasks().get(1).toString());
        assertEquals("[E][ ] project meeting (from: Aug 06 2019 14:00 to: Aug 06 2019 16:00)",
                loaded.getTasks().get(2).toString());
    }

    /**
     * Verifies that loading a missing file returns an empty result.
     */
    @Test
    void load_missingFile_returnsEmptyResultWithoutWarnings() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("missing/friday.txt").toString());

        Storage.LoadResult result = storage.load();

        assertTrue(result.getTasks().isEmpty());
        assertTrue(result.getWarnings().isEmpty());
    }

    /**
     * Verifies that malformed records are skipped while valid records load.
     */
    @Test
    void load_corruptedRecords_skipsInvalidLinesAndLoadsValidRecords() throws Exception {
        Path dataFile = temporaryDirectory.resolve("friday.txt");
        Files.writeString(dataFile, String.join("\n",
                "T | 0 | valid todo",
                "D | 2 | bad status | 2019-06-06",
                "X | 0 | unknown task",
                "D | 0 | valid deadline | 2019-06-06"));

        Storage.LoadResult result = new Storage(dataFile.toString()).load();

        assertEquals(2, result.getTasks().size());
        assertEquals(2, result.getWarnings().size());
        assertEquals("[T][ ] valid todo", result.getTasks().get(0).toString());
        assertEquals("[D][ ] valid deadline (by: Jun 06 2019)", result.getTasks().get(1).toString());
        assertTrue(result.getWarnings().get(0).contains("line 2"));
        assertTrue(result.getWarnings().get(1).contains("line 3"));
    }

    /**
     * Verifies that duplicate and chronologically invalid records are skipped safely.
     */
    @Test
    void load_duplicateAndInvalidEvent_recordsAreSkipped() throws Exception {
        Path dataFile = temporaryDirectory.resolve("friday.txt");
        Files.writeString(dataFile, String.join("\n",
                "T | 0 | duplicate",
                "T | 1 | duplicate",
                "E | 0 | invalid range | 2019-08-06T16:00 | 2019-08-06T14:00"));

        Storage.LoadResult result = new Storage(dataFile.toString()).load();

        assertEquals(1, result.getTasks().size());
        assertEquals(2, result.getWarnings().size());
        assertTrue(result.getWarnings().get(0).contains("duplicate"));
        assertTrue(result.getWarnings().get(1).contains("line 3"));
    }
}
