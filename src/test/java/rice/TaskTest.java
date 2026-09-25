package rice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests the base Task model and its concrete subclasses. */
public class TaskTest {

    @Test
    void defaultsToIncompleteAndUsesTodoFormat() {
        Todo todo = new Todo("Read book");

        assertEquals("Read book", todo.getTask());
        assertFalse(todo.isDone());
        assertEquals("[ ]", todo.completionMarker());
        assertEquals(LocalDate.MAX, todo.getDeadlineDate());
        assertEquals("[T][ ] Read book", todo.toString());
    }

    @Test
    void markAndUnmarkUpdateCompletionState() {
        Todo todo = new Todo("Read book");

        todo.mark();
        assertTrue(todo.isDone());
        assertEquals("[T][X] Read book", todo.toString());

        todo.unmark();
        assertFalse(todo.isDone());
        assertEquals("[T][ ] Read book", todo.toString());
    }

    @Test
    void parsesDeadlineAndFormatsOutput() {
        Deadline deadline = new Deadline("Submit report", "2026-12-12");

        assertEquals("2026-12-12", deadline.getDeadline());
        assertEquals(LocalDate.of(2026, 12, 12), deadline.getDeadlineDate());
        assertEquals("[D][ ] Submit report(by: Dec 12 2026)", deadline.toString());

        deadline.mark();
        assertEquals("[D][X] Submit report(by: Dec 12 2026)", deadline.toString());
    }

    @Test
    void tracksStartAndEndDatesAndFormatsOutput() {
        Event event = new Event("Team meeting", "2026-12-13", "2026-12-14");

        assertEquals("2026-12-13", event.getStart());
        assertEquals("2026-12-14", event.getEnd());
        assertEquals(LocalDate.of(2026, 12, 14), event.getDeadlineDate());
        assertEquals("[E][ ] Team meeting(from: Dec 13 2026 to: Dec 14 2026)", event.toString());
    }

    @Test
    void taskListChronologicalPlacement() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read book"));
        tasks.add(new Deadline("Submit assignment", "2026-12-01"));
        tasks.add(new Event("Attend meeting", "2026-12-10", "2026-12-12"));
        tasks.add(new Todo("Buy rice"));

        assertEquals("Read book", tasks.get(0).getTask());
        assertEquals("Submit assignment", tasks.get(1).getTask());
        assertEquals("Attend meeting", tasks.get(2).getTask());
        assertEquals("Buy rice", tasks.get(3).getTask());
    }
}
