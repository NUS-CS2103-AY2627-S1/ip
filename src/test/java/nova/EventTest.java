package nova;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class EventTest {

    private Event eventFrom(int startHour, int endHour) {
        return new Event("event " + startHour, false,
                LocalDateTime.of(2026, 1, 5, startHour, 0),
                LocalDateTime.of(2026, 1, 5, endHour, 0));
    }

    @Test
    public void clashesWith_partialOverlap_returnsTrue() {
        assertTrue(eventFrom(9, 11).clashesWith(eventFrom(10, 12)));
        assertTrue(eventFrom(10, 12).clashesWith(eventFrom(9, 11)));
    }

    @Test
    public void clashesWith_oneContainingTheOther_returnsTrue() {
        assertTrue(eventFrom(9, 17).clashesWith(eventFrom(12, 13)));
        assertTrue(eventFrom(12, 13).clashesWith(eventFrom(9, 17)));
    }

    @Test
    public void clashesWith_identicalRanges_returnsTrue() {
        assertTrue(eventFrom(9, 10).clashesWith(eventFrom(9, 10)));
    }

    @Test
    public void clashesWith_touchingAtBoundary_returnsFalse() {
        assertFalse(eventFrom(9, 10).clashesWith(eventFrom(10, 11)));
        assertFalse(eventFrom(10, 11).clashesWith(eventFrom(9, 10)));
    }

    @Test
    public void clashesWith_disjointRanges_returnsFalse() {
        assertFalse(eventFrom(9, 10).clashesWith(eventFrom(14, 15)));
    }

    @Test
    public void clashesWith_zeroLengthEvent_returnsFalse() {
        Event instant = eventFrom(10, 10);

        assertFalse(instant.clashesWith(eventFrom(9, 11)));
        assertFalse(eventFrom(9, 11).clashesWith(instant));
        assertFalse(instant.clashesWith(instant));
    }

    @Test
    public void clashesWith_differentDays_returnsFalse() {
        Event tuesday = new Event("tuesday", false,
                LocalDateTime.of(2026, 1, 6, 9, 0),
                LocalDateTime.of(2026, 1, 6, 17, 0));

        assertFalse(eventFrom(9, 17).clashesWith(tuesday));
    }
}
