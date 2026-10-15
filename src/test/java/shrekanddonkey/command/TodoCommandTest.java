package shrekanddonkey.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import shrekanddonkey.storage.Storage;
import shrekanddonkey.task.TaskList;
import shrekanddonkey.ui.Ui;

/**
 * Tests executing {@link TodoCommand}.
 */
public class TodoCommandTest {

    @Test
    public void execute_validDescription_addsTaskSuccessfully() {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage("./data/test.txt");

        new TodoCommand("read book").execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertTrue(ui.getRecordedOutput().contains("Alright, added to the swamp list:"));
        assertTrue(ui.getRecordedOutput().contains("[T][ ] read book"));
    }

    @Test
    public void execute_emptyDescription_showsErrorMessage() {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage("./data/test.txt");

        new TodoCommand("").execute(tasks, ui, storage);

        assertEquals(0, tasks.size());
        assertTrue(ui.getRecordedOutput().contains("description of a todo cannot be empty"));
    }
}
