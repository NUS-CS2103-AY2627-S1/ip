package ted.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ted.Storage;
import ted.Ui;
import ted.task.TaskList;
import ted.task.Todo;

/**
 * Tests {@link ExitCommand}, the only command that ends the conversation.
 */
public class ExitCommandTest {
    /**
     * Verifies that only the exit command asks Ted to stop.
     */
    @Test
    public void isExit_exitAndOtherCommand_onlyExitStops() {
        assertTrue(new ExitCommand().isExit());
        assertFalse(new ListCommand().isExit());
    }

    /**
     * Verifies that running the exit command leaves the list and the reply
     * alone, since Ted says goodbye itself.
     */
    @Test
    public void execute_anyList_changesNothing(@TempDir Path tempDir) {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        Ui ui = new Ui();

        new ExitCommand().execute(tasks, ui, new Storage(tempDir.resolve("ted.txt").toString()));

        assertEquals(1, tasks.size());
        assertEquals("", ui.flush());
    }
}
