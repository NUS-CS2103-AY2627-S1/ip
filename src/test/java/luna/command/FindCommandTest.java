package luna.command;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import luna.LunaException;
import luna.storage.Storage;
import luna.task.TaskList;
import luna.task.Todo;
import luna.ui.Ui;

/**
 * Tests for {@link FindCommand}.
 */
public class FindCommandTest {
    private PrintStream originalOut;
    private java.io.InputStream originalIn;

    @BeforeEach
    public void setUpStreams() {
        originalOut = System.out;
        originalIn = System.in;
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    public void execute_missingKeyword_throwsLunaException() {
        FindCommand command = new FindCommand("find   ");

        assertThrows(LunaException.class, () -> command.execute(new TaskList(), createUi(), new Storage()));
    }

    @Test
    public void execute_keywordMatchesTasks_showsMatchingTasks() throws LunaException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        TaskList tasks = new TaskList(
                new Todo("read book"),
                new Todo("submit quiz"),
                new Todo("return book"));
        FindCommand command = new FindCommand("find book");

        command.execute(tasks, createUi(), new Storage());

        String output = outputStream.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
        assertTrue(output.contains("These tasks came into view:"));
        assertTrue(output.contains("1. [T][ ] read book"));
        assertTrue(output.contains("3. [T][ ] return book"));
    }

    private Ui createUi() {
        System.setIn(new ByteArrayInputStream(new byte[0]));
        return new Ui();
    }
}
