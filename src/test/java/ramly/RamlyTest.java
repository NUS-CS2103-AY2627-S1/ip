package ramly;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ramly.exception.StorageException;
import ramly.model.Task;
import ramly.model.TaskList;
import ramly.storage.Storage;
import ramly.ui.Ui;

/** Tests the command-processing bridge shared by the text and JavaFX interfaces. */
public class RamlyTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    public void executeCommand_addsTaskAndReturnsResponse() {
        Ramly ramly = new Ramly(temporaryDirectory.resolve("ramly.txt").toString());
        ArrayList<String> responses = new ArrayList<>();

        boolean isExit = ramly.executeCommand("todo buy milk", new Ui(responses::add));

        assertFalse(isExit);
        assertTrue(responses.stream().anyMatch(response -> response.contains("Trail marker set")));
    }

    @Test
    public void executeCommand_byeReturnsExitSignal() {
        Ramly ramly = new Ramly(temporaryDirectory.resolve("ramly.txt").toString());
        ArrayList<String> responses = new ArrayList<>();

        boolean isExit = ramly.executeCommand("bye", new Ui(responses::add));

        assertTrue(isExit);
        assertTrue(responses.stream().anyMatch(response -> response.contains("next checkpoint")));
    }

    @Test
    public void executeCommand_nullInput_throwsAssertionError() {
        Ramly ramly = new Ramly(temporaryDirectory.resolve("ramly.txt").toString());

        assertThrows(AssertionError.class, () -> ramly.executeCommand(null, new Ui(message -> { })));
    }

    @Test
    public void executeCommand_nullOutputUi_throwsAssertionError() {
        Ramly ramly = new Ramly(temporaryDirectory.resolve("ramly.txt").toString());

        assertThrows(AssertionError.class, () -> ramly.executeCommand("list", null));
    }

    @Test
    public void executeCommand_undoAdd_removesTaskAndUpdatesStorage() throws IOException {
        Path storageFile = temporaryDirectory.resolve("ramly.txt");
        Ramly ramly = new Ramly(storageFile.toString());
        ArrayList<String> responses = new ArrayList<>();
        execute(ramly, "todo buy milk", responses);

        execute(ramly, "undo", responses);

        assertEquals(List.of(
                "One step back—I've removed the task you added:",
                " [•][ ] buy milk",
                "Now you have 0 tasks in the list."), responses);
        assertEquals("", Files.readString(storageFile));
    }

    @Test
    public void executeCommand_undoDelete_restoresOriginalPosition() {
        Ramly ramly = new Ramly(temporaryDirectory.resolve("ramly.txt").toString());
        ArrayList<String> responses = new ArrayList<>();
        execute(ramly, "todo first", responses);
        execute(ramly, "todo second", responses);
        execute(ramly, "todo third", responses);
        execute(ramly, "delete 2", responses);

        execute(ramly, "undo", responses);

        assertEquals(List.of(
                "One step back—I've restored the task you removed:",
                " [•][ ] second",
                "Now you have 3 tasks in the list."), responses);

        execute(ramly, "list", responses);
        assertEquals(List.of(
                "Here's your trail ahead:",
                "1.[•][ ] first",
                "2.[•][ ] second",
                "3.[•][ ] third"), responses);
    }

    @Test
    public void executeCommand_undoDeadlineAndEvent_removesAddedTask() {
        Ramly ramly = new Ramly(temporaryDirectory.resolve("ramly.txt").toString());
        ArrayList<String> responses = new ArrayList<>();
        execute(ramly, "deadline submit report /by 2019-10-15", responses);

        execute(ramly, "undo", responses);
        assertEquals(" [⏳][ ] submit report (by: Oct 15 2019)", responses.get(1));

        execute(ramly, "event meeting /from 2019-10-15 1000 /to 2019-10-15 1100", responses);
        execute(ramly, "undo", responses);
        assertEquals(" [◆][ ] meeting (from: 2019-10-15 1000 to: 2019-10-15 1100)", responses.get(1));
    }

    @Test
    public void executeCommand_undoStatusChanges_restoresPreviousStatus() {
        Ramly ramly = new Ramly(temporaryDirectory.resolve("ramly.txt").toString());
        ArrayList<String> responses = new ArrayList<>();
        execute(ramly, "todo buy milk", responses);
        execute(ramly, "mark 1", responses);

        execute(ramly, "undo", responses);

        assertEquals(List.of(
                "One step back—I've restored this task's previous status:",
                " [•][ ] buy milk"), responses);

        execute(ramly, "mark 1", responses);
        execute(ramly, "unmark 1", responses);
        execute(ramly, "undo", responses);
        assertEquals(List.of(
                "One step back—I've restored this task's previous status:",
                " [•][✓] buy milk"), responses);
    }

    @Test
    public void executeCommand_readOnlyAndFailedCommands_preserveUndoHistory() {
        Ramly ramly = new Ramly(temporaryDirectory.resolve("ramly.txt").toString());
        ArrayList<String> responses = new ArrayList<>();
        execute(ramly, "todo buy milk", responses);
        execute(ramly, "list", responses);
        execute(ramly, "find milk", responses);
        execute(ramly, "delete 99", responses);
        execute(ramly, "mark 99", responses);
        execute(ramly, "unmark invalid", responses);
        execute(ramly, "deadline report /by invalid", responses);
        execute(ramly, "event meeting", responses);
        execute(ramly, "hello", responses);

        execute(ramly, "undo", responses);

        assertEquals("One step back—I've removed the task you added:", responses.get(0));
    }

    @Test
    public void executeCommand_newMutationReplacesHistoryAndUndoIsSingleUse() {
        Ramly ramly = new Ramly(temporaryDirectory.resolve("ramly.txt").toString());
        ArrayList<String> responses = new ArrayList<>();
        execute(ramly, "todo first", responses);
        execute(ramly, "todo second", responses);

        execute(ramly, "undo", responses);
        execute(ramly, "list", responses);
        assertEquals(List.of("Here's your trail ahead:", "1.[•][ ] first"), responses);

        execute(ramly, "undo", responses);
        assertEquals(List.of("No steps to retrace yet."), responses);
    }

    @Test
    public void executeCommand_restartedApplication_hasNoUndoHistory() {
        Path storageFile = temporaryDirectory.resolve("ramly.txt");
        Ramly firstSession = new Ramly(storageFile.toString());
        execute(firstSession, "todo buy milk", new ArrayList<>());
        Ramly secondSession = new Ramly(storageFile.toString());
        ArrayList<String> responses = new ArrayList<>();

        execute(secondSession, "undo", responses);

        assertEquals(List.of("No steps to retrace yet."), responses);
        execute(secondSession, "list", responses);
        assertEquals(List.of("Here's your trail ahead:", "1.[•][ ] buy milk"), responses);
    }

    @Test
    public void executeCommand_invalidUndoSyntax_returnsSpecifiedMessages() {
        Ramly ramly = new Ramly(temporaryDirectory.resolve("ramly.txt").toString());
        ArrayList<String> responses = new ArrayList<>();

        execute(ramly, "undo 1", responses);
        assertEquals(List.of("Use this format: undo"), responses);

        execute(ramly, "Undo", responses);
        assertEquals(List.of("I lost that trail. Check the command and try again!"), responses);
    }

    @Test
    public void executeCommand_flexibleWhitespace_normalizesDescription() {
        Ramly ramly = new Ramly(temporaryDirectory.resolve("ramly.txt").toString());
        ArrayList<String> responses = new ArrayList<>();

        execute(ramly, "  todo   buy    milk  ", responses);
        execute(ramly, "list", responses);

        assertEquals(List.of("Here's your trail ahead:", "1.[•][ ] buy milk"), responses);
    }

    @Test
    public void executeCommand_duplicateTask_preservesUndoHistory() {
        Ramly ramly = new Ramly(temporaryDirectory.resolve("ramly.txt").toString());
        ArrayList<String> responses = new ArrayList<>();
        execute(ramly, "todo Buy Milk", responses);

        execute(ramly, "todo buy   milk", responses);
        assertEquals(List.of("That exact task is already on your trail."), responses);
        execute(ramly, "undo", responses);

        assertEquals("One step back—I've removed the task you added:", responses.get(0));
    }

    @Test
    public void executeCommand_saveFailure_rollsBackTaskAndUndoHistory() {
        ToggleSaveStorage storage = new ToggleSaveStorage();
        Ramly ramly = new Ramly(storage);
        ArrayList<String> responses = new ArrayList<>();
        execute(ramly, "todo first", responses);
        storage.failSaves = true;

        execute(ramly, "todo second", responses);

        assertEquals(List.of("Save failed. No changes were applied."), responses);
        storage.failSaves = false;
        execute(ramly, "list", responses);
        assertEquals(List.of("Here's your trail ahead:", "1.[•][ ] first"), responses);
        execute(ramly, "undo", responses);
        assertEquals("One step back—I've removed the task you added:", responses.get(0));
    }

    @Test
    public void executeCommand_statusSaveFailure_restoresCompletionState() {
        ToggleSaveStorage storage = new ToggleSaveStorage();
        Ramly ramly = new Ramly(storage);
        ArrayList<String> responses = new ArrayList<>();
        execute(ramly, "todo first", responses);
        storage.failSaves = true;

        execute(ramly, "mark 1", responses);

        assertEquals(List.of("Save failed. No changes were applied."), responses);
        execute(ramly, "list", responses);
        assertEquals(List.of("Here's your trail ahead:", "1.[•][ ] first"), responses);
    }

    @Test
    public void executeCommand_undoSaveFailure_restoresTaskAndUndoOpportunity() {
        ToggleSaveStorage storage = new ToggleSaveStorage();
        Ramly ramly = new Ramly(storage);
        ArrayList<String> responses = new ArrayList<>();
        execute(ramly, "todo first", responses);
        storage.failSaves = true;

        execute(ramly, "undo", responses);

        assertEquals(List.of("Save failed. No changes were applied."), responses);
        storage.failSaves = false;
        execute(ramly, "undo", responses);
        assertEquals("One step back—I've removed the task you added:", responses.get(0));
    }

    @Test
    public void executeCommand_invalidTaskData_reportsSpecificErrors() {
        Ramly ramly = new Ramly(temporaryDirectory.resolve("ramly.txt").toString());
        ArrayList<String> responses = new ArrayList<>();

        execute(ramly, "todo first | second", responses);
        assertEquals(List.of("The task description cannot contain '|' or control characters."), responses);

        execute(ramly, "deadline report /by 2026-02-30", responses);
        assertEquals(List.of(
                "That date does not exist. Use yyyy-MM-dd, yyyy-MM-dd HHmm, or d/M/yyyy HHmm."), responses);

        execute(ramly, "event meeting /from 2026-10-01 1000 /to 2026-10-01 0900", responses);
        assertEquals(List.of("The event start must be earlier than its end."), responses);
    }

    @Test
    public void constructor_unusableStorage_marksApplicationUnavailable() {
        Ramly ramly = new Ramly(temporaryDirectory.toString());
        ArrayList<String> responses = new ArrayList<>();

        boolean isExit = ramly.executeCommand("list", new Ui(responses::add));

        assertFalse(isExit);
        assertFalse(ramly.isStorageAvailable());
        assertTrue(responses.get(0).startsWith("The task data path is not a regular file:"));
    }

    /** Storage test double that can fail saves after initialization. */
    private static class ToggleSaveStorage extends Storage {
        private boolean failSaves;

        ToggleSaveStorage() {
            super("unused.txt");
        }

        @Override
        public ArrayList<Task> load() {
            return new ArrayList<>();
        }

        @Override
        public void save(TaskList tasks) {
            if (failSaves) {
                throw new StorageException("Save failed. No changes were applied.");
            }
        }
    }

    /** Executes a command after clearing responses from the previous command. */
    private void execute(Ramly ramly, String command, ArrayList<String> responses) {
        responses.clear();
        ramly.executeCommand(command, new Ui(responses::add));
    }
}
