package larper.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EventTest {
    private static final String TODAY_PROPERTY = "larper.today";

    @BeforeEach
    public void setUp() {
        System.setProperty(TODAY_PROPERTY, "2026-08-23");
    }

    @AfterEach
    public void tearDown() {
        System.clearProperty(TODAY_PROPERTY);
    }

    @Test
    public void toStringAndFileString_eventWithTimes_expectedFormat() {
        Event event = new Event("meeting", LocalDate.of(2026, 8, 24), "1400",
                LocalDate.of(2026, 8, 25), "1600");

        assertEquals("E", event.getTypeIcon());
        assertEquals("[E][ ] meeting (from: Aug 24 2026 1400 to: Aug 25 2026 1600)", event.toString());
        assertEquals("E | 0 | meeting | 2026-08-24 | 1400 | 2026-08-25 | 1600", event.toFileString());
    }

    @Test
    public void toStringAndFileString_eventWithoutTimes_expectedFormat() {
        Event event = new Event("camp", LocalDate.of(2026, 8, 24), "no time",
                LocalDate.of(2026, 8, 25), "no time");

        assertEquals("[E][ ] camp (from: Aug 24 2026 to: Aug 25 2026)", event.toString());
        assertEquals("E | 0 | camp | 2026-08-24 | no time | 2026-08-25 | no time",
                event.toFileString());
    }

    @Test
    public void stringConstructor_isoDates_expectedFormat() {
        Event event = new Event("meeting", "2026-08-24", "1400", "2026-08-25", "1600");

        assertEquals("[E][ ] meeting (from: Aug 24 2026 1400 to: Aug 25 2026 1600)", event.toString());
        assertEquals("E | 0 | meeting | 2026-08-24 | 1400 | 2026-08-25 | 1600", event.toFileString());
    }

    @Test
    public void legacyStringConstructor_stringDatesAndTimes_expectedFormat() {
        Event event = new Event("project meeting", "8 aug 2pm", "8 aug 4pm");

        assertEquals("[E][ ] project meeting (from: Aug 08 2026 1400 to: Aug 08 2026 1600)",
                event.toString());
        assertEquals("E | 0 | project meeting | 2026-08-08 | 1400 | 2026-08-08 | 1600",
                event.toFileString());
    }
}
