package shrekanddonkey.command;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import shrekanddonkey.storage.Storage;
import shrekanddonkey.task.TaskList;
import shrekanddonkey.task.Todo;
import shrekanddonkey.ui.Ui;

/**
 * Tests executing {@link ListCommand}.
 */
public class ListCommandTest {

    @Test
    public void execute_taskList_displaysAllTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book"));
        Ui ui = new Ui();
        Storage storage = new Storage("./data/test.txt");

        new ListCommand().execute(tasks, ui, storage);

        String output = ui.getRecordedOutput();
        assertTrue(output.contains("Here's what's lurkin' in the swamp:"));
        assertTrue(output.contains("1.[T][ ] read book"));
        assertTrue(output.contains("2.[T][ ] return book"));
    }
}
