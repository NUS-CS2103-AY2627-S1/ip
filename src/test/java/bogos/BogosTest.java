package bogos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for Bogos command responses.
 */
public class BogosTest {
    /**
     * Verifies that help displays every supported command and the documentation link.
     */
    @Test
    public void getResponse_help_returnsHelpMessage() {
        Bogos bogos = new Bogos(new Ui());

        String response = bogos.getResponse("help");

        String expectedResponse = String.join(System.lineSeparator(),
                "Bogos' basic business:",
                "",
                "  todo DESCRIPTION [#TAG]...",
                "  deadline DESCRIPTION /by YYYY-MM-DD [#TAG]...",
                "  event DESCRIPTION /from YYYY-MM-DD /to YYYY-MM-DD [#TAG]...",
                "",
                "  list",
                "  find KEYWORD",
                "  mark NUMBER",
                "  unmark NUMBER",
                "  delete NUMBER",
                "  help",
                "  bye",
                "",
                "Browse beyond basics: https://oofky.github.io/ip/");
        assertEquals(expectedResponse, response);
    }

    /**
     * Verifies that help is recognized only as a complete command.
     */
    @Test
    public void getResponse_helpWithArguments_returnsUnknownCommandError() {
        Bogos bogos = new Bogos(new Ui());

        String response = bogos.getResponse("help todo");

        assertEquals("Bwhat? Best browse: help", response);
    }
}
