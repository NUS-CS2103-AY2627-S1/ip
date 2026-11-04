package soar.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import soar.exception.InvalidTaskFormatException;
import soar.exception.InvalidTaskNumberException;
import soar.exception.StorageException;
import soar.storage.Storage;
import soar.task.Deadline;
import soar.task.Event;
import soar.task.Task;
import soar.task.TaskList;
import soar.task.ToDo;
import soar.ui.Ui;

/**
 * Tests persistence, rollback, and task-number validation for mutating commands.
 */
public class CommandTest {
    private Path testDirectory;
    private Ui silentUi;

    /** Creates isolated relative storage and a UI that suppresses command output. */
    @BeforeEach
    public void setUp() throws IOException {
        Path testRoot = Path.of("build", "junit-command");
        Files.createDirectories(testRoot);
        testDirectory = Files.createTempDirectory(testRoot, "case-");
        silentUi = new SilentUi();
    }

    /** Removes generated command-test data. */
    @AfterEach
    public void tearDown() throws IOException {
        if (testDirectory == null || !Files.exists(testDirectory)) {
            return;
        }
        try (var paths = Files.walk(testDirectory)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        }
    }

    /** Verifies successful additions and deletions are persisted in list order. */
    @Test
    public void execute_addAndDeleteCommands_persistsChangedList() throws Exception {
        Storage storage = new Storage(testDirectory.resolve("tasks.txt"));
        TaskList tasks = new TaskList();
        ToDo first = new ToDo("first");
        ToDo second = new ToDo("second");

        new AddCommand(first).execute(tasks, silentUi, storage);
        new AddCommand(second).execute(tasks, silentUi, storage);
        new DeleteCommand(1).execute(tasks, silentUi, storage);

        assertEquals(1, tasks.size());
        assertSame(second, tasks.get(0));
        assertEquals(List.of("[T][ ] second"),
                storage.load().stream().map(Task::toString).toList());
    }

    /** Verifies successful mark and unmark commands persist completion state. */
    @Test
    public void execute_markAndUnmarkCommands_persistsCompletionState() throws Exception {
        Storage storage = new Storage(testDirectory.resolve("tasks.txt"));
        ToDo task = new ToDo("stateful");
        TaskList tasks = new TaskList(List.of(task));

        new MarkCommand(1).execute(tasks, silentUi, storage);
        assertTrue(task.isDone());
        assertTrue(storage.load().get(0).isDone());

        new UnmarkCommand(1).execute(tasks, silentUi, storage);
        assertFalse(task.isDone());
        assertFalse(storage.load().get(0).isDone());
    }

