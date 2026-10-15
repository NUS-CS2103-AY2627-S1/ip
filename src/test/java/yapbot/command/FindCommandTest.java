package yapbot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import yapbot.exception.YapBotException;
import yapbot.task.TaskList;
import yapbot.task.Todo;
import yapbot.ui.Ui;

public class FindCommandTest {

    private final ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
    private PrintStream originalOut;

    @BeforeEach
    public void redirectSystemOut() {
        originalOut = System.out;
        System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    public void restoreSystemOut() {
        System.setOut(originalOut);
    }

    private String output() {
        return capturedOutput.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void execute_keywordMatchesSomeTasks_showsOnlyMatchesAndDoesNotModifyList() throws YapBotException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        taskList.add(new Todo("write code"));
        FindCommand command = new FindCommand("find book");

        command.execute(taskList, new Ui());

        assertEquals(2, taskList.size());
        assertTrue(output().contains("Here's what I found for you:"));
        assertTrue(output().contains("1.[T][ ]read book"));
        assertFalse(output().contains("write code"));
    }

    @Test
    public void execute_noMatches_showsHeaderOnly() throws YapBotException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        FindCommand command = new FindCommand("find nomatch");

        command.execute(taskList, new Ui());

        assertTrue(output().contains("Here's what I found for you:"));
        assertFalse(output().contains("1."));
    }

    @Test
    public void execute_noKeywordGiven_exceptionThrown() {
        TaskList taskList = new TaskList();
        FindCommand command = new FindCommand("find");

        assertThrows(YapBotException.class, () -> command.execute(taskList, new Ui()));
    }
}
