package zaru.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests chatbot response construction and accessors. */
public class ResponseTest {
    /** Verifies that the convenience constructor creates a non-exit status response. */
    @Test
    public void constructor_textOnly_createsStatusResponse() {
        Response response = new Response("Done.");

        assertEquals("Done.", response.getText());
        assertEquals(Response.ResponseType.STATUS, response.getType());
        assertFalse(response.isExit());
    }

    /** Verifies that the full constructor preserves category and exit state. */
    @Test
    public void constructor_allFields_preservesValues() {
        Response response = new Response("Goodbye.", Response.ResponseType.ERROR, true);

        assertEquals("Goodbye.", response.getText());
        assertEquals(Response.ResponseType.ERROR, response.getType());
        assertTrue(response.isExit());
    }

    /** Verifies that response construction rejects missing invariant values. */
    @Test
    public void constructor_missingRequiredValue_assertionThrown() {
        assertThrows(AssertionError.class, () ->
                new Response(null, Response.ResponseType.STATUS, false));
        assertThrows(AssertionError.class, () -> new Response("text", null, false));
    }
}
