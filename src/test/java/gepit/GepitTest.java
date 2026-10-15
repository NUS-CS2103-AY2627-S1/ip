package gepit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class GepitTest {

    @Test
    void process_unknownCommand_returnsErrorResponse() {
        Gepit.Response response = new Gepit().process("dance");

        assertTrue(response.isError());
        assertTrue(response.message().contains("help"));
    }

    @Test
    void process_help_returnsCommandList() {
        Gepit.Response response = new Gepit().process("  HELP  ");

        assertFalse(response.isError());
        assertTrue(response.message().contains("deadline DESCRIPTION"));
    }

    @Test
    void process_blankInput_returnsErrorResponse() {
        Gepit.Response response = new Gepit().process("   ");

        assertTrue(response.isError());
    }

    @Test
    void process_listWithExtraArgument_returnsErrorResponse() {
        Gepit.Response response = new Gepit().process("list extra");

        assertTrue(response.isError());
    }
}
