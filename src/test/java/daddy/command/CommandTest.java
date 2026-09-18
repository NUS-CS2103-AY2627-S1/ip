package daddy.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import daddy.exception.DaddyException;
import daddy.storage.Storage;
import daddy.task.Deadline;
import daddy.task.Task;
import daddy.task.TaskList;
import daddy.task.Todo;
import daddy.ui.Ui;

/**
 * Tests successful command execution and task-index validation.
 */
class CommandTest {
    /** Provides isolated persistent storage for commands that change tasks. */
    @TempDir
    Path temporaryDirectory;

    /**
     * Verifies that adding a task persists it and displays the new list size.
     *
     * @throws DaddyException if the valid command cannot save its task
     */
    @Test
    void addCommand_newTask_addsSavesAndDisplaysTask() throws DaddyException {
        TaskList tasks = new TaskList();
        Task task = new Todo("read book");
        List<String> output = new ArrayList<>();

        new AddCommand(task).execute(tasks, new Ui(output::add), createStorage());

        assertEquals(1, tasks.size());
        assertSame(task, tasks.getTaskAt(0));
        assertTrue(output.stream().anyMatch(line -> line.contains("Good. The plan just got stronger")));
        assertTrue(output.stream().anyMatch(line -> line.contains("That's 1 task")));
    }

    /**
     * Verifies that marking and unmarking persist and display the selected task.
     *
     * @throws DaddyException if a valid status update cannot be saved
     */
    @Test
    void statusCommands_existingTask_updateSaveAndDisplayTask() throws DaddyException {
        TaskList tasks = new TaskList();
        Task task = new Todo("read book");
        tasks.add(task);
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);
        Storage storage = createStorage();

        new MarkCommand(0).execute(tasks, ui, storage);
        assertTrue(task.isDone());
        assertTrue(output.stream().anyMatch(line -> line.contains("Task crushed")));

        output.clear();
        new UnmarkCommand(0).execute(tasks, ui, storage);
        assertFalse(task.isDone());
        assertTrue(output.stream().anyMatch(line -> line.contains("back in the programme")));
    }

    /**
     * Verifies that repeated status commands leave the requested state intact.
     *
     * @throws DaddyException if a valid status update cannot be saved
     */
    @Test
    void statusCommands_taskAlreadyInRequestedState_keepsRequestedState() throws DaddyException {
        TaskList tasks = new TaskList();
        Task task = new Todo("read book");
        tasks.add(task);
        Storage storage = createStorage();
        Ui ui = new Ui(ignored -> { });

        new UnmarkCommand(0).execute(tasks, ui, storage);
        assertFalse(task.isDone());

        task.markAsDone();
        new MarkCommand(0).execute(tasks, ui, storage);
        assertTrue(task.isDone());
    }

    /**
     * Verifies that deleting a task persists the shorter list and displays its remaining size.
     *
     * @throws DaddyException if the valid deletion cannot be saved
     */
    @Test
    void deleteCommand_existingTask_removesSavesAndDisplaysTask() throws DaddyException {
        TaskList tasks = new TaskList();
        Task removedTask = new Todo("read book");
        tasks.add(removedTask, new Todo("buy bread"));
        List<String> output = new ArrayList<>();

        new DeleteCommand(0).execute(tasks, new Ui(output::add), createStorage());

        assertEquals(1, tasks.size());
        assertEquals("buy bread", tasks.getTaskAt(0).getDescription());
        assertTrue(output.stream().anyMatch(line -> line.contains("I've removed this task")));
        assertTrue(output.stream().anyMatch(line -> line.contains("1 task remains")));
    }

    /** Verifies that indexed commands distinguish an empty list from an out-of-range number. */
    @Test
    void indexedCommands_missingTask_reportSpecificRangeErrors() {
        TaskList emptyTasks = new TaskList();
        DaddyException emptyError = assertThrows(DaddyException.class, () ->
                new MarkCommand(0).execute(emptyTasks, new Ui(ignored -> { }), null));
        assertTrue(emptyError.getMessage().contains("task list is empty"));

        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        DaddyException rangeError = assertThrows(DaddyException.class, () ->
                new DeleteCommand(1).execute(tasks, new Ui(ignored -> { }), null));
        assertTrue(rangeError.getMessage().contains("Pick a number from 1 to 1"));
    }

    /** Verifies that read-only commands display all, dated, and matching task selections. */
    @Test
    void readOnlyCommands_matchingAndEmptySelections_displayExpectedMessages() {
        LocalDate date = LocalDate.of(2026, 12, 2);
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"), new Deadline("return book", date.atTime(18, 0)));
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);

        new ListCommand().execute(tasks, ui, null);
        assertTrue(output.stream().anyMatch(line -> line.contains("1.[T][ ] read book")));

        output.clear();
        new ListOnDateCommand(date).execute(tasks, ui, null);
        assertTrue(output.stream().anyMatch(line -> line.contains("return book")));

        output.clear();
        new ListOnDateCommand(date.plusDays(1)).execute(tasks, ui, null);
        assertTrue(output.stream().anyMatch(line -> line.contains("That day is clear")));

        output.clear();
        new FindCommand("BOOK").execute(tasks, ui, null);
        assertEquals(3, output.size());

        output.clear();
        new FindCommand("missing").execute(tasks, ui, null);
        assertTrue(output.stream().anyMatch(line -> line.contains("No match")));
    }

    /** Verifies that an exit command displays a farewell and identifies itself as terminal. */
    @Test
    void exitCommand_execute_displaysFarewellAndRequestsExit() {
        List<String> output = new ArrayList<>();
        ExitCommand command = new ExitCommand();

        command.execute(new TaskList(), new Ui(output::add), null);

        assertTrue(command.isExit());
        assertEquals(List.of("Go make Daddy proud, my friend. See you soon."), output);
    }

    /**
     * Creates storage backed by this test's isolated temporary directory.
     *
     * @return storage safe for a command test to modify
     */
    private Storage createStorage() {
        return new Storage(temporaryDirectory.resolve("daddy.txt"), temporaryDirectory.resolve("duke.txt"));
    }
}
