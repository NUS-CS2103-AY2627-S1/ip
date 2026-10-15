package magnus.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import magnus.exception.CommandSyntaxException;
import magnus.exception.DuplicateTaskException;
import magnus.exception.MagnusException;
import magnus.task.DeadlineTask;
import magnus.task.EventTask;
import magnus.task.Task;
import magnus.task.TaskList;
import magnus.task.ToDoTask;

/**
 * Tests the two-step task update interaction and its type-specific validation.
 */
public class UpdateCommandTest {
    @Test
    public void execute_todoTask_returnsTodoUpdateFormat() throws MagnusException {
        UpdateCommand command = new UpdateCommand(
                new TaskList(List.of(new ToDoTask("read book"))));

        String response = command.execute("1");

        assertEquals("\tYour move - enter the updated To-Do task in this format:\n"
                + "\t<new task name>", response);
        assertTrue(command.isAwaitingUpdatedTask());
    }

    @Test
    public void execute_deadlineTask_returnsDeadlineUpdateFormat() throws MagnusException {
        DeadlineTask task = new DeadlineTask(
                "submit report", LocalDateTime.of(2026, 9, 2, 15, 0));
        UpdateCommand command = new UpdateCommand(new TaskList(List.of(task)));

        String response = command.execute("1");

        assertEquals("\tYour move - enter the updated Deadline task in this format:\n"
                + "\t<new task name> /by <deadline time>\n"
                + "\tDeadline time format: dd/MM/yyyy HHmm", response);
    }

    @Test
    public void execute_eventTask_returnsEventUpdateFormat() throws MagnusException {
        EventTask task = new EventTask(
                "meeting", LocalDateTime.of(2026, 9, 2, 15, 0),
                LocalDateTime.of(2026, 9, 2, 16, 0));
        UpdateCommand command = new UpdateCommand(new TaskList(List.of(task)));

        String response = command.execute("1");

        assertEquals("\tYour move - enter the updated Event task in this format:\n"
                + "\t<new task name> /from <start time> /to <end time>\n"
                + "\tStart and end time format: dd/MM/yyyy HHmm", response);
    }

    @Test
    public void completeUpdate_validTodoInput_replacesTaskAndPreservesCompletion() throws MagnusException {
        ToDoTask originalTask = new ToDoTask("read book");
        originalTask.markAsDone();
        TaskList tasks = new TaskList(List.of(originalTask));
        UpdateCommand command = new UpdateCommand(tasks);
        command.execute("1");

        String response = command.completeUpdate("read two books");

        assertEquals("\tPosition updated - I've updated this task:\n\n\t[T][X] read two books", response);
        assertEquals("[T][X] read two books", tasks.getTask(0).toString());
        assertFalse(command.isAwaitingUpdatedTask());
    }

    @Test
    public void completeUpdate_validDeadlineInput_replacesTask() throws MagnusException {
        TaskList tasks = new TaskList(List.of(new DeadlineTask(
                "submit report", LocalDateTime.of(2026, 9, 2, 15, 0))));
        UpdateCommand command = new UpdateCommand(tasks);
        command.execute("1");

        String response = command.completeUpdate("submit final report /by 03/09/2026 1630");

        assertEquals("\tPosition updated - I've updated this task:\n\n\t[D][ ] submit final report "
                + "(by: Sep 03, 2026 16:30)", response);
    }

    @Test
    public void completeUpdate_validEventInput_replacesTask() throws MagnusException {
        TaskList tasks = new TaskList(List.of(new EventTask(
                "meeting", LocalDateTime.of(2026, 9, 2, 15, 0),
                LocalDateTime.of(2026, 9, 2, 16, 0))));
        UpdateCommand command = new UpdateCommand(tasks);
        command.execute("1");

        String response = command.completeUpdate(
                "team meeting /from 04/09/2026 0900 /to 04/09/2026 1030");

        assertEquals("\tPosition updated - I've updated this task:\n\n\t[E][ ] team meeting "
                + "(from: Sep 04, 2026 09:00 to: Sep 04, 2026 10:30)", response);
    }

