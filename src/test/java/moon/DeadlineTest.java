package moon;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests the user-facing and persistence formats of deadline tasks.
 */
public class DeadlineTest {
    /** Verifies that a deadline formats its date and completion status correctly. */
    @Test
    void deadlineFormats_datesAndStatusCorrectly() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2));

        assertEquals("[D][ ] return book (by: Dec 02 2019)", deadline.toString());
        assertEquals("D | 0 | return book | 2019-12-02", deadline.toSaveFormat());

        deadline.markAsDone();
        assertEquals("[D][X] return book (by: Dec 02 2019)", deadline.toString());
        assertEquals("D | 1 | return book | 2019-12-02", deadline.toSaveFormat());
    }
}
