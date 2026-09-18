package com.fireinyu.themyth.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.fireinyu.themyth.exceptions.DateFormatException;

/**
 * Unit tests for {@link MythDateTime}.
 */
public class MythDateTimeTest {

    /**
     * Tests parsing a fully specified date-time string.
     */
    @Test
    public void parse_fullDateTime_success() {
        MythDateTime dt = MythDateTime.parse("2025-10-15-18-30-45");
        assertEquals("2025-10-15-18-30-45", dt.dump());
    }

    /**
     * Tests parsing partial date-time strings with defaulted missing fields.
     */
    @Test
    public void parse_partialDateTimes_defaultsFields() {
        MythDateTime yearOnly = MythDateTime.parse("2025");
        assertEquals("2025-01-01-00-00-00", yearOnly.dump());

        MythDateTime yearMonth = MythDateTime.parse("2025-06");
        assertEquals("2025-06-01-00-00-00", yearMonth.dump());

        MythDateTime yearMonthDay = MythDateTime.parse("2025-06-15");
        assertEquals("2025-06-15-00-00-00", yearMonthDay.dump());

        MythDateTime withHour = MythDateTime.parse("2025-06-15-14");
        assertEquals("2025-06-15-14-00-00", withHour.dump());

        MythDateTime withMinute = MythDateTime.parse("2025-06-15-14-30");
        assertEquals("2025-06-15-14-30-00", withMinute.dump());
    }

    /**
     * Tests that parsing invalid strings throws {@link DateFormatException}.
     */
    @Test
    public void parse_invalidStrings_throwsDateFormatException() {
        assertThrows(DateFormatException.class, () -> MythDateTime.parse("not-a-date"));
        assertThrows(DateFormatException.class, () -> MythDateTime.parse("2025-10-15-12-30-45-99"));
    }

    /**
     * Tests {@link MythDateTime#now()} returns a non-null instance.
     */
    @Test
    public void now_returnsCurrentInstance() {
        MythDateTime now = MythDateTime.now();
        assertNotNull(now);
    }

    /**
     * Tests the {@link MythDateTime#isBefore(MythDateTime)} method.
     */
    @Test
    public void isBefore_comparesCorrectly() {
        MythDateTime earlier = MythDateTime.parse("2025-01-01-00-00-00");
        MythDateTime later = MythDateTime.parse("2025-01-02-00-00-00");

        assertTrue(earlier.isBefore(later));
        assertFalse(later.isBefore(earlier));
        assertFalse(earlier.isBefore(earlier));
    }

    /**
     * Tests the {@link MythDateTime#isBetween(MythDateTime, MythDateTime)} method.
     */
    @Test
    public void isBetween_checksIntervalExclusively() {
        MythDateTime start = MythDateTime.parse("2025-01-01-00-00-00");
        MythDateTime middle = MythDateTime.parse("2025-01-02-00-00-00");
        MythDateTime end = MythDateTime.parse("2025-01-03-00-00-00");

        assertTrue(middle.isBetween(start, end));
        assertFalse(start.isBetween(start, end));
        assertFalse(end.isBetween(start, end));
    }

    /**
     * Tests {@link MythDateTime#compareTo(MythDateTime)}.
     */
    @Test
    public void compareTo_ordersCorrectly() {
        MythDateTime dt1 = MythDateTime.parse("2025-01-01-00-00-00");
        MythDateTime dt2 = MythDateTime.parse("2025-01-02-00-00-00");

        assertTrue(dt1.compareTo(dt2) < 0);
        assertTrue(dt2.compareTo(dt1) > 0);
        assertEquals(0, dt1.compareTo(dt1));
    }

    /**
     * Tests human-readable output of {@link MythDateTime#toString()}.
     */
    @Test
    public void toString_formatsHumanReadable() {
        MythDateTime dt = MythDateTime.parse("2025-05-20-14-30-00");
        assertNotNull(dt.toString());
        assertTrue(dt.toString().contains("2025"));
    }
}
