package friday.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import friday.exception.FridayException;
import friday.model.TaskList;
import friday.model.ToDo;
import friday.ui.Ui;

/**
 * Tests command state changes, responses, and command metadata.
 */
class CommandTest {
    /**
     * Verifies that adding a task updates the list and reports success.
     */
    @Test
    void addCommand_addsTaskAndReportsSuccess() throws FridayException {
        TaskList tasks = new TaskList();
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        new AddCommand(new ToDo("read book")).execute(tasks, new TaskList(), createUi(output), null, null);

        assertEquals(1, tasks.size());
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("I've added this task"));
        assertTrue(new AddCommand(new ToDo("another")).changesTasks());
    }

    /**
     * Verifies that duplicate active or archived task details are rejected.
     */
    @Test
    void addCommand_rejectsDuplicateActiveAndArchivedTasks() {
        ToDo duplicate = new ToDo("read book");
        TaskList tasks = new TaskList(duplicate);
        TaskList archivedTasks = new TaskList();

        assertThrows(FridayException.class, () -> execute(new AddCommand(new ToDo("read book")), tasks, archivedTasks));

        ToDo archivedDuplicate = new ToDo("return book");
        archivedTasks.add(archivedDuplicate);
        assertThrows(FridayException.class, () -> execute(new AddCommand(archivedDuplicate), tasks, archivedTasks));
    }

    /**
     * Verifies deletion removes the selected one-based task.
     */
    @Test
    void deleteCommand_removesSelectedTask() throws FridayException {
        TaskList tasks = new TaskList(new ToDo("first"), new ToDo("second"));

        new DeleteCommand(2).execute(tasks, new TaskList(), createUi(new ByteArrayOutputStream()), null, null);

        assertEquals(1, tasks.size());
        assertEquals("first", tasks.get(0).getDescription());
        assertTrue(new DeleteCommand(1).changesTasks());
    }

    /**
     * Verifies marking and unmarking update the selected task.
     */
    @Test
    void markAndUnmarkCommands_updateSelectedTaskStatus() throws FridayException {
        TaskList tasks = new TaskList(new ToDo("task"));
        Ui ui = createUi(new ByteArrayOutputStream());

        new MarkCommand(1).execute(tasks, new TaskList(), ui, null, null);
        assertEquals("X", tasks.get(0).getStatusIcon());
        assertTrue(new MarkCommand(1).changesTasks());

        new UnmarkCommand(1).execute(tasks, new TaskList(), ui, null, null);
        assertEquals(" ", tasks.get(0).getStatusIcon());
        assertTrue(new UnmarkCommand(1).changesTasks());
    }

    /**
     * Verifies task-number validation prevents invalid command execution.
     */
    @Test
    void taskCommands_rejectOutOfRangeNumbers() {
        TaskList tasks = new TaskList(new ToDo("task"));

        assertThrows(FridayException.class, () -> execute(new DeleteCommand(2), tasks, new TaskList()));
        assertThrows(FridayException.class, () -> execute(new MarkCommand(0), tasks, new TaskList()));
        assertThrows(FridayException.class, () -> execute(new UnmarkCommand(2), tasks, new TaskList()));
    }

    /**
     * Verifies list and find commands display the expected task information.
     */
    @Test
    void listAndFindCommands_displayTaskInformation() throws FridayException {
        TaskList tasks = new TaskList(new ToDo("read book"), new ToDo("write notes"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        new ListCommand().execute(tasks, new TaskList(), ui, null, null);
        new FindCommand("book").execute(tasks, new TaskList(), ui, null, null);

        String response = output.toString(StandardCharsets.UTF_8);
        assertTrue(response.contains("Here are the tasks in your list:"));
        assertTrue(response.contains("Here are the matching tasks in your list:"));
        assertTrue(response.contains("read book"));
    }

    /**
     * Verifies exit commands report exit state without changing tasks.
     */
    @Test
    void exitCommand_reportsExitWithoutChangingTasks() throws FridayException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ExitCommand command = new ExitCommand();

        command.execute(new TaskList(), new TaskList(), createUi(output), null, null);

        assertTrue(command.isExit());
        assertFalse(command.changesTasks());
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("Standing by."));
    }

    private static Ui createUi(ByteArrayOutputStream output) {
        PrintStream printStream = new PrintStream(output, true, StandardCharsets.UTF_8);
        return new Ui(new Scanner(""), printStream);
    }

    private static void execute(Command command, TaskList tasks, TaskList archivedTasks) throws FridayException {
        command.execute(tasks, archivedTasks, createUi(new ByteArrayOutputStream()), null, null);
    }
}
