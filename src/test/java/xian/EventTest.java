package xian;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;


public class EventTest {

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm");

    @Test
    public void toString_notDone_correctFormat() {
        LocalDateTime from = LocalDateTime.parse("21/3/2021 1200", DATE_TIME_FORMAT);
        LocalDateTime to = LocalDateTime.parse("21/3/2021 1800", DATE_TIME_FORMAT);
        Event event = new Event("partyyy", from, to);
        assertEquals("[E][ ] partyyy (from: 21/3/2021 1200 to: 21/3/2021 1800)", event.toString());
    }

    @Test
    public void toString_marked_correctFormat() {
        LocalDateTime from = LocalDateTime.parse("21/3/2021 1200", DATE_TIME_FORMAT);
        LocalDateTime to = LocalDateTime.parse("21/3/2021 1800", DATE_TIME_FORMAT);
        Event event = new Event("partyyy", from, to);
        event.mark();
        assertEquals("[E][X] partyyy (from: 21/3/2021 1200 to: 21/3/2021 1800)", event.toString());
    }

    @Test
    public void getFrom_returnsCorrectDateTime() {
        LocalDateTime from = LocalDateTime.parse("21/3/2021 1200", DATE_TIME_FORMAT);
        LocalDateTime to = LocalDateTime.parse("21/3/2021 1800", DATE_TIME_FORMAT);
        Event event = new Event("partyyy", from, to);
        assertEquals(from, event.getFrom());
    }

    @Test
    public void getTo_returnsCorrectDateTime() {
        LocalDateTime from = LocalDateTime.parse("21/3/2021 1200", DATE_TIME_FORMAT);
        LocalDateTime to = LocalDateTime.parse("21/3/2021 1800", DATE_TIME_FORMAT);
        Event event = new Event("partyyy", from, to);
        assertEquals(to, event.getTo());
    }
}
