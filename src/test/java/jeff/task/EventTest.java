package jeff.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class EventTest {
    @Test
    public void constructor_blankFrom_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Event("trip", null, "2019-11-05"));
        assertThrows(IllegalArgumentException.class, () -> new Event("trip", "", "2019-11-05"));
    }

    @Test
    public void constructor_blankTo_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Event("trip", "2019-11-01", null));
        assertThrows(IllegalArgumentException.class, () -> new Event("trip", "2019-11-01", ""));
    }

    @Test
    public void constructor_malformedFromDate_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Event("trip", "not a date", "2019-11-05"));
    }

    @Test
    public void constructor_malformedToDate_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Event("trip", "2019-11-01", "not a date"));
    }

    @Test
    public void constructor_fromAfterTo_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Event("trip", "2019-11-05", "2019-11-01"));
    }

    @Test
    public void constructor_fromEqualsTo_isAllowed() {
        Event event = new Event("trip", "2019-11-01", "2019-11-01");
        assertEquals(LocalDate.of(2019, 11, 1), event.getChronologicalDate().orElseThrow());
    }

    @Test
    public void toString_validDates_printsBothInMmmDdYyyyFormat() {
        Event event = new Event("trip", "2019-11-01", "2019-11-05");
        assertEquals("[E][ ] trip (from: Nov 01 2019 to: Nov 05 2019)", event.toString());
    }

    @Test
    public void toSaveFormat_encodesBothDatesInIsoFormat() {
        Event event = new Event("trip", "2019-11-01", "2019-11-05");
        assertEquals("E | 0 | trip | 2019-11-01 | 2019-11-05", event.toSaveFormat());
    }

    @Test
    public void getChronologicalDate_returnsFromDate() {
        Event event = new Event("trip", "2019-11-01", "2019-11-05");
        assertEquals(LocalDate.of(2019, 11, 1), event.getChronologicalDate().orElseThrow());
    }
}
