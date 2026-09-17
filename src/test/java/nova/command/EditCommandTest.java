package nova.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nova.exception.NovaException;
import nova.storage.Storage;
import nova.task.Deadline;
import nova.task.Event;
import nova.task.TaskList;
import nova.task.Todo;
import nova.ui.Ui;

/**
 * Tests partial task edits and their validation rules.
 */
public class EditCommandTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void execute_descriptionEdit_preservesTypeStatusDetailsAndPosition() throws NovaException {
        Deadline deadline = new Deadline("return book",
                LocalDateTime.of(2019, 12, 2, 18, 0), true);
        deadline.markAsDone();
        TaskList tasks = new TaskList(List.of(new Todo("first"), deadline, new Todo("third")));

        execute(new EditCommand(2, EditField.DESCRIPTION, "return library book"), tasks);

        assertEquals("[T][ ] first", tasks.get(0).toString());
        assertEquals("[D][X] return library book (by: Dec 02 2019, 6:00 PM)",
                tasks.get(1).toString());
        assertEquals("[T][ ] third", tasks.get(2).toString());
        assertTrue(tasks.get(1).isDone());
    }

    @Test
    public void execute_timeOnlyEventEndEdit_preservesExistingEndDate() throws NovaException {
        Event event = new Event("meeting",
                LocalDateTime.of(2019, 12, 3, 14, 0), true,
                LocalDateTime.of(2019, 12, 3, 16, 0), true);
        TaskList tasks = new TaskList(List.of(event));

        execute(new EditCommand(1, EditField.TO, "1700"), tasks);

        assertEquals("[E][ ] meeting (from: Dec 03 2019, 2:00 PM to: Dec 03 2019, 5:00 PM)",
                tasks.get(0).toString());
    }

    @Test
    public void execute_dateOnlyDeadlineEdit_removesTime() throws NovaException {
        Deadline deadline = new Deadline("return book",
                LocalDateTime.of(2019, 12, 2, 18, 0), true);
        TaskList tasks = new TaskList(List.of(deadline));

        execute(new EditCommand(1, EditField.BY, "2019-12-04"), tasks);

        assertEquals("[D][ ] return book (by: Dec 04 2019)", tasks.get(0).toString());
        assertEquals("D | 0 | return book | 2019-12-04", tasks.get(0).toFileString());
    }

    @Test
    public void execute_eventStartAfterEnd_throwsWithoutChangingTask() {
        Event event = new Event("meeting",
                LocalDateTime.of(2019, 12, 3, 14, 0), true,
                LocalDateTime.of(2019, 12, 3, 16, 0), true);
        TaskList tasks = new TaskList(List.of(event));

        NovaException exception = assertThrows(NovaException.class, () ->
                execute(new EditCommand(1, EditField.FROM, "1700"), tasks));

        assertEquals("An event's /to date/time cannot be before its /from date/time.",
                exception.getMessage());
        assertEquals(event, tasks.get(0));
    }

    @Test
    public void execute_eventStartEqualsEnd_succeeds() throws NovaException {
        Event event = new Event("meeting",
                LocalDateTime.of(2019, 12, 3, 14, 0), true,
                LocalDateTime.of(2019, 12, 3, 16, 0), true);
        TaskList tasks = new TaskList(List.of(event));

        execute(new EditCommand(1, EditField.FROM, "1600"), tasks);

        assertEquals("[E][ ] meeting (from: Dec 03 2019, 4:00 PM to: Dec 03 2019, 4:00 PM)",
                tasks.get(0).toString());
    }

    @Test
    public void execute_fieldNotSupportedByTask_throwsActionableException() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        NovaException exception = assertThrows(NovaException.class, () ->
                execute(new EditCommand(1, EditField.TO, "1700"), tasks));

        assertEquals("A todo can only edit /description.", exception.getMessage());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    public void execute_sameDescription_succeedsAndShowsBothVersions() throws NovaException {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        Ui ui = new Ui(new PrintStream(outputBytes, true, StandardCharsets.UTF_8));

        new EditCommand(1, EditField.DESCRIPTION, "read book")
                .execute(tasks, ui, createStorage());

        assertEquals(String.join(System.lineSeparator(),
                " Flight plan updated:",
                "   Before: [T][ ] read book",
                "   After:  [T][ ] read book",
                ""), outputBytes.toString(StandardCharsets.UTF_8));
    }

    private void execute(EditCommand command, TaskList tasks) throws NovaException {
        command.execute(tasks, new Ui(), createStorage());
    }

    private Storage createStorage() {
        return new Storage(temporaryDirectory.resolve("nova.txt"));
    }
}
