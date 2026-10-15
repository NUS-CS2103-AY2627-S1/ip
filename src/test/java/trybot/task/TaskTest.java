package trybot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task status, formatting, dates, and event validation. */
class TaskTest {

    @Test
    void task_statusIconAndMutators_reflectCompletionState() {
        Task task = new Todo("read book");

        assertEquals(" ", task.getStatusIcon());
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void deadline_numericDate_formatsForDisplayAndStorage() {
        Deadline deadline = new Deadline("submit report", "2024-02-29 1830");

        assertEquals(LocalDateTime.of(2024, 2, 29, 18, 30), deadline.getByDateTime());
        assertEquals("Feb 29 2024 18:30", deadline.getBy());
        assertEquals("D | 0 | submit report | 2024-02-29 1830", deadline.toStorageString());
        assertEquals("[D][ ] submit report (by: Feb 29 2024 18:30)", deadline.toString());
    }

    @Test
    void deadline_legacyText_trimsTextAndPreservesIt() {
        Deadline deadline = new Deadline("submit report", "  Friday  ");

        assertEquals(null, deadline.getByDateTime());
        assertEquals("Friday", deadline.getBy());
        assertEquals("D | 0 | submit report | Friday", deadline.toStorageString());
    }

    @Test
    void event_numericDates_formatsForDisplayAndStorage() {
        Event event = new Event("meeting", "2024-02-29 0900", "2024-02-29 1030");
        event.addTag("work");

        assertEquals(LocalDateTime.of(2024, 2, 29, 9, 0), event.getFromDateTime());
        assertEquals(LocalDateTime.of(2024, 2, 29, 10, 30), event.getToDateTime());
        assertEquals("Feb 29 2024 09:00", event.getFrom());
        assertEquals("Feb 29 2024 10:30", event.getTo());
        assertEquals("E | 0 | meeting | 2024-02-29 0900 | 2024-02-29 1030 | work",
                event.toStorageString());
    }

    @Test
    void event_legacyText_trimsBothEndpoints() {
        Event event = new Event("meeting", "  Monday  ", "  Tuesday  ");

        assertEquals("Monday", event.getFrom());
        assertEquals("Tuesday", event.getTo());
        assertEquals("[E][ ] meeting (from: Monday to: Tuesday)", event.toString());
    }

    @Test
    void event_invalidChronology_rejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Event("meeting", "2024-02-29 1030", "2024-02-29 0900"));
        assertThrows(IllegalArgumentException.class,
                () -> new Event("meeting", "2024-02-29 0900", "2024-02-29 0900"));
    }

    @Test
    void task_tags_areImmutableAndStorageEscapesSpecialCharacters() {
        Task task = new Todo("description");
        task.addTag("urgent");

        assertThrows(UnsupportedOperationException.class, () -> task.getTags().add("school"));
        assertEquals(List.of("urgent"), task.getTags());
    }
}
