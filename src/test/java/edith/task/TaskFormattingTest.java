package edith.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task status transitions and storage-compatible string representations. */
public class TaskFormattingTest {
    @Test
    public void todo_markAndUnmark_updatesStatusInStringRepresentation() {
        Todo todo = new Todo("read book");

        assertEquals("[T][ ] read book", todo.toString());
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
        todo.markAsNotDone();
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void deadline_withAndWithoutTime_formatsExpectedDateDetails() {
        assertEquals("[D][ ] submit work (by: Sep 06 2026)",
                new Deadline("submit work", LocalDateTime.of(2026, 9, 6, 0, 0), false).toString());
        assertEquals("[D][ ] submit work (by: Sep 06 2026 14:30)",
                new Deadline("submit work", LocalDateTime.of(2026, 9, 6, 14, 30), true).toString());
    }

    @Test
    public void event_mixedOptionalTimes_formatsEachEndpointIndependently() {
        Event event = new Event("conference", LocalDateTime.of(2026, 9, 6, 0, 0), false,
                LocalDateTime.of(2026, 9, 7, 17, 30), true);

        assertEquals("[E][ ] conference (from: Sep 06 2026 to: Sep 07 2026 17:30)", event.toString());
    }

    @Test
    public void addTag_mixedCaseDuplicates_preservesFirstSpellingAndInsertionOrder() {
        Todo todo = new Todo("read book");

        assertTrue(todo.addTag("#Fun"));
        assertTrue(todo.addTag("#school"));
        assertFalse(todo.addTag("#fun"));

        assertEquals("[T][ ] read book [tags: #Fun #school]", todo.toString());
        assertEquals("[T][ ] read book", todo.toStorageString());
        assertEquals(List.of("#Fun", "#school"), todo.getTags());
    }

    @Test
    public void tags_allTaskTypes_followDateDetails() {
        Deadline deadline = new Deadline("submit work", LocalDateTime.of(2026, 9, 20, 0, 0), false);
        Event event = new Event("meeting", LocalDateTime.of(2026, 9, 20, 0, 0), false,
                LocalDateTime.of(2026, 9, 21, 0, 0), false);
        deadline.addTag("#school");
        event.addTag("#team");

        assertEquals("[D][ ] submit work (by: Sep 20 2026) [tags: #school]", deadline.toString());
        assertEquals("[E][ ] meeting (from: Sep 20 2026 to: Sep 21 2026) [tags: #team]", event.toString());
    }

    @Test
    public void removeTag_caseInsensitiveRemoval_preservesOtherTagsAndDropsEmptySuffix() {
        Todo todo = new Todo("read book");
        todo.addTag("#Fun");
        todo.addTag("#school");

        assertTrue(todo.removeTag("#FUN"));
        assertFalse(todo.removeTag("#missing"));
        assertEquals("[T][ ] read book [tags: #school]", todo.toString());
        assertTrue(todo.removeTag("#school"));
        assertEquals("[T][ ] read book", todo.toString());
    }
}
