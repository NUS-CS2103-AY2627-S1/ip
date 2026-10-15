package shrekanddonkey.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import shrekanddonkey.storage.Storage;
import shrekanddonkey.task.TaskList;
import shrekanddonkey.task.Todo;
import shrekanddonkey.ui.Ui;

/**
 * Tests executing {@link DeleteCommand}.
 */
public class DeleteCommandTest {

    @Test
    public void execute_validIndex_removesTaskSuccessfully() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Ui ui = new Ui();
        Storage storage = new Storage("./data/test.txt");

        new DeleteCommand("1").execute(tasks, ui, storage);

        assertEquals(0, tasks.size());
        assertTrue(ui.getRecordedOutput().contains("Poof! Banished from the swamp:"));
    }

    @Test
    public void execute_outOfBoundsIndex_showsErrorMessage() {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage("./data/test.txt");

        new DeleteCommand("1").execute(tasks, ui, storage);

        assertTrue(ui.getRecordedOutput().contains("Pick a number from 1 to"));
    }

    @Test
    public void execute_nonNumericArgument_showsErrorMessage() {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage("./data/test.txt");

        new DeleteCommand("abc").execute(tasks, ui, storage);

        assertTrue(ui.getRecordedOutput().contains("That's not a number! Use: delete N"));
    }
}
