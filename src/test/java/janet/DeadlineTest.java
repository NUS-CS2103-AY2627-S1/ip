package janet;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.DateTimeException;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class DeadlineTest {

    @Test
    public void deadlineDate_invalidDate_exceptionThrown() {
        assertThrows(DateTimeException.class, () -> new Deadline(
                false,
                "Submit report",
                LocalDate.of(2026, 2, 30)
        ));
    }
}
