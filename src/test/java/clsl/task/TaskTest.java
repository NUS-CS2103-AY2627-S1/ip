package clsl.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests common task behavior and type-specific task formatting. */
public class TaskTest {
    @Test
    void taskStatusCanBeMarkedAndUnmarked() {
        Task task = new Task("read book");

        assertEquals("[ ]", task.getStatusIcon());
        task.markAsDone();
        assertEquals("[X]", task.getStatusIcon());
        task.unmarkAsDone();
        assertEquals("[ ]", task.getStatusIcon());
    }

    @Test
    void taskFormatsDisplayAndSaveStrings() {
        Task task = new Task("read book");

        assertEquals("[ ] read book", task.toString());
        assertEquals("0 | read book", task.getSaveString());
        task.markAsDone();
        assertEquals("1 | read book", task.getSaveString());
    }

    @Test
    void plainTaskDoesNotOccurOnAnyDate() {
        Task task = new Task("read book");

        assertFalse(task.occursOn(LocalDate.of(2026, 9, 1)));
    }

    @Test
    void todoFormatsDisplayAndSaveStrings() {
        ToDo todo = new ToDo("read book");

        assertEquals("[T][ ] read book", todo.toString());
        assertEquals("T | 0 | read book", todo.getSaveString());
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
        assertEquals("T | 1 | read book", todo.getSaveString());
    }

    @Test
    void deadlineFormatsDatesAndMatchesOnlyItsDueDate() {
        Deadline deadline = new Deadline("submit report", "2026-09-02");

        assertEquals("[D][ ] submit report (by: Sep 2 2026)", deadline.toString());
        assertEquals("D | 0 | submit report | 2026-09-02", deadline.getSaveString());
        assertTrue(deadline.occursOn(LocalDate.of(2026, 9, 2)));
        assertFalse(deadline.occursOn(LocalDate.of(2026, 9, 1)));
    }

    @Test
    void eventFormatsDatesAndMatchesItsInclusiveDateRange() {
        Event event = new Event("conference", "2026-09-01", "2026-09-03");

        assertEquals("[E][ ] conference (from: Sep 1 2026 to: Sep 3 2026)", event.toString());
        assertEquals("E | 0 | conference | 2026-09-01 | 2026-09-03", event.getSaveString());
        assertTrue(event.occursOn(LocalDate.of(2026, 9, 1)));
        assertTrue(event.occursOn(LocalDate.of(2026, 9, 2)));
        assertTrue(event.occursOn(LocalDate.of(2026, 9, 3)));
        assertFalse(event.occursOn(LocalDate.of(2026, 9, 4)));
    }

    @Test
    void duplicateComparisonUsesTypeNameAndDates() {
        Task todo = new ToDo("Read   Book");
        Task sameTodo = new ToDo("read book");
        Task deadline = new Deadline("read book", "2026-09-01");
        Task differentDeadline = new Deadline("read book", "2026-09-02");
        Task event = new Event("read book", "2026-09-01", "2026-09-02");

        assertTrue(todo.isDuplicateOf(sameTodo));
        assertFalse(todo.isDuplicateOf(deadline));
        assertTrue(deadline.isDuplicateOf(new Deadline("READ BOOK", "2026-09-01")));
        assertFalse(deadline.isDuplicateOf(differentDeadline));
        assertFalse(deadline.isDuplicateOf(event));
    }
}
