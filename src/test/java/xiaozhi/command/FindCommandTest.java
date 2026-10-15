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
 * Tests {@link FindCommand}: that it shows only the matching tasks, still
 * numbered from their position in the search results, and never touches storage.
 */
public class FindCommandTest {

    @TempDir
    Path tempDir;

    @Test
    public void execute_someTasksMatch_showsOnlyThoseNumberedFromOne() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("write essay"));
        tasks.add(new Todo("return book"));
        FindCommand command = new FindCommand("book");

        String output = OutputCapture.capture(() -> command.execute(tasks, new Ui(),
                new Storage(tempDir.resolve("xiaozhi.txt").toString()), new CommandHistory()));

        assertEquals(
                "These are the tasks that echo your search:" + System.lineSeparator()
                        + "1.[T][ ] read book" + System.lineSeparator()
                        + "2.[T][ ] return book" + System.lineSeparator(),
                output);
    }

    @Test
    public void execute_noTasksMatch_showsOnlyHeading() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        FindCommand command = new FindCommand("homework");

        String output = OutputCapture.capture(() -> command.execute(tasks, new Ui(),
                new Storage(tempDir.resolve("xiaozhi.txt").toString()), new CommandHistory()));

        assertEquals("These are the tasks that echo your search:" + System.lineSeparator(), output);
    }

    @Test
    public void isUndoable_returnsFalse() {
        assertFalse(new FindCommand("book").isUndoable());
    }
}