    /** Verifies a multi-field edit preserves state and persists every requested value. */
    @Test
    public void execute_editMultipleEventFields_preservesStateAndPersistsValues() throws Exception {
        Storage storage = new Storage(testDirectory.resolve("tasks.txt"));
        Event original = new Event("meeting", "2pm", "4pm");
        original.markAsDone();
        TaskList tasks = new TaskList(List.of(original));
        LinkedHashMap<EditField, String> edits = new LinkedHashMap<>();
        edits.put(EditField.TO, "2026-09-20 17:00");
        edits.put(EditField.DESCRIPTION, "team workshop");

        new EditCommand(1, edits).execute(tasks, silentUi, storage);

        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0).isDone());
        assertEquals("[E][X] team workshop (from: 2pm to: 2026-09-20 17:00)",
                tasks.get(0).toString());
        assertEquals(tasks.get(0).toDataString(), storage.load().get(0).toDataString());
    }

    /** Verifies unsupported fields and invalid dates leave the original task untouched. */
    @Test
    public void execute_invalidEditValues_throwsWithoutChangingTask() {
        Storage storage = new Storage(testDirectory.resolve("tasks.txt"));
        ToDo todo = new ToDo("unchanged");
        TaskList todoTasks = new TaskList(List.of(todo));

        assertThrows(InvalidTaskFormatException.class, () ->
                new EditCommand(1, Map.of(EditField.TO, "5pm"))
                        .execute(todoTasks, silentUi, storage));
        assertSame(todo, todoTasks.get(0));

        Deadline deadline = new Deadline("original", LocalDate.of(2026, 9, 20));
        TaskList deadlineTasks = new TaskList(List.of(deadline));
        LinkedHashMap<EditField, String> edits = new LinkedHashMap<>();
        edits.put(EditField.DESCRIPTION, "changed");
        edits.put(EditField.BY, "2026-02-29");

        assertThrows(InvalidTaskFormatException.class, () ->
                new EditCommand(1, edits).execute(deadlineTasks, silentUi, storage));
        assertSame(deadline, deadlineTasks.get(0));
    }

    /** Verifies an edit cannot reverse a dated event while leaving it partially changed. */
    @Test
    public void execute_eventEditWithReversedTimes_throwsWithoutChangingTask() {
        Storage storage = new Storage(testDirectory.resolve("tasks.txt"));
        Event event = new Event("workshop", "2026-09-20 14:00", "2026-09-20 16:00");
        TaskList tasks = new TaskList(List.of(event));

        assertThrows(InvalidTaskFormatException.class, () ->
                new EditCommand(1, Map.of(EditField.TO, "2026-09-20 13:00"))
                        .execute(tasks, silentUi, storage));
        assertSame(event, tasks.get(0));
    }

    /** Verifies all mutating commands roll back their in-memory change after save failure. */
    @Test
    public void execute_saveFails_rollsBackEveryMutation() throws Exception {
        Path storageTarget = testDirectory.resolve("unwritable-target");
        Files.createDirectories(storageTarget);
        Storage failingStorage = new Storage(storageTarget);

        TaskList addTasks = new TaskList();
        TaskList markTasks = new TaskList(List.of(new ToDo("incomplete")));
        ToDo completed = new ToDo("complete");
        completed.markAsDone();
        TaskList unmarkTasks = new TaskList(List.of(completed));
        ToDo first = new ToDo("first");
        ToDo second = new ToDo("second");
        TaskList deleteTasks = new TaskList(List.of(first, second));
        ToDo edited = new ToDo("before edit");
        TaskList editTasks = new TaskList(List.of(edited));

        assertSaveFailure(new AddCommand(new ToDo("added")), addTasks, failingStorage);
        assertSaveFailure(new MarkCommand(1), markTasks, failingStorage);
        assertSaveFailure(new UnmarkCommand(1), unmarkTasks, failingStorage);
        assertSaveFailure(new DeleteCommand(1), deleteTasks, failingStorage);
        assertSaveFailure(new EditCommand(1,
                Map.of(EditField.DESCRIPTION, "after edit")), editTasks, failingStorage);

        assertTrue(addTasks.asList().isEmpty());
        assertFalse(markTasks.get(0).isDone());
        assertTrue(unmarkTasks.get(0).isDone());
        assertEquals(List.of(first, second), deleteTasks.asList());
        assertSame(edited, editTasks.get(0));
    }

    /** Verifies numbered commands reject empty-list and out-of-range selections without mutation. */
    @Test
    public void execute_invalidTaskNumbers_throwsWithoutChangingList() {
        Storage storage = new Storage(testDirectory.resolve("tasks.txt"));
        TaskList emptyTasks = new TaskList();
        ToDo task = new ToDo("unchanged");
        TaskList oneTask = new TaskList(List.of(task));

        assertThrows(InvalidTaskNumberException.class, () ->
                new MarkCommand(1).execute(emptyTasks, silentUi, storage));
        assertThrows(InvalidTaskNumberException.class, () ->
                new UnmarkCommand(0).execute(oneTask, silentUi, storage));
        assertThrows(InvalidTaskNumberException.class, () ->
                new DeleteCommand(2).execute(oneTask, silentUi, storage));
        assertEquals(List.of(task), oneTask.asList());
        assertFalse(task.isDone());
    }

    /** Executes a mutation and verifies save failure uses the shared rollback message. */
    private void assertSaveFailure(Command command, TaskList tasks, Storage storage) {
        StorageException exception = assertThrows(StorageException.class, () ->
                command.execute(tasks, silentUi, storage));
        assertTrue(exception.getMessage().contains("change was not kept"));
    }

    /** UI test double that keeps command tests focused on state and persistence. */
    private static class SilentUi extends Ui {
        @Override
        public void showTaskAdded(Task task, int taskCount) {
            // The console UI is covered by the UI test plan.
        }

        @Override
        public void showTaskMarked(Task task, boolean isDone) {
            // The console UI is covered by the UI test plan.
        }

        @Override
        public void showTaskDeleted(Task task, int taskCount) {
            // The console UI is covered by the UI test plan.
        }

        @Override
        public void showTaskEdited(Task originalTask, Task updatedTask) {
            // The console UI is covered by the UI test plan.
        }

        @Override
        public void showTaskUnchanged(Task task) {
            // The console UI is covered by the UI test plan.
        }
    }
}
