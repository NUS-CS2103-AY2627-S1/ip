package oreo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import oreo.exception.OreoException;

/** Tests shared task validation, tags, and type-specific display details. */
class TaskTest {
    @Test
    void todo_statusAndTags_areDisplayedAndTagsAreReadOnly() {
        Todo task = new Todo("read book");
        task.addTags(List.of("#School", "#school", "#urgent"));

        assertEquals("[T][ ] read book #school #urgent", task.toString());
        assertThrows(UnsupportedOperationException.class, () -> task.getTags().add("#new"));
        task.markAsDone();
        assertEquals("[T][X] read book #school #urgent", task.toString());
        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void deadlineAndEvent_renderDatesAndCompareDetails() {
        Deadline deadline = new Deadline("submit report", "2026-09-30");
        Event event = new Event("meeting", "2026-09-15", "2026-09-16");

        assertTrue(deadline.toString().contains("by: Sep 30 2026"));
        assertTrue(event.toString().contains("from: Sep 15 2026 to: Sep 16 2026"));
        assertTrue(new Todo("READ BOOK").hasSameDetails(new Todo("read book")));
        assertFalse(deadline.hasSameDetails(new Deadline("submit report", "2026-10-01")));
    }

    @Test
    void taskValidation_rejectsInvalidDescriptionsTagsAndEventRanges() {
        assertThrows(OreoException.class, () -> new Todo(" "));
        assertThrows(OreoException.class, () -> new Todo("line\nbreak"));
        assertThrows(OreoException.class, () -> new Todo("contains | separator"));
        assertThrows(IllegalArgumentException.class, () -> new Todo("valid").addTags(List.of("not-a-tag")));
        assertThrows(OreoException.class, () -> new Event("meeting", "2026-09-16", "2026-09-16"));
    }

    @Test
    void isValidTag_acceptsOnlySupportedTagCharacters() {
        assertTrue(Task.isValidTag("#topic-2_name"));
        assertFalse(Task.isValidTag("#"));
        assertFalse(Task.isValidTag("topic"));
        assertFalse(Task.isValidTag("#two words"));
    }
}
