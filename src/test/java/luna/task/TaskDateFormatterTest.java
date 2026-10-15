package luna.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import luna.exception.LunaException;

public class TaskDateFormatterTest {

    @Test
    void parse_validDate_returnsDate() throws LunaException {
        assertEquals(LocalDate.of(2019, 10, 15), TaskDateFormatter.parse("2019-10-15"));
    }

    @Test
    void parse_invalidDate_throwsException() {
        LunaException exception = assertThrows(LunaException.class, () ->
                TaskDateFormatter.parse("15-10-2019"));

        assertEquals("Invalid date format. Use yyyy-MM-dd (e.g., 2019-10-15)",
                exception.getMessage());
    }

    @Test
    void formatForDisplay_validDate_returnsDisplayFormat() {
        LocalDate date = LocalDate.of(2019, 10, 15);

        assertEquals("Oct 15 2019", TaskDateFormatter.formatForDisplay(date));
    }

    @Test
    void formatForStorage_validDate_returnsStorageFormat() {
        LocalDate date = LocalDate.of(2019, 10, 15);

        assertEquals("2019-10-15", TaskDateFormatter.formatForStorage(date));
    }
}
