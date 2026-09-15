package monkey.storage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import monkey.model.Deadline;
import monkey.model.Event;
import monkey.model.Task;
import monkey.model.ToDos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void load_missingFile_returnsEmptyListWithoutError() {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());

        List<Task> tasks = storage.load();

        assertEquals(List.of(), tasks);
        assertNull(storage.getLastError());
    }

    @Test
    void load_fileContainingMalformedLine_keepsValidTasksAndReportsLine() throws Exception {
        Path saveFile = temporaryDirectory.resolve("monkey.txt");
        Files.writeString(saveFile, "T | 0 | buy milk\nD | nope | broken | 2026-09-20\n");
        Storage storage = new Storage(saveFile.toString());

        List<Task> tasks = storage.load();

        assertEquals(1, tasks.size());
        assertEquals("buy milk", tasks.get(0).getDescription());
        assertEquals("Ignored malformed saved task data on line 2.", storage.getLastError());
    }

    @Test
    void load_fileContainingDuplicateTask_keepsFirstTaskAndReportsLine() throws Exception {
        Path saveFile = temporaryDirectory.resolve("monkey.txt");
        Files.writeString(saveFile, "T | 0 | buy milk\nT | 1 | BUY   MILK\n");
        Storage storage = new Storage(saveFile.toString());

        List<Task> tasks = storage.load();

        assertEquals(1, tasks.size());
        assertFalse(tasks.get(0).isDone());
        assertEquals("Ignored duplicate saved task data on line 2.", storage.getLastError());
    }

    @Test
    void save_parentPathIsFile_returnsFalseAndReportsError() throws Exception {
        Path blockingFile = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(blockingFile, "block child creation");
        Storage storage = new Storage(blockingFile.resolve("monkey.txt").toString());

        boolean isSaved = storage.save(List.of(new ToDos("buy milk")));

        assertFalse(isSaved);
        assertEquals("Could not save tasks to disk.", storage.getLastError());
    }

    @Test
    void saveAndLoad_allTaskTypesAndStatuses_roundTripsData() {
        Path saveFile = temporaryDirectory.resolve("nested/data/monkey.txt");
        Storage storage = new Storage(saveFile.toString());
        ToDos todo = new ToDos("buy milk");
        Deadline deadline = new Deadline("submit", "2099-01-01 1730");
        Event event = new Event("class", "2pm", "3pm");
        deadline.markAsDone();

        assertTrue(storage.save(List.of(todo, deadline, event)));
        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertEquals("[T][ ] buy milk", loadedTasks.get(0).toString());
        assertEquals("[D][X] submit (by: Jan 01 2099, 5:30 PM)", loadedTasks.get(1).toString());
        assertEquals("[E][ ] class (from: 2pm to: 3pm)", loadedTasks.get(2).toString());
        assertNull(storage.getLastError());
    }

    @Test
    void save_nullTaskList_returnsFalse() {
        Storage storage = new Storage(temporaryDirectory.resolve("monkey.txt").toString());

        assertFalse(storage.save(null));
        assertEquals("Could not save tasks to disk.", storage.getLastError());
    }

    @Test
    void save_listContainingNull_skipsNullEntry() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("monkey.txt").toString());
        java.util.ArrayList<Task> tasks = new java.util.ArrayList<>();
        tasks.add(null);
        tasks.add(new ToDos("buy milk"));

        assertTrue(storage.save(tasks));

        assertEquals("T | 0 | buy milk", Files.readString(temporaryDirectory.resolve("monkey.txt")).trim());
    }

    @Test
    void load_multipleMalformedAndDuplicateLines_reportsCombinedWarnings() throws Exception {
        Path saveFile = temporaryDirectory.resolve("monkey.txt");
        Files.writeString(saveFile, "unknown\nT | 0 | task\nT | 1 | TASK\nbroken\n");
        Storage storage = new Storage(saveFile.toString());

        List<Task> tasks = storage.load();

        assertEquals(1, tasks.size());
        assertEquals("Ignored malformed saved task data on 2 lines. "
                + "Ignored duplicate saved task data on line 3.", storage.getLastError());
    }

    @Test
    void load_multipleDuplicateLines_reportsDuplicateCount() throws Exception {
        Path saveFile = temporaryDirectory.resolve("monkey.txt");
        Files.writeString(saveFile, "T | 0 | task\nT | 1 | TASK\nT | 0 | task\n");
        Storage storage = new Storage(saveFile.toString());

        List<Task> tasks = storage.load();

        assertEquals(1, tasks.size());
        assertEquals("Ignored duplicate saved task data on 2 lines.", storage.getLastError());
    }

    @Test
    void load_invalidFieldShapes_skipsEveryInvalidRecord() throws Exception {
        Path saveFile = temporaryDirectory.resolve("monkey.txt");
        Files.writeString(saveFile, "T | 2 | task\nT | 0 | \nT | 0 | task | extra\n"
                + "D | 0 | task\nD | 0 | task | bad-date\n"
                + "E | 0 | task | 2pm\nE | 0 | task | 3pm | 2pm\nX | 0 | task\n");
        Storage storage = new Storage(saveFile.toString());

        assertTrue(storage.load().isEmpty());
        assertEquals("Ignored malformed saved task data on 8 lines.", storage.getLastError());
    }

    @Test
    void load_pathIsDirectory_returnsEmptyListAndReportsReadError() {
        Storage storage = new Storage(temporaryDirectory.toString());

        assertTrue(storage.load().isEmpty());
        assertEquals("Could not read saved tasks from disk.", storage.getLastError());
    }
}
