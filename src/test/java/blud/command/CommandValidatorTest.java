package blud.command;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import blud.exception.CommandFormatException;

/** Tests shared command syntax and argument validation. */
public class CommandValidatorTest {
    /** Verifies that valid command text is accepted. */
    @Test
    public void validate_validCommand_doesNotThrow() {
        CommandValidator.validate("deadline submit report /by 10/6/2026 0900");
    }

    /** Verifies that invalid whitespace is rejected. */
    @Test
    public void validate_invalidWhitespace_throwsCommandFormatException() {
        assertThrows(CommandFormatException.class, () -> CommandValidator.validate(" todo task"));
        assertThrows(CommandFormatException.class, () -> CommandValidator.validate("todo  task"));
        assertThrows(CommandFormatException.class, () -> CommandValidator.validate("todo\ttask"));
        assertThrows(CommandFormatException.class, () -> CommandValidator.validate("todo milk | eggs"));
    }

    /** Verifies that null and blank command text is rejected. */
    @Test
    public void validate_missingCommand_throwsCommandFormatException() {
        assertThrows(CommandFormatException.class, () -> CommandValidator.validate(null));
        assertThrows(CommandFormatException.class, () -> CommandValidator.validate("   "));
    }

    /** Verifies command-specific word-count validation. */
    @Test
    public void validateCommand_wrongArity_throwsCommandFormatException() {
        assertThrows(CommandFormatException.class, () -> CommandValidator.validateCommand(Command.LIST, "list now"));
        assertThrows(CommandFormatException.class, () -> CommandValidator.validateCommand(Command.FIND, "find"));
        assertThrows(CommandFormatException.class,
                () -> CommandValidator.validateCommand(Command.SORT, "sort deadline asc extra"));
        assertThrows(CommandFormatException.class,
                () -> CommandValidator.validateCommand(Command.EVENT, "event meeting /from start /to end /extra"));
    }

    /** Verifies task parts are split at slash-prefixed parameter markers. */
    @Test
    public void splitTaskParts_taskParameters_returnsExpectedParts() {
        assertArrayEquals(new String[] {"deadline report", "by 10/6/2026 0900"},
                CommandValidator.splitTaskParts("deadline report /by 10/6/2026 0900"));
    }

    /** Verifies positive one-based task numbers become zero-based indices. */
    @Test
    public void parseTaskIndex_positiveNumber_returnsZeroBasedIndex() {
        assertEquals(2, CommandValidator.parseTaskIndex("3"));
    }

    /** Verifies malformed and non-positive task numbers are rejected. */
    @Test
    public void parseTaskIndex_invalidNumber_throwsCommandFormatException() {
        assertThrows(CommandFormatException.class, () -> CommandValidator.parseTaskIndex("0"));
        assertThrows(CommandFormatException.class, () -> CommandValidator.parseTaskIndex("abc"));
    }
}
