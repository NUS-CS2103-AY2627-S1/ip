package ev.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ev.EvException;
import ev.storage.Storage;
import ev.task.TaskList;
import ev.task.Todo;
import ev.ui.Ui;

public class ListCommandTest {

    @TempDir
    Path workFolder;

    @Test
    public void execute_twoTasks_bothListedInOrder() throws EvException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("water plants"));
        Ui ui = new Ui();

        new ListCommand().execute(tasks, ui, new Storage(workFolder.resolve("ev.txt")));

        assertEquals("Your list:\n1.[T][ ] read book\n2.[T][ ] water plants", ui.takeResponse());
    }

    @Test
    public void execute_emptyList_saysSoAndWritesNothing() throws EvException {
        Path file = workFolder.resolve("ev.txt");
        Ui ui = new Ui();

        new ListCommand().execute(new TaskList(), ui, new Storage(file));

        assertEquals("Nothing on your list.", ui.takeResponse());
        assertTrue(Files.notExists(file));
    }

    @Test
    public void isExit_always_false() {
        assertFalse(new ListCommand().isExit());
    }
}
