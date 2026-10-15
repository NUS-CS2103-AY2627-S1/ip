package jiji.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jiji.storage.Storage;
import jiji.task.Deadline;
import jiji.task.TaskList;
import jiji.task.Todo;
import jiji.ui.Ui;

/**
 * Unit tests for {@link FindCommand}.
 */
public class FindCommandTest {

    @TempDir
    public Path tempDir;

    private TaskList tasks;
    private Ui ui;
    private Storage storage;

    @BeforeEach
    public void setUp() {
        tasks = new TaskList();
        ui = new Ui();
        storage = new Storage(tempDir.resolve("test.txt").toString());
    }

    @Test
    public void execute_matchingKeyword_returnsMatchingTasks() {
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", "Sunday"));
        tasks.add(new Todo("write code"));

        FindCommand command = new FindCommand("book");
        String result = command.execute(tasks, ui, storage);

        assertTrue(result.contains("Here are the matching tasks in your list:"));
        assertTrue(result.contains("1.[T][ ] read book"));
        assertTrue(result.contains("2.[D][ ] return book"));
        assertTrue(!result.contains("write code"));
    }

    @Test
    public void execute_noMatchingKeyword_returnsEmptyNotice() {
        tasks.add(new Todo("read book"));

        FindCommand command = new FindCommand("swimming");
        String result = command.execute(tasks, ui, storage);

        assertEquals("Here are the matching tasks in your list:", result);
    }
}
