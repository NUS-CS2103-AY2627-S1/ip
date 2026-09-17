package yachiyo.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static yachiyo.TestAssertions.assertYachiyoException;
import static yachiyo.exception.ErrorCategory.INVALID_OPERATION;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import yachiyo.exception.YachiyoException;
import yachiyo.storage.Storage;
import yachiyo.task.Deadline;
import yachiyo.task.Event;
import yachiyo.task.Task;
import yachiyo.task.TaskList;
import yachiyo.task.ToDo;
import yachiyo.ui.Ui;

/**
 * Tests editing and validating deadline and event date-times.
 */
public class EditDateTimeCommandTest {
    private static final LocalDateTime START = LocalDateTime.of(2026, 9, 20, 9, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 9, 20, 17, 0);

    @Test
    public void editDeadline_completedDeadline_onlyDueDateTimeChangedAndSaved()
            throws YachiyoException {
        Deadline originalTask = new Deadline("Submit report", END);
        originalTask.markAsDone();
        TaskList tasks = new TaskList(originalTask);
        RecordingStorage storage = new RecordingStorage();
        RecordingUi ui = new RecordingUi();
        LocalDateTime replacementDateTime = END.plusDays(1);

        new EditDeadlineDateTimeCommand(1, replacementDateTime)
                .execute(tasks, ui, storage);

        Deadline editedTask = (Deadline) tasks.get(1);
        assertNotSame(originalTask, editedTask);
        assertEquals("Submit report", editedTask.getDescription());
        assertEquals(replacementDateTime, editedTask.getBy());
        assertTrue(editedTask.isCompleted());
        assertEquals(List.of(editedTask), storage.savedTasks);
        assertSame(editedTask, ui.editedTask);
    }

    @Test
    public void editDeadline_toDoSelected_exceptionThrownWithoutChanges() {
        assertIncompatibleTaskRejected(
                new EditDeadlineDateTimeCommand(1, END),
                new ToDo("Read book")
        );
    }

    @Test
    public void editEventStart_completedEvent_onlyStartChangedAndSaved()
            throws YachiyoException {
        Event originalTask = createCompletedEvent();
        TaskList tasks = new TaskList(originalTask);
        RecordingStorage storage = new RecordingStorage();
        RecordingUi ui = new RecordingUi();
        LocalDateTime replacementStart = START.plusHours(1);

        new EditEventStartDateTimeCommand(1, replacementStart)
                .execute(tasks, ui, storage);

        Event editedTask = (Event) tasks.get(1);
        assertNotSame(originalTask, editedTask);
        assertEquals("Workshop", editedTask.getDescription());
        assertEquals(replacementStart, editedTask.getFrom());
        assertEquals(END, editedTask.getTo());
        assertTrue(editedTask.isCompleted());
        assertEquals(List.of(editedTask), storage.savedTasks);
        assertSame(editedTask, ui.editedTask);
    }

    @Test
    public void editEventStart_startNotBeforeEnd_exceptionThrownWithoutChanges() {
        Event originalTask = new Event("Workshop", START, END);
        TaskList tasks = new TaskList(originalTask);
        RecordingStorage storage = new RecordingStorage();
        RecordingUi ui = new RecordingUi();

        assertYachiyoException(INVALID_OPERATION, () ->
                new EditEventStartDateTimeCommand(1, END)
                        .execute(tasks, ui, storage));
        assertEquals(List.of(originalTask), tasks.getTasks());
        assertEquals(0, storage.saveCallCount);
        assertEquals(0, ui.showEditedCallCount);
    }

    @Test
    public void editEventStart_deadlineSelected_exceptionThrownWithoutChanges() {
        assertIncompatibleTaskRejected(
                new EditEventStartDateTimeCommand(1, START),
                new Deadline("Submit report", END)
        );
    }

    @Test
    public void editEventEnd_completedEvent_onlyEndChangedAndSaved()
            throws YachiyoException {
        Event originalTask = createCompletedEvent();
        TaskList tasks = new TaskList(originalTask);
        RecordingStorage storage = new RecordingStorage();
        RecordingUi ui = new RecordingUi();
        LocalDateTime replacementEnd = END.plusHours(1);

        new EditEventEndDateTimeCommand(1, replacementEnd)
                .execute(tasks, ui, storage);

        Event editedTask = (Event) tasks.get(1);
        assertNotSame(originalTask, editedTask);
        assertEquals("Workshop", editedTask.getDescription());
        assertEquals(START, editedTask.getFrom());
        assertEquals(replacementEnd, editedTask.getTo());
        assertTrue(editedTask.isCompleted());
        assertEquals(List.of(editedTask), storage.savedTasks);
        assertSame(editedTask, ui.editedTask);
    }

    @Test
    public void editEventEnd_endNotAfterStart_exceptionThrownWithoutChanges() {
        Event originalTask = new Event("Workshop", START, END);
        TaskList tasks = new TaskList(originalTask);
        RecordingStorage storage = new RecordingStorage();
        RecordingUi ui = new RecordingUi();

        assertYachiyoException(INVALID_OPERATION, () ->
                new EditEventEndDateTimeCommand(1, START)
                        .execute(tasks, ui, storage));
        assertEquals(List.of(originalTask), tasks.getTasks());
        assertEquals(0, storage.saveCallCount);
        assertEquals(0, ui.showEditedCallCount);
    }

    @Test
    public void editEventEnd_deadlineSelected_exceptionThrownWithoutChanges() {
        assertIncompatibleTaskRejected(
                new EditEventEndDateTimeCommand(1, END),
                new Deadline("Submit report", END)
        );
    }

    private static Event createCompletedEvent() {
        Event event = new Event("Workshop", START, END);
        event.markAsDone();
        return event;
    }

    private static void assertIncompatibleTaskRejected(EditCommand command, Task originalTask) {
        TaskList tasks = new TaskList(originalTask);
        RecordingStorage storage = new RecordingStorage();
        RecordingUi ui = new RecordingUi();

        assertYachiyoException(
                INVALID_OPERATION, () -> command.execute(tasks, ui, storage));
        assertEquals(List.of(originalTask), tasks.getTasks());
        assertEquals(0, storage.saveCallCount);
        assertEquals(0, ui.showEditedCallCount);
    }

    /**
     * Storage test double that records saved tasks.
     */
    private static final class RecordingStorage extends Storage {
        private List<Task> savedTasks;
        private int saveCallCount;

        private RecordingStorage() {
            super(Path.of("unused"));
        }

        @Override
        public void saveTasks(List<Task> tasks) {
            saveCallCount++;
            savedTasks = List.copyOf(tasks);
        }
    }

    /**
     * UI test double that records task-edited confirmations.
     */
    private static final class RecordingUi extends Ui {
        private Task editedTask;
        private int showEditedCallCount;

        @Override
        public void showTaskEdited(Task task) {
            editedTask = task;
            showEditedCallCount++;
        }
    }
}
