package fox.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import fox.exception.FoxException;

/** JUnit tests for strict, locale-independent Level-8 date handling. */
class FoxDateJUnitTest {
    @Test
    void parsesAndFormatsValidDate() throws FoxException {
        LocalDate date = FoxDate.parse("2026-12-02");

        assertEquals(LocalDate.of(2026, 12, 2), date);
        assertEquals("Dec 02 2026", FoxDate.format(date));
    }

    @Test
    void rejectsImpossibleAndWronglyFormattedDates() {
        assertThrows(FoxException.class, () -> FoxDate.parse("2026-02-30"));
        assertThrows(FoxException.class, () -> FoxDate.parse("2026/12/02"));
        FoxException exception = assertThrows(FoxException.class, () ->
                FoxDate.parse("2026-2-2"));
        assertEquals("☹ OOPS!!! Please enter a valid date in yyyy-MM-dd format.",
                exception.getMessage());
    }

    @Test
    void legacyDeadlineValuesRemainUnchanged() {
        Deadline deadline = new Deadline("submit", "Friday");

        assertEquals("Friday", deadline.getBy());
        assertEquals("[D][ ] submit (by: Friday)", deadline.toString());
    }
}
