package zucc.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import zucc.ZuccException;
import zucc.storage.Storage;
import zucc.task.Deadline;
import zucc.task.Event;
import zucc.task.Task;
import zucc.task.TaskList;
import zucc.task.Todo;
import zucc.ui.Ui;

/**
 * Tests the observable behavior of commands against their state, storage, and UI collaborators.
 */
public class CommandExecutionTest {
    @TempDir
    private Path temporaryDirectory;

    private Path taskFile;
    private Storage storage;
    private TaskList tasks;
    private RecordingUi ui;

    /**
     * Creates an isolated task list and persistent store for each command test.
     *
     * @throws ZuccException if a valid task fixture cannot be created.
     */
    @BeforeEach
    public void setUp() throws ZuccException {
        taskFile = temporaryDirectory.resolve("tasks.txt");
        storage = new Storage(taskFile);
        tasks = new TaskList(List.of(
                new Todo("Read book"),
                new Deadline("Submit report", "10/9/2026 1200"),
                new Event("Conference", "9/9/2026 0900", "11/9/2026 1700")));
        ui = new RecordingUi();
    }

    /**
     * Verifies that list displays every task in its original order.
     *
     * @throws ZuccException if the valid command cannot be parsed or executed.
     */
    @Test
    public void execute_listCommand_allTasksShown() throws ZuccException {
        Command.parse("list").execute(tasks, ui, storage);

        assertEquals("Here are the tasks in your list:\n" + tasks, ui.getLastMessage());
    }

    /**
     * Verifies that find displays only tasks containing the supplied keyword.
     *
     * @throws ZuccException if the valid command cannot be parsed or executed.
     */
    @Test
    public void execute_findCommand_matchingTasksShown() throws ZuccException {
        Command.parse("find book").execute(tasks, ui, storage);

        assertEquals(
                "Here are the matching tasks in your list:\n1.[T][ ] Read book",
                ui.getLastMessage());
    }

    /**
     * Verifies that a date query displays scheduled tasks with their original numbers.
     *
     * @throws ZuccException if the valid command cannot be parsed or executed.
     */
    @Test
    public void execute_onCommandWithMatches_scheduledTasksShown() throws ZuccException {
        Command.parse("on 10/9/2026").execute(tasks, ui, storage);

        assertEquals(
                "Here are the tasks on Sep 10 2026:\n"
                        + "2.[D][ ] Submit report (by: Sep 10 2026, 12:00PM)\n"
                        + "3.[E][ ] Conference "
                        + "(from: Sep 09 2026, 9:00AM to: Sep 11 2026, 5:00PM)",
                ui.getLastMessage());
    }

    /**
     * Verifies that a date query gives a clear response when no task is scheduled.
     *
     * @throws ZuccException if the valid command cannot be parsed or executed.
     */
    @Test
    public void execute_onCommandWithoutMatches_emptyDateMessageShown() throws ZuccException {
        Command.parse("on 12/9/2026").execute(tasks, ui, storage);

        assertEquals(
                "Zucc scanned the timeline and found nothing on Sep 12 2026. "
                        + "Suspiciously peaceful.",
                ui.getLastMessage());
    }

    /**
     * Verifies that marking and unmarking update both the task list and persistent data.
     *
     * @throws ZuccException if a valid command cannot be parsed, executed, or loaded.
     */
    @Test
    public void execute_markThenUnmark_taskAndStorageUpdated() throws ZuccException {
        Command.parse("mark 1").execute(tasks, ui, storage);

        assertEquals(
                "Nice! I've marked this task as done:\n  [T][X] Read book",
                ui.getLastMessage());
        assertEquals("T | 1 | 0 | Read book", loadStoredTasks().getFirst().toStorageString());

        Command.parse("unmark 1").execute(tasks, ui, storage);

        assertEquals(
                "OK, I've marked this task as not done yet:\n  [T][ ] Read book",
                ui.getLastMessage());
        assertEquals("T | 0 | 0 | Read book", loadStoredTasks().getFirst().toStorageString());
    }

    /**
     * Verifies that deleting removes the selected task, renumbers the list, and saves the result.
     *
     * @throws ZuccException if a valid command cannot be parsed, executed, or loaded.
     */
    @Test
    public void execute_deleteCommand_taskRemovedAndStorageUpdated() throws ZuccException {
        Command.parse("delete 2").execute(tasks, ui, storage);

        assertEquals(
                "Noted. I've removed this task:\n  "
                        + "[D][ ] Submit report (by: Sep 10 2026, 12:00PM)\n"
                        + "Now you have 2 tasks in the list.",
                ui.getLastMessage());
        assertEquals(
                List.of("[T][ ] Read book", "[E][ ] Conference "
                        + "(from: Sep 09 2026, 9:00AM to: Sep 11 2026, 5:00PM)"),
                loadStoredTasks().stream().map(Task::toString).toList());
    }

    /** Returns tasks reloaded from the command's isolated persistent store. */
    private List<Task> loadStoredTasks() throws ZuccException {
        return new Storage(taskFile).loadTasks();
    }

    /** Records command responses without depending on a concrete frontend. */
    private static final class RecordingUi extends Ui {
        private final List<String> messages = new ArrayList<>();

        @Override
        public void showMessage(String message) {
            messages.add(message);
        }

        @Override
        public void showError(String message) {
            messages.add(message);
        }

        /** Returns the most recently recorded response. */
        private String getLastMessage() {
            return messages.getLast();
        }
    }
}
