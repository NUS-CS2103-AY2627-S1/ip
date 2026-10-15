package shrekanddonkey.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import shrekanddonkey.storage.Storage;
import shrekanddonkey.task.TaskList;
import shrekanddonkey.task.Todo;
import shrekanddonkey.ui.Ui;

/**
 * Tests executing {@link MarkCommand}.
 */
public class MarkCommandTest {

    @Test
    public void execute_validIndex_marksTaskAsDone() {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        tasks.add(todo);
        Ui ui = new Ui();
        Storage storage = new Storage("./data/test.txt");

        new MarkCommand("1").execute(tasks, ui, storage);

        assertEquals("X", todo.getStatusIcon());
        assertTrue(ui.getRecordedOutput().contains("Shrek approves! Marked as done:"));
    }

    @Test
    public void execute_outOfBounds_showsErrorMessage() {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage("./data/test.txt");

        new MarkCommand("5").execute(tasks, ui, storage);

        assertTrue(ui.getRecordedOutput().contains("Pick a number from 1 to"));
    }

    @Test
    public void execute_invalidFormat_showsErrorMessage() {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage("./data/test.txt");

        new MarkCommand("abc").execute(tasks, ui, storage);

        assertTrue(ui.getRecordedOutput().contains("That's not a number! Use: mark N"));
    }
}
