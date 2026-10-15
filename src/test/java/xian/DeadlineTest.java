package xian;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

public class DeadlineTest {

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm");

    @Test
    public void toString_notDone_correctFormat() {
        LocalDateTime by = LocalDateTime.parse("2/12/2019 1800", DATE_TIME_FORMAT);
        Deadline deadline = new Deadline("project work", by);
        assertEquals("[D][ ] project work (by: 2/12/2019 1800)", deadline.toString());
    }

    @Test
    public void toString_marked_correctFormat() {
        LocalDateTime by = LocalDateTime.parse("2/12/2019 1800", DATE_TIME_FORMAT);
        Deadline deadline = new Deadline("project work", by);
        deadline.mark();
        assertEquals("[D][X] project work (by: 2/12/2019 1800)", deadline.toString());
    }

    @Test
    public void getByDate_returnsCorrectDateTime() {
        LocalDateTime by = LocalDateTime.parse("2/12/2019 1800", DATE_TIME_FORMAT);
        Deadline deadline = new Deadline("project work", by);
        assertEquals(by, deadline.getByDate());
    }

    @Test
    public void getDescription_returnsCorrectDescription() {
        LocalDateTime by = LocalDateTime.parse("2/12/2019 1800", DATE_TIME_FORMAT);
        Deadline deadline = new Deadline("project work", by);
        assertEquals("project work", deadline.getDescription());
    }
}
