package ben;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests for {@link Task}'s shared behaviour: status, tags, serialization, and duplicate detection. */
class TaskTest {
    @Test
    void markAsDoneAndMarkAsNotDone_toggleStatusIconAndIsDone() {
        Task task = new Todo("read book");
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());

        task.markAsNotDone();
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void addTag_sameTagTwice_storedOnce() {
        Task task = new Todo("read book");
        task.addTag("urgent");
        task.addTag("urgent");
        assertEquals(1, task.getTags().size());
    }

    @Test
    void addTag_appearsInToStringInInsertionOrder() {
        Task task = new Todo("read book");
        task.addTag("b");
        task.addTag("a");
        assertEquals("[T][ ] read book #b #a", task.toString());
    }

    @Test
    void serialize_todoWithTags_includesTagsField() {
        Task task = new Todo("read book");
        task.addTag("urgent");
        task.addTag("errand");
        assertEquals("T | 0 | read book | urgent,errand", task.serialize());
    }

    @Test
    void serialize_taskWithNoTags_omitsTagsField() {
        Task task = new Todo("read book");
        assertEquals("T | 0 | read book", task.serialize());
    }

    @Test
    void deserialize_todoLine_rebuildsMatchingTask() throws BenException {
        Task task = Task.deserialize("T | 1 | read book");
        assertEquals("[T][X] read book", task.toString());
    }

    @Test
    void deserialize_eventLineWithTags_restoresTags() throws BenException {
        Task task = Task.deserialize("E | 0 | trip | Mon | Fri | fun,travel");
        assertEquals("[E][ ] trip (from: Mon to: Fri) #fun #travel", task.toString());
    }

    @Test
    void deserialize_lineMissingRequiredFields_throwsBenException() {
        assertThrows(BenException.class, () -> Task.deserialize("D | 0 | return book"));
    }

    @Test
    void deserialize_unrecognisedTypeLetter_throwsBenException() {
        assertThrows(BenException.class, () -> Task.deserialize("X | 0 | mystery"));
    }

    @Test
    void isDuplicateOf_sameTypeAndDescriptionDifferentCase_isDuplicate() {
        Task a = new Todo("read book");
        Task b = new Todo("Read Book");
        assertTrue(a.isDuplicateOf(b));
    }

    @Test
    void isDuplicateOf_ignoresDoneStatusAndTags() {
        Task a = new Todo("read book");
        Task b = new Todo("read book");
        a.markAsDone();
        b.addTag("urgent");
        assertTrue(a.isDuplicateOf(b));
    }

    @Test
    void isDuplicateOf_differentDescription_isNotDuplicate() {
        Task a = new Todo("read book");
        Task b = new Todo("read magazine");
        assertFalse(a.isDuplicateOf(b));
    }
}
