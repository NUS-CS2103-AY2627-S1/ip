package quu.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.format.DateTimeParseException;
import java.util.Map;

import org.junit.jupiter.api.Test;

import quu.exception.InvalidDateException;
import quu.exception.MissingArgumentException;
import quu.exception.QuuException;
import quu.exception.UnsupportedEditException;

/**
 * Tests {@link Deadline}'s date handling: friendly rendering, ISO storage, and rejection of
 * anything that is not a {@code yyyy-mm-dd} date.
 */
public class DeadlineTest {

    @Test
    public void toString_rendersDateAsMonthDayYear() {
        Deadline deadline = new Deadline("return book", "2026-06-06");
        assertEquals("[D][ ] return book (by: Jun 6 2026)", deadline.toString());
    }

    @Test
    public void toFileString_keepsTheIsoDate() {
        Deadline deadline = new Deadline("return book", "2026-06-06");
        assertEquals("D | 0 | return book /by 2026-06-06", deadline.toFileString());
    }

    @Test
    public void constructor_nonIsoDate_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> new Deadline("return book", "Sunday"));
    }

    @Test
    public void toFileString_thenFromFileString_roundTripsTask()
            throws MissingArgumentException, InvalidDateException {
        Deadline original = new Deadline("return book", "2026-06-06");
        Deadline restored = Deadline.fromFileString(original.toFileString().split("\\s*\\|\\s*", 3));
        assertEquals(original.toString(), restored.toString());
    }

    @Test
    public void fromFileString_missingByClause_throwsMissingArgument() {
        String[] fields = {"D", "0", "return book"};
        assertThrows(MissingArgumentException.class, () -> Deadline.fromFileString(fields));
    }

    @Test
    public void fromFileString_unparseableDate_throwsInvalidDate() {
        String[] fields = {"D", "0", "return book /by Sunday"};
        assertThrows(InvalidDateException.class, () -> Deadline.fromFileString(fields));
    }

    @Test
    public void withEdits_dueDateOnly_keepsTheDescription() throws QuuException {
        Deadline editedDeadline = new Deadline("return book", "2026-06-06").withEdits(Map.of("by", "2026-07-07"));
        assertEquals("[D][ ] return book (by: Jul 7 2026)", editedDeadline.toString());
    }

    @Test
    public void withEdits_descriptionOnly_keepsTheDueDate() throws QuuException {
        Deadline editedDeadline = new Deadline("return book", "2026-06-06")
                .withEdits(Map.of(Task.KEY_DESCRIPTION, "return two books"));
        assertEquals("[D][ ] return two books (by: Jun 6 2026)", editedDeadline.toString());
    }

    @Test
    public void withEdits_flagADeadlineDoesNotHave_throwsUnsupportedEdit() {
        assertThrows(UnsupportedEditException.class, () ->
                new Deadline("return book", "2026-06-06").withEdits(Map.of("to", "2026-07-07")));
    }

    @Test
    public void withEdits_dueDateThatIsNotADate_throwsInvalidDate() {
        assertThrows(InvalidDateException.class, () ->
                new Deadline("return book", "2026-06-06").withEdits(Map.of("by", "next tuesday")));
    }

}
