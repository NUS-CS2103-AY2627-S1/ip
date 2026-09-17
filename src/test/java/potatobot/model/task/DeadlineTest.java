package potatobot.model.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import potatobot.exception.PotatoBotException;

public class DeadlineTest {
    @Test
    public void constructor_leapDay_acceptsOnlyLeapYears() throws PotatoBotException {
        assertEquals("report (Deadline, by: Feb 29 2024)", new Deadline("report", "2024-02-29").toString());
        assertThrows(PotatoBotException.class, () -> new Deadline("report", "2025-02-29"));
    }

    @Test
    public void constructor_isoDate_formattedDeadlineCreated() throws PotatoBotException {
        Deadline deadline = new Deadline("submit report", "2026-08-31");

        assertEquals(
                "submit report (Deadline, by: Aug 31 2026)",
                deadline.toString());
    }

    @Test
    public void constructor_storedDisplayDate_formattedDeadlineCreated()
            throws PotatoBotException {
        Deadline deadline = new Deadline("submit report", "Aug 31 2026");

        assertEquals(
                "submit report (Deadline, by: Aug 31 2026)",
                deadline.toString());
    }

    @Test
    public void constructor_invalidDate_exceptionThrown() {
        PotatoBotException exception = assertThrows(
                PotatoBotException.class,
                () -> new Deadline("submit report", "2026-02-30"));

        assertEquals(
                "Use the date format YYYY-MM-DD, for example 2019-12-02.",
                exception.getMessage());
    }
}
