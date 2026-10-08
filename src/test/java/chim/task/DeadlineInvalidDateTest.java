package chim.task;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

public class DeadlineInvalidDateTest {

    @Test
    public void localDateParse_nonExistentDate_throwsException() {
        assertThrows(DateTimeParseException.class, () -> LocalDate.parse("2020-02-30"));
    }
}
