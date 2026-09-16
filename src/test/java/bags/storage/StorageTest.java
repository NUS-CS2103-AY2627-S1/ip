package bags.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bags.exception.BagsException;
import bags.parser.Parser;
import bags.task.Task;

/**
 * Tests the {@link Storage} class for saving and loading task records.
 *
 * <p>
 * These tests verify that task records are correctly written to files,
 * loaded from files, and handled when invalid records or file-related
 * errors are encountered.
 * </p>
 *
 */
class StorageTest {

    @TempDir
    Path tempDir;

    @Test
    void save_validRecords_writesRecordsToFile() throws Exception {
        Path file = tempDir.resolve("Bags.txt");
        Storage storage = new Storage(file.toString());

        List<String> records = List.of(
                "T | [ ] | Read book",
                "T | [X] | Do homework"
        );

        storage.saveRecords(records);

        List<String> savedRecords = Files.readAllLines(file);

        assertEquals(records, savedRecords);
    }

    @Test
    void saveRecords_existingRecords_overwritesPreviousContent() throws Exception {
        Path file = tempDir.resolve("Bags.txt");
        Files.write(file, List.of("T | [ ] | Old task"));
        Storage storage = new Storage(file.toString());

        storage.saveRecords(List.of("T | [X] | New task"));

        assertEquals(List.of("T | [X] | New task"), Files.readAllLines(file));
    }

    @Test
    void saveRecords_missingParentDirectory_createsFileAndDirectory() throws Exception {
        Path file = tempDir.resolve("data").resolve("Bags.txt");
        Storage storage = new Storage(file.toString());

        storage.saveRecords(List.of("T | [ ] | Read book"));

        assertTrue(Files.exists(file));
        assertEquals(List.of("T | [ ] | Read book"), Files.readAllLines(file));
    }

    @Test
    void saveRecords_emptyRecords_clearsExistingFile() throws Exception {
        Path file = tempDir.resolve("Bags.txt");
        Files.write(file, List.of("T | [ ] | Read book"));
        Storage storage = new Storage(file.toString());

        storage.saveRecords(List.of());

        assertTrue(Files.readAllLines(file).isEmpty());
    }

    /**
     * Tests that saving to a directory instead of a file handles
     * the I/O exception correctly.
     *
     * @throws Exception if an unexpected file operation error occurs
     */
    @Test
    void save_directoryAsFile_throwsException() throws Exception {
        Path directory = tempDir.resolve("Bags");
        Files.createDirectory(directory);

        Storage storage = new Storage(directory.toString());

        BagsException exception = assertThrows(
                BagsException.class, () -> storage.saveRecords(List.of("T | [ ] | Read book"))
        );

        assertTrue(exception.getMessage().contains("Unable to save tasks"));
    }

    /**
     * Tests that valid task records are correctly loaded and converted
     * into {@link Task} objects.
     *
     * @throws Exception if an unexpected file or parsing error occurs
     */
    @Test
    void loadTasks_validRecords_returnsCorrectTasks() throws Exception {
        Path file = tempDir.resolve("Bags.txt");

        Files.write(
                file,
                List.of(
                        "T | [ ] | Read book",
                        "T | [X] | Do homework"
                )
        );

        Storage storage = new Storage(file.toString());
        Parser parser = new Parser();

        List<Task> tasks = storage.loadTasks(parser);

        assertEquals(2, tasks.size());

        assertEquals("Read book", tasks.get(0).getDescription());
        assertFalse(tasks.get(0).isDone());

        assertEquals("Do homework", tasks.get(1).getDescription());
        assertTrue(tasks.get(1).isDone());
    }

    /**
     * Tests that invalid task records are ignored while valid
     * records are loaded correctly.
     *
     * @throws Exception if an unexpected file or parsing error occurs
     */
    @Test
    void loadTasks_invalidRecord_ignoresInvalidTask() throws Exception {
        Path file = tempDir.resolve("Bags.txt");

        Files.write(
                file,
                List.of(
                        "T | [ ] | Read book",
                        "X | [ ] | Invalid task",
                        "T | [X] | Do homework"
                )
        );

        Storage storage = new Storage(file.toString());
        Parser parser = new Parser();

        List<Task> tasks = storage.loadTasks(parser);

        assertEquals(2, tasks.size());
        assertEquals("Read book", tasks.get(0).getDescription());
        assertEquals("Do homework", tasks.get(1).getDescription());
    }

    /**
     * Tests that attempting to load tasks from a file that does not
     * exist returns an empty task list.
     *
     * @throws Exception if an unexpected application error occurs
     */
    @Test
    void loadTasks_missingFile_returnsEmptyList() throws Exception {
        Path file = tempDir.resolve("DoesNotExist.txt");

        Storage storage = new Storage(file.toString());
        Parser parser = new Parser();

        List<Task> tasks = storage.loadTasks(parser);

        assertTrue(tasks.isEmpty());
        assertTrue(storage.isSaveFileMissing());
    }

    @Test
    void loadTasks_emptyFile_returnsEmptyList() throws Exception {
        Path file = tempDir.resolve("Bags.txt");
        Files.createFile(file);
        Storage storage = new Storage(file.toString());

        List<Task> tasks = storage.loadTasks(new Parser());

        assertTrue(tasks.isEmpty());
    }

    @Test
    void loadTasks_invalidDateRecord_throwsException() throws Exception {
        Path file = tempDir.resolve("Bags.txt");
        Files.write(file, List.of("D | [ ] | Submit report | invalid-date"));
        Storage storage = new Storage(file.toString());

        BagsException exception = assertThrows(
                BagsException.class, () -> storage.loadTasks(new Parser())
        );

        assertTrue(exception.getMessage().contains("Invalid date"));
    }

    /**
     * Tests that task records saved to a file can be loaded back
     * as the correct task objects.
     *
     * @throws Exception if an unexpected file or parsing error occurs
     */
    @Test
    void saveThenLoad_validRecords_returnsSameTasks() throws Exception {
        Path file = tempDir.resolve("Bags.txt");
        Storage storage = new Storage(file.toString());

        List<String> records = List.of(
                "T | [ ] | Read book",
                "T | [X] | Do homework"
        );

        storage.saveRecords(records);

        Parser parser = new Parser();
        List<Task> tasks = storage.loadTasks(parser);

        assertEquals(2, tasks.size());

        assertEquals("Read book", tasks.get(0).getDescription());
        assertFalse(tasks.get(0).isDone());

        assertEquals("Do homework", tasks.get(1).getDescription());
        assertTrue(tasks.get(1).isDone());
    }
}
