package xiaozhi.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import xiaozhi.storage.Storage;
import xiaozhi.task.TaskList;
import xiaozhi.task.Todo;
import xiaozhi.testutil.OutputCapture;
import xiaozhi.ui.Ui;

/**
 * Tests {@link ListCommand}: that it shows every task, numbered, and never
 * touches storage.
 */
public class ListCommandTest {

    @TempDir
    Path tempDir;

    @Test
    public void execute_multipleTasks_showsThemNumberedFromOne() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("write essay"));
        ListCommand command = new ListCommand();

        String output = OutputCapture.capture(() -> command.execute(tasks, new Ui(),
                new Storage(tempDir.resolve("xiaozhi.txt").toString()), new CommandHistory()));

        assertEquals(
                "The tasks that occupy your mind:" + System.lineSeparator()
                        + "1.[T][ ] read book" + System.lineSeparator()
                        + "2.[T][ ] write essay" + System.lineSeparator(),
                output);
    }

    @Test
    public void execute_emptyList_showsOnlyHeading() throws Exception {
        ListCommand command = new ListCommand();

        String output = OutputCapture.capture(() -> command.execute(new TaskList(), new Ui(),
                new Storage(tempDir.resolve("xiaozhi.txt").toString()), new CommandHistory()));

        assertEquals("The tasks that occupy your mind:" + System.lineSeparator(), output);
    }

    @Test
    public void isUndoable_returnsFalse() {
        assertFalse(new ListCommand().isUndoable());
    }
}
