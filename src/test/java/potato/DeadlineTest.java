package potato.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Contains unit tests for testing the behavior of the {@link Deadline} class.
 */
public class DeadlineTest {

    /**
     * Tests that string representation formats a valid ISO date into 'MMM dd yyyy'.
     */
    @Test
    public void toStringValidDateFormattedCorrectly() {
        Deadline deadline = new Deadline("return book", "2026-09-15");
        assertEquals("[D][ ] return book (by: Sept 15 2026)", deadline.toString());
    }

    /**
     * Tests that compact 24-hour date-time input is formatted into a readable date and time.
     */
    @Test
    public void toStringCompactDateTimeFormattedCorrectly() {
        Deadline deadline = new Deadline("submit report", "2026-06-15 1800");
        assertEquals("[D][ ] submit report (by: Jun 15 2026, 18:00)", deadline.toString());
    }

    /**
     * Tests that colon-separated 24-hour date-time input is formatted into a readable date and time.
     */
    @Test
    public void toStringColonDateTimeFormattedCorrectly() {
        Deadline deadline = new Deadline("submit report", "2026-06-15 18:00");
        assertEquals("[D][ ] submit report (by: Jun 15 2026, 18:00)", deadline.toString());
    }

    /**
     * Tests that {@code getBy()} returns the exact raw deadline string passed to the constructor.
     */
    @Test
    public void getByValidInputReturnsByString() {
        Deadline deadline = new Deadline("return book", "2026-09-15");
        assertEquals("2026-09-15", deadline.getBy());
    }

    /**
     * Tests that deadline tasks show the correct status before and after being marked done.
     */
    @Test
    public void testStringConversion() {
        Deadline deadline = new Deadline("submit recipe", "2026-09-15");
        assertEquals("[D][ ] submit recipe (by: Sept 15 2026)", deadline.toString());

        deadline.markAsDone();
        assertEquals("[D][X] submit recipe (by: Sept 15 2026)", deadline.toString());
    }

    /**
     * Tests that deadline tasks are converted into the correct save file format.
     */
    @Test
    public void testFileFormat() {
        Deadline deadline = new Deadline("submit recipe", "2026-09-15");
        assertEquals("D | 0 | submit recipe | 2026-09-15", deadline.toFileFormat());

        deadline.markAsDone();
        assertEquals("D | 1 | submit recipe | 2026-09-15", deadline.toFileFormat());
    }
}
