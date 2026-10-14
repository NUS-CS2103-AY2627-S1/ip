package verity.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class DeadlineTest {

    @Test
    void getBy_returnsDateGivenToConstructor() {
        LocalDate by = LocalDate.of(2019, 10, 15);
        Deadline deadline = new Deadline("return book", by);

        assertEquals(by, deadline.getBy());
    }

    @Test
    void toString_singleDigitDay_dayIsZeroPadded() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 6, 6));

        assertEquals("[D][ ] return book (by: Jun 06 2019)", deadline.toString());
    }

    @Test
    void toString_matchesSpecExample() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));

        assertEquals("[D][ ] return book (by: " + deadline.getBy().format(Task.DATE_DISPLAY_FORMAT) + ")",
                deadline.toString());
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    void toString_done_showsXInsideTypeTag() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        deadline.markAsDone();

        assertEquals("[D][X] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    void toSaveFormat_savesDateInIsoFormat() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));

        assertEquals("D | 0 | return book | 2019-10-15", deadline.toSaveFormat());
    }

    @Test
    void toSaveFormat_done_savesDoneFlagAsOne() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        deadline.markAsDone();

        assertEquals("D | 1 | return book | 2019-10-15", deadline.toSaveFormat());
    }
}
