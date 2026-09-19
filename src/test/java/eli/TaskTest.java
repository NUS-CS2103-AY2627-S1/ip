package eli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests the common behaviour shared by task types. */
public class TaskTest {
    @Test
    public void todo_hasExpectedDisplayText() {
        Todo task = new Todo("read notes");
        assertEquals("[T][ ] read notes", task.toString());
    }

    @Test
    public void markingTaskDoneChangesStatusIcon() {
        Todo task = new Todo("read notes");
        task.markAsDone();
        assertTrue(task.toString().contains("[X]"));

        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void deadlineIncludesDueDate() {
        Deadline task = new Deadline("submit", "2026-09-01");
        assertTrue(task.toString().contains("Sep 1 2026"));
    }

    @Test
    public void eventWithDateTimes_hasFormattedDisplayText() {
        Event task = new Event("lecture", "2026-09-08 1400", "2026-09-08 1600");

        assertTrue(task.toString().contains("Sep 8 2026, 2:00PM"));
        assertTrue(task.toString().contains("Sep 8 2026, 4:00PM"));
    }

    @Test
    public void taskWithNaturalLanguageDate_preservesOriginalText() {
        Deadline task = new Deadline("submit", "next Friday");

        assertTrue(task.toString().contains("next Friday"));
    }

    @Test
    public void todo_blankDescription_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Todo("  "));
    }

    @Test
    public void task_invalidConstructorArguments_throwAssertionError() {
        assertThrows(AssertionError.class, () -> new Todo(null));
        assertThrows(AssertionError.class, () -> new Task("read notes", null) {
        });
    }

    @Test
    public void taskAccessors_returnTypeDescriptionAndSortDates() {
        Todo todo = new Todo("read notes");
        Deadline deadline = new Deadline("submit", "2026-09-18");
        Event event = new Event("meeting", "2026-09-15 1400", "2026-09-15 1600");

        assertEquals("read notes", todo.getDescription());
        assertEquals(TaskType.TODO, todo.getTaskType());
        assertTrue(todo.getSortDate().isEmpty());
        assertEquals(18, deadline.getSortDate().orElseThrow().getDayOfMonth());
        assertEquals(14, event.getSortDate().orElseThrow().getHour());
    }

    @Test
    public void sameDetails_compareAllTypeSpecificValues() {
        Deadline deadline = new Deadline("submit", "2026-09-18");
        Event event = new Event("meeting", "2026-09-15 1400", "2026-09-15 1600");

        assertTrue(deadline.hasSameDetails(new Deadline("SUBMIT", "2026-09-18")));
        assertFalse(deadline.hasSameDetails(new Deadline("submit", "2026-09-19")));
        assertFalse(deadline.hasSameDetails(new Todo("submit")));
        assertFalse(deadline.hasSameDetails(null));
        assertTrue(event.hasSameDetails(
                new Event("MEETING", "2026-09-15 1400", "2026-09-15 1600")));
        assertFalse(event.hasSameDetails(
                new Event("meeting", "2026-09-15 1400", "2026-09-15 1700")));
    }

    @Test
    public void taskTypeMetadata_hasExpectedDisplayValuesAndOrder() {
        assertEquals("T", TaskType.TODO.getIcon());
        assertEquals("Todos", TaskType.TODO.getSectionName());
        assertEquals(2, TaskType.TODO.getSortOrder());
        assertEquals("D", TaskType.DEADLINE.getIcon());
        assertEquals(0, TaskType.DEADLINE.getSortOrder());
        assertEquals("E", TaskType.EVENT.getIcon());
        assertEquals(1, TaskType.EVENT.getSortOrder());
    }

    @Test
    public void eliException_preservesMessage() {
        EliException exception = new EliException("problem");

        assertEquals("problem", exception.getMessage());
    }
}
