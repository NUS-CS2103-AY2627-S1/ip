package magnus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests the success and error response factories.
 */
public class MagnusResponseTest {
    @Test
    public void createSuccess_message_returnsNonErrorResponse() {
        MagnusResponse response = MagnusResponse.createSuccess("Task added");

        assertEquals("Task added", response.message());
        assertFalse(response.isError());
    }

    @Test
    public void createError_message_returnsErrorResponse() {
        MagnusResponse response = MagnusResponse.createError("Invalid command");

        assertEquals("Invalid command", response.message());
        assertTrue(response.isError());
    }
}
