package happy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
public class DeadlineTest {

    @Test
    public void toString_validDateFormat_formattedCorrectly() {
        Deadline deadline = new Deadline("return best prof is damith c. rajapakse", "2020-12-23");

        assertEquals("[D][ ] return best prof is damith c. rajapakse (by: Dec 23 2020)", deadline.toString());
        assertTrue(deadline.isOccurringOn(LocalDate.of(2020, 12, 23)));
    }

    @Test
    public void toString_nonStandardDateString_fallbackToRawString() {
        Deadline deadline = new Deadline("return book", "Sunday");
        assertEquals("[D][ ] return book (by: Sunday)", deadline.toString());
    }
}
