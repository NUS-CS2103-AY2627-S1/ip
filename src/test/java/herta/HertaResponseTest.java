package herta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests that a response preserves each result property independently.
 */
class HertaResponseTest {

    @Test
    void responseProperties_nonExitError_preservesMessageFlagAndCategory() {
        HertaResponse response = new HertaResponse("invalid input", false, ResponseCategory.ERROR);

        assertEquals("invalid input", response.getMessage());
        assertFalse(response.isExitRequested());
        assertEquals(ResponseCategory.ERROR, response.getResponseCategory());
    }

    @Test
    void responseProperties_exitResponse_preservesMessageFlagAndCategory() {
        HertaResponse response = new HertaResponse("goodbye", true, ResponseCategory.EXIT);

        assertEquals("goodbye", response.getMessage());
        assertTrue(response.isExitRequested());
        assertEquals(ResponseCategory.EXIT, response.getResponseCategory());
    }
}
