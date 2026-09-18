package friday;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import friday.model.Deadline;
import friday.model.Event;
import friday.model.ToDo;

/**
 * Tests the readable task-card representation used by the JavaFX interface.
 */
class TaskCardTest {
    @Test
    void from_todoUsesReadableCategoryWithoutMetadata() {
        TaskCard card = TaskCard.from(new ToDo("read book"));

        assertEquals(TaskCard.Category.TODO, card.category());
        assertEquals("To-do", card.category().getLabel());
        assertEquals("read book", card.title());
        assertTrue(card.metadata().isEmpty());
        assertFalse(card.isDone());
    }

    @Test
    void from_deadlineAndEventShowsReadableScheduleMetadata() {
        TaskCard deadline = TaskCard.from(new Deadline("submit report", LocalDate.of(2026, 9, 30)));
        TaskCard event = TaskCard.from(new Event("team meeting", LocalDateTime.of(2026, 10, 1, 14, 0),
                LocalDateTime.of(2026, 10, 1, 15, 0)));

        assertEquals("Deadline", deadline.category().getLabel());
        assertEquals("Due Sep 30, 2026", deadline.metadata());
        assertEquals("Event", event.category().getLabel());
        assertTrue(event.metadata().contains("Oct 1, 2026 14:00"));
        assertTrue(event.metadata().contains("Oct 1, 2026 15:00"));
    }
}
