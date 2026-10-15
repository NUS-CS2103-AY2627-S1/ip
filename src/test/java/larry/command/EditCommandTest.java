package larry.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import larry.exception.LarryException;
import larry.storage.Storage;
import larry.task.Deadline;
import larry.task.Event;
import larry.task.TaskList;
import larry.task.Todo;
import larry.ui.Ui;

/**
 * Tests single-field changes performed by {@link EditCommand}.
 */
class EditCommandTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void execute_editDescriptions_typesStatusesPositionsAndOtherFieldsPreserved()
            throws LarryException {
        TaskList tasks = createTaskList();
        Todo todo = (Todo) tasks.get(0);
        Deadline deadline = (Deadline) tasks.get(1);
        Event event = (Event) tasks.get(2);
        TaskListFixture fixture = new TaskListFixture(tasks, temporaryDirectory);

        new EditCommand(0, EditField.DESCRIPTION, "updated todo")
                .execute(tasks, fixture.ui(), fixture.storage());
        new EditCommand(1, EditField.DESCRIPTION, "updated deadline")
                .execute(tasks, fixture.ui(), fixture.storage());
        new EditCommand(2, EditField.DESCRIPTION, "updated event")
                .execute(tasks, fixture.ui(), fixture.storage());

        assertSame(todo, tasks.get(0));
        assertSame(deadline, tasks.get(1));
        assertSame(event, tasks.get(2));
        assertEquals("updated todo", todo.getDescription());
        assertEquals("updated deadline", deadline.getDescription());
        assertEquals("updated event", event.getDescription());
        assertTrue(todo.isDone());
        assertFalse(deadline.isDone());
        assertTrue(event.isDone());
        assertEquals(LocalDateTime.of(2026, 9, 10, 12, 0),
                deadline.getDueDateTime().getValue().orElseThrow());
        assertEquals(LocalDateTime.of(2026, 9, 11, 9, 0),
                event.getStartDateTime().getValue().orElseThrow());
        assertEquals(LocalDateTime.of(2026, 9, 11, 10, 0),
                event.getEndDateTime().getValue().orElseThrow());
    }

    @Test
    void execute_editTypedDateFields_onlyRequestedFieldChanged() throws LarryException {
        TaskList tasks = createTaskList();
        Deadline deadline = (Deadline) tasks.get(1);
        Event event = (Event) tasks.get(2);
        TaskListFixture fixture = new TaskListFixture(tasks, temporaryDirectory);

        new EditCommand(1, EditField.DUE_DATE_TIME, "10-9-2026 1400")
                .execute(tasks, fixture.ui(), fixture.storage());
        new EditCommand(2, EditField.START_DATE_TIME, "11-9-2026 0830")
                .execute(tasks, fixture.ui(), fixture.storage());
        new EditCommand(2, EditField.END_DATE_TIME, "11-9-2026 1030")
                .execute(tasks, fixture.ui(), fixture.storage());

        assertEquals("submit report", deadline.getDescription());
        assertEquals(LocalDateTime.of(2026, 9, 10, 14, 0),
                deadline.getDueDateTime().getValue().orElseThrow());
        assertFalse(deadline.isDone());
        assertEquals("team meeting", event.getDescription());
        assertEquals(LocalDateTime.of(2026, 9, 11, 8, 30),
                event.getStartDateTime().getValue().orElseThrow());
        assertEquals(LocalDateTime.of(2026, 9, 11, 10, 30),
                event.getEndDateTime().getValue().orElseThrow());
        assertTrue(event.isDone());
    }

    @Test
    void execute_noOpDateEdit_successfulBeforeAndAfterResponseReturned() throws LarryException {
        TaskList tasks = createTaskList();
        ArrayList<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add, output::add);
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));

        new EditCommand(1, EditField.DUE_DATE_TIME, "10-9-2026 1200")
                .execute(tasks, ui, storage);

        assertEquals(3, output.size());
        assertEquals("     EVIL LARRY updated task 2:", output.get(0));
        assertEquals("     Before: [D][ ] submit report (by: 10 Sep 2026, 12:00 PM)",
                output.get(1));
        assertEquals("     After:  [D][ ] submit report (by: 10 Sep 2026, 12:00 PM)",
                output.get(2));
    }

    @Test
    void execute_incompatibleFields_specificErrorsAndTasksUnchanged() {
        TaskList tasks = createTaskList();
        TaskListFixture fixture = new TaskListFixture(tasks, temporaryDirectory);

        LarryException todoException = assertThrows(LarryException.class, () ->
                new EditCommand(0, EditField.DUE_DATE_TIME, "10-9-2026 1400")
                        .execute(tasks, fixture.ui(), fixture.storage()));
        LarryException deadlineException = assertThrows(LarryException.class, () ->
                new EditCommand(1, EditField.START_DATE_TIME, "10-9-2026 1400")
                        .execute(tasks, fixture.ui(), fixture.storage()));
        LarryException eventException = assertThrows(LarryException.class, () ->
                new EditCommand(2, EditField.DUE_DATE_TIME, "10-9-2026 1400")
                        .execute(tasks, fixture.ui(), fixture.storage()));

        assertEquals("ERROR: You cannot edit /by on a todo task.", todoException.getMessage());
        assertEquals("ERROR: You cannot edit /from on a deadline task.",
                deadlineException.getMessage());
        assertEquals("ERROR: You cannot edit /by on an event task.",
                eventException.getMessage());
        assertEquals("read book", tasks.get(0).getDescription());
        assertEquals("submit report", tasks.get(1).getDescription());
        assertEquals("team meeting", tasks.get(2).getDescription());
    }

    @Test
    void execute_outOfRangeIndex_specificErrorReturned() {
        TaskList tasks = createTaskList();
        TaskListFixture fixture = new TaskListFixture(tasks, temporaryDirectory);

        LarryException exception = assertThrows(LarryException.class, () ->
                new EditCommand(3, EditField.DESCRIPTION, "unreachable")
                        .execute(tasks, fixture.ui(), fixture.storage()));

        assertEquals("ERROR: There is no task at index 4 to edit.", exception.getMessage());
    }

    /**
     * Creates one task of every editable type, including completed tasks.
     */
    private static TaskList createTaskList() {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("submit report",
                LocalDateTime.of(2026, 9, 10, 12, 0));
        Event event = new Event("team meeting",
                LocalDateTime.of(2026, 9, 11, 9, 0),
                LocalDateTime.of(2026, 9, 11, 10, 0));
        todo.markAsDone();
        event.markAsDone();
        tasks.add(todo);
        tasks.add(deadline);
        tasks.add(event);
        return tasks;
    }

    /**
     * Groups the collaborators needed to execute edit commands in tests.
     */
    private record TaskListFixture(Ui ui, Storage storage) {
        private TaskListFixture(TaskList tasks, Path temporaryDirectory) {
            this(new Ui(ignored -> { }, ignored -> { }),
                    new Storage(temporaryDirectory.resolve("tasks-" + tasks.hashCode() + ".txt")));
        }
    }
}
