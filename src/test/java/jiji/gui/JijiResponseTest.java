package jiji.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link JijiResponse}.
 */
public class JijiResponseTest {

    @Test
    public void constructor_successResponse_storesMessageAndFalseError() {
        JijiResponse response = new JijiResponse("Task added successfully.", false);
        assertEquals("Task added successfully.", response.getMessage());
        assertFalse(response.isError());
    }

    @Test
    public void constructor_errorResponse_storesMessageAndTrueError() {
        JijiResponse response = new JijiResponse("Invalid command!", true);
        assertEquals("Invalid command!", response.getMessage());
        assertTrue(response.isError());
    }
}
