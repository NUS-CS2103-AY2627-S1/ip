package serina;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import serina.exception.SerinaError;
import serina.exception.SerinaException;
import serina.storage.Storage;
import serina.task.Task;
import serina.task.Todo;

/**
 * Tests Serina's shared command-processing behavior.
 */
public class SerinaTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getGreeting_newConversation_returnsConsoleGreeting() {
        Serina serina = createSerina();

        assertEquals(String.join("\n",
                "Standing by, Captain Cutter. Type help for available commands.",
                "Standard orbit achieved, all systems normal."), serina.getGreeting());
    }

    @Test
    public void executeCommand_help_returnsPersonalizedHelpText() {
        Serina serina = createSerina();

        CommandResult result = serina.executeCommand("help");

        assertResponses(result, String.join("\n",
                "Expecting trouble, Captain Cutter? Here are the available commands:",
                "help - show this command list",
                "todo <task> - add a todo",
                "deadline <task> /by <yyyy-MM-dd> - add a deadline",
                "event <task> /from <yyyy-MM-dd> /to <yyyy-MM-dd> - add an event",
                "list - show all tasks",
                "mark <number> - mark a task as done",
                "unmark <number> - mark a task as not done",
                "delete <number> - remove a task",
                "find <keywords> - find tasks containing all keywords",
                "bye - exit Serina"));
        assertFalse(result.shouldExit());
    }

    @Test
    public void executeCommand_findWithMultiplePartialKeywords_returnsTasksContainingEveryKeyword() {
        Serina serina = createSerina();
        serina.executeCommand("todo Submit Project report");
        serina.executeCommand("todo Draft project slides");
        serina.executeCommand("todo Repair printer");

        CommandResult result = serina.executeCommand("find REP PROJ");

        assertResponses(result, String.join("\n",
                "Matching tasks, Captain Cutter:",
                "1.[T][ ] Submit Project report"));
        assertFalse(result.shouldExit());
    }

    @Test
    public void executeCommand_addListAndReload_preservesResponsesAndTaskData() {
        Path saveFile = temporaryDirectory.resolve("serina.txt");
        Serina serina = new Serina(new Storage(saveFile));

        CommandResult addResult = serina.executeCommand("todo read book");
        CommandResult listResult = serina.executeCommand("list");
        Serina reloadedSerina = new Serina(new Storage(saveFile));
        CommandResult reloadedListResult = reloadedSerina.executeCommand("list");

        assertResponses(addResult, String.join("\n",
                "Task logged, Captain Cutter. So...nothing too difficult, then?",
                "  [T][ ] read book",
                "Now you have 1 task on the roster."));
        assertResponses(listResult, String.join("\n",
                "Your task roster, Captain Cutter:",
                "1.[T][ ] read book"));
        assertEquals(listResult.getResponses(), reloadedListResult.getResponses());
    }

    @Test
    public void executeCommand_unknownCommand_returnsTypedErrorAndContinues() {
        Serina serina = createSerina();

        CommandResult result = serina.executeCommand("nonsense");

        assertResponses(result, "Captain Cutter, that order is unclear. Type help for available commands.");
        assertEquals(ResponseType.ERROR, result.getMessages().get(0).getType());
        assertFalse(result.shouldExit());
    }

    @Test
    public void executeCommand_bye_returnsGoodbyeAndExits() {
        Serina serina = createSerina();

        CommandResult result = serina.executeCommand("bye");

        assertResponses(result, "Signing off, Captain Cutter. Do try to keep things orderly.");
        assertEquals(ResponseType.NORMAL, result.getMessages().get(0).getType());
        assertTrue(result.shouldExit());
    }

    @Test
    public void constructor_corruptSaveFile_reportsLineAndBlocksDataAccess() throws IOException {
        Path saveFile = temporaryDirectory.resolve("serina.txt");
        Files.writeString(saveFile, "invalid save data");

        Serina serina = new Serina(new Storage(saveFile));

        assertEquals(List.of("Captain Cutter, I couldn't load your saved tasks. Invalid record on line 1."),
                serina.getStartupMessages());
        assertEquals(ResponseType.WARNING, serina.getStartupResponses().get(0).getType());
        assertFalse(serina.getGreeting().contains("all systems normal"));
        assertResponses(serina.executeCommand("list"),
                "Captain Cutter, saved tasks are unavailable. Repair the data file or its permissions, "
                        + "then restart Serina.");
        assertTrue(serina.executeCommand("help").getResponses().get(0).contains("available commands"));
        assertTrue(serina.executeCommand("bye").shouldExit());
        assertEquals(ResponseType.ERROR, serina.executeCommand("todo blocked").getMessages().get(0).getType());
    }

    @Test
    public void executeCommand_emptyListAndSearch_returnsHelpfulResponses() {
        Serina serina = createSerina();

        assertResponses(serina.executeCommand("list"),
                "No tasks on the roster, Captain Cutter. An unusually peaceful situation.");
        assertResponses(serina.executeCommand("find missing"),
                "No matching tasks, Captain Cutter. Try different keywords.");
    }

    @Test
    public void executeCommand_updateTasks_returnsPersonalizedStatusMessages() {
        Serina serina = createSerina();
        serina.executeCommand("todo first task");
        serina.executeCommand("todo second task");

        assertResponses(serina.executeCommand("mark 1"), String.join("\n",
                "Marked complete, Captain Cutter. A measurable improvement.",
                "  [T][X] first task"));
        assertResponses(serina.executeCommand("unmark 1"), String.join("\n",
                "Back on the roster, Captain Cutter. Optimism was premature.",
                "  [T][ ] first task"));
        assertResponses(serina.executeCommand("delete 1"), String.join("\n",
                "Task removed, Captain Cutter. One less item on the roster.",
                "  [T][ ] first task",
                "Now you have 1 task on the roster."));
    }

    @Test
    public void executeCommand_taskLimitError_keepsApplicationUsable() {
        Serina serina = createSerina();
        for (int taskNumber = 1; taskNumber <= 100; taskNumber++) {
            serina.executeCommand("todo task " + taskNumber);
        }

        CommandResult result = serina.executeCommand("todo one task too many");

        assertResponses(result,
                "Captain Cutter, the 100-task limit has been reached. Delete a task before adding another.");
        assertEquals(ResponseType.ERROR, result.getMessages().get(0).getType());
        assertFalse(result.shouldExit());
        assertFalse(serina.executeCommand("delete 1").shouldExit());
    }

    @Test
    public void executeCommand_flexibleWhitespaceAndInvalidFormats_handlesWithoutCrashing() {
        Serina serina = createSerina();

        assertFalse(serina.executeCommand("  todo   inspect engines  ").shouldExit());
        assertEquals(ResponseType.ERROR, serina.executeCommand("list now").getMessages().get(0).getType());
        assertEquals(ResponseType.ERROR,
                serina.executeCommand("deadline report /by 2026-09-20 /by 2026-09-21")
                        .getMessages().get(0).getType());
        assertEquals(ResponseType.ERROR,
                serina.executeCommand("event briefing /from 2026-09-20 /to 2026-09-20")
                        .getMessages().get(0).getType());
        assertEquals(ResponseType.ERROR, serina.executeCommand("mark 1 2").getMessages().get(0).getType());
    }

    @Test
    public void executeCommand_duplicateTask_rejectsNormalizedDuplicate() {
        Serina serina = createSerina();
        serina.executeCommand("todo Inspect engines");

        CommandResult result = serina.executeCommand("todo   inspect   ENGINES");

        assertEquals(ResponseType.ERROR, result.getMessages().get(0).getType());
        assertTrue(result.getResponses().get(0).contains("task 1"));
    }

    @Test
    public void executeCommand_failedSave_doesNotChangeMemoryOrFile() throws IOException {
        Path saveFile = temporaryDirectory.resolve("serina.txt");
        Files.writeString(saveFile, "T | 0 | original task\n");
        Storage failingStorage = new Storage(saveFile) {
            @Override
            public void saveTasks(List<serina.task.Task> tasks) throws serina.exception.SerinaException {
                throw new serina.exception.SerinaException(serina.exception.SerinaError.SAVE_FAILED);
            }
        };
        Serina serina = new Serina(failingStorage);

        CommandResult result = serina.executeCommand("mark 1");

        assertEquals(ResponseType.ERROR, result.getMessages().get(0).getType());
        assertTrue(serina.executeCommand("list").getResponses().get(0).contains("[T][ ] original task"));
        assertEquals("T | 0 | original task\n", Files.readString(saveFile));
    }

    @Test
    public void commandResult_typedMessages_preservesTextAccessorOrder() {
        CommandResult result = CommandResult.ofMessages(false,
                ResponseMessage.warning("first"), ResponseMessage.normal("second"));

        assertEquals(List.of("first", "second"), result.getResponses());
        assertEquals(List.of(ResponseType.WARNING, ResponseType.NORMAL), result.getMessages().stream()
                .map(ResponseMessage::getType)
                .toList());
    }

    @Test
    public void executeCommand_allTaskTypesAndRestart_preservesFinalState() {
        Path saveFile = temporaryDirectory.resolve("complete-sequence.txt");
        Serina serina = new Serina(new Storage(saveFile));

        serina.executeCommand("todo inspect engines");
        serina.executeCommand("deadline submit report /by 2028-02-29");
        serina.executeCommand("event mission /from 2026-09-14 /to 2026-09-16");
        serina.executeCommand("mark 2");
        serina.executeCommand("delete 1");

        String list = new Serina(new Storage(saveFile)).executeCommand("list").getResponses().get(0);
        assertTrue(list.contains("1.[D][X] submit report (by: Feb 29 2028)"));
        assertTrue(list.contains("2.[E][ ] mission (from: Sep 14 2026 to: Sep 16 2026)"));
        assertFalse(list.contains("inspect engines"));
    }

    @Test
    public void executeCommand_readOnlyAndNoOpCommands_doNotSave() {
        RecordingStorage storage = new RecordingStorage(List.of(new Todo("task")));
        Serina serina = new Serina(storage);

        serina.executeCommand("help");
        serina.executeCommand("list");
        serina.executeCommand("find task");
        serina.executeCommand("unmark 1");
        assertEquals(0, storage.saveCount);

        serina.executeCommand("mark 1");
        assertEquals(1, storage.saveCount);
        serina.executeCommand("mark 1");
        assertEquals(1, storage.saveCount);
    }

    @Test
    public void executeCommand_eachFailedMutation_preservesPublishedTasks() {
        RecordingStorage storage = new RecordingStorage(List.of(new Todo("first"), new Todo("second")));
        storage.shouldFailSave = true;
        Serina serina = new Serina(storage);

        assertEquals(ResponseType.ERROR, serina.executeCommand("todo third").getMessages().get(0).getType());
        assertEquals(ResponseType.ERROR, serina.executeCommand("delete 1").getMessages().get(0).getType());
        assertEquals(ResponseType.ERROR, serina.executeCommand("mark 1").getMessages().get(0).getType());
        String unchangedList = serina.executeCommand("list").getResponses().get(0);
        assertTrue(unchangedList.contains("1.[T][ ] first"));
        assertTrue(unchangedList.contains("2.[T][ ] second"));
        assertFalse(unchangedList.contains("third"));

        storage.shouldFailSave = false;
        serina.executeCommand("mark 1");
        storage.shouldFailSave = true;
        assertEquals(ResponseType.ERROR, serina.executeCommand("unmark 1").getMessages().get(0).getType());
        assertTrue(serina.executeCommand("list").getResponses().get(0).contains("1.[T][X] first"));
    }

    /** Provides deterministic load and save behavior for command-level persistence tests. */
    private static class RecordingStorage extends Storage {
        private final List<Task> loadedTasks;
        private int saveCount;
        private boolean shouldFailSave;

        RecordingStorage(List<Task> loadedTasks) {
            this.loadedTasks = loadedTasks;
        }

        @Override
        public List<Task> loadTasks() {
            return loadedTasks.stream().map(Task::copy).toList();
        }

        @Override
        public void saveTasks(List<Task> tasks) throws SerinaException {
            saveCount++;
            if (shouldFailSave) {
                throw new SerinaException(SerinaError.SAVE_FAILED);
            }
        }
    }

    /**
     * Returns a Serina instance backed by an isolated temporary save file.
     */
    private Serina createSerina() {
        return new Serina(new Storage(temporaryDirectory.resolve("serina.txt")));
    }

    /**
     * Checks that a command produces the supplied responses in order.
     */
    private static void assertResponses(CommandResult result, String... expectedResponses) {
        assertEquals(List.of(expectedResponses), result.getResponses());
    }
}
