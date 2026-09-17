package botavius.tasklist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import botavius.exception.BotaviusException;
import org.junit.jupiter.api.Test;

/** Tests validation and formatting of deadline and event date/time values. */
class DateTimeTaskTest {
    /** Verifies that a missing deadline is reported clearly. */
    @Test
    void deadline_missingDate_throwsError() {
        assertEquals("deadline not provided.", assertThrows(BotaviusException.class,
                () -> new Deadline("submit report", "")).getMessage());
    }

    /** Verifies that a date without a time defaults to midnight. */
    @Test
    void deadline_dateOnly_defaultsToMidnight() {
        Deadline deadline = new Deadline("submit report", "09-09-2026");

        assertEquals("[D][ ] submit report (by: 09-Sep-26 12:00 am)", deadline.toString());
        assertEquals("[D][ ] submit report by: 09-09-2026 00:00",
                deadline.toStorageString());
    }

    /** Verifies that malformed dates are rejected. */
    @Test
    void event_invalidDate_throwsError() {
        assertThrows(BotaviusException.class,
                () -> new Event("meeting", "31-02-2026", "10-09-2026 14:30"));
    }

    /** Verifies that an event with no start time is rejected. */
    @Test
    void event_missingStartTime_throwsError() {
        assertEquals("event start time not provided.", assertThrows(BotaviusException.class,
                () -> new Event("meeting", "", "10-09-2026 14:30")).getMessage());
    }

    /** Verifies that an event with no end time is rejected. */
    @Test
    void event_missingEndTime_throwsError() {
        assertEquals("event end time not provided.", assertThrows(BotaviusException.class,
                () -> new Event("meeting", "10-09-2026 14:30", "")).getMessage());
    }

    /** Verifies that an event with an invalid start time format is rejected. */
    @Test
    void event_invalidStartTimeFormat_throwsError() {
        assertEquals("event start time must use dd-MM-yyyy with an optional 24-hour time (HH:mm).",
                assertThrows(BotaviusException.class,
                        () -> new Event("meeting", "2026-09-10 14:30", "10-09-2026 16:00"))
                        .getMessage());
    }

    /** Verifies that an event with an invalid end time format is rejected. */
    @Test
    void event_invalidEndTimeFormat_throwsError() {
        assertEquals("event end time must use dd-MM-yyyy with an optional 24-hour time (HH:mm).",
                assertThrows(BotaviusException.class,
                        () -> new Event("meeting", "10-09-2026 14:30", "10/09/2026 16:00"))
                        .getMessage());
    }

    /** Verifies that impossible event times are rejected. */
    @Test
    void event_impossibleTime_throwsError() {
        assertThrows(BotaviusException.class,
                () -> new Event("meeting", "10-09-2026 24:00", "11-09-2026 01:00"));
    }

    /** Verifies that date-only event values default to midnight. */
    @Test
    void event_dateOnly_defaultsToMidnight() {
        Event event = new Event("meeting", "10-09-2026", "11-09-2026");

        assertEquals(LocalDateTime.of(2026, 9, 10, 0, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2026, 9, 11, 0, 0), event.getTo());
    }

    /** Verifies display and storage formatting for an event. */
    @Test
    void event_validDateTime_formatsForDisplayAndStorage() {
        Event event = new Event("meeting", "10-09-2026 14:30", "10-09-2026 16:00");

        assertEquals("[E][ ] meeting (from: 10-Sep-26 02:30 pm to: 10-Sep-26 04:00 pm)",
                event.toString());
        assertEquals("[E][ ] meeting from: 10-09-2026 14:30 to: 10-09-2026 16:00",
                event.toStorageString());
    }

    /** Verifies that an event cannot end before it starts. */
    @Test
    void event_startAfterEnd_throwsError() {
        assertEquals("event start time must not be after event end time.",
                assertThrows(BotaviusException.class,
                        () -> new Event("meeting", "11-09-2026", "10-09-2026")).getMessage());
    }

    /** Verifies that an event may start and end at the same time. */
    @Test
    void event_startEqualsEnd_isAccepted() {
        Event event = new Event("meeting", "10-09-2026 14:30", "10-09-2026 14:30");

        assertEquals("[E][ ] meeting (from: 10-Sep-26 02:30 pm to: 10-Sep-26 02:30 pm)",
                event.toString());
    }

    /** Verifies that event accessors return the parsed date-time values. */
    @Test
    void event_getters_returnParsedValues() {
        Event event = new Event("meeting", "10-09-2026 14:30", "10-09-2026 16:00");

        assertEquals(LocalDateTime.of(2026, 9, 10, 14, 30), event.getFrom());
        assertEquals(LocalDateTime.of(2026, 9, 10, 16, 0), event.getTo());
        assertEquals("meeting", event.getDescription());
    }

    /** Verifies that marking an event done updates both display formats. */
    @Test
    void event_markedDone_updatesDisplayAndStorage() {
        Event event = new Event("meeting", "10-09-2026 14:30", "10-09-2026 16:00");
        event.setDone(true);

        assertEquals("[E][X] meeting (from: 10-Sep-26 02:30 pm to: 10-Sep-26 04:00 pm)",
                event.toString());
        assertEquals("[E][X] meeting from: 10-09-2026 14:30 to: 10-09-2026 16:00",
                event.toStorageString());
    }

    /** Verifies display and storage formatting for a do-after task. */
    @Test
    void doAfter_validDateTime_formatsForDisplayAndStorage() {
        DoAfter doAfter = new DoAfter("call client", "10-09-2026 14:30");

        assertEquals("[A][ ] call client (after: 10-Sep-26 02:30 pm)", doAfter.toString());
        assertEquals("[A][ ] call client after: 10-09-2026 14:30",
                doAfter.toStorageString());
    }

    /** Verifies that a missing do-after time is reported clearly. */
    @Test
    void doAfter_missingTime_throwsError() {
        assertEquals("do-after time not provided.", assertThrows(BotaviusException.class,
                () -> new DoAfter("call client", "")).getMessage());
    }
}
