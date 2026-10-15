package lumine.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import lumine.LumineException;
import lumine.task.Deadline;
import lumine.task.Event;
import lumine.task.Task;
import lumine.task.Todo;

/**
 * Tests task persistence, malformed save files, and storage failures.
 */
class StorageTest {
    private static final String LOAD_ERROR = "Sorry, I couldn't load your tasks. :C";
    private static final String SAVE_ERROR = "Sorry, I couldn't save your tasks. :C";

    @TempDir
    private Path temporaryDirectory;

    @Test
    void load_missingFile_returnsEmptyList() {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());

        assertTrue(storage.load().isEmpty());
    }

    @Test
    void load_blankLines_ignoresEmptyRecords() throws IOException {
        Path saveFile = temporaryDirectory.resolve("lumine.txt");
        Files.writeString(saveFile, "\nT | 0 | first task\n   \nT | 1 | second task\n");

        List<Task> tasks = new Storage(saveFile.toString()).load();

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] first task", tasks.get(0).toString());
        assertEquals("[T][X] second task", tasks.get(1).toString());
    }

    @Test
    void saveAndLoad_allTaskTypesAndEscapedFields_roundTrip() {
        Path saveFile = temporaryDirectory.resolve("nested").resolve("lumine.txt");
        Todo todo = new Todo("pipe | slash \\\nline");
        Deadline deadline = new Deadline("submit report", "2026 01 02 1530");
        Event event = new Event("team meeting", "2026 01 02 1400", "2026 01 03 1600");
        deadline.markDone();

        Storage storage = new Storage(saveFile.toString());
        storage.save(List.of(todo, deadline, event));
        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertEquals("[T][ ] pipe | slash \\\nline", loadedTasks.get(0).toString());
        assertEquals("[D][X] submit report (by: Jan 02 2026 15:30)", loadedTasks.get(1).toString());
        assertEquals("[E][ ] team meeting (from: Jan 02 2026 14:00 to: Jan 03 2026 16:00)",
                loadedTasks.get(2).toString());
    }

    @Test
    void load_unknownTaskType_reportsCorruptLine() throws IOException {
        Path saveFile = temporaryDirectory.resolve("lumine.txt");
        Files.writeString(saveFile, "T | 0 | valid\nX | 0 | corrupt\n");

        LumineException exception = assertThrows(
                LumineException.class, () -> new Storage(saveFile.toString()).load());

        assertEquals(LOAD_ERROR + "\nInvalid saved task on line 2.", exception.getMessage());
    }

    @Test
    void load_invalidDoneFlag_reportsCorruptLine() throws IOException {
        Path saveFile = temporaryDirectory.resolve("lumine.txt");
        Files.writeString(saveFile, "T | 2 | invalid status\n");

        LumineException exception = assertThrows(
                LumineException.class, () -> new Storage(saveFile.toString()).load());

        assertEquals(LOAD_ERROR + "\nInvalid saved task on line 1.", exception.getMessage());
    }

    @Test
    void load_wrongFieldCount_reportsCorruptLine() throws IOException {
        Path saveFile = temporaryDirectory.resolve("lumine.txt");
        Files.writeString(saveFile, "D | 0 | missing deadline\n");

        LumineException exception = assertThrows(
                LumineException.class, () -> new Storage(saveFile.toString()).load());

        assertEquals(LOAD_ERROR + "\nInvalid saved task on line 1.", exception.getMessage());
    }

    @Test
    void load_danglingEscape_reportsCorruptLine() throws IOException {
        Path saveFile = temporaryDirectory.resolve("lumine.txt");
        Files.writeString(saveFile, "T | 0 | trailing escape\\");

        LumineException exception = assertThrows(
                LumineException.class, () -> new Storage(saveFile.toString()).load());

        assertEquals(LOAD_ERROR + "\nInvalid saved task on line 1.", exception.getMessage());
    }

    @Test
    void load_invalidStructuredDate_reportsCorruptLine() throws IOException {
        Path saveFile = temporaryDirectory.resolve("lumine.txt");
        Files.writeString(saveFile, "T | 0 | valid\n\nD | 0 | deadline | 2026 02 30\n");

        LumineException exception = assertThrows(
                LumineException.class, () -> new Storage(saveFile.toString()).load());

        assertEquals(LOAD_ERROR + "\nInvalid saved task on line 3.", exception.getMessage());
    }

    @Test
    void load_invalidEventDate_reportsCorruptLine() throws IOException {
        Path saveFile = temporaryDirectory.resolve("lumine.txt");
        Files.writeString(saveFile, "T | 0 | valid\nE | 0 | meeting | 2026 02 30 1400 | 2026 03 01 1600\n");

        LumineException exception = assertThrows(
                LumineException.class, () -> new Storage(saveFile.toString()).load());

        assertEquals(LOAD_ERROR + "\nInvalid saved task on line 2.", exception.getMessage());
    }

    @Test
    void load_invalidEventRange_reportsCorruptLine() throws IOException {
        Path saveFile = temporaryDirectory.resolve("lumine.txt");
        Files.writeString(saveFile, "T | 0 | valid\nE | 0 | meeting | 2026 01 02 1400 | 2026 01 01 1600\n");

        LumineException exception = assertThrows(
                LumineException.class, () -> new Storage(saveFile.toString()).load());

        assertEquals(LOAD_ERROR + "\nInvalid saved task on line 2.", exception.getMessage());
    }

    @Test
    void load_eventWithoutRequiredTime_reportsCorruptLine() throws IOException {
        Path saveFile = temporaryDirectory.resolve("lumine.txt");
        Files.writeString(saveFile, "E | 0 | meeting | 2026 01 01 | 2026 01 02\n");

        LumineException exception = assertThrows(
                LumineException.class, () -> new Storage(saveFile.toString()).load());

        assertEquals(LOAD_ERROR + "\nInvalid saved task on line 1.", exception.getMessage());
    }

    @Test
    void load_directoryPath_throwsLoadError() {
        Path directory = temporaryDirectory.resolve("lumine.txt");
        assertTrue(directory.toFile().mkdir());

        LumineException exception = assertThrows(
                LumineException.class, () -> new Storage(directory.toString()).load());

        assertEquals(LOAD_ERROR, exception.getMessage());
    }

    @Test
    void save_nullTaskList_throwsSaveError() {
        Storage storage = new Storage(temporaryDirectory.resolve("lumine.txt").toString());

        LumineException exception = assertThrows(LumineException.class, () -> storage.save(null));

        assertEquals(SAVE_ERROR, exception.getMessage());
    }

    @Test
    void save_nullTask_throwsSaveError() {
        Storage storage = new Storage(temporaryDirectory.resolve("lumine.txt").toString());
        List<Task> tasks = new ArrayList<>(List.of(new Todo("valid")));
        tasks.add(null);

        LumineException exception = assertThrows(
                LumineException.class, () -> storage.save(tasks));

        assertEquals(SAVE_ERROR, exception.getMessage());
    }

    @Test
    void save_directoryPath_throwsSaveError() {
        Path directory = temporaryDirectory.resolve("lumine.txt");
        assertTrue(directory.toFile().mkdir());
        Storage storage = new Storage(directory.toString());

        LumineException exception = assertThrows(
                LumineException.class, () -> storage.save(List.of(new Todo("task"))));

        assertEquals(SAVE_ERROR, exception.getMessage());
    }
}
