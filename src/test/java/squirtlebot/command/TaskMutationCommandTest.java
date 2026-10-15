package squirtlebot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import squirtlebot.exception.CommandException;
import squirtlebot.storage.Storage;
import squirtlebot.task.TaskList;
import squirtlebot.task.ToDo;
import squirtlebot.ui.Ui;

/**
 * Tests commands that mark, unmark, and delete existing tasks.
 */
public class TaskMutationCommandTest {
    private Storage storage;
    private TaskList tasks;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        storage = new Storage();
        storage.disable();
        tasks = new TaskList();
        tasks.add(new ToDo("first task"));
        tasks.add(new ToDo("second task"));
        ui = new Ui();
    }

    @Test
    public void mark_execute_marksSelectedOneBasedIndex() {
        new MarkCommand(new String[]{"mark", "2"}).execute(tasks, ui, storage);

        assertFalse(tasks.get(0).isDone());
        assertTrue(tasks.get(1).isDone());
        assertEquals("Congrats on completing the following task:\n[T] [X] second task",
                ui.getSavedMessage());
    }

    @Test
    public void unmark_execute_unmarksSelectedTask() {
        tasks.get(0).setDone(true);

        new UnmarkCommand(new String[]{"unmark", "1"}).execute(tasks, ui, storage);

        assertFalse(tasks.get(0).isDone());
        assertEquals("The following task was marked as not done:\n[T] [ ] first task",
                ui.getSavedMessage());
    }

    @Test
    public void delete_execute_removesSelectedOneBasedIndex() {
        new DeleteCommand(new String[]{"delete", "1"}).execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertEquals(new ToDo("second task"), tasks.get(0));
        assertEquals("The following task was removed:\n[T] [ ] first task", ui.getSavedMessage());
    }

    @Test
    public void mark_nonNumericIndex_throwsCommandException() {
        CommandException exception = assertThrows(
                CommandException.class, () -> new MarkCommand(new String[]{"mark", "first"}));

        assertEquals("Please insert a valid index :(", exception.getMessage());
    }

    @Test
    public void mark_missingIndex_throwsCommandException() {
        CommandException exception = assertThrows(
                CommandException.class, () -> new MarkCommand(new String[]{"mark"}));

        assertEquals("Please enter an index to mark :(", exception.getMessage());
    }

    @Test
    public void mark_indexOutsideList_throwsCommandException() {
        MarkCommand command = new MarkCommand(new String[]{"mark", "3"});

        CommandException exception = assertThrows(
                CommandException.class, () -> command.execute(tasks, ui, storage));

        assertEquals("Please enter a valid index :(", exception.getMessage());
        assertFalse(tasks.get(0).isDone());
        assertFalse(tasks.get(1).isDone());
    }

    @Test
    public void unmark_nonNumericIndex_throwsCommandException() {
        CommandException exception = assertThrows(
                CommandException.class, () -> new UnmarkCommand(
                        new String[]{"unmark", "not a number"}));

        assertEquals("Please enter a valid index :(", exception.getMessage());
    }

    @Test
    public void unmark_missingIndex_throwsCommandException() {
        CommandException exception = assertThrows(
                CommandException.class, () -> new UnmarkCommand(new String[]{"unmark"}));

        assertEquals("Please enter an index to unmark :(", exception.getMessage());
    }

    @Test
    public void unmark_zeroIndex_throwsCommandException() {
        tasks.get(0).setDone(true);
        UnmarkCommand command = new UnmarkCommand(new String[]{"unmark", "0"});

        CommandException exception = assertThrows(
                CommandException.class, () -> command.execute(tasks, ui, storage));

        assertEquals("Please enter a valid index :(", exception.getMessage());
        assertTrue(tasks.get(0).isDone());
    }

    @Test
    public void delete_nonNumericIndex_throwsCommandException() {
        CommandException exception = assertThrows(
                CommandException.class, () -> new DeleteCommand(
                        new String[]{"delete", "not a number"}));

        assertEquals("Please enter a valid index :(", exception.getMessage());
    }

    @Test
    public void delete_missingIndex_throwsCommandException() {
        CommandException exception = assertThrows(
                CommandException.class, () -> new DeleteCommand(new String[]{"delete"}));

        assertEquals("Please enter an index to delete :(", exception.getMessage());
    }

    @Test
    public void delete_indexOutsideList_throwsCommandException() {
        DeleteCommand command = new DeleteCommand(new String[]{"delete", "3"});

        CommandException exception = assertThrows(
                CommandException.class, () -> command.execute(tasks, ui, storage));

        assertEquals("Please enter a valid index :(", exception.getMessage());
        assertEquals(2, tasks.size());
    }
}
