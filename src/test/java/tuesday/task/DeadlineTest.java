package tuesday.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

public class DeadlineTest {
    @Test
    public void testStringConversion() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 8),
                LocalTime.of(14, 0));

        assertEquals("[D][ ] return book (by: Oct 08 2019 14:00)", deadline.toString());

        deadline.mark();
        assertEquals("[D][X] return book (by: Oct 08 2019 14:00)", deadline.toString());

        Deadline dateOnlyDeadline = new Deadline("read book", LocalDate.of(2019, 10, 8), null);
        assertEquals("[D][ ] read book (by: Oct 08 2019)", dateOnlyDeadline.toString());

        Deadline timeOnlyDeadline = new Deadline("call Mum", null, LocalTime.of(14, 0));
        assertEquals("[D][ ] call Mum (by: 14:00)", timeOnlyDeadline.toString());
    }
}
