package jeryl.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class DeadlineTest {
    @Test
    public void toString_isoDate_printsInMmmDdYyyyFormat() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void toFileString_isoDate_encodesDateInIsoFormat() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        assertEquals("D | 0 | return book | 2019-10-15", deadline.toFileString());
    }

    @Test
    public void toString_singleDigitDayAndMonth_padsToTwoDigits() {
        Deadline deadline = new Deadline("pay bill", LocalDate.of(2019, 1, 2));
        assertEquals("[D][ ] pay bill (by: Jan 02 2019)", deadline.toString());
    }
}
