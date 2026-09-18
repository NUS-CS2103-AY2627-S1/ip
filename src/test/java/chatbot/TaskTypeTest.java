package chatbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests task subtypes, formatting, and duplicate rules. */
class TaskTypeTest {

    @Test
    void task_trimsDescriptionAndComparesExactText() {
        Task task = new Task("  read book  ");

        assertEquals("read book", task.getDescription());
        assertTrue(task.hasSameDescription("read book"));
        assertFalse(task.hasSameDescription("Read book"));
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    void todo_formatsAndDetectsDuplicates() {
        Todo first = new Todo("buy milk");
        Todo same = new Todo("buy milk");
        Todo different = new Todo("buy eggs");

        assertEquals("[T] [ ] buy milk", first.toString());
        assertTrue(first.isDuplicate(same));
        assertFalse(first.isDuplicate(different));
    }

    @Test
    void deadline_parsesFormatsAndDetectsDuplicates() {
        Deadline first = new Deadline("submit report", "2026-09-01 1800");
        Deadline same = new Deadline("submit report", "2026-09-01 1800");
        Deadline differentTime = new Deadline("submit report", "2026-09-01 1900");

        assertEquals("2026-09-01T18:00", first.getDeadline().toString());
        assertTrue(first.isDuplicate(same));
        assertFalse(first.isDuplicate(differentTime));
        assertEquals("[D] [ ] submit report (by: 01 Sep 2026 6:00 PM)", first.toString());
    }

    @Test
    void event_parsesFormatsAndDetectsDuplicates() {
        Event first = new Event("team meeting", "2026-09-02 1000", "2026-09-02 1100");
        Event same = new Event("team meeting", "2026-09-02 1000", "2026-09-02 1100");
        Event differentEnd = new Event("team meeting", "2026-09-02 1000", "2026-09-02 1200");

        assertEquals("2026-09-02T10:00", first.getStart().toString());
        assertEquals("2026-09-02T11:00", first.getEnd().toString());
        assertTrue(first.isDuplicate(same));
        assertFalse(first.isDuplicate(differentEnd));
        assertEquals("[E] [ ] team meeting (from: 02 Sep 2026 10:00 AM to: 02 Sep 2026 11:00 AM)",
            first.toString());
    }

    @Test
    void dateTimeConstructors_rejectMalformedInput() {
        org.junit.jupiter.api.Assertions.assertThrows(InvalidInputException.class, () ->
            new Deadline("task", "2026/09/01 1800"));
        org.junit.jupiter.api.Assertions.assertThrows(InvalidInputException.class, () ->
            new Event("task", "2026-09-02 1100", "not-a-date"));
    }
}
