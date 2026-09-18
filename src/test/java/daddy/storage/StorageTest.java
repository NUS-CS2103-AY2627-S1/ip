package daddy.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import daddy.exception.DaddyException;
import daddy.task.Deadline;
import daddy.task.Event;
import daddy.task.Task;
import daddy.task.TaskList;
import daddy.task.Todo;

/**
 * Tests persistence, legacy migration, and corrupt-record recovery in task storage.
 */
class StorageTest {
    /** Provides an empty temporary folder for every storage scenario. */
    @TempDir
    Path temporaryDirectory;

    /**
     * Verifies that all supported task types and done statuses survive a save-and-load cycle.
     *
     * @throws Exception if temporary test data cannot be written or read
     */
    @Test
    void saveAndLoad_mixedTasks_roundTripsTaskData() throws Exception {
        Path dataFile = temporaryDirectory.resolve("nested").resolve("daddy.txt");
        Storage storage = new Storage(dataFile, temporaryDirectory.resolve("duke.txt"));
        TaskList savedTasks = new TaskList();
        Task todo = new Todo("read book");
        Task deadline = new Deadline("return book", LocalDateTime.of(2026, 12, 2, 18, 0));
        Task event = new Event("meeting", LocalDateTime.of(2026, 12, 3, 14, 0),
                LocalDateTime.of(2026, 12, 3, 16, 0));
        deadline.markAsDone();
        savedTasks.add(todo);
        savedTasks.add(deadline);
        savedTasks.add(event);

        storage.save(savedTasks);
        TaskList loadedTasks = new TaskList();
        List<String> messages = storage.loadInto(loadedTasks);

        assertTrue(messages.isEmpty());
        assertEquals(List.of(
                "T|0|read book",
                "D|1|return book|2026-12-02T18:00",
                "E|0|meeting|2026-12-03T14:00|2026-12-03T16:00"), Files.readAllLines(dataFile));
        assertEquals(3, loadedTasks.size());
        assertEquals("read book", taskAt(loadedTasks, 0).getDescription());
        assertEquals("X", taskAt(loadedTasks, 1).getStatusIcon());
        assertEquals("[D]", taskAt(loadedTasks, 1).getTypeIcon());
        assertEquals("[E]", taskAt(loadedTasks, 2).getTypeIcon());
    }

    /**
     * Verifies that loading with no data file leaves the supplied list unchanged.
     */
    @Test
    void loadInto_noDataFiles_returnsNoMessagesAndLeavesListEmpty() {
        Storage storage = new Storage(temporaryDirectory.resolve("daddy.txt"),
                temporaryDirectory.resolve("duke.txt"));
        TaskList tasks = new TaskList();

        List<String> messages = storage.loadInto(tasks);

        assertTrue(messages.isEmpty());
        assertEquals(0, tasks.size());
    }

    /**
     * Verifies that a legacy Duke data file is moved to the Daddy data file before loading.
     *
     * @throws IOException if the legacy data file cannot be created
     */
    @Test
    void loadInto_onlyLegacyDataFileExists_movesFileAndLoadsTasks() throws IOException {
        Path dataFile = temporaryDirectory.resolve("daddy.txt");
        Path legacyDataFile = temporaryDirectory.resolve("duke.txt");
        Files.write(legacyDataFile, List.of("T|1|keep me"));
        Storage storage = new Storage(dataFile, legacyDataFile);
        TaskList tasks = new TaskList();

        List<String> messages = storage.loadInto(tasks);

        assertTrue(Files.exists(dataFile));
        assertFalse(Files.exists(legacyDataFile));
        assertEquals(List.of("T|1|keep me"), Files.readAllLines(dataFile));
        assertEquals(1, tasks.size());
        assertEquals("X", taskAt(tasks, 0).getStatusIcon());
        assertTrue(messages.getFirst().contains("moved your saved tasks"));
    }

    /**
     * Verifies that an existing current file takes priority over an obsolete legacy file.
     *
     * @throws IOException if the temporary data files cannot be created
     */
    @Test
    void loadInto_currentAndLegacyFilesExist_loadsCurrentFileWithoutMigration() throws IOException {
        Path dataFile = temporaryDirectory.resolve("daddy.txt");
        Path legacyDataFile = temporaryDirectory.resolve("duke.txt");
        Files.write(dataFile, List.of("T|0|current task"));
        Files.write(legacyDataFile, List.of("T|0|legacy task"));
        Storage storage = new Storage(dataFile, legacyDataFile);
        TaskList tasks = new TaskList();

        List<String> messages = storage.loadInto(tasks);

        assertTrue(messages.isEmpty());
        assertEquals(1, tasks.size());
        assertEquals("current task", tasks.getTaskAt(0).getDescription());
        assertTrue(Files.exists(legacyDataFile));
    }

