package daddy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests task invariants, state changes, display values, and date matching.
 */
class TaskTest {
    /** Verifies that a general task exposes its description, type, and initial status. */
    @Test
    void task_validDescription_exposesInitialValues() {
        Task task = new Task("read book");

        assertEquals("read book", task.getDescription());
        assertEquals("read book", task.getDisplayDescription());
        assertEquals(" ", task.getStatusIcon());
        assertFalse(task.isDone());
        assertEquals("", task.getTypeIcon());
        assertEquals(TaskType.GENERAL, task.getType());
        assertFalse(task.occursOn(LocalDate.of(2026, 9, 18)));
    }

    /** Verifies that marking and unmarking update both status accessors. */
    @Test
    void task_markAndUnmark_updatesDoneStateAndIcon() {
        Task task = new Task("read book");

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());

        task.markAsNotDone();
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    /** Verifies that missing descriptions, reserved separators, and missing types are rejected. */
    @Test
    void task_invalidConstructorArguments_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> new Task(null));
        assertThrows(IllegalArgumentException.class, () -> new Task("   "));
        assertThrows(IllegalArgumentException.class, () -> new Task("read|book"));
        assertThrows(IllegalArgumentException.class, () -> new Task("read book", null));
    }

    /** Verifies that equivalent task details ignore case and completion but retain task type. */
    @Test
    void hasSameDetailsAs_variedTasks_comparesTypeAndDescription() {
        Task task = new Task("Read Book");
        Task sameTask = new Task("read book");
        sameTask.markAsDone();

        assertTrue(task.hasSameDetailsAs(sameTask));
        assertFalse(task.hasSameDetailsAs(new Todo("read book")));
        assertFalse(task.hasSameDetailsAs(new Task("write book")));
        assertFalse(task.hasSameDetailsAs(null));
    }

    /** Verifies deadline display, identity, accessors, date matching, and constructor validation. */
    @Test
    void deadline_variedDetails_exposesExpectedBehavior() {
        LocalDateTime due = LocalDateTime.of(2026, 12, 2, 18, 0);
        Deadline deadline = new Deadline("return book", due);

        assertEquals("return book (by: Dec 2 2026 18:00)", deadline.getDisplayDescription());
        assertEquals(due, deadline.getDeadline());
        assertEquals("[D]", deadline.getTypeIcon());
        assertTrue(deadline.occursOn(LocalDate.of(2026, 12, 2)));
        assertFalse(deadline.occursOn(LocalDate.of(2026, 12, 3)));
        assertTrue(deadline.hasSameDetailsAs(new Deadline("RETURN BOOK", due)));
        assertFalse(deadline.hasSameDetailsAs(new Deadline("return book", due.plusHours(1))));
        assertFalse(deadline.hasSameDetailsAs(new Todo("return book")));
        assertThrows(IllegalArgumentException.class, () -> new Deadline("return book", null));
    }

    /** Verifies event display, identity, accessors, date matching, and constructor validation. */
    @Test
    void event_variedDetails_exposesExpectedBehavior() {
        LocalDateTime start = LocalDateTime.of(2026, 12, 2, 23, 0);
        LocalDateTime end = LocalDateTime.of(2026, 12, 3, 1, 0);
        Event event = new Event("meeting", start, end);

        assertEquals("meeting (from: Dec 2 2026 23:00 to: Dec 3 2026 01:00)",
                event.getDisplayDescription());
        assertEquals(start, event.getFrom());
        assertEquals(end, event.getTo());
        assertEquals("[E]", event.getTypeIcon());
        assertTrue(event.occursOn(start.toLocalDate()));
        assertTrue(event.occursOn(end.toLocalDate()));
        assertFalse(event.occursOn(end.toLocalDate().plusDays(1)));
        assertTrue(event.hasSameDetailsAs(new Event("MEETING", start, end)));
        assertFalse(event.hasSameDetailsAs(new Event("meeting", start, end.plusHours(1))));
        assertFalse(event.hasSameDetailsAs(new Todo("meeting")));
        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", null, end));
        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", start, null));
    }

    /** Verifies that each task type supplies its documented display marker. */
    @Test
    void taskType_allValues_returnExpectedIcons() {
        assertEquals("[T]", TaskType.TODO.getIcon());
        assertEquals("[D]", TaskType.DEADLINE.getIcon());
        assertEquals("[E]", TaskType.EVENT.getIcon());
        assertEquals("", TaskType.GENERAL.getIcon());
    }
}
