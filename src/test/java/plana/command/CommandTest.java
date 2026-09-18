package plana.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import plana.exception.PlanaException;
import plana.storage.Storage;
import plana.task.Deadline;
import plana.task.Event;
import plana.task.Task;
import plana.task.TaskList;
import plana.task.ToDo;
import plana.ui.Ui;

/**
 * Tests command execution through the same collaborators used by the app.
 */
class CommandTest {
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private PrintStream originalOut;
    private java.io.InputStream originalIn;

    @BeforeEach
    void redirectConsole() {
        originalOut = System.out;
        originalIn = System.in;
        System.setOut(new PrintStream(output));
        System.setIn(new ByteArrayInputStream(new byte[0]));
    }

    @AfterEach
    void restoreConsole() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    void addCommand_execute_taskAddedPersistedAndDisplayed(@TempDir Path temporaryDirectory) {
        TaskList tasks = new TaskList();
        Storage storage = storageAt(temporaryDirectory);

        String response = execute(new AddCommand(new ToDo("buy milk")), tasks, storage);

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] buy milk", tasks.get(0).toString());
        assertEquals("[T][ ] buy milk", storage.loadTasks().get(0).toString());
        assertTrue(response.contains("Yay, I've added this task:"));
        assertTrue(response.contains("Now you have 1 task in your list!"));
    }

    @Test
    void deleteCommand_execute_taskRemovedPersistedAndDisplayed(@TempDir Path temporaryDirectory)
            throws PlanaException {
        TaskList tasks = new TaskList(java.util.List.of(new ToDo("remove me"), new ToDo("keep me")));
        Storage storage = storageAt(temporaryDirectory);
        storage.saveTasks(tasks);

        String response = execute(new DeleteCommand("1"), tasks, storage);

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] keep me", tasks.get(0).toString());
        assertEquals("[T][ ] keep me", storage.loadTasks().get(0).toString());
        assertTrue(response.contains("I've removed this task:"));
        assertTrue(response.contains("Now you have 1 task in the list."));
    }

    @Test
    void deleteCommand_saveFailure_taskIsRestored(@TempDir Path temporaryDirectory) throws IOException {
        Path blockedDirectory = temporaryDirectory.resolve("blocked-directory");
        java.nio.file.Files.writeString(blockedDirectory, "This path is deliberately a file.");
        TaskList tasks = new TaskList(java.util.List.of(new ToDo("keep me")));
        Storage storage = new Storage(blockedDirectory.resolve("tasks.txt").toString());

        PlanaException exception = assertThrows(PlanaException.class, () ->
                executeAllowingFailure(new DeleteCommand("1"), tasks, storage));

        assertEquals("Oops, I couldn't save that change."
                + " Please check that the data folder is writable.", exception.getMessage());
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] keep me", tasks.get(0).toString());
    }

    @Test
    void markAndUnmarkCommand_execute_statusPersistedAndDisplayed(@TempDir Path temporaryDirectory)
            throws PlanaException {
        TaskList tasks = new TaskList(java.util.List.of(new ToDo("finish me")));
        Storage storage = storageAt(temporaryDirectory);

        String markResponse = execute(new MarkCommand("1"), tasks, storage);
        assertEquals("[T][X] finish me", tasks.get(0).toString());
        assertEquals("[T][X] finish me", storage.loadTasks().get(0).toString());
        assertTrue(markResponse.contains("Amazing work! You've completed this task:"));

        String unmarkResponse = execute(new UnmarkCommand("1"), tasks, storage);
        assertEquals("[T][ ] finish me", tasks.get(0).toString());
        assertEquals("[T][ ] finish me", storage.loadTasks().get(0).toString());
        String unmarkMessage = "No worries at all! Plans can change, so I've marked this task as not done:";
        assertTrue(unmarkResponse.contains(unmarkMessage));
    }

    @Test
    void markAndUnmarkCommand_saveFailure_originalCompletionStatusIsRestored(@TempDir Path temporaryDirectory)
            throws IOException {
        Path blockedDirectory = temporaryDirectory.resolve("blocked-directory");
        java.nio.file.Files.writeString(blockedDirectory, "This path is deliberately a file.");
        Storage storage = new Storage(blockedDirectory.resolve("tasks.txt").toString());

        Task incompleteTask = new ToDo("unfinished");
        TaskList incompleteTasks = new TaskList(java.util.List.of(incompleteTask));
        assertThrows(PlanaException.class, () ->
                executeAllowingFailure(new MarkCommand("1"), incompleteTasks, storage));
        assertEquals(" ", incompleteTask.getStatusIcon());

        Task completedTask = new ToDo("finished");
        completedTask.markAsDone();
        TaskList completedTasks = new TaskList(java.util.List.of(completedTask));
        assertThrows(PlanaException.class, () ->
                executeAllowingFailure(new UnmarkCommand("1"), completedTasks, storage));
        assertEquals("X", completedTask.getStatusIcon());
    }

    @Test
    void onCommand_execute_matchingDeadlinesAndEventsDisplayed(@TempDir Path temporaryDirectory) {
        LocalDate queriedDate = LocalDate.of(2026, 8, 31);
        TaskList tasks = new TaskList(java.util.List.of(
                new ToDo("not shown"),
                new Deadline("submit report", queriedDate),
                new Event("team retreat", queriedDate.minusDays(1), queriedDate.plusDays(1))));

        String response = execute(new OnCommand(queriedDate), tasks, storageAt(temporaryDirectory));

        assertTrue(response.contains("deadlines and events on Aug 31 2026"));
        assertTrue(response.contains("2.[D][ ] submit report"));
        assertTrue(response.contains("3.[E][ ] team retreat"));
        assertFalse(response.contains("not shown"));
        assertFalse(response.contains("No deadlines or events found"));
    }

    @Test
    void onCommand_noMatches_emptyResultDisplayed(@TempDir Path temporaryDirectory) {
        LocalDate queriedDate = LocalDate.of(2026, 8, 31);
        TaskList tasks = new TaskList(java.util.List.of(new ToDo("not shown")));

        String response = execute(new OnCommand(queriedDate), tasks, storageAt(temporaryDirectory));

        assertTrue(response.contains("No deadlines or events found on Aug 31 2026."));
        assertFalse(response.contains("not shown"));
    }

    @Test
    void findCommand_execute_matchingDescriptionsDisplayedWithOriginalNumbers(@TempDir Path temporaryDirectory) {
        TaskList tasks = new TaskList(java.util.List.of(
                new ToDo("read a book"),
                new Deadline("submit report", LocalDate.of(2026, 8, 31)),
                new ToDo("return BOOK")));

        String response = execute(new FindCommand("book"), tasks, storageAt(temporaryDirectory));

        assertTrue(response.contains("Here are the matching tasks in your list:"));
        assertTrue(response.contains("1.[T][ ] read a book"));
        assertTrue(response.contains("3.[T][ ] return BOOK"));
        assertFalse(response.contains("submit report"));
    }

    @Test
    void findCommand_noMatches_emptyResultDisplayed(@TempDir Path temporaryDirectory) {
        TaskList tasks = new TaskList(java.util.List.of(new ToDo("read a book")));

        String response = execute(new FindCommand("movie"), tasks, storageAt(temporaryDirectory));

        assertTrue(response.contains("No matching tasks found."));
        assertFalse(response.contains("read a book"));
    }

    @Test
    void listCommand_execute_allTasksDisplayedInOrder(@TempDir Path temporaryDirectory) {
        TaskList tasks = new TaskList(java.util.List.of(new ToDo("first"), new ToDo("second")));

        String response = execute(new ListCommand(), tasks, storageAt(temporaryDirectory));

        assertTrue(response.contains("Here are your tasks:"));
        assertTrue(response.indexOf("1.[T][ ] first") < response.indexOf("2.[T][ ] second"));
    }

    @Test
    void helpCommand_execute_helpTextAndSeparatorDisplayed(@TempDir Path temporaryDirectory) {
        String response = execute(new HelpCommand(), new TaskList(), storageAt(temporaryDirectory));

        assertTrue(response.contains("todo <description>"));
        assertTrue(response.contains("deadline <description> /by <date>"));
        assertTrue(response.contains("event <description> /from <start> /to <end>"));
        assertTrue(response.contains("help or ?"));
        assertTrue(response.contains("____________________________________________________________"));
    }

    @Test
    void exitCommand_execute_goodbyeDisplayedAndExitFlagSet(@TempDir Path temporaryDirectory) {
        ExitCommand command = new ExitCommand();

        String response = execute(command, new TaskList(), storageAt(temporaryDirectory));

        assertTrue(command.isExit());
        assertTrue(response.contains("Bye-bye! Keep being awesome, okay?"));
    }

    @Test
    void invalidCommand_execute_storedErrorThrown() {
        InvalidCommand command = new InvalidCommand("bad command");
        Ui ui = new Ui();

        PlanaException exception;
        try {
            exception = assertThrows(PlanaException.class, () ->
                    command.execute(new TaskList(), ui, new Storage("unused-test-file.txt")));
        } finally {
            ui.close();
        }

        assertEquals("bad command", exception.getMessage());
    }

    @Test
    void command_defaultIsExitFlag_isFalse() {
        Command command = new Command() {
            @Override
            public void execute(TaskList tasks, Ui ui, Storage storage) {
                // No behavior is needed for this anonymous command.
            }
        };

        assertFalse(command.isExit());
    }

    private Storage storageAt(Path temporaryDirectory) {
        return new Storage(temporaryDirectory.resolve("tasks.txt").toString());
    }

    private String execute(Command command, TaskList tasks, Storage storage) {
        Ui ui = new Ui();
        try {
            command.execute(tasks, ui, storage);
        } catch (PlanaException exception) {
            throw new AssertionError("Unexpected command failure", exception);
        } finally {
            ui.close();
        }
        return output.toString();
    }

    private void executeAllowingFailure(Command command, TaskList tasks, Storage storage)
            throws PlanaException {
        Ui ui = new Ui();
        try {
            command.execute(tasks, ui, storage);
        } finally {
            ui.close();
        }
    }
}
