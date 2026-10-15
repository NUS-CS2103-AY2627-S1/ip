package task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import org.junit.jupiter.api.Test;

public class EventTest {
    private static final DateTimeFormatter FLEXIBLE_FORMATTER = new DateTimeFormatterBuilder().appendPattern("[dd/MM/yyyy][dd-MM-yyyy]")
            .appendPattern("[ HHmm]")
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 23)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 59)
            .toFormatter();

    @Test
    public void EventtoStringTest() {
        Event temp = new Event("date Sunny", null, null, "start", "married");
        assertEquals("[E][ ] date Sunny(From: start to: married)", temp.toString());
    }

    @Test
    public void EventgetFileFormatTest() {
        Event temp = new Event("date Sunny", null, null, "start", "married");
        assertEquals(" | start | married | 0", temp.getFileFormat());
    }

    @Test
    public void EventtoStringActualDateTest() {
        Event temp = new Event("date Sunny", LocalDateTime.parse("25/12/2026 1323", FLEXIBLE_FORMATTER),
                                                  LocalDateTime.parse("30/12/2026", FLEXIBLE_FORMATTER), "", "");
        assertEquals("[E][ ] date Sunny(From: 25-12-2026 13:23 to: 30-12-2026 23:59)", temp.toString());
    }
}
