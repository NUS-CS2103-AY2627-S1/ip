package bob.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import bob.exception.BobException;

/**
 * Tests the behavior of {@link Event}.
 */
public class EventTest {

    @Test
    public void toString_unmarkedAndMarked_formattedCorrectly() throws BobException {
        LocalDateTime from = LocalDateTime.of(2026, 11, 11, 14, 0);
        LocalDateTime to = LocalDateTime.of(2026, 11, 11, 16, 0);
        Event event = new Event("project meeting", from, to);

        assertEquals(
                "[E][ ] project meeting (from: 11 November 2026, 1400 hrs to: 11 November 2026, 1600 hrs)",
                event.toString());

        event.mark();
        assertEquals(
                "[E][X] project meeting (from: 11 November 2026, 1400 hrs to: 11 November 2026, 1600 hrs)",
                event.toString());
    }

    @Test
    public void export_unmarkedAndMarked_formattedCorrectly() throws BobException {
        LocalDateTime from = LocalDateTime.of(2026, 11, 11, 14, 0);
        LocalDateTime to = LocalDateTime.of(2026, 11, 11, 16, 0);
        Event event = new Event("project meeting", from, to);

        assertEquals("E\u00010\u0001project meeting\u00012026-11-11T14:00:00\u00012026-11-11T16:00:00",
                event.export());

        event.mark();
        assertEquals("E\u00011\u0001project meeting\u00012026-11-11T14:00:00\u00012026-11-11T16:00:00",
                event.export());
    }

    @Test
    public void constructor_fromAfterTo_throwsBobException() {
        LocalDateTime from = LocalDateTime.of(2026, 11, 11, 16, 0);
        LocalDateTime to = LocalDateTime.of(2026, 11, 11, 14, 0);
        assertThrows(BobException.class, () -> new Event("project meeting", from, to));
    }

    @Test
    public void update_validFields_updatesAndPreservesDoneStatus() throws BobException {
        LocalDateTime from = LocalDateTime.of(2026, 11, 11, 14, 0);
        LocalDateTime to = LocalDateTime.of(2026, 11, 11, 16, 0);
        Event event = new Event("project meeting", from, to);
        event.mark();

        LocalDateTime newFrom = LocalDateTime.of(2026, 12, 15, 10, 0);
        LocalDateTime newTo = LocalDateTime.of(2026, 12, 15, 12, 0);
        Task updatedAll = event.update("revised meeting", null, newFrom, newTo);
        assertEquals(
                "[E][X] revised meeting (from: 15 December 2026, 1000 hrs to: 15 December 2026, 1200 hrs)",
                updatedAll.toString());

        Task updatedStartOnly = event.update(null, null, LocalDateTime.of(2026, 11, 11, 15, 0), null);
        assertEquals(
                "[E][X] project meeting (from: 11 November 2026, 1500 hrs to: 11 November 2026, 1600 hrs)",
                updatedStartOnly.toString());

        Task updatedEndOnly = event.update(null, null, null, LocalDateTime.of(2026, 11, 11, 17, 0));
        assertEquals(
                "[E][X] project meeting (from: 11 November 2026, 1400 hrs to: 11 November 2026, 1700 hrs)",
                updatedEndOnly.toString());
    }

    @Test
    public void update_incompatibleDeadlineFlag_throwsBobException() throws BobException {
        LocalDateTime from = LocalDateTime.of(2026, 11, 11, 14, 0);
        LocalDateTime to = LocalDateTime.of(2026, 11, 11, 16, 0);
        Event event = new Event("project meeting", from, to);

        LocalDateTime deadline = LocalDateTime.of(2026, 12, 12, 12, 0);
        assertThrows(BobException.class, () -> event.update(null, deadline, null, null));
    }

    @Test
    public void update_chronologyViolation_throwsBobException() throws BobException {
        LocalDateTime from = LocalDateTime.of(2026, 11, 11, 14, 0);
        LocalDateTime to = LocalDateTime.of(2026, 11, 11, 16, 0);
        Event event = new Event("project meeting", from, to);

        LocalDateTime invalidStart = LocalDateTime.of(2026, 11, 11, 17, 0);
        assertThrows(BobException.class, () -> event.update(null, null, invalidStart, null));

        LocalDateTime invalidEnd = LocalDateTime.of(2026, 11, 11, 13, 0);
        assertThrows(BobException.class, () -> event.update(null, null, null, invalidEnd));
    }
}
