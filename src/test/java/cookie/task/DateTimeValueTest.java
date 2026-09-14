package cookie.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/** Tests date/time storage parsing and user-facing formatting. */
public class DateTimeValueTest {
    @Test
    public void constructor_withoutDateOrTime_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new DateTimeValue(null, null));
    }

    @Test
    public void dateTimeValue_allComponentShapes_returnsComponentsAndFormats() {
        DateTimeValue dateTime = new DateTimeValue(LocalDate.of(2026, 8, 27),
                LocalTime.of(9, 15));
        assertEquals(LocalDate.of(2026, 8, 27), dateTime.getDate());
        assertEquals(LocalTime.of(9, 15), dateTime.getTime());
        assertEquals("2026-08-27T09:15", dateTime.toStorageString());
        assertEquals("Aug 27 2026, 9:15 AM", dateTime.toDisplayString());

        DateTimeValue dateOnly = new DateTimeValue(LocalDate.of(2026, 8, 27), null);
        assertEquals("2026-08-27", dateOnly.toStorageString());
        assertEquals("Aug 27 2026", dateOnly.toDisplayString());
        assertNull(dateOnly.getTime());

        DateTimeValue timeOnly = new DateTimeValue(null, LocalTime.of(17, 5));
        assertEquals("17:05", timeOnly.toStorageString());
        assertEquals("5:05 PM", timeOnly.toDisplayString());
        assertNull(timeOnly.getDate());
    }

    @Test
    public void parseStorageValue_supportedFormats_roundTripsComponents() {
        DateTimeValue dateTime = DateTimeValue.parseStorageValue("2026-08-27T09:15");
        assertEquals(LocalDate.of(2026, 8, 27), dateTime.getDate());
        assertEquals(LocalTime.of(9, 15), dateTime.getTime());

        DateTimeValue dateOnly = DateTimeValue.parseStorageValue("2026-08-27");
        assertEquals(LocalDate.of(2026, 8, 27), dateOnly.getDate());
        assertNull(dateOnly.getTime());

        DateTimeValue timeOnly = DateTimeValue.parseStorageValue("17:05");
        assertNull(timeOnly.getDate());
        assertEquals(LocalTime.of(17, 5), timeOnly.getTime());
    }

    @Test
    public void parseStorageValue_invalidValue_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeValue.parseStorageValue("2026-02-30"));
        assertThrows(DateTimeParseException.class, () -> DateTimeValue.parseStorageValue("not-a-date"));
    }

    @Test
    public void comparison_matchingAndDifferentComponents_validatesAndOrdersValues() {
        DateTimeValue earlier = new DateTimeValue(LocalDate.of(2026, 8, 27),
                LocalTime.of(9, 0));
        DateTimeValue later = new DateTimeValue(LocalDate.of(2026, 8, 27),
                LocalTime.of(10, 0));
        DateTimeValue dateOnly = new DateTimeValue(LocalDate.of(2026, 8, 27), null);

        assertTrue(earlier.hasSameComponentsAs(later));
        assertTrue(earlier.isBefore(later));
        assertFalse(later.isBefore(earlier));
        assertFalse(earlier.hasSameComponentsAs(dateOnly));
        assertFalse(earlier.hasSameComponentsAs(null));
        assertThrows(IllegalArgumentException.class, () -> earlier.isBefore(dateOnly));
        assertEquals(earlier, new DateTimeValue(LocalDate.of(2026, 8, 27),
                LocalTime.of(9, 0)));
        assertEquals(earlier.hashCode(), new DateTimeValue(LocalDate.of(2026, 8, 27),
                LocalTime.of(9, 0)).hashCode());
        assertNotEquals(earlier, null);
        assertNotEquals(earlier, "2026-08-27T09:00");
    }
}
