package goop.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests event schedule validation without relying on an input interface.
 */
class EventTest {
    @Test
    void constructor_validSchedules_preservesDisplayText() {
        String[][] schedules = {
            {"29/2/2024 2300", "2024-03-01 0100"},
            {"2026-09-15", "16/9/2026"},
            {"2pm", "16:00"}, {"12am", "12pm"}, {"2:30 pm", "1600"},
            {"Mon 2pm", "4pm"}, {"start", "end"}
        };
        for (String[] schedule : schedules) {
            Event event = new Event("task", schedule[0], schedule[1]);
            assertEquals(schedule[0], event.getFrom());
            assertEquals(schedule[1], event.getTo());
        }
    }

    @Test
    void constructor_invalidSchedules_rejectsMissingAndImpossibleValues() {
        String[][] schedules = {
            {null, "end"}, {"start", null}, {" ", "end"}, {"start", " "},
            {"29/2/2025", "1/3/2025"}, {"2026-13-01", "2026-13-02"},
            {"2026-09-15 2400", "2026-09-16 0100"}, {"13pm", "14pm"},
            {"14:60", "16:00"}, {"23:00", "01:00"}, {"end", "end"}
        };
        for (String[] schedule : schedules) {
            assertThrows(IllegalArgumentException.class, () ->
                    new Event("task", schedule[0], schedule[1]));
        }
    }
}
