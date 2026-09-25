package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import exception.FriedbergException;
import task.Deadline;
import task.Event;
import task.ToDo;

/**
 * Checks that shared addition responses retain their exact wording and line breaks.
 */
class TaskResponseFormatterTest {
    @Test
    void formatAddition_todo_preservesExactResponse() {
        assertEquals("Got it. I've added this task:\n[T][ ] read book\nNow you have 1 tasks in the list.",
                TaskResponseFormatter.formatAddition(new ToDo("read book"), 1));
    }

    @Test
    void formatAddition_deadline_preservesExactResponse() throws FriedbergException {
        assertEquals("Got it. I've added this task:\n[D][ ] work (by: 09/09/2026 (Wed))\n"
                + "Now you have 2 tasks in the list.",
                TaskResponseFormatter.formatAddition(new Deadline("work", "2026-09-09"), 2));
    }

    @Test
    void formatAddition_event_preservesExactResponse() throws FriedbergException {
        assertEquals("Got it. I've added this task:\n"
                + "[E][ ] meeting (from: 09/09/2026 (Wed) to: 10/09/2026 (Thu))\n"
                + "Now you have 3 tasks in the list.",
                TaskResponseFormatter.formatAddition(new Event("meeting", "2026-09-09", "2026-09-10"), 3));
    }

    @Test
    void formatAddition_successiveCalls_doNotAccumulateResponsesOrChangeTask() {
        ToDo task = new ToDo("read book");
        String originalTask = task.serialize();
        TaskResponseFormatter.formatAddition(new ToDo("other task"), 5);

        assertEquals("Got it. I've added this task:\n[T][ ] read book\nNow you have 1 tasks in the list.",
                TaskResponseFormatter.formatAddition(task, 1));
        assertEquals(originalTask, task.serialize());
    }
}
