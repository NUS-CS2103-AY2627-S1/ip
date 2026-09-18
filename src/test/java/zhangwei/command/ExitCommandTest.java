package zhangwei.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import zhangwei.storage.Storage;
import zhangwei.task.TaskList;
import zhangwei.task.Todo;
import zhangwei.ui.Ui;

/**
 * Tests {@link ExitCommand}. It is the only command whose {@code isExit} is
 * true, and that single value is what stops the chatbot's loop -- so it is
 * worth a test even though the class is tiny.
 */
public class ExitCommandTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream captured;

    @BeforeEach
    public void redirectOutput() {
        captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
    }

    @AfterEach
    public void restoreOutput() {
        System.setOut(originalOut);
    }

    @Test
    public void isExit_exitCommand_trueReturned() {
        assertTrue(new ExitCommand().isExit());
    }

    @Test
    public void isExit_otherCommands_falseReturned() {
        // Only the exit command may end the session.
        assertFalse(new ListCommand().isExit());
        assertFalse(new AddCommand(new Todo("read book")).isExit());
    }

    @Test
    public void execute_anyList_goodbyeShown() {
        new ExitCommand().execute(new TaskList(), new Ui(), new Storage("unused.txt"));
        assertTrue(captured.toString().contains("Bye."));
    }

    @Test
    public void execute_listWithTasks_listLeftUnchanged() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        new ExitCommand().execute(tasks, new Ui(), new Storage("unused.txt"));
        assertTrue(tasks.size() == 1);
    }
}
