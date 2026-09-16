package zucc.task;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import zucc.ZuccException;

/**
 * Tests event validation, date matching, persistence fields, and display formatting.
 */
public class EventTest {
    private static final String EVENT_DESCRIPTION = "Attend software engineering workshop";
    private static final String EVENT_START = "10/6/2026 2300";
    private static final String EVENT_END = "12/6/2026 0100";

    private Event multiDayEvent;

    /**
     * Creates a valid multi-day event used by the date-matching and formatting tests.
     *
     * @throws ZuccException if the shared event fixture cannot be created.
     */
    @BeforeEach
    public void setUp() throws ZuccException {
        multiDayEvent = new Event(EVENT_DESCRIPTION, EVENT_START, EVENT_END);
    }

    /**
     * Verifies that an event rejects null, empty, and whitespace-only descriptions.
     */
    @Test
    public void constructor_nullOrBlankDescription_exceptionThrown() {
        assertAll(
                () -> assertThrows(ZuccException.class,
                        () -> new Event(null, EVENT_START, EVENT_END)),
                () -> assertThrows(ZuccException.class,
                        () -> new Event("", EVENT_START, EVENT_END)),
                () -> assertThrows(ZuccException.class,
                        () -> new Event("   ", EVENT_START, EVENT_END)));
    }

    /**
     * Verifies that an event rejects null, empty, and whitespace-only start values.
     */
    @Test
    public void constructor_nullOrBlankStart_exceptionThrown() {
        assertAll(
                () -> assertThrows(ZuccException.class,
                        () -> new Event(EVENT_DESCRIPTION, null, EVENT_END)),
                () -> assertThrows(ZuccException.class,
                        () -> new Event(EVENT_DESCRIPTION, "", EVENT_END)),
                () -> assertThrows(ZuccException.class,
                        () -> new Event(EVENT_DESCRIPTION, "   ", EVENT_END)));
    }

    /**
     * Verifies that an event rejects null, empty, and whitespace-only end values.
     */
    @Test
    public void constructor_nullOrBlankEnd_exceptionThrown() {
        assertAll(
                () -> assertThrows(ZuccException.class,
                        () -> new Event(EVENT_DESCRIPTION, EVENT_START, null)),
                () -> assertThrows(ZuccException.class,
                        () -> new Event(EVENT_DESCRIPTION, EVENT_START, "")),
                () -> assertThrows(ZuccException.class,
                        () -> new Event(EVENT_DESCRIPTION, EVENT_START, "   ")));
    }

    /**
     * Verifies that an event rejects a malformed start date and time.
     */
    @Test
    public void constructor_malformedStart_exceptionThrown() {
        assertThrows(ZuccException.class,
                () -> new Event(EVENT_DESCRIPTION, "not a date", EVENT_END));
    }

    /**
     * Verifies that an event rejects an impossible end date.
     */
    @Test
    public void constructor_impossibleEndDate_exceptionThrown() {
        assertThrows(ZuccException.class,
                () -> new Event(EVENT_DESCRIPTION, EVENT_START, "31/2/2027 1200"));
    }

    /**
     * Verifies that an event cannot end before it starts.
     */
    @Test
    public void constructor_endBeforeStart_exceptionThrown() {
        assertThrows(ZuccException.class,
                () -> new Event(EVENT_DESCRIPTION, "12/6/2026 0100", "10/6/2026 2300"));
    }

    /** Verifies that an event cannot start and end at the same instant. */
    @Test
    public void constructor_sameStartAndEnd_exceptionThrown() {
        assertThrows(ZuccException.class,
                () -> new Event(
                        "Submit project", "15/6/2026 1200", "15/6/2026 1200"));
    }

    /**
     * Verifies that valid decoded storage fields reconstruct the complete event state.
     *
     * @throws ZuccException if the valid stored event cannot be reconstructed.
     */
    @Test
    public void storageConstructor_validFields_eventRestored() throws ZuccException {
        Event restoredEvent = new Event(new String[] {
            "E", "1", "2", EVENT_DESCRIPTION, EVENT_START, EVENT_END
        });

        assertEquals(
                "[E][X][P2] Attend software engineering workshop "
                        + "(from: Jun 10 2026, 11:00PM to: Jun 12 2026, 1:00AM)",
                restoredEvent.toString());
    }

