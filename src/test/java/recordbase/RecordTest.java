package recordbase;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import recordbase.exceptions.RecordException;

/** Tests command guidance exposed by the Record command processor. */
public class RecordTest {
    @Test
    void parseInput_help_returnsCommandSummary() {
        String response = Record.parseInput("help");

        assertTrue(response.contains("todo"));
        assertTrue(response.contains("clear"));
        assertTrue(response.contains("help"));
    }

    @Test
    void parseInput_unknownCommand_suggestsHelpAndClear() {
        RecordException exception = assertThrows(RecordException.class, () ->
                Record.parseInput("unknown"));

        assertTrue(exception.getMessage().contains("clear"));
        assertTrue(exception.getMessage().contains("help"));
    }
}
