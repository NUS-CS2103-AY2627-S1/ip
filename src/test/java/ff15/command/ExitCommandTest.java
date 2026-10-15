package ff15.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

/** Tests that {@code bye} says goodbye and is the one command that ends the session. */
public class ExitCommandTest {

    @TempDir
    Path tempDir;

    private PrintStream realOut;

    @BeforeEach
    public void setUp() {
        realOut = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(realOut);
    }

    @Test
    public void execute_saysGoodbye() {
        Ui ui = new Ui();
        Storage storage = new Storage(tempDir.resolve("t.txt").toString(),
                tempDir.resolve("c.txt").toString());

        new ExitCommand().execute(new TaskList(), new ContactList(), ui, storage);

        assertEquals("See ya tomorrow, boss.", ui.drainTranscript());
    }

    @Test
    public void isExit_isTrue() {
        assertTrue(new ExitCommand().isExit());
    }
}
