package emma.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import emma.EmmaException;
import emma.Storage;
import emma.TaskList;

/**
 * Checks that the three add commands store the task they were built with, report it,
 * and save it, and that they refuse a task the list already holds.
 */
public class AddCommandTest {

    @TempDir
    Path tempDir;

    private Storage storage() {
        return new Storage(tempDir.resolve("emma.json").toString());
    }

    @Test
    public void execute_addTodo_storesAndReportsTheTask() throws EmmaException {
        TaskList tasks = new TaskList(List.of());
        String response = new AddTodoCommand("read book").execute(tasks, storage());
        assertEquals("Got it, I've added this:\n  [T][ ] read book", response);
        assertEquals("1. [T][ ] read book", tasks.format());
    }

    @Test
    public void execute_addDeadline_showsTheDueDateInWords() throws EmmaException {
        TaskList tasks = new TaskList(List.of());
        String response = new AddDeadlineCommand("return book", LocalDate.of(2019, 10, 15))
                .execute(tasks, storage());
        assertEquals("Got it, I've added this:\n  [D][ ] return book (by: Oct 15 2019)", response);
        assertEquals("1. [D][ ] return book (by: Oct 15 2019)", tasks.format());
    }

    @Test
    public void execute_addEvent_showsBothDatesInWords() throws EmmaException {
        TaskList tasks = new TaskList(List.of());
        String response = new AddEventCommand("meeting", LocalDate.of(2019, 10, 15),
                LocalDate.of(2019, 10, 16)).execute(tasks, storage());
        assertEquals("Got it, I've added this:\n"
                + "  [E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)", response);
    }

    @Test
    public void execute_severalAdds_appendToTheEndInOrder() throws EmmaException {
        TaskList tasks = new TaskList(List.of());
        Storage storage = storage();
        new AddTodoCommand("first").execute(tasks, storage);
        new AddDeadlineCommand("second", LocalDate.of(2019, 10, 15)).execute(tasks, storage);
        new AddTodoCommand("third").execute(tasks, storage);
        assertEquals(3, tasks.size());
        assertEquals("1. [T][ ] first\n"
                + "2. [D][ ] second (by: Oct 15 2019)\n"
                + "3. [T][ ] third", tasks.format());
    }

    @Test
    public void execute_addTask_writesItToStorage() throws EmmaException {
        TaskList tasks = new TaskList(List.of());
        Storage storage = storage();
        new AddDeadlineCommand("return book", LocalDate.of(2019, 10, 15)).execute(tasks, storage);

        TaskList reloaded = new TaskList(storage.load());
        assertEquals("1. [D][ ] return book (by: Oct 15 2019)", reloaded.format());
    }

    @Test
    public void execute_duplicateTodo_isRefusedAndNotStored() throws EmmaException {
        TaskList tasks = new TaskList(List.of());
        Storage storage = storage();
        new AddTodoCommand("read book").execute(tasks, storage);

        EmmaException error = assertThrows(EmmaException.class, () ->
                new AddTodoCommand("read book").execute(tasks, storage));
        assertEquals("You're already tracking this:\n  [T][ ] read book", error.getMessage());
        assertEquals(1, tasks.size());
    }

    @Test
    public void execute_duplicateOfADoneTask_isStillRefused() throws EmmaException {
        TaskList tasks = new TaskList(List.of());
        Storage storage = storage();
        new AddTodoCommand("read book").execute(tasks, storage);
        tasks.applyMark(1, true);

        assertThrows(EmmaException.class, () ->
                new AddTodoCommand("read book").execute(tasks, storage));
        assertEquals(1, tasks.size());
    }

    @Test
    public void execute_sameDescriptionDifferentType_isNotADuplicate() throws EmmaException {
        TaskList tasks = new TaskList(List.of());
        Storage storage = storage();
        new AddTodoCommand("return book").execute(tasks, storage);
        new AddDeadlineCommand("return book", LocalDate.of(2019, 10, 15)).execute(tasks, storage);
        assertEquals(2, tasks.size());
    }

    @Test
    public void execute_sameDescriptionDifferentDate_isNotADuplicate() throws EmmaException {
        TaskList tasks = new TaskList(List.of());
        Storage storage = storage();
        new AddDeadlineCommand("return book", LocalDate.of(2019, 10, 15)).execute(tasks, storage);
        new AddDeadlineCommand("return book", LocalDate.of(2019, 10, 16)).execute(tasks, storage);
        assertEquals(2, tasks.size());

        assertThrows(EmmaException.class, () ->
                new AddDeadlineCommand("return book", LocalDate.of(2019, 10, 15))
                        .execute(tasks, storage));
    }

    @Test
    public void execute_eventDifferingOnlyInEndDate_isNotADuplicate() throws EmmaException {
        TaskList tasks = new TaskList(List.of());
        Storage storage = storage();
        new AddEventCommand("meeting", LocalDate.of(2019, 10, 15), LocalDate.of(2019, 10, 16))
                .execute(tasks, storage);
        new AddEventCommand("meeting", LocalDate.of(2019, 10, 15), LocalDate.of(2019, 10, 17))
                .execute(tasks, storage);
        assertEquals(2, tasks.size());

        assertThrows(EmmaException.class, () ->
                new AddEventCommand("meeting", LocalDate.of(2019, 10, 15), LocalDate.of(2019, 10, 16))
                        .execute(tasks, storage));
    }

    @Test
    public void execute_duplicateAfterAReload_isRefused() throws EmmaException {
        Storage storage = storage();
        TaskList tasks = new TaskList(List.of());
        new AddTodoCommand("read book").execute(tasks, storage);

        TaskList reloaded = new TaskList(storage.load());
        assertThrows(EmmaException.class, () ->
                new AddTodoCommand("read book").execute(reloaded, storage));
    }

    @Test
    public void execute_addedTask_startsNotDone() throws EmmaException {
        TaskList tasks = new TaskList(List.of());
        new AddTodoCommand("read book").execute(tasks, storage());
        assertEquals(false, tasks.get(1).isDone());
    }
}
