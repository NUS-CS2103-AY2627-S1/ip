package sage.task;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

class TaskDateTimeTest {
    @Test
    void validate_bareNumbersAndSymbols_rejectsAmbiguousDates() {
        for (String value : List.of("0", "12345", "20260918", "-10", "+2026", "1 2 3",
                "999999999999999999999999", "25:00", "???", "１２３", "١٢٣")) {
            assertThrows(IllegalArgumentException.class, () -> TaskDateTime.validate(value), value);
        }
    }

    @Test
    void parse_yearOutsideSupportedRange_returnsNullAndValidationRejectsIt() {
        for (String value : List.of("0000-01-01", "1/1/0000", "-0001-01-01", "+10000-01-01",
                "0000-01-01T12:30:00")) {
            assertNull(TaskDateTime.parse(value), value);
            assertThrows(IllegalArgumentException.class, () -> TaskDateTime.validate(value), value);
        }
        assertEquals(LocalDateTime.of(1, 1, 1, 0, 0), TaskDateTime.parse("0001-01-01"));
        assertEquals(LocalDateTime.of(9999, 12, 31, 0, 0), TaskDateTime.parse("9999-12-31"));
    }

    @Test
    void validate_invalidTwelveHourClock_rejectsImpossibleTimeLabels() {
        for (String value : List.of("0am", "13pm", "25pm", "4:60pm", "4:5pm", "-1pm",
                "Monday 99pm", "999999999999999999999pm")) {
            assertThrows(IllegalArgumentException.class, () -> TaskDateTime.validate(value), value);
        }
    }

    @Test
    void validate_validLabelsAndBoundaryClockTimes_preservesDescriptiveTimes() {
        for (String value : List.of("Sunday", "tomorrow", "Mon 2pm", "4pm", "12am", "12:59 PM",
                "1:00am", "2 days from now", "明天")) {
            assertDoesNotThrow(() -> TaskDateTime.validate(value), value);
        }
    }

    @Test
    void parse_supportedDateTimes_preservesEveryInputFormat() {
        LocalDateTime expected = LocalDateTime.of(2025, 2, 14, 9, 30);
        for (String input : List.of("14/2/2025 0930", "14/2/2025 09:30", "2025-02-14 0930",
                "2025-02-14 09:30", "2025-02-14T09:30:00", " 2025-02-14 09:30 ")) {
            assertEquals(expected, Deadline.parseDateTime(input), input);
            assertEquals(expected, Event.parseDateTime(input), input);
        }
    }

    @Test
    void parse_dateOnly_usesMidnight() {
        LocalDateTime expected = LocalDateTime.of(2025, 2, 14, 0, 0);
        assertEquals(expected, TaskDateTime.parse("14/2/2025"));
        assertEquals(expected, TaskDateTime.parse("2025-02-14"));
    }

    @Test
    void parse_repeatedSpacesAndTabsBetweenDateAndTime_preservesTheTime() {
        LocalDateTime expected = LocalDateTime.of(2026, 9, 18, 14, 30);
        for (String value : List.of("2026-09-18   1430", "2026-09-18\t14:30",
                "18/9/2026 \t 1430", " 18/9/2026 \t 14:30  ")) {
            assertEquals(expected, TaskDateTime.parse(value), value);
            TaskDateTime.validate(value);
        }
        assertNull(TaskDateTime.parse("2026-09-18 \t 24:00"));
        assertThrows(IllegalArgumentException.class, () -> TaskDateTime.validate("2026-09-18 \t 24:00"));
    }

    @Test
    void parse_unrecognizedOrEmptyInput_returnsNull() {
        assertNull(TaskDateTime.parse(null));
        for (String input : List.of("", "   ", "Sunday", "2025-13-14", "2025-02-14 25:00")) {
            assertNull(TaskDateTime.parse(input), input);
        }
    }

    @Test
    void toString_unparsedAndMissingTimes_preservesFallbackText() {
        assertEquals("[D][ ] read (by: Sunday)", new Deadline("read", " Sunday ").toString());
        assertEquals("[E][ ] meet (from: Mon 2pm to: 4pm)",
                new Event("meet", " Mon 2pm ", "4pm").toString());
        assertEquals("[D][ ] read (by: )", new Deadline("read", (String) null).toString());
        assertEquals("[E][ ] meet (from:  to: )", new Event("meet", (String) null, null).toString());
    }

    @Test
    void toString_midnightEvent_omitsTime() {
        assertEquals("[E][ ] meet (from: Feb 14 2025 to: Feb 15 2025)",
                new Event("meet", "2025-02-14", "2025-02-15").toString());
    }

    @Test
    void parse_invalidDatesAndTimes_doesNotRepairOrDiscardInvalidFields() {
        assertThrows(IllegalArgumentException.class, () -> TaskDateTime.validate(null));
        assertThrows(IllegalArgumentException.class, () -> TaskDateTime.validate(" "));
        for (String value : List.of("2026-02-30", "29/2/2025", "2026-04-31", "2026-09-18 24:00",
                "2026-09-18 25:00", "18/9/2026 2460", "2026-09-18T24:00:00")) {
            assertNull(TaskDateTime.parse(value), value);
            assertThrows(IllegalArgumentException.class, () -> TaskDateTime.validate(value), value);
        }
        assertEquals(LocalDateTime.of(2024, 2, 29, 0, 0), TaskDateTime.parse("29/2/2024"));
        assertEquals(LocalDateTime.of(2026, 9, 18, 14, 30, 45), TaskDateTime.parse("2026-09-18T14:30:45"));
    }

    @Test
    void format_nonEnglishSystemLocale_keepsEnglishDates() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.CHINESE);
            assertEquals("[D][ ] read (by: Sep 18 2026)", new Deadline("read", "2026-09-18").toString());
        } finally {
            Locale.setDefault(original);
        }
    }
}