    @Test
    public void completeUpdate_wrongFormat_failsWithoutReplacingTaskAndClearsPendingUpdate()
            throws MagnusException {
        Task originalTask = new ToDoTask("read book");
        TaskList tasks = new TaskList(List.of(originalTask));
        UpdateCommand command = new UpdateCommand(tasks);
        command.execute("1");

        CommandSyntaxException exception = assertThrows(
                CommandSyntaxException.class, () -> command.completeUpdate("read /by tomorrow"));

        assertEquals("\tThat move does not match the required format - the task was not updated.",
                exception.getMessage());
        assertSame(originalTask, tasks.getTask(0));
        assertFalse(command.isAwaitingUpdatedTask());
    }

    @Test
    public void completeUpdate_invalidDeadlineTime_failsWithoutReplacingTask() throws MagnusException {
        Task originalTask = new DeadlineTask(
                "submit report", LocalDateTime.of(2026, 9, 2, 15, 0));
        TaskList tasks = new TaskList(List.of(originalTask));
        UpdateCommand command = new UpdateCommand(tasks);
        command.execute("1");
        String invalidUpdate = "submit final report /by 31/09/2026 2500";

        CommandSyntaxException exception = assertThrows(CommandSyntaxException.class, () ->
                command.completeUpdate(invalidUpdate));

        assertEquals("\tThat move does not match the required format - the task was not updated.",
                exception.getMessage());
        assertSame(originalTask, tasks.getTask(0));
    }

    @Test
    public void completeUpdate_eventFieldsInWrongOrder_failsWithoutReplacingTask()
            throws MagnusException {
        Task originalTask = new EventTask(
                "meeting", LocalDateTime.of(2026, 9, 2, 15, 0),
                LocalDateTime.of(2026, 9, 2, 16, 0));
        TaskList tasks = new TaskList(List.of(originalTask));
        UpdateCommand command = new UpdateCommand(tasks);
        command.execute("1");

        assertThrows(CommandSyntaxException.class, () -> command.completeUpdate(
                "team meeting /to 04/09/2026 1030 /from 04/09/2026 0900"));
        assertSame(originalTask, tasks.getTask(0));
    }

    @Test
    public void completeUpdate_sameDetailsAsSelectedTask_allowsUpdate() throws MagnusException {
        Task originalTask = new ToDoTask("read book");
        TaskList tasks = new TaskList(List.of(originalTask));
        UpdateCommand command = new UpdateCommand(tasks);
        command.execute("1");

        command.completeUpdate("read book");

        assertEquals("[T][ ] read book", tasks.getTask(0).toString());
        assertFalse(command.isAwaitingUpdatedTask());
    }

    @Test
    public void completeUpdate_duplicateOfOtherTask_rejectsUpdateAndClearsPendingState()
            throws MagnusException {
        Task originalTask = new ToDoTask("read book");
        Task otherTask = new ToDoTask("write notes");
        TaskList tasks = new TaskList(List.of(originalTask, otherTask));
        UpdateCommand command = new UpdateCommand(tasks);
        command.execute("1");

        assertThrows(DuplicateTaskException.class, () -> command.completeUpdate("write notes"));

        assertSame(originalTask, tasks.getTask(0));
        assertFalse(command.isAwaitingUpdatedTask());
    }

    @Test
    public void completeUpdate_blankNullOrMultilineInput_rejectsEachInput() throws MagnusException {
        assertInvalidTodoUpdate("   ");
        assertInvalidTodoUpdate(null);
        assertInvalidTodoUpdate("read book\nwrite notes");
        assertInvalidTodoUpdate("read book\rwrite notes");
    }

    /**
     * Verifies that invalid to-do replacement text leaves the original task untouched.
     *
     * @param updateInput Invalid replacement text to submit.
     * @throws MagnusException If selecting the fixture task unexpectedly fails.
     */
    private void assertInvalidTodoUpdate(String updateInput) throws MagnusException {
        Task originalTask = new ToDoTask("read book");
        TaskList tasks = new TaskList(List.of(originalTask));
        UpdateCommand command = new UpdateCommand(tasks);
        command.execute("1");

        assertThrows(CommandSyntaxException.class, () -> command.completeUpdate(updateInput));
        assertSame(originalTask, tasks.getTask(0));
        assertFalse(command.isAwaitingUpdatedTask());
    }
}
