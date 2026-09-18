package anby.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class EventTest {
    @Test
    public void toString_validDates_returnsFormattedDates() {
        Event event = new Event("my backyard burger fest", "2026-08-12", "2026-08-22");

        assertEquals("[E][ ] my backyard burger fest (from: Aug 12 2026, to: Aug 22 2026)", event.toString());
    }

    @Test
    public void toString_doneEvent_returnsDoneDisplayString() {
        Event event = new Event("my backyard burger fest", "2026-08-12", "2026-08-22");

        event.markAsDone();

        assertEquals("[E][X] my backyard burger fest (from: Aug 12 2026, to: Aug 22 2026)", event.toString());
    }

    @Test
    public void toFileString_undoneEvent_returnsEventStorageString() {
        Event event = new Event("my backyard burger fest", "2026-08-12", "2026-08-22");

        assertEquals("E | 0 | my backyard burger fest | 2026-08-12 | 2026-08-22", event.toFileString());
    }

    @Test
    public void toFileString_doneEvent_returnsDoneEventStorageString() {
        Event event = new Event("my backyard burger fest", "2026-08-12", "2026-08-22");

        event.markAsDone();

        assertEquals("E | 1 | my backyard burger fest | 2026-08-12 | 2026-08-22", event.toFileString());
    }

    @Test
    public void constructor_startDateAfterEndDate_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Event("burger fest but after time warping", "2026-08-12", "2021-08-12"));
    }

    @Test
    public void constructor_sameStartAndEndDate_createsEvent() {
        Event event = new Event("one day burger fest", "2026-08-12", "2026-08-12");

        assertEquals("[E][ ] one day burger fest (from: Aug 12 2026, to: Aug 12 2026)", event.toString());
    }
}
