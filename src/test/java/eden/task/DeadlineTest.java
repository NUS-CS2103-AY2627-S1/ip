package eden.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests the display and persistence formats of typed deadline dates.
 */
public class DeadlineTest {
    /**
     * Verifies that a date is displayed readably but stored in ISO format.
     */
    @Test
    public void displayAndData_validDate_useFriendlyAndIsoFormats() {
        Deadline deadline = new Deadline(
                "return book", LocalDate.of(2019, 12, 2));

        assertEquals("[D][ ] return book (by: Dec 02 2019)", deadline.toString());
        assertEquals("D | 0 | return book | 2019-12-02", deadline.toDataString());

        deadline.mark();

        assertEquals("[D][X] return book (by: Dec 02 2019)", deadline.toString());
        assertEquals("D | 1 | return book | 2019-12-02", deadline.toDataString());
    }
}
