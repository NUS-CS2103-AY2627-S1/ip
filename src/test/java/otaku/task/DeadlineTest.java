package otaku.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests deadline-specific date access and display formatting. */
public class DeadlineTest {
    @Test
    public void toString_validDeadline_formatsTypeStatusAndDate() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 1, 5));

        assertEquals("[D][ ] return book (by: Jan 05 2026)", deadline.toString());
    }

    @Test
    public void getBy_validDeadline_returnsOriginalDate() {
        LocalDate date = LocalDate.of(2028, 2, 29);
        Deadline deadline = new Deadline("submit report", date);

        assertEquals(date, deadline.getBy());
    }
}
