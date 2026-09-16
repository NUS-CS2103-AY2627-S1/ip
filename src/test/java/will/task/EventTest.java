package will.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class EventTest {

    @Test
    public void clashesWith_overlappingRanges_returnsTrue() {
        Event meeting = new Event("project meeting", "2026-09-20", "2026-09-21");
        Event workshop = new Event("workshop", "2026-09-21", "2026-09-22");
        assertTrue(meeting.clashesWith(workshop));
        assertTrue(workshop.clashesWith(meeting));
    }

    @Test
    public void clashesWith_nonOverlappingRanges_returnsFalse() {
        Event meeting = new Event("project meeting", "2026-09-20", "2026-09-21");
        Event workshop = new Event("workshop", "2026-09-22", "2026-09-23");
        assertFalse(meeting.clashesWith(workshop));
    }

    @Test
    public void clashesWith_oneEventFreeTextDate_returnsFalse() {
        Event meeting = new Event("project meeting", "2026-09-20", "2026-09-21");
        Event vague = new Event("something", "sometime", "later");
        assertFalse(meeting.clashesWith(vague));
    }

    @Test
    public void clashesWith_nonEventTask_returnsFalse() {
        Event meeting = new Event("project meeting", "2026-09-20", "2026-09-21");
        Todo todo = new Todo("buy milk");
        assertFalse(meeting.clashesWith(todo));
    }
}
