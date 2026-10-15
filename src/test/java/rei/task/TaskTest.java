package rei.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task state changes and scheduled-task display formatting. */
public class TaskTest {
    @Test
    public void markAndUnmark_updatesCompletionStateAndIcon() {
        Task task = new Task("read book");
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());

        task.markAsUndone();
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void toString_scheduledTasks_formatsTypedDatesForUsers() {
        Deadlines deadline = new Deadlines(
                "submit report", LocalDateTime.of(2026, 8, 28, 18, 0));
        Events event = new Events(
                "project meeting",
                LocalDateTime.of(2026, 8, 29, 10, 0),
                LocalDateTime.of(2026, 8, 29, 12, 0));

        assertEquals("submit report (by: Aug 28 2026, 6:00 PM)", deadline.toString());
        assertEquals("project meeting (from: Aug 29 2026, 10:00 AM "
                + "to: Aug 29 2026, 12:00 PM)", event.toString());
    }

    @Test
    public void hasKeyword_variedKeywords_matchesDescriptionCaseInsensitively() {
        Task task = new Deadlines(
                "Return Library Book", LocalDateTime.of(2026, 8, 28, 18, 0));

        assertTrue(task.hasKeyword("book"));
        assertTrue(task.hasKeyword("LIBRARY"));
        assertTrue(task.hasKeyword("turn lib"));
        assertFalse(task.hasKeyword("report"));
        assertFalse(task.hasKeyword("2026"));
    }

    @Test
    public void addTag_validAndDuplicateTags_normalizesAndDisplaysTags() {
        Task task = new Task("read book");

        assertTrue(task.addTag("#School"));
        assertTrue(task.addTag("#week_5"));
        assertFalse(task.addTag("#SCHOOL"));

        assertEquals(List.of("#school", "#week_5"), task.getTags());
        assertEquals("read book #school #week_5", task.toString());
        assertTrue(task.hasKeyword("#SCHOOL"));
        assertFalse(task.hasKeyword("#week"));
        assertThrows(IllegalArgumentException.class, () -> task.addTag("school"));
    }
}
