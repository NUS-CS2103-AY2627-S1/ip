package tianyi.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import tianyi.Response;
import tianyi.TianyiException;
import tianyi.command.CommandTestFixture.RecordingStorage;
import tianyi.task.TaskList;
import tianyi.task.ToDo;

/**
 * Tests filtering and reporting tasks through {@link FindCommand}.
 */
public class FindCommandTest {
    @Test
    public void execute_matchingTasks_returnsMatchesWithoutSaving()
            throws TianyiException {
        TaskList tasks = new TaskList(List.of(
                new ToDo("read book"),
                new ToDo("buy milk"),
                new ToDo("return book")));
        RecordingStorage storage = new RecordingStorage();
        FindCommand command = new FindCommand("book");

        Response response = command.execute(tasks, storage);

        assertEquals("Here's what I found for you:", response.getHeader());
        assertEquals("1.[T][ ] read book\n"
                + "3.[T][ ] return book", response.getMessage());
        assertNull(storage.savedTasks);
    }

    @Test
    public void execute_noMatchingTasks_returnsNoTasksFoundWithoutSaving()
            throws TianyiException {
        TaskList tasks = new TaskList(List.of(new ToDo("read book")));
        RecordingStorage storage = new RecordingStorage();
        FindCommand command = new FindCommand("milk");

        Response response = command.execute(tasks, storage);

        assertEquals("", response.getHeader());
        assertEquals("I couldn't find a match. Try another keyword?", response.getMessage());
        assertNull(storage.savedTasks);
    }
}
