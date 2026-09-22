package oz.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import oz.exception.OzException;

/**
 * Tests shared validation and lookup operations for command arguments.
 */
public class CommandArgumentValidatorTest {
    /** Verifies parameter flags are recognized only as distinct words. */
    @Test
    public void containsFlag_similarWords_matchesOnlyDistinctFlag() {
        assertFalse(CommandArgumentValidator.containsFlag("read /only notes", "/on"));
        assertTrue(CommandArgumentValidator.containsFlag("read /on Monday", "/on"));
        assertEquals(5, CommandArgumentValidator.findFlagIndex("read /on Monday", "/on"));
    }

    /** Verifies duplicate validation reports the first repeated flag requested. */
    @Test
    public void validateNoDuplicateFlags_repeatedFlag_throwsException() {
        OzException exception = assertThrows(OzException.class, () ->
                CommandArgumentValidator.validateNoDuplicateFlags(
                        "meeting /from Monday /from Tuesday /to Friday",
                        "/from", "/to"));

        assertEquals("Duplicate '/from' parameter detected.", exception.getMessage());
    }

    /** Verifies the reserved storage delimiter is rejected centrally. */
    @Test
    public void validateNoStorageDelimiter_pipeCharacter_throwsException() {
        OzException exception = assertThrows(OzException.class, () ->
                CommandArgumentValidator.validateNoStorageDelimiter("tea | coffee"));

        assertEquals("Task input cannot contain the '|' character because it is "
                + "reserved for storage.", exception.getMessage());
    }
}
