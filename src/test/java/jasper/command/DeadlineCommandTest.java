package jasper.command;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import jasper.JasperException;

public class DeadlineCommandTest {
    @Test
    public void constructor_validArguments_success() {
        try {
            DeadlineCommand cmd = new DeadlineCommand("return book /by 2017-10-26 12:30");
            assertNotNull(cmd);
        } catch (JasperException e) {
            fail();
        }
    }

    @Test
    public void constructor_missingTask_exceptionThrown() {
        assertThrows(JasperException.class, () -> new DeadlineCommand(" /by 2018-07-12 23:56"));
    }

    @Test
    public void constructor_missingByKeyword_exceptionThrown() {
        assertThrows(JasperException.class, () -> new DeadlineCommand("return book to library"));
    }

    @Test
    public void constructor_taskContainsByKeyword_success() {
        assertDoesNotThrow(() -> new DeadlineCommand("return /by book /by 2019-03-25 17:45"));
    }

    @Test
    public void constructor_missingDateTime_exceptionThrown() {
        assertThrows(JasperException.class, () -> new DeadlineCommand("return book /by "));
    }

    @Test
    public void constructor_invalidDateTimeFormat_exceptionThrown() {
        assertThrows(JasperException.class, () -> new DeadlineCommand("return book /by 26-07-2025 16:30"));
        assertThrows(JasperException.class, () -> new DeadlineCommand("return book /by 2025-07-26 5pm"));
    }
}
