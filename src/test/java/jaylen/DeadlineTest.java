package jaylen;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests deadline construction, typed dates, and display text. */
public class DeadlineTest {
    @Test
    public void constructor_dateOnly_storesStartOfDay() {
        Deadline deadline = new Deadline("return book", "2/12/2019");

        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), deadline.getBy());
        assertEquals("[D][ ] return book (by: Dec 02 2019)", deadline.toString());
    }

    @Test
    public void constructor_dateAndTime_storesExactTime() {
        Deadline deadline = new Deadline("return book", "2/12/2019 1800");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getBy());
        assertEquals("[D][ ] return book (by: Dec 02 2019 18:00)", deadline.toString());
    }
}
