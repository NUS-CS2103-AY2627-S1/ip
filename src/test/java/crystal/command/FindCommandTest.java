package crystal.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.Test;

import crystal.task.Deadline;
import crystal.task.TaskList;
import crystal.task.Todo;

/**
 * Tests description searching and read-only output through {@link FindCommand}.
 */
public class FindCommandTest extends CommandTestBase {
    @Test
    public void execute_matchingDescriptions_displaysMatchesWithoutSaving() {
        Todo completedTodo = new Todo("READ book");
        completedTodo.markAsDone();
        TaskList tasks = new TaskList(List.of(
                completedTodo,
                new Deadline("return book", "June 6th"),
                new Deadline("submit report", "book collection day")));

        new FindCommand("BoOk").execute(tasks, createUi(), createStorage());

        assertEquals("Crystal: Yay, I found these matching tasks:\n"
                + "         1.[T][X] READ book\n"
                + "         2.[D][ ] return book (by: June 6th)\n", getOutput());
        assertFalse(Files.exists(tempDirectory.resolve("data").resolve("crystal.txt")));
    }

    @Test
    public void execute_noMatchingDescriptions_displaysEmptyMessageWithoutSaving() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        new FindCommand("homework").execute(tasks, createUi(), createStorage());

        assertEquals("Crystal: Aww, I couldn't find any matching tasks!\n",
                getOutput());
        assertFalse(Files.exists(tempDirectory.resolve("data").resolve("crystal.txt")));
    }
}
