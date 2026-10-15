package jeff.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class DeadlineTest {
    @Test
    public void constructor_blankBy_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Deadline("return book", null));
        assertThrows(IllegalArgumentException.class, () -> new Deadline("return book", ""));
        assertThrows(IllegalArgumentException.class, () -> new Deadline("return book", "   "));
    }

    @Test
    public void constructor_malformedDate_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Deadline("return book", "15/10/2019"));
        assertThrows(IllegalArgumentException.class, () -> new Deadline("return book", "2019-13-40"));
        assertThrows(IllegalArgumentException.class, () -> new Deadline("return book", "not a date"));
    }

    @Test
    public void constructor_blankDescription_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Deadline("", "2019-10-15"));
    }

    @Test
    public void toString_validDate_printsInMmmDdYyyyFormat() {
        Deadline deadline = new Deadline("return book", "2019-10-15");
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void toString_dateWithLeadingOrTrailingWhitespace_isTrimmedBeforeParsing() {
        Deadline deadline = new Deadline("return book", "  2019-10-15  ");
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void toSaveFormat_encodesDateInIsoFormat() {
        Deadline deadline = new Deadline("return book", "2019-10-15");
        assertEquals("D | 0 | return book | 2019-10-15", deadline.toSaveFormat());
    }

    @Test
    public void getChronologicalDate_returnsByDate() {
        Deadline deadline = new Deadline("return book", "2019-10-15");
        assertEquals(LocalDate.of(2019, 10, 15), deadline.getChronologicalDate().orElseThrow());
    }
}
