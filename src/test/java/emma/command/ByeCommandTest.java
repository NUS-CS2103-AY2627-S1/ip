package emma.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import emma.EmmaException;
import emma.Storage;
import emma.TaskList;
import emma.Todo;

/**
 * Checks the one command that ends the conversation: it says goodbye, says so, and
 * leaves the tasks exactly as they were.
 */
public class ByeCommandTest {

    @TempDir
    Path tempDir;

    private Path saveFile() {
        return tempDir.resolve("emma.json");
    }

    @Test
    public void execute_always_saysGoodbye() throws EmmaException {
        assertEquals("Bye for now! Hope to see you again soon.",
                new ByeCommand().execute(new TaskList(List.of()), new Storage(saveFile().toString())));
    }

    @Test
    public void isExit_always_isTrue() {
        assertTrue(new ByeCommand().isExit());
    }

    @Test
    public void execute_listWithTasks_leavesThemUnchanged() throws EmmaException {
        TaskList tasks = new TaskList(List.of(new Todo("read book"), new Todo("buy milk")));
        String before = tasks.format();

        new ByeCommand().execute(tasks, new Storage(saveFile().toString()));

        assertEquals(before, tasks.format());
        assertEquals(2, tasks.size());
    }

    @Test
    public void execute_always_writesNothingToDisk() throws EmmaException {
        new ByeCommand().execute(new TaskList(List.of(new Todo("read book"))),
                new Storage(saveFile().toString()));
        assertFalse(Files.exists(saveFile()));
    }
}