    /**
     * Verifies that stored event data must contain exactly the required fields.
     */
    @Test
    public void storageConstructor_nullOrIncorrectFieldCount_exceptionThrown() {
        assertAll(
                () -> assertThrows(ZuccException.class,
                        () -> new Event((String[]) null)),
                () -> assertThrows(ZuccException.class,
                        () -> new Event(new String[] {
                            "E", "0", "0", EVENT_DESCRIPTION, EVENT_START
                        })),
                () -> assertThrows(ZuccException.class,
                        () -> new Event(new String[] {
                            "E", "0", "0", EVENT_DESCRIPTION, EVENT_START, EVENT_END, "extra"
                        })));
    }

    /**
     * Verifies that stored event data rejects an invalid completion status.
     */
    @Test
    public void storageConstructor_invalidCompletionStatus_exceptionThrown() {
        assertThrows(ZuccException.class,
                () -> new Event(new String[] {
                    "E", "done", "0", EVENT_DESCRIPTION, EVENT_START, EVENT_END
                }));
    }

    /**
     * Verifies that stored event data cannot reconstruct a reversed time range.
     */
    @Test
    public void storageConstructor_endBeforeStart_exceptionThrown() {
        assertThrows(ZuccException.class,
                () -> new Event(new String[] {
                    "E", "0", "0", EVENT_DESCRIPTION,
                    "12/6/2026 0100", "10/6/2026 2300"
                }));
    }

    /** Verifies that stored event data cannot reconstruct a zero-duration time range. */
    @Test
    public void storageConstructor_sameStartAndEnd_exceptionThrown() {
        assertThrows(ZuccException.class,
                () -> new Event(new String[] {
                    "E", "0", "0", EVENT_DESCRIPTION,
                    "12/6/2026 0100", "12/6/2026 0100"
                }));
    }

    /**
     * Verifies that an event does not occur on a date before its start date.
     */
    @Test
    public void occursOn_dateBeforeEvent_returnsFalse() {
        assertFalse(multiDayEvent.occursOn(LocalDate.of(2026, 6, 9)));
    }

    /**
     * Verifies that an event occurs on its start date.
     */
    @Test
    public void occursOn_eventStartDate_returnsTrue() {
        assertTrue(multiDayEvent.occursOn(LocalDate.of(2026, 6, 10)));
    }

    /**
     * Verifies that a multi-day event occurs on a date inside its range.
     */
    @Test
    public void occursOn_dateBetweenStartAndEnd_returnsTrue() {
        assertTrue(multiDayEvent.occursOn(LocalDate.of(2026, 6, 11)));
    }

    /**
     * Verifies that an event occurs on its end date.
     */
    @Test
    public void occursOn_eventEndDate_returnsTrue() {
        assertTrue(multiDayEvent.occursOn(LocalDate.of(2026, 6, 12)));
    }

    /**
     * Verifies that an event does not occur on a date after its end date.
     */
    @Test
    public void occursOn_dateAfterEvent_returnsFalse() {
        assertFalse(multiDayEvent.occursOn(LocalDate.of(2026, 6, 13)));
    }

    /**
     * Verifies that a same-day event occurs on its scheduled date.
     *
     * @throws ZuccException if the valid same-day event cannot be created.
     */
    @Test
    public void occursOn_sameDayEventDate_returnsTrue() throws ZuccException {
        Event sameDayEvent = new Event(
                "Attend tutorial",
                "15/6/2026 1000",
                "15/6/2026 1200");

        assertTrue(sameDayEvent.occursOn(LocalDate.of(2026, 6, 15)));
    }

    /**
     * Verifies that an event spanning New Year occurs on dates in both calendar years.
     *
     * @throws ZuccException if the valid cross-year event cannot be created.
     */
    @Test
    public void occursOn_eventSpanningYearBoundary_bothDatesReturnTrue()
            throws ZuccException {
        Event newYearEvent = new Event(
                "Attend countdown",
                "31/12/2026 2300",
                "1/1/2027 0100");

        assertAll(
                () -> assertTrue(newYearEvent.occursOn(LocalDate.of(2026, 12, 31))),
                () -> assertTrue(newYearEvent.occursOn(LocalDate.of(2027, 1, 1))));
    }

    /**
     * Verifies that an event supplies its type, start, and end values for storage.
     */
    @Test
    public void getStorageFields_event_returnsTypeAndTimeRange() {
        assertArrayEquals(
                new String[] {"E", "10/6/2026 2300", "12/6/2026 0100"},
                multiDayEvent.getStorageFields());
    }

    /**
     * Verifies the display format of an incomplete event and its time range.
     */
    @Test
    public void toString_incompleteEvent_returnsFormattedEvent() {
        assertEquals(
                "[E][ ] Attend software engineering workshop "
                        + "(from: Jun 10 2026, 11:00PM to: Jun 12 2026, 1:00AM)",
                multiDayEvent.toString());
    }
}
