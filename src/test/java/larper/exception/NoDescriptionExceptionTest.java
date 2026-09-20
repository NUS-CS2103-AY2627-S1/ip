package larper.exception;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class NoDescriptionExceptionTest {
    @Test
    public void getMessage_missingDescription_includesPersonalisedHelp() {
        String message = new NoDescriptionException().getMessage();

        assertTrue(message.contains("Insufficient lore"));
        assertTrue(message.contains("todo DESCRIPTION"));
        assertTrue(message.contains("deadline DESCRIPTION /by DATE TIME"));
        assertTrue(message.contains("event DESCRIPTION /from START_DATE START_TIME /to END_DATE END_TIME"));
    }
}
