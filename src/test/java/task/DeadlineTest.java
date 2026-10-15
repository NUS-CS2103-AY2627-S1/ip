package task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import org.junit.jupiter.api.Test;

public class DeadlineTest {
    private static final DateTimeFormatter FLEXIBLE_FORMATTER = new DateTimeFormatterBuilder().appendPattern("[dd/MM/yyyy][dd-MM-yyyy]")
            .appendPattern("[ HHmm]")
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 23)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 59)
            .toFormatter();
    @Test
    public void DeadlinetoStringTest(){
        Deadline temp = new Deadline("date Sunny", null, "tomorrow");
        assertEquals("[D][ ] date Sunny (by: tomorrow)", temp.toString());
    }

    @Test
    public void DeadlinegetFileFormat(){
        Deadline temp = new Deadline("date Sunny", null, "tomorrow");
        assertEquals(" | tomorrow | 0", temp.getFileFormat());
    }

    @Test
    public void DeadlinetoStringActualDateTest(){
        Deadline temp = new Deadline("date Sunny", LocalDateTime.parse("25/12/2026", FLEXIBLE_FORMATTER), "");
        assertEquals("[D][ ] date Sunny (by: 25-12-2026 23:59)", temp.toString());
    }
}