package shrekanddonkey.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import shrekanddonkey.storage.Storage;
import shrekanddonkey.task.TaskList;
import shrekanddonkey.task.Todo;
import shrekanddonkey.ui.Ui;

/**
 * Tests executing {@link UnmarkCommand}.
 */
public class UnmarkCommandTest {

    @Test
    public void execute_validIndex_marksTaskAsNotDone() {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        tasks.add(todo);
        Ui ui = new Ui();
        Storage storage = new Storage("./data/test.txt");

        new UnmarkCommand("1").execute(tasks, ui, storage);

        assertEquals(" ", todo.getStatusIcon());
        assertTrue(ui.getRecordedOutput().contains("Back in the swamp it goes! Unmarked:"));
    }

    @Test
    public void execute_outOfBounds_showsErrorMessage() {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage("./data/test.txt");

        new UnmarkCommand("5").execute(tasks, ui, storage);

        assertTrue(ui.getRecordedOutput().contains("Pick a number from 1 to"));
    }

    @Test
    public void execute_invalidFormat_showsErrorMessage() {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage("./data/test.txt");

        new UnmarkCommand("abc").execute(tasks, ui, storage);

        assertTrue(ui.getRecordedOutput().contains("That's not a number! Use: unmark N"));
    }
}
