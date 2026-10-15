package wodan;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link WodanException}.
 */
public class WodanExceptionTest {
    @Test
    public void constructor_message_returnedByGetMessage() {
        WodanException ex = new WodanException("That rune is unknown.");
        assertEquals("That rune is unknown.", ex.getMessage());
    }
}