    /**
     * Verifies that migration creates missing destination directories before moving legacy data.
     *
     * @throws IOException if the legacy data file cannot be created
     */
    @Test
    void loadInto_nestedCurrentPath_migratesLegacyFileAndCreatesDirectories() throws IOException {
        Path dataFile = temporaryDirectory.resolve("nested").resolve("data").resolve("daddy.txt");
        Path legacyDataFile = temporaryDirectory.resolve("duke.txt");
        Files.write(legacyDataFile, List.of("T|0|legacy task"));
        Storage storage = new Storage(dataFile, legacyDataFile);
        TaskList tasks = new TaskList();

        List<String> messages = storage.loadInto(tasks);

        assertTrue(messages.stream().anyMatch(message -> message.contains("moved your saved tasks")));
        assertTrue(Files.exists(dataFile));
        assertEquals("legacy task", tasks.getTaskAt(0).getDescription());
    }

    /**
     * Verifies that a migration failure is reported while preserving the legacy data.
     *
     * @throws IOException if the blocking path or legacy file cannot be created
     */
    @Test
    void loadInto_migrationDestinationBlocked_reportsFailureAndKeepsLegacyFile() throws IOException {
        Path blockingParent = temporaryDirectory.resolve("blocked");
        Files.writeString(blockingParent, "not a directory");
        Path dataFile = blockingParent.resolve("daddy.txt");
        Path legacyDataFile = temporaryDirectory.resolve("duke.txt");
        Files.write(legacyDataFile, List.of("T|0|legacy task"));
        Storage storage = new Storage(dataFile, legacyDataFile);

        List<String> messages = storage.loadInto(new TaskList());

        assertTrue(messages.stream().anyMatch(message -> message.contains("couldn't migrate")));
        assertTrue(Files.exists(legacyDataFile));
    }

    /**
     * Verifies that malformed records are skipped while valid records and recovery files are preserved.
     *
     * @throws IOException if temporary test data cannot be written or read
     */
    @Test
    void loadInto_corruptRecords_skipsRecordsAndCreatesRecoveryFiles() throws IOException {
        Path dataFile = temporaryDirectory.resolve("daddy.txt");
        List<String> originalRecords = List.of(
                "T|1|keep me",
                "BROKEN",
                "D|0|return book|2026-12-02T18:00",
                "E|x|bad",
                "E|0|backwards|2026-12-03T16:00|2026-12-03T14:00",
                "D|0|impossible|2026-02-30T18:00",
                "T|0|keep me");
        Files.write(dataFile, originalRecords);
        Storage storage = new Storage(dataFile, temporaryDirectory.resolve("duke.txt"));
        TaskList tasks = new TaskList();

        List<String> messages = storage.loadInto(tasks);

        assertEquals(2, tasks.size());
        assertEquals("keep me", taskAt(tasks, 0).getDescription());
        assertEquals("return book", taskAt(tasks, 1).getDescription());
        assertEquals(originalRecords, Files.readAllLines(temporaryDirectory.resolve("daddy.txt.backup")));
        assertEquals(List.of("line 2: BROKEN", "line 4: E|x|bad",
                        "line 5: E|0|backwards|2026-12-03T16:00|2026-12-03T14:00",
                        "line 6: D|0|impossible|2026-02-30T18:00",
                        "line 7: T|0|keep me"),
                Files.readAllLines(temporaryDirectory.resolve("daddy.txt.corrupt")));
        assertTrue(messages.stream().anyMatch(message -> message.contains("line 2")));
        assertTrue(messages.stream().anyMatch(message -> message.contains("line 4")));
        assertTrue(messages.stream().anyMatch(message -> message.contains("line 5")));
        assertTrue(messages.stream().anyMatch(message -> message.contains("line 6")));
        assertTrue(messages.stream().anyMatch(message -> message.contains("duplicate task on line 7")));
    }

    /**
     * Verifies blank lines are ignored and unsupported record shapes are quarantined.
     *
     * @throws IOException if temporary test data cannot be written or read
     */
    @Test
    void loadInto_blankAndUnsupportedRecords_ignoresBlankAndQuarantinesInvalidRecords() throws IOException {
        Path dataFile = temporaryDirectory.resolve("daddy.txt");
        Files.write(dataFile, List.of(
                "",
                "X|0|unknown",
                "T|0|too|many",
                "D|0|too few",
                "E|0|too|few",
                "T|2|bad status",
                "T|0|valid task"));
        Storage storage = new Storage(dataFile, temporaryDirectory.resolve("duke.txt"));
        TaskList tasks = new TaskList();

        List<String> messages = storage.loadInto(tasks);

        assertEquals(1, tasks.size());
        assertEquals("valid task", tasks.getTaskAt(0).getDescription());
        assertEquals(5, messages.stream().filter(message -> message.contains("corrupted data")).count());
        assertEquals(5, Files.readAllLines(temporaryDirectory.resolve("daddy.txt.corrupt")).size());
    }

