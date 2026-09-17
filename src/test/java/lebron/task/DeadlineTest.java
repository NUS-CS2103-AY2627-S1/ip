package lebron.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class DeadlineTest {
    private final LocalDateTime by = LocalDateTime.of(2019, 10, 15, 18, 0);

    @Test
    public void equals_sameDescriptionAndDate_returnsTrue() {
        assertEquals(new Deadline("return book", by), new Deadline("return book", by));
    }

    @Test
    public void equals_differentDate_returnsFalse() {
        Deadline other = new Deadline("return book", LocalDateTime.of(2019, 10, 20, 18, 0));
        assertNotEquals(new Deadline("return book", by), other);
    }

    @Test
    public void equals_todoWithSameDescription_returnsFalse() {
        assertNotEquals(new Deadline("return book", by), new Todo("return book"));
    }

    @Test
    public void toString_notDone_correctFormatWithHumanReadableDate() {
        Deadline deadline = new Deadline("return book", by);
        assertEquals("[D][ ] return book (by: Oct 15 2019, 6:00 PM)", deadline.toString());
    }

    @Test
    public void toString_done_correctFormat() {
        Deadline deadline = new Deadline("return book", by);
        deadline.setStatus(true);
        assertEquals("[D][X] return book (by: Oct 15 2019, 6:00 PM)", deadline.toString());
    }

    @Test
    public void reformat_notDone_correctSaveFormatWithIsoDate() {
        Deadline deadline = new Deadline("return book", by);
        assertEquals("D | 0 | return book | 2019-10-15T18:00", deadline.reformat());
    }
}
