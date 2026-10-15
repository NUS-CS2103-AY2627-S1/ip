package ff15.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ff15.Storage;
import ff15.Ui;
import ff15.contact.ContactList;
import ff15.task.TaskList;
import ff15.task.Todo;

/** Tests the two things {@code list} can say: the tasks, or that there are none. */
public class ListCommandTest {

    @TempDir
    Path tempDir;

    private PrintStream realOut;
    private Ui ui;
    private Storage storage;

    @BeforeEach
    public void setUp() {
        realOut = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        ui = new Ui();
        storage = new Storage(tempDir.resolve("t.txt").toString(), tempDir.resolve("c.txt").toString());
    }

    @AfterEach
    public void tearDown() {
        System.setOut(realOut);
    }

    @Test
    public void execute_emptyList_remarksOnTheEmptiness() {
        new ListCommand().execute(new TaskList(), new ContactList(), ui, storage);
        assertTrue(ui.drainTranscript().contains("Nothing on the list"));
    }

    @Test
    public void execute_withTasks_listsThemNumbered() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"), new Todo("b"));

        new ListCommand().execute(tasks, new ContactList(), ui, storage);

        String[] lines = ui.drainTranscript().split(System.lineSeparator());
        assertEquals("Here's what we're working with, people:", lines[0]);
        assertEquals("1.[T][ ] a", lines[1]);
        assertEquals("2.[T][ ] b", lines[2]);
    }

    @Test
    public void isExit_isFalse() {
        assertFalse(new ListCommand().isExit());
    }
}
