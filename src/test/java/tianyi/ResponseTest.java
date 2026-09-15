package tianyi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests structured response content and status factories.
 */
public class ResponseTest {
    @Test
    public void getFullMessage_headerAndBody_joinsWithNewline() {
        Response response = new Response("Header", "Body");

        assertEquals("Header\nBody", response.getFullMessage());
        assertFalse(response.isError());
        assertFalse(response.isExit());
    }

    @Test
    public void getFullMessage_headerBodyAndFooter_joinsAllPartsWithNewlines() {
        Response response = new Response("Header", "Body", "Footer");

        assertEquals("Header\nBody\nFooter", response.getFullMessage());
        assertEquals("Footer", response.getFooter());
    }

    @Test
    public void getFullMessage_missingHeader_returnsBodyWithoutLeadingNewline() {
        Response response = new Response("", "Body");

        assertEquals("Body", response.getFullMessage());
    }

    @Test
    public void getFullMessage_missingBody_returnsHeaderWithoutTrailingNewline() {
        Response response = new Response("Header", "");

        assertEquals("Header", response.getFullMessage());
    }

    @Test
    public void error_validMessage_setsOnlyErrorStatus() {
        Response response = Response.error("Invalid command");

        assertEquals("", response.getHeader());
        assertEquals("Invalid command", response.getMessage());
        assertEquals("", response.getFooter());
        assertTrue(response.isError());
        assertFalse(response.isExit());
    }

    @Test
    public void exit_validMessage_setsOnlyExitStatus() {
        Response response = Response.exit("Goodbye");

        assertEquals("", response.getHeader());
        assertEquals("Goodbye", response.getMessage());
        assertEquals("", response.getFooter());
        assertFalse(response.isError());
        assertTrue(response.isExit());
    }
}
