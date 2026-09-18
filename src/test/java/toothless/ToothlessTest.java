package toothless;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import toothless.storage.Storage;
import toothless.storage.StorageException;
import toothless.task.Deadline;
import toothless.task.TaskList;
import toothless.task.Todo;

/**
 * Tests application-level command, storage, and restart behavior.
 */
@ResourceLock("java.lang.System.in")
@ResourceLock(Resources.SYSTEM_OUT)
public class ToothlessTest {
    @TempDir
    private Path temporaryDirectory;

    /**
     * Verifies only successful commands that change task state trigger persistence.
     */
    @Test
    public void run_mutatingCommands_savesOnlySuccessfulStateChanges() throws Exception {
        CountingStorage storage = new CountingStorage(temporaryDirectory.resolve("tasks.txt"));
        storage.save(new TaskList());
        storage.resetSaveCount();

        runWithInput(storage, "todo first task\n"
                + "deadline second task /by 2019-12-02\n"
                + "event third task /from 2pm /to 3pm\n"
                + "mark 0\n"
                + "mark -1\n"
                + "mark 99\n"
                + "mark nope\n"
                + "mark\n"
                + "mark 1\n"
                + "mark 1\n"
                + "unmark 1\n"
                + "mark 1\n"
                + "unmark 0\n"
                + "unmark -1\n"
                + "unmark 99\n"
                + "unmark nope\n"
                + "unmark\n"
                + "unmark 1\n"
                + "unmark 1\n"
                + "delete 0\n"
                + "delete -1\n"
                + "delete 99\n"
                + "delete nope\n"
                + "delete\n"
                + "delete 3\n"
                + "bye\n");

        assertEquals(8, storage.getSaveCount());
        assertEquals(List.of(
                "T | 0 | first task",
                "D | 0 | second task | 2019-12-02"),
                Files.readAllLines(storage.getDataFile(), StandardCharsets.UTF_8));

        TaskList reloadedTasks = new Storage(storage.getDataFile()).load().getTaskList();
        assertEquals(2, reloadedTasks.size());
        assertEquals("first task", reloadedTasks.getTask(0).getDescription());
        assertFalse(reloadedTasks.getTask(0).isDone());
        assertEquals("second task", reloadedTasks.getTask(1).getDescription());
    }

