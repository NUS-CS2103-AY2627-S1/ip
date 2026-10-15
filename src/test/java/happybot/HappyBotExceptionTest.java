package happybot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class HappyBotExceptionTest {
    @Test
    public void constructor_message_messageAvailableToCaller() {
        HappyBotException exception = new HappyBotException("Explain the invalid command.");

        assertEquals("Explain the invalid command.", exception.getMessage());
    }
}
