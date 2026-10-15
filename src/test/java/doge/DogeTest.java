package doge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests user-facing command responses from the Doge application. */
class DogeTest {

    /** Verifies that blank or null GUI input returns a readable error response. */
    @Test
    void getResponse_blankOrNullInput_returnsCommandError() {
        Doge doge = new Doge();

        assertEquals("Oops, much confusion: Please enter a command.", doge.getResponse(" "));
        assertEquals("Oops, much confusion: Please enter a command.", doge.getResponse(null));
    }

    /** Verifies that an unknown GUI command is converted into the Doge error style. */
    @Test
    void getResponse_unknownCommand_returnsCommandError() {
        Doge doge = new Doge();

        assertTrue(doge.getResponse("fetch").startsWith("Oops, much confusion:"));
    }

    /** Verifies that unexpected command arguments are rejected instead of ignored. */
    @Test
    void getResponse_unexpectedArguments_returnsCommandError() {
        Doge doge = new Doge();

        assertTrue(doge.getResponse("list extra").startsWith("Oops, much confusion:"));
        assertTrue(doge.getResponse("mark 1 extra").startsWith("Oops, much confusion:"));
    }
}
