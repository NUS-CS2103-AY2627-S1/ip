package bany.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import bany.tags.Tag;

/** Tests tag storage and task-specific tag validation. */
class TaskTagTest {

    @Test
    void todo_canStoreAFlagTag() {
        ToDo todo = new ToDo("Read book", 1);

        todo.updateTags(List.of(new Tag("urgent")));

        assertEquals(List.of(new Tag("urgent")), todo.getTags());
        assertTrue(todo.getTag("URGENT").isPresent());
    }

    @Test
    void deadline_storesAndReplacesByTag() {
        LocalDateTime originalBy = LocalDateTime.of(2026, 3, 21, 21, 3);
        LocalDateTime newBy = LocalDateTime.of(2026, 3, 22, 21, 3);
        Deadline deadline = new Deadline("Submit report", originalBy, 1);

        deadline.setBy(newBy);

        assertEquals(newBy, deadline.getByDateTime());
        assertEquals("22-03-2026 21:03", deadline.getBy());
    }

    @Test
    void event_storesBothScheduleTags() {
        LocalDateTime from = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime to = LocalDateTime.of(2026, 3, 21, 11, 0);
        Event event = new Event("Meeting", from, to, 1);

        assertEquals(List.of(new Tag("from", "21-03-2026 10:00"),
                new Tag("to", "21-03-2026 11:00")), event.getTags());
    }

    @Test
    void event_invalidTagUpdate_doesNotChangeExistingSchedule() {
        LocalDateTime originalFrom = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime originalTo = LocalDateTime.of(2026, 3, 21, 11, 0);
        Event event = new Event("Meeting", originalFrom, originalTo, 1);

        assertThrows(IllegalArgumentException.class, () -> event.updateTags(List.of(
                new Tag("from", "22-03-2026 12:00"),
                new Tag("to", "22-03-2026 11:00"))));

        assertEquals(originalFrom, event.getFromDateTime());
        assertEquals(originalTo, event.getToDateTime());
    }

    @Test
    void updateTags_duplicateNamesRejectsEntireUpdate() {
        ToDo todo = new ToDo("Read book", 1);

        assertThrows(IllegalArgumentException.class, () -> todo.updateTags(List.of(
                new Tag("note", "first"),
                new Tag("NOTE", "second"))));

        assertTrue(todo.getTags().isEmpty());
    }
}
