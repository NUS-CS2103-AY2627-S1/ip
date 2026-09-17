package rocky.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import rocky.RockyException;

/** Tests for creating a {@link Deadline} from a typed date, and for how it is displayed. */
public class DeadlineTest {
    private static final String INVALID_DATE_MESSAGE = "Please give the date as yyyy-mm-dd, e.g. 2019-10-15.";

    @Test
    public void constructor_isoDate_detailsStoredAndNotDone() throws RockyException {
        Deadline deadline = new Deadline("return book", "2019-12-01");

        assertEquals("return book", deadline.getDescription());
        assertEquals("2019-12-01", deadline.getDate());
        assertFalse(deadline.isDone());
    }

    @Test
    public void constructor_leapDay_accepted() throws RockyException {
        assertEquals("2020-02-29", new Deadline("submit essay", "2020-02-29").getDate());
    }

    @Test
    public void constructor_nonIsoOrImpossibleDate_exceptionThrown() {
        String[] invalidDates = {"tomorrow", "2019/12/01", "01-12-2019", "2019-12-1", "2019-13-01",
                "2019-02-30", "2019-02-29", ""};

        for (String date : invalidDates) {
            RockyException e = assertThrows(RockyException.class, () -> new Deadline("return book", date),
                    "date \"" + date + "\" should be rejected");
            assertEquals(INVALID_DATE_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void toString_notDone_emptyCheckboxAndFormattedDate() throws RockyException {
        assertEquals("[D][ ] return book (by: Dec 01 2019)",
                new Deadline("return book", "2019-12-01").toString());
    }

    @Test
    public void toString_done_tickedCheckbox() throws RockyException {
        Deadline deadline = new Deadline("return book", "2019-12-01");
        deadline.mark();

        assertEquals("[D][x] return book (by: Dec 01 2019)", deadline.toString());
    }
}
