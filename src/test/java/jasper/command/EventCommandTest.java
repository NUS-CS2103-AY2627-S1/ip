package jasper.command;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import jasper.JasperException;

public class EventCommandTest {
    @Test
    public void constructor_validArguments_success() {
        try {
            EventCommand cmd = new EventCommand("Project meeting /from 2026-08-26 14:00 /to 2026-08-26 16:30");
            assertNotNull(cmd);
        } catch (JasperException e) {
            fail();
        }
    }

    @Test
    public void constructor_missingTask_exceptionThrown() {
        assertThrows(JasperException.class, () ->
                new EventCommand(" /from 2013-10-28 14:00 /to 2013-11-05 16:50"));
    }

    @Test
    public void constructor_missingFromKeyword_exceptionThrown() {
        assertThrows(JasperException.class, () -> new EventCommand("Project meeting /to 2012-10-03 07:30"));
    }

    @Test
    public void constructor_taskContainsFromKeyword_success() {
        try {
            EventCommand cmd = new EventCommand(
                    "Walk back /from work /from 2026-08-26 14:00 /to 2026-08-26 16:30"
            );
            assertNotNull(cmd);
        } catch (JasperException e) {
            fail();
        }
    }

    @Test
    public void constructor_missingFromDateTime_exceptionThrown() {
        assertThrows(JasperException.class, () ->
                new EventCommand("Project meeting /from /to 2026-08-26 16:30"));
    }

    @Test
    public void constructor_missingToKeyword_exceptionThrown() {
        assertThrows(JasperException.class, () -> new EventCommand("Project meeting /from 2026-08-26 14:00"));
    }

    @Test
    public void constructor_taskContainsToKeyword_success() {
        try {
            EventCommand cmd = new EventCommand(
                    "Deliver parcel /to John /from 2026-08-26 14:00 /to 2026-08-26 16:30"
            );
            assertNotNull(cmd);
        } catch (JasperException e) {
            fail();
        }
    }

    @Test
    public void constructor_missingToDateTime_exceptionThrown() {
        assertThrows(JasperException.class, () ->
                new EventCommand("Project meeting /from 2026-08-26 14:00 /to "));
    }

    @Test
    public void constructor_invalidDateTimeFormat_exceptionThrown() {
        assertThrows(JasperException.class, () ->
                new EventCommand("Project meeting /from 2025-07-26 11:30 /to 2025-08-26 4pm"));
        assertThrows(JasperException.class, () ->
                new EventCommand("Project meeting /from 2025-07-26 4pm /to 2025-08-26 19:30"));
        assertThrows(JasperException.class, () ->
                new EventCommand("Project meeting /from 26-07-2025 11:30 /to 2025-08-26 4pm"));
    }
}
