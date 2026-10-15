package magnus.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import magnus.exception.MagnusException;
import magnus.exception.TaskNotFoundException;
import magnus.task.Task;
import magnus.task.TaskList;
import magnus.task.ToDoTask;

/**
 * Tests commands that mark, unmark, and delete existing tasks.
 */
public class TaskStateCommandsTest {
    @Test
    public void markExecute_validTaskNumber_marksSelectedTask() throws MagnusException {
        Task firstTask = new ToDoTask("read book");
        Task secondTask = new ToDoTask("write notes");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));

        String response = new MarkCommand(tasks).execute("2");

        assertEquals("\tCheckmate for this task - I've marked it as completed:\n\n\t[T][X] write notes\n\n"
                + "\tYou have completed 1 task.", response);
        assertFalse(firstTask.isDone());
        assertTrue(secondTask.isDone());
    }

    @Test
    public void markExecute_secondCompletedTask_reportsPluralCompletedCount() throws MagnusException {
        Task firstTask = new ToDoTask("read book");
        firstTask.markAsDone();
        TaskList tasks = new TaskList(List.of(firstTask, new ToDoTask("write notes")));

        String response = new MarkCommand(tasks).execute("2");

        assertEquals("\tCheckmate for this task - I've marked it as completed:\n\n\t[T][X] write notes\n\n"
                + "\tYou have completed 2 tasks.", response);
    }

    @Test
    public void unmarkExecute_validTaskNumber_unmarksSelectedTask() throws MagnusException {
        Task firstTask = new ToDoTask("read book");
        Task secondTask = new ToDoTask("write notes");
        firstTask.markAsDone();
        secondTask.markAsDone();
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));

        String response = new UnmarkCommand(tasks).execute("1");

        assertEquals("\tThis piece is back in play - I've marked the task as incomplete:\n\n"
                + "\t[T][ ] read book\n\n"
                + "\tYou have completed 1 task.", response);
        assertFalse(firstTask.isDone());
        assertTrue(secondTask.isDone());
    }

    @Test
    public void unmarkExecute_onlyCompletedTask_reportsZeroCompletedTasks() throws MagnusException {
        Task task = new ToDoTask("read book");
        task.markAsDone();
        TaskList tasks = new TaskList(List.of(task));

        String response = new UnmarkCommand(tasks).execute("1");

        assertEquals("\tThis piece is back in play - I've marked the task as incomplete:\n\n"
                + "\t[T][ ] read book\n\n"
                + "\tYou have completed 0 tasks.", response);
    }

    @Test
    public void deleteExecute_validTaskNumber_removesAndReturnsSelectedTask() throws MagnusException {
        Task firstTask = new ToDoTask("read book");
        Task secondTask = new ToDoTask("write notes");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));

        String response = new DeleteCommand(tasks).execute("1");

        assertEquals("\tPiece captured and cleared - I've deleted this task:\n\n\t[T][ ] read book\n\n"
                + "\tYou now have 1 task in the list.", response);
        assertEquals(1, tasks.getTaskCount());
        assertSame(secondTask, tasks.getTask(0));
    }

    @Test
    public void deleteExecute_lastTask_reportsEmptyTaskCount() throws MagnusException {
        TaskList tasks = new TaskList(List.of(new ToDoTask("read book")));

        String response = new DeleteCommand(tasks).execute("1");

        assertEquals("\tPiece captured and cleared - I've deleted this task:\n\n\t[T][ ] read book\n\n"
                + "\tYou now have 0 tasks in the list.", response);
    }

    @Test
    public void mutatingCommands_outOfRangeTaskNumber_leaveTaskListUnchanged() {
        Task task = new ToDoTask("read book");
        TaskList tasks = new TaskList(List.of(task));

        assertThrows(TaskNotFoundException.class, () -> new MarkCommand(tasks).execute("2"));
        assertThrows(TaskNotFoundException.class, () -> new UnmarkCommand(tasks).execute("2"));
        assertThrows(TaskNotFoundException.class, () -> new DeleteCommand(tasks).execute("2"));

        assertEquals(1, tasks.getTaskCount());
        assertSame(task, tasks.getTask(0));
        assertFalse(task.isDone());
    }
}
