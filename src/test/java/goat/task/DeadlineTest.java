package goat.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests how a deadline renders its due date.
 *
 * The two renderings differ on purpose: the screen drops a midnight time, the
 * file always keeps one so a single pattern reads every saved line.
 */
public class DeadlineTest {

    @Test
    public void toString_withTime_showsFriendlyDateAndTime() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        assertEquals("[D][ ] return book (by: Dec 02 2019, 6:00pm)", deadline.toString());
    }

    @Test
    public void toString_atMidnight_showsDateOnly() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 0, 0));
        assertEquals("[D][ ] return book (by: Dec 02 2019)", deadline.toString());
    }

    @Test
    public void toString_done_showsX() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        deadline.markAsDone();
        assertEquals("[D][X] return book (by: Dec 02 2019, 6:00pm)", deadline.toString());
    }

    @Test
    public void toFileFormat_withTime_writesTheTypedFormat() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        assertEquals("D | 0 | return book | 2019-12-02 1800", deadline.toFileFormat());
    }

    @Test
    public void toFileFormat_atMidnight_stillWritesATime() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 0, 0));
        assertEquals("D | 0 | return book | 2019-12-02 0000", deadline.toFileFormat());
    }

    @Test
    public void toFileFormat_done_writesOne() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        deadline.markAsDone();
        assertEquals("D | 1 | return book | 2019-12-02 1800", deadline.toFileFormat());
    }
}