    /**
     * Verifies tasks loaded at startup are immediately available to commands.
     */
    @Test
    public void run_savedCompletedTask_displaysLoadedTaskAtStartup() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataFile);
        TaskList tasks = new TaskList();
        Todo todo = new Todo("borrow book");
        todo.markAsDone();
        tasks.addTask(todo);
        storage.save(tasks);

        String output = runWithInput(storage, "list\nbye\n");

        assertTrue(output.contains("1.[T][" + DisplaySymbols.getDoneMark() + "] borrow book"));
    }

    /**
     * Verifies a save failure is reported without publishing unsaved changes.
     */
    @Test
    public void run_saveFailure_reportsFriendlyMessageWithoutChangingMemory() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "", StandardCharsets.UTF_8);
        Storage storage = new FailingSaveStorage(dataFile);

        String output = runWithInput(storage, "todo keep this task\nlist\nbye\n");

        assertTrue(output.contains("Toothless couldn’t tuck these changes into his data file."));
        assertTrue(output.contains("Your task list is empty. Ready for a new adventure!"));
        assertFalse(output.contains("1.[T][ ] keep this task"));
        assertFalse(output.contains("Exception"));
    }

    /**
     * Verifies malformed storage is reported while valid saved tasks remain usable.
     */
    @Test
    public void run_malformedSavedData_reportsWarningAndKeepsValidTasks() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.write(dataFile, List.of("T | 0 | valid task", "unknown line"),
                StandardCharsets.UTF_8);

        String output = runWithInput(new Storage(dataFile), "list\nbye\n");

        assertTrue(output.contains("Toothless found 1 puzzling line in his saved quests."));
        assertTrue(output.contains("1.[T][ ] valid task"));
        assertFalse(output.contains("Exception"));
    }

    /**
     * Verifies a task marked through a command remains completed after restarting Toothless.
     */
    @Test
    public void run_markTaskThenRestart_preservesCompletedStatus() {
        Storage storage = new Storage(
                temporaryDirectory.resolve("data").resolve("tasks.txt"));

        runWithInput(storage, "todo borrow book\nmark 1\nbye\n");
        String restartedOutput = runWithInput(storage, "list\nbye\n");

        assertTrue(restartedOutput.contains(
                "1.[T][" + DisplaySymbols.getDoneMark() + "] borrow book"));
    }

    /**
     * Verifies successful edits are saved while invalid edits leave saved state unchanged.
     */
    @Test
    public void run_editTasksThenRestart_preservesFieldsTypesStatusAndOrder()
            throws Exception {
        CountingStorage storage = new CountingStorage(
                temporaryDirectory.resolve("data").resolve("tasks.txt"));

        runWithInput(storage, "todo old todo\n"
                + "deadline old deadline /by 2026-09-20\n"
                + "event old event /from 2pm /to 3pm\n"
                + "mark 2\n"
                + "edit 1 /by 2026-09-21\n"
                + "edit 2 /from 4pm\n"
                + "edit 3 /by 2026-09-21\n"
                + "edit 4 /description missing\n"
                + "edit 2 /by 2026-02-30\n"
                + "edit 1 /description read the new textbook\n"
                + "edit 2 /by 2026-09-21\n"
                + "edit 3 /description consultation /from 4pm /to 6pm\n"
                + "bye\n");

        assertEquals(7, storage.getSaveCount());
        assertEquals(List.of(
                "T | 0 | read the new textbook",
                "D | 1 | old deadline | 2026-09-21",
                "E | 0 | consultation | 4pm | 6pm"),
                Files.readAllLines(storage.getDataFile(), StandardCharsets.UTF_8));

        String restartedOutput = runWithInput(new Storage(storage.getDataFile()),
                "list\nbye\n");
        assertTrue(restartedOutput.contains("Here are the tasks in your list:\n"
                + "1.[T][ ] read the new textbook\n"
                + "2.[D][" + DisplaySymbols.getDoneMark()
                + "] old deadline (by: Sep 21 2026)\n"
                + "3.[E][ ] consultation (from: 4pm to: 6pm)"));
    }

    /**
     * Verifies consecutive finds show fresh results without saving or changing tasks.
     */
    @Test
    public void run_findCommands_displayFreshResultsWithoutSavingOrMutatingTasks()
            throws Exception {
        CountingStorage storage = new CountingStorage(temporaryDirectory.resolve("tasks.txt"));
        saveFindTestTasks(storage);

        String output = runWithInput(storage, "find Book\n"
                + "find dragon\n"
                + "find return book\n"
                + "list\n"
                + "bye\n");

        assertFindResponses(output);
        assertFindLeavesSavedTasksUnchanged(storage);
    }

    /**
     * Saves a completed todo, a deadline, and an unrelated todo for find checks.
     */
    private void saveFindTestTasks(CountingStorage storage) throws StorageException {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        tasks.addTask(todo);
        tasks.addTask(new Deadline("return book", LocalDate.of(2019, 12, 6)));
        tasks.addTask(new Todo("write report"));
        storage.save(tasks);
        storage.resetSaveCount();
    }

    /**
     * Checks that each find uses fresh numbering and the final list stays complete.
     */
    private void assertFindResponses(String output) {
        String completedTodo = "1.[T][" + DisplaySymbols.getDoneMark() + "] read book\n";

        assertTrue(output.contains("Here are the matching tasks in your list:\n"
                + completedTodo
                + "2.[D][ ] return book (by: Dec 6 2019)"));
        assertTrue(output.contains("Toothless couldn’t find any matching tasks in the cave.\n"
                + "Try another keyword and he'll sniff around again!"));
        assertTrue(output.contains("Here are the matching tasks in your list:\n"
                + "1.[D][ ] return book (by: Dec 6 2019)"));
        assertTrue(output.contains("Here are the tasks in your list:\n"
                + completedTodo
                + "2.[D][ ] return book (by: Dec 6 2019)\n"
                + "3.[T][ ] write report"));
    }

    /**
     * Checks that finding tasks did not trigger a save or change saved task data.
     */
    private void assertFindLeavesSavedTasksUnchanged(CountingStorage storage) throws Exception {
        assertEquals(0, storage.getSaveCount());
        assertEquals(List.of(
                "T | 1 | read book",
                "D | 0 | return book | 2019-12-06",
                "T | 0 | write report"),
                Files.readAllLines(storage.getDataFile(), StandardCharsets.UTF_8));
    }

    /**
     * Verifies GUI responses reuse command execution and retain state between messages.
     */
    @Test
    public void getResponse_commandSequence_returnsResponsesAndPersistsState() throws Exception {
        Path dataFile = temporaryDirectory.resolve("gui-tasks.txt");
        Toothless toothless = new Toothless(new Storage(dataFile));

        String addResponse = normalizeLineEndings(toothless.getResponse("todo prepare saddle"));
        String markResponse = normalizeLineEndings(toothless.getResponse("mark 1"));
        String listResponse = normalizeLineEndings(toothless.getResponse("list"));

        assertEquals("", toothless.getStartupMessage());
        assertEquals("Got it! Toothless has added this task for you:\n"
                + "  [T][ ] prepare saddle\n"
                + "Now you have 1 task in the list. "
                + DisplaySymbols.getDecorativeMark(), addResponse);
        assertTrue(markResponse.contains("I've starred this task as done:"));
        assertEquals("Here are the tasks in your list:\n"
                + "1.[T][" + DisplaySymbols.getDoneMark() + "] prepare saddle", listResponse);
        assertEquals(List.of("T | 1 | prepare saddle"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    /**
     * Verifies raw loading warnings remain available without appearing in the chat.
     */
    @Test
    public void getResponse_malformedStorageAndBye_reportsWarningThenExits() throws Exception {
        Path dataFile = temporaryDirectory.resolve("gui-tasks.txt");
        Files.write(dataFile, List.of("T | 0 | valid task", "unknown line"),
                StandardCharsets.UTF_8);
        Toothless toothless = new Toothless(new Storage(dataFile));

        String goodbyeResponse = toothless.getResponse("bye");

        assertEquals("Toothless found 1 puzzling line in his saved quests.\n"
                + "He skipped them and kept every task he could understand.\n"
                + "Saved changes are paused until the file is repaired; your data stays untouched.",
                normalizeLineEndings(toothless.getStartupMessage()));
        assertEquals("", toothless.getChatStartupResponse().text());
        assertFalse(toothless.getChatStartupResponse().isError());
        assertEquals("Bye. Hope to see you again soon!", goodbyeResponse);
        assertTrue(toothless.hasExited());
    }

    @Test
    public void getCommandResult_invalidAndSuccessfulCommands_classifiesResponses() {
        Toothless toothless = new Toothless(new Storage(temporaryDirectory.resolve("gui-tasks.txt")));

        Toothless.Response invalid = toothless.getCommandResult("unknown");
        Toothless.Response successful = toothless.getCommandResult("list");

        assertTrue(invalid.isError());
        assertTrue(invalid.text().contains("doesn’t recognise that command"));
        assertFalse(successful.isError());
        assertEquals("Your task list is empty. Ready for a new adventure!", successful.text());
    }

    @Test
    public void getCommandResult_saveFailure_marksResponseAsError() throws Exception {
        Path dataFile = temporaryDirectory.resolve("gui-tasks.txt");
        Files.writeString(dataFile, "", StandardCharsets.UTF_8);
        Toothless toothless = new Toothless(new FailingSaveStorage(dataFile));

        Toothless.Response response = toothless.getCommandResult("todo keep this task");

        assertTrue(response.isError());
        assertTrue(response.text().contains("couldn’t tuck these changes"));
    }

    @Test
    public void getStartupResponse_loadWarning_marksResponseAsError() throws Exception {
        Path dataFile = temporaryDirectory.resolve("gui-tasks.txt");
        Files.write(dataFile, List.of("T | 0 | valid task", "unknown line"),
                StandardCharsets.UTF_8);

        Toothless toothless = new Toothless(new Storage(dataFile));

        assertTrue(toothless.getStartupResponse().isError());
        assertTrue(toothless.getStartupResponse().text().contains("puzzling line"));
    }

    @Test
    public void getChatStartupResponse_unreadableFile_keepsLoadFailureVisible() throws Exception {
        Path directoryAsFile = Files.createDirectory(temporaryDirectory.resolve("tasks.txt"));
        Toothless toothless = new Toothless(new Storage(directoryAsFile));

        assertTrue(toothless.getChatStartupResponse().isError());
        assertTrue(toothless.getChatStartupResponse().text().contains("trouble reading"));
    }

    @Test
    public void getCommandResult_byeVariants_returnsFarewellBeforeExit() {
        for (String input : List.of("bye", "Bye", "BYE", "  bYe  ")) {
            Toothless toothless = new Toothless(new Storage(
                    temporaryDirectory.resolve("gui-" + input.trim() + ".txt")));

            Toothless.Response response = toothless.getCommandResult(input);

            assertEquals("Bye. Hope to see you again soon!", response.text());
            assertFalse(response.isError());
            assertTrue(toothless.hasExited());
        }
    }

    @Test
    public void getCommandResult_duplicateSubmissionsAndBye_doNotAddOrSaveTwice() throws Exception {
        CountingStorage storage = new CountingStorage(temporaryDirectory.resolve("tasks.txt"));
        Toothless toothless = new Toothless(storage);

        assertFalse(toothless.getCommandResult("todo  read   book ").isError());
        assertTrue(toothless.getCommandResult("todo read book").isError());
        assertFalse(toothless.getCommandResult("bye").isError());
        assertTrue(toothless.getCommandResult("todo another").isError());

        assertEquals(1, storage.getSaveCount());
        assertEquals(List.of("T | 0 | read   book"),
                Files.readAllLines(storage.getDataFile(), StandardCharsets.UTF_8));
    }

    @Test
    public void getCommandResult_failedSaveLeavesEveryMutationUnpublished() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Storage initialStorage = new Storage(dataFile);
        TaskList initialTasks = new TaskList();
        initialTasks.addTask(new Todo("original"));
        initialStorage.save(initialTasks);
        Toothless toothless = new Toothless(new FailingSaveStorage(dataFile));

        for (String command : List.of("todo new", "mark 1", "edit 1 /description changed", "delete 1")) {
            Toothless.Response response = toothless.getCommandResult(command);
            assertTrue(response.isError(), command);
            assertTrue(response.text().contains("Nothing changed"), command);
        }
        assertEquals("Here are the tasks in your list:\n1.[T][ ] original",
                normalizeLineEndings(toothless.getResponse("list")));
        assertEquals(List.of("T | 0 | original"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    public void getCommandResult_malformedSavedData_blocksWritesButAllowsSearch() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        String original = "T | 0 | good\ncorrupted\n";
        Files.writeString(dataFile, original, StandardCharsets.UTF_8);
        Toothless toothless = new Toothless(new Storage(dataFile));

        assertTrue(toothless.getCommandResult("todo new").isError());
        assertEquals("Here are the matching tasks in your list:\n1.[T][ ] good",
                normalizeLineEndings(toothless.getResponse("find good")));
        assertEquals(original, Files.readString(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    public void getCommandResult_unexpectedProcessingFailure_hidesInternalDetailsAndKeepsState() {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt")) {
            @Override
            public void save(TaskList tasks) {
                throw new IllegalStateException("private storage path");
            }
        };
        Toothless toothless = new Toothless(storage);

        Toothless.Response response = toothless.getCommandResult("todo example");

        assertTrue(response.isError());
        assertEquals("Toothless hit a snag with that command. Please try again.", response.text());
        assertEquals("Your task list is empty. Ready for a new adventure!",
                toothless.getResponse("list"));
    }

    /**
     * Runs Toothless with isolated input and output streams.
     */
    private String runWithInput(Storage storage, String input) {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            Toothless.run(storage);
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
        return normalizeLineEndings(output.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void normalizeLineEndings_windowsOutput_returnsUnixLineEndings() {
        assertEquals("first\nsecond", normalizeLineEndings("first\r\nsecond"));
    }

    /**
     * Normalizes platform-specific line endings for portable output assertions.
     *
     * @param text captured application output.
     * @return output using Unix line endings.
     */
    private static String normalizeLineEndings(String text) {
        return text.replace("\r\n", "\n");
    }

    /**
     * Records how often Toothless asks storage to persist a changed list.
     */
    private static class CountingStorage extends Storage {
        private final Path dataFile;
        private int saveCount;

        CountingStorage(Path dataFile) {
            super(dataFile);
            this.dataFile = dataFile;
        }

        @Override
        public void save(TaskList taskList) throws StorageException {
            saveCount++;
            super.save(taskList);
        }

        int getSaveCount() {
            return saveCount;
        }

        void resetSaveCount() {
            saveCount = 0;
        }

        Path getDataFile() {
            return dataFile;
        }
    }

    /**
     * Simulates a predictable write failure after startup loading succeeds.
     */
    private static class FailingSaveStorage extends Storage {
        FailingSaveStorage(Path dataFile) {
            super(dataFile);
        }

        @Override
        public void save(TaskList taskList) throws StorageException {
            throw new StorageException("Expected test failure", new IllegalStateException());
        }
    }
}