    /**
     * Verifies that valid records still load when recovery files cannot be written.
     *
     * @throws IOException if the data and blocking recovery paths cannot be created
     */
    @Test
    void loadInto_recoveryPathBlocked_keepsValidTasksAndReportsRecoveryFailure() throws IOException {
        Path dataFile = temporaryDirectory.resolve("daddy.txt");
        Files.write(dataFile, List.of("T|0|valid task", "BROKEN"));
        Path backupDirectory = temporaryDirectory.resolve("daddy.txt.backup");
        Files.createDirectory(backupDirectory);
        Files.writeString(backupDirectory.resolve("keep.txt"), "prevent replacement");
        Storage storage = new Storage(dataFile, temporaryDirectory.resolve("duke.txt"));
        TaskList tasks = new TaskList();

        List<String> messages = storage.loadInto(tasks);

        assertEquals(1, tasks.size());
        assertEquals("valid task", tasks.getTaskAt(0).getDescription());
        assertTrue(messages.stream().anyMatch(message -> message.contains("could not create the recovery files")));
    }

    /**
     * Verifies that a directory at the data-file path is reported without crashing startup.
     *
     * @throws IOException if the temporary directory cannot be created
     */
    @Test
    void loadInto_dataPathIsDirectory_reportsSpecificProblem() throws IOException {
        Path dataFile = temporaryDirectory.resolve("daddy.txt");
        Files.createDirectory(dataFile);
        Storage storage = new Storage(dataFile, temporaryDirectory.resolve("duke.txt"));

        List<String> messages = storage.loadInto(new TaskList());

        assertEquals(1, messages.size());
        assertTrue(messages.getFirst().contains("expected a data file"));
    }

    /**
     * Verifies that an unusable parent path produces a user-facing save error.
     *
     * @throws IOException if the blocking file cannot be created
     */
    @Test
    void save_parentPathIsFile_throwsDaddyException() throws IOException {
        Path blockingParent = temporaryDirectory.resolve("not-a-folder");
        Files.writeString(blockingParent, "blocking file");
        Storage storage = new Storage(blockingParent.resolve("daddy.txt"),
                temporaryDirectory.resolve("duke.txt"));
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        DaddyException exception = assertThrows(DaddyException.class, () -> storage.save(tasks));

        assertTrue(exception.getMessage().contains("writable file location"));
    }

    /**
     * Verifies that an empty list and a general task serialize to the supported file format.
     *
     * @throws Exception if temporary task data cannot be saved or read
     */
    @Test
    void save_emptyThenGeneralTask_writesExpectedRecords() throws Exception {
        Path dataFile = temporaryDirectory.resolve("daddy.txt");
        Storage storage = new Storage(dataFile, temporaryDirectory.resolve("duke.txt"));
        TaskList tasks = new TaskList();

        storage.save(tasks);
        assertTrue(Files.readAllLines(dataFile).isEmpty());

        tasks.add(new Task("general task"));
        storage.save(tasks);
        assertEquals(List.of("T|0|general task"), Files.readAllLines(dataFile));
    }

    /**
     * Verifies failed replacement removes the temporary file and reports a save error.
     *
     * @throws IOException if the blocking destination cannot be created
     */
    @Test
    void save_destinationIsDirectory_reportsFailureAndCleansTemporaryFile() throws IOException {
        Path dataFile = temporaryDirectory.resolve("daddy.txt");
        Files.createDirectory(dataFile);
        Files.writeString(dataFile.resolve("keep.txt"), "prevent replacement");
        Storage storage = new Storage(dataFile, temporaryDirectory.resolve("duke.txt"));
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        DaddyException exception = assertThrows(DaddyException.class, () -> storage.save(tasks));

        assertTrue(exception.getMessage().contains("save"));
        assertTrue(exception.getMessage().contains(dataFile.toString()));
        try (var children = Files.list(temporaryDirectory)) {
            assertFalse(children.anyMatch(path -> path.getFileName().toString().startsWith("daddy-save-")));
        }
    }

    /**
     * Returns the task at a zero-based list index.
     *
     * @param tasks the task list to inspect
     * @param index the zero-based index to read
     * @return the selected task
     */
    private Task taskAt(TaskList tasks, int index) {
        List<Task> taskValues = new ArrayList<>();
        for (Task task : tasks) {
            taskValues.add(task);
        }
        return taskValues.get(index);
    }
}
