package benbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests saving and loading tasks through a temporary data file. */
class TaskStorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void store_writesReloadableTasksAndCompletionState() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("data/stored-task");
        Task firstTask = new Task("read book");
        Task secondTask = new Task("buy pen");
        Task thirdTask = new Task("book flight");
        firstTask.markDone();
        secondTask.markDone();

        new TaskDataStore(storedTaskPath).store(new Task[] {firstTask, secondTask, thirdTask}, 3);

        String storedData = Files.readString(storedTaskPath);
        assertEquals("todo read book" + System.lineSeparator()
                        + "todo buy pen" + System.lineSeparator()
                        + "todo book flight" + System.lineSeparator()
                        + "mark 1" + System.lineSeparator()
                        + "mark 2" + System.lineSeparator(),
                storedData);
        assertNoTemporaryStorageFiles(storedTaskPath.getParent());
    }

    @Test
    void store_emptyList_replacesExistingFileWithEmptyData() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("stored-task");
        Files.writeString(storedTaskPath, "todo obsolete task\n");

        new TaskDataStore(storedTaskPath).store(new Task[1], 0);

        assertEquals("", Files.readString(storedTaskPath));
    }

    @Test
    void store_replacementFailure_preservesTargetAndCleansTemporaryFile() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("data/stored-task");
        Files.createDirectories(storedTaskPath);
        Path sentinelPath = storedTaskPath.resolve("sentinel");
        Files.writeString(sentinelPath, "last good data");

        assertThrows(IOException.class, () ->
                new TaskDataStore(storedTaskPath).store(new Task[1], 0));

        assertEquals("last good data", Files.readString(sentinelPath));
        assertNoTemporaryStorageFiles(storedTaskPath.getParent());
    }

    @Test
    void store_parentPathFile_throwsIoException() throws Exception {
        Path blockingFile = temporaryDirectory.resolve("blocking-file");
        Files.writeString(blockingFile, "not a directory");
        Path storedTaskPath = blockingFile.resolve("stored-task");

        assertThrows(IOException.class, () ->
                new TaskDataStore(storedTaskPath).store(new Task[1], 0));
    }

    @Test
    void store_invalidState_throwsAssertionError() {
        TaskDataStore dataStore = new TaskDataStore(temporaryDirectory.resolve("stored-task"));
        List<String> commandsWithNull = new ArrayList<>();
        commandsWithNull.add(null);

        assertThrows(AssertionError.class, () -> dataStore.store(null, 0));
        assertThrows(AssertionError.class, () -> dataStore.store(new Task[1], -1));
        assertThrows(AssertionError.class, () -> dataStore.store(new Task[1], 2));
        assertThrows(AssertionError.class, () -> dataStore.store(new Task[1], 0, null));
        assertThrows(AssertionError.class, () ->
                dataStore.store(new Task[1], 0, commandsWithNull));
        assertThrows(AssertionError.class, () -> dataStore.store(new Task[1], 1));
    }

    @Test
    void load_savedTasks_recreatesTasksAndCompletionState() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("stored-task");
        Files.writeString(storedTaskPath, "todo read book\n"
                + "deadline return book /by 1/12/2029\n"
                + "mark 2\n");
        Task[] tasks = new Task[3];
        int[] taskCount = {0};

        String output = OutputCapture.capture(() -> new StoredTaskLoader(storedTaskPath)
                .load(new TaskLoader(3), tasks, taskCount));

        assertEquals(2, taskCount[0]);
        assertEquals("todo read book", tasks[0].toStorageString());
        assertEquals("deadline return book /by 1/12/2029", tasks[1].toStorageString());
        assertTrue(tasks[1].isDone());
        assertTrue(output.contains("2 items already in storage."));
    }

    @Test
    void storeAndLoad_contact_preservesContactDetails() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("stored-task");
        TaskLoader sourceLoader = new TaskLoader(1);
        sourceLoader.addTask("contact Alice Tan /phone 91234567 /email alice@example.com",
                new Task[1], new int[] {0}, false);
        new TaskDataStore(storedTaskPath).store(
                new Task[1], 0, sourceLoader.getExtensionStorageCommands());

        TaskLoader loadedLoader = new TaskLoader(1);
        String loadOutput = OutputCapture.capture(() -> new StoredTaskLoader(storedTaskPath)
                .load(loadedLoader, new Task[1], new int[] {0}));
        loadedLoader.addTask("contacts", new Task[1], new int[] {0}, false);

        assertEquals("contact Alice Tan /phone 91234567 /email alice@example.com"
                        + System.lineSeparator(), Files.readString(storedTaskPath));
        assertTrue(loadOutput.contains("1 item already in storage."));
        assertTrue(loadedLoader.getLastResponse().contains(
                "1.[C] Alice Tan (phone: 91234567; email: alice@example.com)"));
    }

    @Test
    void storeAndLoad_note_preservesNoteText() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("stored-task");
        TaskLoader sourceLoader = new TaskLoader(1);
        sourceLoader.addTask("note Watch Arrival", new Task[1], new int[] {0}, false);
        new TaskDataStore(storedTaskPath).store(
                new Task[1], 0, sourceLoader.getExtensionStorageCommands());

        TaskLoader loadedLoader = new TaskLoader(1);
        String loadOutput = OutputCapture.capture(() -> new StoredTaskLoader(storedTaskPath)
                .load(loadedLoader, new Task[1], new int[] {0}));
        loadedLoader.addTask("notes", new Task[1], new int[] {0}, false);

        assertEquals("note Watch Arrival" + System.lineSeparator(),
                Files.readString(storedTaskPath));
        assertTrue(loadOutput.contains("1 item already in storage."));
        assertTrue(loadedLoader.getLastResponse().contains("1.[N] Watch Arrival"));
    }

    @Test
    void storeAndLoad_expense_preservesDescriptionAndAmount() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("stored-task");
        TaskLoader sourceLoader = new TaskLoader(1);
        sourceLoader.addTask("expense bus fare /amount 2.4",
                new Task[1], new int[] {0}, false);
        new TaskDataStore(storedTaskPath).store(
                new Task[1], 0, sourceLoader.getExtensionStorageCommands());

        TaskLoader loadedLoader = new TaskLoader(1);
        String loadOutput = OutputCapture.capture(() -> new StoredTaskLoader(storedTaskPath)
                .load(loadedLoader, new Task[1], new int[] {0}));
        loadedLoader.addTask("expenses", new Task[1], new int[] {0}, false);

        assertEquals("expense bus fare /amount 2.40" + System.lineSeparator(),
                Files.readString(storedTaskPath));
        assertTrue(loadOutput.contains("1 item already in storage."));
        assertTrue(loadedLoader.getLastResponse().contains("1.[$] bus fare ($2.40)"));
        assertTrue(loadedLoader.getLastResponse().contains("Total expenses: $2.40"));
    }

    @Test
    void storeAndLoad_mixedItems_preservesAllData() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("stored-task");
        TaskLoader sourceLoader = new TaskLoader(2);
        Task[] sourceTasks = new Task[2];
        int[] sourceTaskCount = {0};
        sourceLoader.addTask("todo read book", sourceTasks, sourceTaskCount, false);
        sourceLoader.addTask("deadline return book /by 1/12/2029",
                sourceTasks, sourceTaskCount, false);
        sourceLoader.addTask("mark 2", sourceTasks, sourceTaskCount, false);
        sourceLoader.addTask("contact Alice Tan /phone 91234567 /email alice@example.com",
                sourceTasks, sourceTaskCount, false);
        sourceLoader.addTask("note Watch Arrival", sourceTasks, sourceTaskCount, false);
        sourceLoader.addTask("expense bus fare /amount 2.40",
                sourceTasks, sourceTaskCount, false);
        new TaskDataStore(storedTaskPath).store(
                sourceTasks, sourceTaskCount[0], sourceLoader.getExtensionStorageCommands());

        TaskLoader loadedLoader = new TaskLoader(2);
        Task[] loadedTasks = new Task[2];
        int[] loadedTaskCount = {0};
        String loadOutput = OutputCapture.capture(() -> new StoredTaskLoader(storedTaskPath)
                .load(loadedLoader, loadedTasks, loadedTaskCount));

        assertEquals(2, loadedTaskCount[0]);
        assertEquals("todo read book", loadedTasks[0].toStorageString());
        assertFalse(loadedTasks[0].isDone());
        assertEquals("deadline return book /by 1/12/2029", loadedTasks[1].toStorageString());
        assertTrue(loadedTasks[1].isDone());
        assertTrue(loadOutput.contains("5 items already in storage."));

        loadedLoader.addTask("contacts", loadedTasks, loadedTaskCount, false);
        assertTrue(loadedLoader.getLastResponse().contains(
                "1.[C] Alice Tan (phone: 91234567; email: alice@example.com)"));
        loadedLoader.addTask("notes", loadedTasks, loadedTaskCount, false);
        assertTrue(loadedLoader.getLastResponse().contains("1.[N] Watch Arrival"));
        loadedLoader.addTask("expenses", loadedTasks, loadedTaskCount, false);
        assertTrue(loadedLoader.getLastResponse().contains("1.[$] bus fare ($2.40)"));
        assertTrue(loadedLoader.getLastResponse().contains("Total expenses: $2.40"));
    }

    @Test
    void load_missingFile_reportsFreshStartWithoutChangingTasks() throws Exception {
        Path missingPath = temporaryDirectory.resolve("missing-task-file");
        Task[] tasks = new Task[1];
        int[] taskCount = {0};

        String output = OutputCapture.capture(() -> new StoredTaskLoader(missingPath)
                .load(new TaskLoader(1), tasks, taskCount));

        assertEquals(0, taskCount[0]);
        assertTrue(output.contains("No stored data found starting from scratch..."));
    }

    @Test
    void load_corruptFile_reportsErrorAndLeavesLiveStateUnchanged() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("stored-task");
        Files.writeString(storedTaskPath, "todo read book\ninvalid command\n");
        Task[] tasks = new Task[2];
        int[] taskCount = {0};

        String output = OutputCapture.capture(() -> new StoredTaskLoader(storedTaskPath)
                .load(new TaskLoader(2), tasks, taskCount));

        assertEquals(0, taskCount[0]);
        assertNull(tasks[0]);
        assertTrue(output.startsWith("ERROR: Unable to load stored data."));
        assertTrue(output.contains("Stored-task file is corrupted at line 2."));
        assertTrue(output.contains("The data already in BenBot was not changed."));
    }

    @Test
    void load_commandsExceedRemainingCapacity_leavesExistingLiveStateUnchanged() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("stored-task");
        Files.writeString(storedTaskPath, "todo first stored task\ntodo second stored task\n");
        Task existingTask = new Task("existing task");
        existingTask.markDone();
        Task[] tasks = {existingTask, null};
        int[] taskCount = {1};

        String output = OutputCapture.capture(() -> new StoredTaskLoader(storedTaskPath)
                .load(new TaskLoader(2), tasks, taskCount));

        assertEquals(1, taskCount[0]);
        assertEquals("todo existing task", tasks[0].toStorageString());
        assertTrue(tasks[0].isDone());
        assertNull(tasks[1]);
        assertTrue(output.contains("Stored-task file is corrupted at line 2."));
    }

    @Test
    void load_markCommandWithExistingTask_marksTaskFromStoredFile() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("stored-task");
        Files.writeString(storedTaskPath, "todo stored task\nmark 1\n");
        Task existingTask = new Task("existing task");
        Task[] tasks = {existingTask, null};
        int[] taskCount = {1};
        boolean[] wasLoaded = {false};

        String output = OutputCapture.capture(() -> wasLoaded[0] = new StoredTaskLoader(storedTaskPath)
                .load(new TaskLoader(2), tasks, taskCount));

        assertTrue(wasLoaded[0]);
        assertEquals(2, taskCount[0]);
        assertFalse(tasks[0].isDone());
        assertEquals("todo stored task", tasks[1].toStorageString());
        assertTrue(tasks[1].isDone());
        assertTrue(output.contains("1 item already in storage."));
    }

    @Test
    void load_duplicateLiveTasks_stagingFailureLeavesLiveStateUnchanged() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("stored-task");
        Files.writeString(storedTaskPath, "todo first stored task\ntodo second stored task\n");
        Task firstDuplicate = new Task("duplicate task");
        Task secondDuplicate = new Task("duplicate task");
        Task[] tasks = {firstDuplicate, secondDuplicate, null};
        int[] taskCount = {2};
        boolean[] wasLoaded = {true};

        String output = OutputCapture.capture(() -> wasLoaded[0] = new StoredTaskLoader(storedTaskPath)
                .load(new TaskLoader(3), tasks, taskCount));

        assertFalse(wasLoaded[0]);
        assertEquals(2, taskCount[0]);
        assertEquals("todo duplicate task", tasks[0].toStorageString());
        assertEquals("todo duplicate task", tasks[1].toStorageString());
        assertNull(tasks[2]);
        assertTrue(output.contains(
                "Existing BenBot data could not be validated safely before loading stored data."));
        assertTrue(output.contains("The data already in BenBot was not changed."));
    }

    @Test
    void load_blankLines_ignoresBlankLines() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("stored-task");
        Files.writeString(storedTaskPath, "todo read book\n\n   \nnote Watch Arrival\n");
        TaskLoader loader = new TaskLoader(1);
        Task[] tasks = new Task[1];
        int[] taskCount = {0};

        String output = OutputCapture.capture(() -> new StoredTaskLoader(storedTaskPath)
                .load(loader, tasks, taskCount));

        assertEquals(1, taskCount[0]);
        assertEquals("todo read book", tasks[0].toStorageString());
        loader.addTask("notes", tasks, taskCount, false);
        assertTrue(loader.getLastResponse().contains("1.[N] Watch Arrival"));
        assertTrue(output.contains("2 items already in storage."));
    }

    @Test
    void load_surroundingAndRepeatedWhitespace_normalizesCommands() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("stored-task");
        Files.writeString(storedTaskPath, "  todo    read   book  \n\tmark   1\t\n");
        Task[] tasks = new Task[1];
        int[] taskCount = {0};

        String output = OutputCapture.capture(() -> new StoredTaskLoader(storedTaskPath)
                .load(new TaskLoader(1), tasks, taskCount));

        assertEquals(1, taskCount[0]);
        assertEquals("todo read book", tasks[0].toStorageString());
        assertTrue(tasks[0].isDone());
        assertTrue(output.contains("1 item already in storage."));
    }

    @Test
    void load_repeatedMarkCommand_reportsCorruptionAndLeavesLiveStateUnchanged() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("stored-task");
        Files.writeString(storedTaskPath, "todo read book\nmark 1\nmark 1\n");
        Task[] tasks = new Task[1];
        int[] taskCount = {0};

        String output = OutputCapture.capture(() -> new StoredTaskLoader(storedTaskPath)
                .load(new TaskLoader(1), tasks, taskCount));

        assertEquals(0, taskCount[0]);
        assertNull(tasks[0]);
        assertTrue(output.contains("Stored-task file is corrupted at line 3."));
    }

    @Test
    void load_invalidMarkCommands_reportCorruption() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("stored-task");
        String[] invalidMarkCommands = {"mark zero", "mark 0"};

        for (String invalidMarkCommand : invalidMarkCommands) {
            Files.writeString(storedTaskPath, "todo read book\n" + invalidMarkCommand + "\n");
            Task[] tasks = new Task[1];
            int[] taskCount = {0};

            String output = OutputCapture.capture(() -> new StoredTaskLoader(storedTaskPath)
                    .load(new TaskLoader(1), tasks, taskCount));

            assertEquals(0, taskCount[0]);
            assertNull(tasks[0]);
            assertTrue(output.contains("Stored-task file is corrupted at line 2."));
        }
    }

    @Test
    void load_disallowedMutatingCommands_leaveLiveStateUnchanged() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("stored-task");
        String[] corruptedDataVersions = {
            "todo read book\ndelete 1\n",
            "todo read book\nmark 1\nunmark 1\n",
            "contact Alice /phone 91234567 /email alice@example.com\ndelete-contact 1\n",
            "note remember this\ndelete-note 1\n",
            "expense lunch /amount 5.00\ndelete-expense 1\n"
        };

        for (String corruptedData : corruptedDataVersions) {
            Files.writeString(storedTaskPath, corruptedData);
            TaskLoader taskLoader = new TaskLoader(1);
            Task[] tasks = new Task[1];
            int[] taskCount = {0};

            String output = OutputCapture.capture(() -> new StoredTaskLoader(storedTaskPath)
                    .load(taskLoader, tasks, taskCount));

            assertEquals(0, taskCount[0]);
            assertEquals(0, taskLoader.getStoredItemCount(taskCount[0]));
            assertNull(tasks[0]);
            assertTrue(output.contains("Stored-task file is corrupted at line "));
        }
    }

    @Test
    void load_pathIsDirectory_reportsReadError() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("stored-task-directory");
        Files.createDirectory(storedTaskPath);
        Task[] tasks = new Task[1];
        int[] taskCount = {0};

        String output = OutputCapture.capture(() -> new StoredTaskLoader(storedTaskPath)
                .load(new TaskLoader(1), tasks, taskCount));

        assertEquals(0, taskCount[0]);
        assertTrue(output.startsWith("ERROR: Unable to load stored data."));
        assertTrue(output.contains("The data already in BenBot was not changed."));
        assertTrue(output.contains(
                "The existing storage file will not be overwritten. Fix it before restarting BenBot."));
    }

    private void assertNoTemporaryStorageFiles(Path directory) throws IOException {
        try (Stream<Path> paths = Files.list(directory)) {
            assertFalse(paths.anyMatch(path ->
                    path.getFileName().toString().startsWith("benbot-storage-")));
        }
    }
}
