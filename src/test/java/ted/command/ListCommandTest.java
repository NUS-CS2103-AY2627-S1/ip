package ted.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ted.Storage;
import ted.Ui;
import ted.task.TaskList;
import ted.task.Todo;

/**
 * Tests {@link ListCommand}, which shows every stored task.
 */
public class ListCommandTest {
    /**
     * Verifies that the tasks are listed in order, numbered from 1.
     */
    @Test
    public void execute_twoTasks_listedInOrderFromOne(@TempDir Path tempDir) {
        TaskList tasks = new TaskList(List.of(new Todo("borrow book"), new Todo("read book")));
        Ui ui = new Ui();

        new ListCommand().execute(tasks, ui, new Storage(tempDir.resolve("ted.txt").toString()));

        String expected = String.join(System.lineSeparator(),
                "Here's everything you said you'd do:",
                "1.[T][ ] borrow book",
                "2.[T][ ] read book");
        assertEquals(expected, ui.flush());
    }
}
