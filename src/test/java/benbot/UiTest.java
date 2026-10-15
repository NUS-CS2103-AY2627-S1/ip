package benbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests BenBot's user-interface welcome and exit behavior. */
class UiTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void welcome_displaysGreetingAndDivider() throws Exception {
        Ui ui = new Ui(new Scanner(""), new TaskDataStore(temporaryDirectory.resolve("tasks")));

        String output = OutputCapture.capture(ui::welcome);

        assertTrue(output.contains("Hello! I'm BenBot."));
        assertTrue(output.contains(BenBot.DIVIDER));
    }

    @Test
    void welcome_displaysEveryAvailableCommand() throws Exception {
        Ui ui = new Ui(new Scanner(""), new TaskDataStore(temporaryDirectory.resolve("tasks")));
        String[] commandLines = {
            "todo DESCRIPTION",
            "deadline DESCRIPTION /by DATE [TIME]",
            "event DESCRIPTION /from START /to END",
            "list",
            "find KEYWORD",
            "mark TASK_NUMBER",
            "unmark TASK_NUMBER",
            "delete TASK_NUMBER",
            "contact NAME /phone PHONE /email EMAIL",
            "contacts",
            "delete-contact CONTACT_NUMBER",
            "note TEXT",
            "notes",
            "delete-note NOTE_NUMBER",
            "expense DESCRIPTION /amount AMOUNT",
            "expenses",
            "delete-expense EXPENSE_NUMBER"
        };

        String output = OutputCapture.capture(ui::welcome);
        List<String> outputLines = output.lines().map(String::strip).toList();

        for (String commandLine : commandLines) {
            assertTrue(outputLines.contains(commandLine));
        }
        assertTrue(outputLines.contains("Type bye to save all your data and exit."));
        assertTrue(outputLines.contains("Leading, trailing, and repeated spaces are ignored."));
        assertTrue(outputLines.contains(
                "START and END each accept a date with an optional time; END must be after START."));
        assertTrue(outputLines.contains("Exact duplicate tasks are rejected."));
        assertTrue(output.contains("15/9/2026"));
        assertTrue(output.contains("1800"));
    }

    @Test
    void run_byeCommand_storesTasksBeforeExiting() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("stored-task");
        Ui ui = new Ui(new Scanner("todo read book\nbye\n"), new TaskDataStore(storedTaskPath));
        Task[] tasks = new Task[2];
        int[] taskCount = {0};

        String output = OutputCapture.capture(() -> ui.run(new TaskLoader(2), tasks, taskCount));

        assertEquals(1, taskCount[0]);
        assertEquals("todo read book" + System.lineSeparator(), Files.readString(storedTaskPath));
        assertTrue(output.contains("Bye. Hope to see you again soon!"));
    }

    @Test
    void run_inputEndsWithoutBye_doesNotStoreTasks() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("stored-task");
        Ui ui = new Ui(new Scanner("todo read book\n"), new TaskDataStore(storedTaskPath));
        Task[] tasks = new Task[1];
        int[] taskCount = {0};

        OutputCapture.capture(() -> ui.run(new TaskLoader(1), tasks, taskCount));

        assertEquals(1, taskCount[0]);
        assertFalse(Files.exists(storedTaskPath));
    }

    @Test
    void storeData_storageFailure_returnsHelpfulError() throws Exception {
        Path parentFile = temporaryDirectory.resolve("parent-file");
        Files.writeString(parentFile, "not a directory");
        Ui ui = new Ui(new Scanner(""), new TaskDataStore(parentFile.resolve("stored-task")));

        String response = ui.storeData(new TaskLoader(1), new Task[1], 0);

        assertEquals(Ui.STORAGE_ERROR_MESSAGE, response);
    }

    @Test
    void run_byeWithStorageFailure_printsErrorAndContinues() throws Exception {
        Path parentFile = temporaryDirectory.resolve("parent-file");
        Files.writeString(parentFile, "not a directory");
        Ui ui = new Ui(new Scanner("bye\ntodo read book\n"),
                new TaskDataStore(parentFile.resolve("stored-task")));
        Task[] tasks = new Task[1];
        int[] taskCount = {0};

        String output = OutputCapture.capture(() ->
                ui.run(new TaskLoader(1), tasks, taskCount));

        assertFalse(output.contains("Bye. Hope to see you again soon!"));
        assertTrue(output.contains(Ui.STORAGE_ERROR_MESSAGE));
        assertTrue(output.contains(BenBot.SAVE_RETRY_MESSAGE));
        assertEquals(1, taskCount[0]);
        assertEquals("todo read book", tasks[0].toStorageString());
    }

    @Test
    void run_retryAfterStorageBecomesWritable_savesAndStops() throws Exception {
        Path parentFile = temporaryDirectory.resolve("parent-file");
        Path storedTaskPath = parentFile.resolve("stored-task");
        Files.writeString(parentFile, "not a directory");
        Ui ui = new Ui(new Scanner("bye\ntodo read book\nbye\ntodo ignored\n"),
                new RepairAfterFailureTaskDataStore(parentFile));
        Task[] tasks = new Task[2];
        int[] taskCount = {0};

        String output = OutputCapture.capture(() ->
                ui.run(new TaskLoader(2), tasks, taskCount));

        assertTrue(output.contains(Ui.STORAGE_ERROR_MESSAGE));
        assertTrue(output.contains(BenBot.SAVE_RETRY_MESSAGE));
        assertEquals(1, taskCount[0]);
        assertEquals("todo read book", tasks[0].toStorageString());
        assertEquals("todo read book" + System.lineSeparator(), Files.readString(storedTaskPath));
        assertFalse(output.contains("ignored"));
    }

    /** Simulates an external storage-path repair immediately after the first failed save. */
    private static final class RepairAfterFailureTaskDataStore extends TaskDataStore {
        /** The file that initially prevents creation of the storage directory. */
        private final Path blockingParentFile;

        /** Whether the blocking path has already been replaced with a writable directory. */
        private boolean hasRepairedStoragePath;

        RepairAfterFailureTaskDataStore(Path blockingParentFile) {
            super(blockingParentFile.resolve("stored-task"));
            this.blockingParentFile = blockingParentFile;
        }

        /** Attempts a save and repairs the blocking parent path after its first failure. */
        @Override
        void store(Task[] tasks, int taskCount, List<String> extensionCommands) throws IOException {
            try {
                super.store(tasks, taskCount, extensionCommands);
            } catch (IOException exception) {
                if (!hasRepairedStoragePath) {
                    Files.delete(blockingParentFile);
                    Files.createDirectory(blockingParentFile);
                    hasRepairedStoragePath = true;
                }
                throw exception;
            }
        }
    }
}
