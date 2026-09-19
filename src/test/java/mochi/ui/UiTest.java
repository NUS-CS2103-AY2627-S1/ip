package mochi.ui;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import mochi.MochiException;
import mochi.task.Deadline;
import mochi.task.TaskList;
import mochi.task.Todo;

/**
 * Tests construction of console responses without exercising the JavaFX interface.
 */
public class UiTest {
    private final Ui ui = new Ui();

    @Test
    public void taskChangeResponses_taskAndCount_returnsFormattedMessages() {
        Todo todo = new Todo("read book");
        todo.mark();

        assertAll(
                () -> assertEquals("Got it. I've added this task:" + System.lineSeparator()
                                + "  [T][X] read book" + System.lineSeparator()
                                + "Now you have 2 tasks in the list.",
                        ui.getTaskAddedResponse(todo, 2)),
                () -> assertEquals("Noted. I've removed this task:" + System.lineSeparator()
                                + "  [T][X] read book" + System.lineSeparator()
                                + "Now you have 1 tasks in the list.",
                        ui.getTaskDeletedResponse(todo, 1)),
                () -> assertEquals("Nice! I've marked this task as done:"
                                + System.lineSeparator() + "  [T][X] read book",
                        ui.getTaskMarkedResponse(todo)),
                () -> assertEquals("OK, I've marked this task as not done yet:"
                                + System.lineSeparator() + "  [T][X] read book",
                        ui.getTaskUnmarkedResponse(todo))
        );
    }

    @Test
    public void taskListResponses_tasks_returnsNumberedMessages() {
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline(
                "submit report", LocalDateTime.of(2026, 8, 6, 16, 0));

        assertAll(
                () -> assertEquals("Here are the tasks in your list:"
                                + System.lineSeparator() + "1.[T][ ] read book"
                                + System.lineSeparator()
                                + "2.[D][ ] submit report (by: Aug 06 2026, 4:00 PM)",
                        ui.getTaskListResponse(new TaskList(List.of(todo, deadline)))),
                () -> assertEquals("Here are the matching tasks in your list:"
                                + System.lineSeparator() + "1.[T][ ] read book",
                        ui.getMatchingTasksResponse(List.of(todo))),
                () -> assertEquals("Here are the matching tasks in your list:",
                        ui.getMatchingTasksResponse(List.of()))
        );
    }

    @Test
    public void fixedResponses_called_returnsExpectedMessages() {
        assertAll(
                () -> assertEquals("Bye. Hope to see you again soon!", ui.getGoodbyeResponse()),
                () -> assertEquals("OOPS!!! invalid command",
                        ui.getErrorResponse(new MochiException("invalid command"))),
                () -> assertEquals("OOPS!!! I couldn't save the task list to the data file.",
                        ui.getSavingErrorResponse())
        );
    }
}
