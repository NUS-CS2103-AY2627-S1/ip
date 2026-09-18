package bobby.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import bobby.BobbyException;

/**
 * Tests task display, storage serialization, and validation helpers.
 */
class TaskTest {
    @Test
    void toString_taskTypesWithAndWithoutTags_returnsDisplayText() throws BobbyException {
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2019, 12, 6, 0, 0));
        Event event = new Event("meeting", LocalDateTime.of(2019, 12, 2, 14, 0),
                LocalDateTime.of(2019, 12, 2, 16, 0));

        todo.markAsDone();
        deadline.addTag("#urgent");
        event.addTag("#school");

        assertEquals("[T][X] read book", todo.toString());
        assertEquals("[D][ ] submit report (by: Dec 6 2019) #urgent", deadline.toString());
        assertEquals("[E][ ] meeting (from: Dec 2 2019, 2:00pm to: Dec 2 2019, 4:00pm) #school",
                event.toString());
    }

    @Test
    void toStorageString_taskTypesWithAndWithoutTags_returnsSerializedText() throws BobbyException {
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2019, 12, 6, 0, 0));
        Event event = new Event("meeting", LocalDateTime.of(2019, 12, 2, 14, 0),
                LocalDateTime.of(2019, 12, 2, 16, 0));

        todo.markAsDone();
        deadline.addTag("#urgent");
        event.addTag("#school");

        assertEquals("T | 1 | read book", todo.toStorageString());
        assertEquals("D | 0 | submit report | 2019-12-06T00:00 | #urgent", deadline.toStorageString());
        assertEquals("E | 0 | meeting | 2019-12-02T14:00 | 2019-12-02T16:00 | #school",
                event.toStorageString());
    }

    @Test
    void getIdentityKey_sameDescriptionDifferentDate_returnsDifferentKeys() {
        Todo todo = new Todo("read book");
        Deadline firstDeadline = new Deadline("read book", LocalDateTime.of(2019, 12, 6, 0, 0));
        Deadline secondDeadline = new Deadline("read book", LocalDateTime.of(2019, 12, 7, 0, 0));
        Event event = new Event("read book", LocalDateTime.of(2019, 12, 2, 14, 0),
                LocalDateTime.of(2019, 12, 2, 16, 0));

        assertEquals("T | read book", todo.getIdentityKey());
        assertEquals("D | read book | 2019-12-06T00:00", firstDeadline.getIdentityKey());
        assertEquals("D | read book | 2019-12-07T00:00", secondDeadline.getIdentityKey());
        assertEquals("E | read book | 2019-12-02T14:00 | 2019-12-02T16:00", event.getIdentityKey());
    }

    @Test
    void isValidTag_validAndInvalidTags_returnsExpectedBoolean() {
        assertTrue(Task.isValidTag("#reading"));
        assertTrue(Task.isValidTag("#read_2026"));
        assertTrue(Task.isValidTag("#read-2026"));

        assertFalse(Task.isValidTag(null));
        assertFalse(Task.isValidTag("#"));
        assertFalse(Task.isValidTag("reading"));
        assertFalse(Task.isValidTag("#bad!"));
        assertFalse(Task.isValidTag("#bad tag"));
    }

    @Test
    void isValidDescription_validAndInvalidDescriptions_returnsExpectedBoolean() {
        assertTrue(Task.isValidDescription("read book"));
        assertTrue(Task.isValidDescription("read #book"));

        assertFalse(Task.isValidDescription(null));
        assertFalse(Task.isValidDescription(""));
        assertFalse(Task.isValidDescription("   "));
        assertFalse(Task.isValidDescription("read | book"));
        assertFalse(Task.isValidDescription("read\nbook"));
    }
}
