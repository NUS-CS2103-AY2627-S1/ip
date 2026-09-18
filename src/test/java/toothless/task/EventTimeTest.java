package toothless.task;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import toothless.exception.ToothlessException;

/**
 * Tests event time syntax and ordering without depending on the current clock.
 */
public class EventTimeTest {
    @Test
    public void validate_supportedClockFormats_acceptsIncreasingTimes() {
        String[][] endpoints = {
            {"12am", "12pm"}, {"2:30PM", "14:31"}, {"00:00", "23:59"},
            {"Monday 2pm", "Monday 3pm"}, {"tomorrow", "tomorrow 2pm"},
            {"today 2pm", "today 3pm"}, {"2024-02-29 23:00", "2024-03-01 00:00"},
            {"31 December 2026 11pm", "1 January 2027 1am"},
            {"2026-09-21 14:00", "3pm"}, {"2pm", "21 September 2026 3pm"}
        };

        for (String[] pair : endpoints) {
            assertDoesNotThrow(() -> EventTime.validate(pair[0], pair[1]), pair[0] + " to " + pair[1]);
        }
    }

    @Test
    public void validate_equalOrReversedTimes_rejectsNonIncreasingEvents() {
        String[][] endpoints = {
            {"2pm", "2pm"}, {"3pm", "2pm"}, {"2026-09-22 1am", "2026-09-21 11pm"},
            {"2026-09-21 3pm", "2pm"}, {"tomorrow 2pm", "tomorrow"}
        };

        for (String[] pair : endpoints) {
            ToothlessException error = assertThrows(ToothlessException.class, () ->
                    EventTime.validate(pair[0], pair[1]), pair[0] + " to " + pair[1]);
            assertEquals("The event's start must be before its end.\n"
                    + "Try: event meeting /from 2pm /to 3pm", error.getMessage());
        }
    }

    @Test
    public void validate_incompatibleDayReferences_rejectsAmbiguousDates() {
        String[][] endpoints = {
            {"Monday 2pm", "Tuesday 3pm"}, {"today 2pm", "tomorrow 3pm"},
            {"Monday 2pm", "2026-09-21 3pm"},
            {"2026-09-21 2pm", "Monday 3pm"}
        };

        for (String[] pair : endpoints) {
            ToothlessException error = assertThrows(ToothlessException.class, () ->
                    EventTime.validate(pair[0], pair[1]), pair[0] + " to " + pair[1]);
            assertEquals("Toothless needs comparable event dates.\n"
                    + "Use the same weekday or full dates for both endpoints.", error.getMessage());
        }
    }

    @Test
    public void validate_invalidCalendarAndClockValues_rejectsBothEndpoints() {
        String[] invalidValues = {
            "", "2pm!", "0pm", "13pm", "2:60pm", "24:00", "12:60", "2 PM today",
            "2025-02-29 2pm", "30 February 2024 2pm", "Monday", "星期一 2pm", "2pm".repeat(26)
        };

        for (String value : invalidValues) {
            ToothlessException startError = assertThrows(ToothlessException.class, () ->
                    EventTime.validate(value, "3pm"), "start: " + value);
            ToothlessException endError = assertThrows(ToothlessException.class, () ->
                    EventTime.validate("1pm", value), "end: " + value);
            assertEquals(startError.getMessage(), endError.getMessage());
            assertEquals("That event time made Toothless tilt his head.\n"
                    + "Use 2pm, 14:30, or 21 September 2026 2pm with a real date and time.",
                    startError.getMessage());
        }
    }
}
