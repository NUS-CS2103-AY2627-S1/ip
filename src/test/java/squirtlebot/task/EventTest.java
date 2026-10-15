package squirtlebot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import squirtlebot.TemporalPair;

/**
 * Tests the string representation of events with multiple possible schedules.
 */
public class EventTest {
    @Test
    public void toString_multipleDateSchedules_displaysAllSchedulesInOrder() {
        ArrayList<TemporalPair> schedules = new ArrayList<>();
        schedules.add(new TemporalPair(LocalDate.of(2026, 9, 13), LocalDate.of(2026, 9, 14)));
        schedules.add(new TemporalPair(LocalDate.of(2026, 9, 20), LocalDate.of(2026, 9, 21)));
        schedules.add(new TemporalPair(LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 2)));
        Event event = new Event("project meeting", schedules);

        String expected = String.join("\n",
                "[E] [ ] project meeting ",
                "\tPossible Schedules: ",
                "\t\tfrom: 2026-09-13 to: 2026-09-14",
                "\t\tfrom: 2026-09-20 to: 2026-09-21",
                "\t\tfrom: 2026-10-01 to: 2026-10-02");

        assertEquals(expected, event.toString());
    }

    @Test
    public void toString_multipleDateTimeSchedules_displaysDatesAndTimes() {
        ArrayList<TemporalPair> schedules = new ArrayList<>();
        schedules.add(new TemporalPair(
                LocalDateTime.of(2026, 9, 13, 9, 30),
                LocalDateTime.of(2026, 9, 13, 11, 0)));
        schedules.add(new TemporalPair(
                LocalDateTime.of(2026, 9, 14, 14, 15),
                LocalDateTime.of(2026, 9, 14, 16, 45)));
        Event event = new Event("project meeting", schedules);

        String expected = String.join("\n",
                "[E] [ ] project meeting ",
                "\tPossible Schedules: ",
                "\t\tfrom: 2026-09-13T09:30 to: 2026-09-13T11:00",
                "\t\tfrom: 2026-09-14T14:15 to: 2026-09-14T16:45");

        assertEquals(expected, event.toString());
    }
}
