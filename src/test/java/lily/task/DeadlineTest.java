package lily.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Deadline}. The interesting behaviour here is not the class's own logic
 * (it is a thin holder for a description and a date) but the two integration points its
 * overridden methods exercise: date formatting via {@link
 * lily.parser.DateTimeParser} (display vs. storage format) and description escaping via
 * {@link lily.storage.Storage#escapeField(String)} (so a description containing a
 * pipe character cannot corrupt the save-file format). Both are worth pinning down at
 * this integration point in addition to being tested directly in their own classes.
 */
public class DeadlineTest {

    @Test
    public void toString_dateAtMidnight_displaysDateOnly() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 10, 15, 0, 0));
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void toString_dateWithTime_displaysDateAndTime() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        assertEquals("[D][ ] return book (by: Dec 02 2019, 6:00PM)", deadline.toString());
    }

    @Test
    public void toString_explicitMidnight_displaysTime() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 10, 15, 0, 0), true);
        assertEquals("[D][ ] return book (by: Oct 15 2019, 12:00AM)", deadline.toString());
    }

    @Test
    public void toString_markedDone_showsXInBox() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 10, 15, 0, 0));
        deadline.markAsDone();
        assertEquals("[D][X] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void toFileString_usesFixedStorageDateFormatNotDisplayFormat() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        assertEquals("D | 0 | return book | 2019-12-02T18:00 | true", deadline.toFileString());
    }

    @Test
    public void toFileString_descriptionContainingPipe_pipeIsEscaped() {
        // Without escaping, a literal "|" in the description would be indistinguishable
        // from a field delimiter and corrupt the record on the next load.
        Deadline deadline = new Deadline("return book | pay fine", LocalDateTime.of(2019, 10, 15, 0, 0));
        assertEquals("D | 0 | return book \\| pay fine | 2019-10-15T00:00 | false", deadline.toFileString());
    }
}
