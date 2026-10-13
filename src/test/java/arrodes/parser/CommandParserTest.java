package arrodes.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import arrodes.command.ByeCommand;
import arrodes.command.Command;
import arrodes.command.DeleteCommand;
import arrodes.command.FindCommand;
import arrodes.command.HelpCommand;
import arrodes.command.ListCommand;
import arrodes.command.MarkCommand;
import arrodes.command.TagCommand;
import arrodes.command.TodoCommand;
import arrodes.command.UnmarkCommand;
import arrodes.exception.ArrodesException;


/**
 * Tests tokenization, validation, and construction of CLI commands.
 */
class CommandParserTest {

    /**
     * Verifies tokenize nullInput exceptionThrown.
     */
    @Test
    void tokenize_nullInput_exceptionThrown() {
        ArrodesException ex = assertThrows(ArrodesException.class, () -> CommandParser.tokenize(null));
        assertEquals(ArrodesException.NO_INPUT, ex.getMessage());
    }

    /**
     * Verifies tokenize blankInput exceptionThrown.
     */
    @Test
    void tokenize_blankInput_exceptionThrown() {
        ArrodesException ex = assertThrows(ArrodesException.class, () -> CommandParser.tokenize("   "));
        assertEquals(ArrodesException.NO_INPUT, ex.getMessage());
    }

    /**
     * Verifies tokenize commandOnly noDescriptionNoParameters.
     */
    @Test
    void tokenize_commandOnly_noDescriptionNoParameters() {
        TokenizedCommand result = CommandParser.tokenize("list");
        assertEquals("list", result.getCommand());
        assertFalse(result.hasDescription());
        assertFalse(result.hasParameters());
    }

    /**
     * Verifies tokenize commandWithDescription descriptionParsedCorrectly.
     */
    @Test
    void tokenize_commandWithDescription_descriptionParsedCorrectly() {
        TokenizedCommand result = CommandParser.tokenize("todo Buy groceries");
        assertEquals("todo", result.getCommand());
        assertEquals("Buy groceries", result.getDescription());
        assertFalse(result.hasParameters());
    }

    /**
     * Verifies tokenize commandWithSingleFlag parameterParsedCorrectly.
     */
    @Test
    void tokenize_commandWithSingleFlag_parameterParsedCorrectly() {
        TokenizedCommand result = CommandParser.tokenize("deadline Submit report /by 2025-12-01");
        assertEquals("deadline", result.getCommand());
        assertEquals("Submit report", result.getDescription());
        assertTrue(result.hasParameters());
        assertEquals("2025-12-01", result.getParameters().get("by"));
    }

    /**
     * Verifies tokenize commandWithMultipleFlags allParametersParsed.
     */
    @Test
    void tokenize_commandWithMultipleFlags_allParametersParsed() {
        TokenizedCommand result = CommandParser.tokenize("event Team meeting /from 2025-12-01 /to 2025-12-02");
        assertEquals("event", result.getCommand());
        assertEquals("Team meeting", result.getDescription());
        assertEquals("2025-12-01", result.getParameters().get("from"));
        assertEquals("2025-12-02", result.getParameters().get("to"));
    }

    /**
     * Verifies tokenize flagWithNoValue exceptionThrown.
     */
    @Test
    void tokenize_flagWithNoValue_exceptionThrown() {
        // "/by" has no value after it
        assertThrows(ArrodesException.class, () -> CommandParser.tokenize("deadline Submit /by"));
    }

    // ── parse – bye ───────────────────────────────────────────────────────────

    /**
     * Verifies parse byeCommand returnsCorrectType.
     */
    @Test
    void parse_byeCommand_returnsCorrectType() {
        Command cmd = CommandParser.parse("bye");
        assertInstanceOf(ByeCommand.class, cmd);
    }

    /**
     * Verifies parse byeWithExtraText exceptionThrown.
     */
    @Test
    void parse_byeWithExtraText_exceptionThrown() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("bye now"));
    }

    // ── parse – help ─────────────────────────────────────────────────────────

    /** Verifies that help creates a help command. */
    @Test
    void parse_helpCommand_returnsHelpCommand() {
        assertInstanceOf(HelpCommand.class, CommandParser.parse("help"));
    }

    /** Verifies that help rejects additional arguments. */
    @Test
    void parse_helpWithArguments_exceptionThrown() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("help now"));
    }

    // ── parse – list ──────────────────────────────────────────────────────────

    /**
     * Verifies parse listCommand returnsCorrectType.
     */
    @Test
    void parse_listCommand_returnsCorrectType() {
        Command cmd = CommandParser.parse("list");
        assertInstanceOf(ListCommand.class, cmd);
    }

    /**
     * Verifies parse listWithExtraText exceptionThrown.
     */
    @Test
    void parse_listWithExtraText_exceptionThrown() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("list all"));
    }

    /**
     * Verifies parse listWithFlag exceptionThrown.
     */
    @Test
    void parse_listWithFlag_exceptionThrown() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("list /on 2025-12-01"));
    }

    /**
     * Verifies that the find keyword creates a command with a search term.
     */
    @Test
    void parse_findCommand_returnsFindCommand() {
        assertInstanceOf(FindCommand.class, CommandParser.parse("find book"));
    }

    /**
     * Verifies that find requires a non-empty search term.
     */
    @Test
    void parse_findWithoutKeyword_exceptionThrown() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("find"));
    }

    /**
     * Verifies that find rejects flag-like parameters.
     */
    @Test
    void parse_findWithParameter_exceptionThrown() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("find book /from shelf"));
    }

    // ── parse – mark ──────────────────────────────────────────────────────────

    /**
     * Verifies parse markValidIndex returnsMarkCommandWithCorrectIndex.
     */
    @Test
    void parse_markValidIndex_returnsMarkCommandWithCorrectIndex() {
        assertInstanceOf(MarkCommand.class, CommandParser.parse("mark 3"));
    }

    /**
     * Verifies parse markNoIndex exceptionThrown.
     */
    @Test
    void parse_markNoIndex_exceptionThrown() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("mark"));
    }

    /**
     * Verifies parse markNonNumericIndex exceptionThrown.
     */
    @Test
    void parse_markNonNumericIndex_exceptionThrown() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("mark abc"));
    }

    /**
     * Verifies parse markWithFlag exceptionThrown.
     */
    @Test
    void parse_markWithFlag_exceptionThrown() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("mark 1 /extra value"));
    }


    /**
     * Verifies parse unmarkValidIndex returnsUnmarkCommand.
     */
    @Test
    void parse_unmarkValidIndex_returnsUnmarkCommand() {
        assertInstanceOf(UnmarkCommand.class, CommandParser.parse("unmark 5"));
    }

    /**
     * Verifies parse unmarkNoIndex exceptionThrown.
     */
    @Test
    void parse_unmarkNoIndex_exceptionThrown() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("unmark"));
    }

    /**
     * Verifies parse unmarkNonNumericIndex exceptionThrown.
     */
    @Test
    void parse_unmarkNonNumericIndex_exceptionThrown() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("unmark xyz"));
    }


    /**
     * Verifies parse deleteValidIndex returnsDeleteCommandWithCorrectIndex.
     */
    @Test
    void parse_deleteValidIndex_returnsDeleteCommandWithCorrectIndex() {
        assertInstanceOf(DeleteCommand.class, CommandParser.parse("delete 2"));
    }

    /**
     * Verifies parse deleteNoIndex exceptionThrown.
     */
    @Test
    void parse_deleteNoIndex_exceptionThrown() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("delete"));
    }

    /**
     * Verifies parse deleteNonNumericIndex exceptionThrown.
     */
    @Test
    void parse_deleteNonNumericIndex_exceptionThrown() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("delete one"));
    }


    /**
     * Verifies parse todoWithDescription returnsTodoCommandWithDescription.
     */
    @Test
    void parse_todoWithDescription_returnsTodoCommandWithDescription() {
        assertInstanceOf(TodoCommand.class, CommandParser.parse("todo Buy milk"));
    }

    /**
     * Verifies parse todoNoDescription exceptionThrown.
     */
    @Test
    void parse_todoNoDescription_exceptionThrown() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("todo"));
    }

    /**
     * Verifies parse todoWithFlag exceptionThrown.
     */
    @Test
    void parse_todoWithFlag_exceptionThrown() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("todo Buy milk /by tomorrow"));
    }

    /**
     * Verifies parse deadlineNoDescription exceptionThrown.
     */
    @Test
    void parse_deadlineNoDescription_exceptionThrown() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("deadline /by 2025-12-01"));
    }

    /**
     * Verifies parse deadlineNoByFlag exceptionThrown.
     */
    @Test
    void parse_deadlineNoByFlag_exceptionThrown() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("deadline Submit report"));
    }

    /**
     * Verifies parse deadlineInvalidDateFormat exceptionThrown.
     */
    @Test
    void parse_deadlineInvalidDateFormat_exceptionThrown() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("deadline Submit /by next-monday"));
    }

    /**
     * Verifies parse deadlineNonExistentDate exceptionThrown.
     */
    @Test
    void parse_deadlineNonExistentDate_exceptionThrown() {
        // Feb 30 doesn't exist
        assertThrows(ArrodesException.class, () -> CommandParser.parse("deadline Submit /by 2025-02-30"));
    }


    /**
     * Verifies parse eventMixedFromDateToDateTime exceptionThrown.
     */
    @Test
    void parse_eventMixedFromDateToDateTime_exceptionThrown() {
        // XOR guard: one has time component, other does not
        assertThrows(ArrodesException.class, () -> CommandParser.parse("event Stand-up "
                + "/from 2025-12-01 /to 2025-12-01T09:30"));
    }

    /**
     * Verifies parse eventNoDescription exceptionThrown.
     */
    @Test
    void parse_eventNoDescription_exceptionThrown() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("event /from 2025-12-01 /to 2025-12-02"));
    }

    /**
     * Verifies parse eventMissingFromFlag exceptionThrown.
     */
    @Test
    void parse_eventMissingFromFlag_exceptionThrown() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("event Team meeting /to 2025-12-02"));
    }

    /**
     * Verifies parse eventMissingToFlag exceptionThrown.
     */
    @Test
    void parse_eventMissingToFlag_exceptionThrown() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("event Team meeting /from 2025-12-01"));
    }

    /**
     * Verifies parse eventInvalidFromDate exceptionThrown.
     */
    @Test
    void parse_eventInvalidFromDate_exceptionThrown() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("event Meeting "
                + "/from not-a-date /to 2025-12-02"));
    }

    /**
     * Verifies parse upcomingMissingOnFlag exceptionThrown.
     */
    @Test
    void parse_upcomingMissingOnFlag_exceptionThrown() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("upcoming"));
    }

    /**
     * Verifies parse upcomingWithDescription exceptionThrown.
     */
    @Test
    void parse_upcomingWithDescription_exceptionThrown() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("upcoming someday /on 2025-12-01"));
    }

    // ── parse – tag ───────────────────────────────────────────────────────────

    /** Verifies that a valid tag command creates a tag command. */
    @Test
    void parse_tagWithTaskNumberAndTag_returnsTagCommand() {
        assertInstanceOf(TagCommand.class, CommandParser.parse("tag 3 /with urgent"));
    }

    /** Verifies that a tag command requires a task number. */
    @Test
    void parse_tagWithoutTaskNumber_exceptionThrown() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("tag /with urgent"));
    }

    /** Verifies that a tag command requires a {@code /with} parameter. */
    @Test
    void parse_tagWithoutWithParameter_exceptionThrown() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("tag 3"));
    }

    /** Verifies that a tag command rejects a non-numeric task number. */
    @Test
    void parse_tagWithNonNumericTaskNumber_exceptionThrown() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("tag abc /with urgent"));
    }

    /** Verifies that a tag command rejects unexpected parameters. */
    @Test
    void parse_tagWithUnexpectedParameter_exceptionThrown() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("tag 3 /with urgent /extra value"));
    }


    /**
     * Verifies parse unknownCommand exceptionThrown.
     */
    @Test
    void parse_unknownCommand_exceptionThrown() {
        ArrodesException ex = assertThrows(ArrodesException.class, () -> CommandParser.parse("fly to the moon"));
        assertEquals(ArrodesException.UNKNOWN_COMMAND, ex.getMessage());
    }


    /**
     * Verifies parse emptyString exceptionThrown.
     */
    @Test
    void parse_emptyString_exceptionThrown() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse(""));
    }

    /**
     * Verifies parse whitespaceOnly exceptionThrown.
     */
    @Test
    void parse_whitespaceOnly_exceptionThrown() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("   "));
    }

    /** Verifies a slash in free text is rejected instead of being misparsed as a flag. */
    @Test
    void tokenize_descriptionWithSlashInMiddle_isRejected() {
        assertThrows(ArrodesException.class, () -> CommandParser.tokenize("event A/B test "
                + "/from 2025-01-01 /to 2025-01-02"));
    }

    /** Verifies leading, trailing, and repeated command whitespace is rejected. */
    @Test
    void tokenize_malformedWhitespace_isRejected() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse(" todo buy milk"));
        assertThrows(ArrodesException.class, () -> CommandParser.parse("todo buy milk "));
        assertThrows(ArrodesException.class, () -> CommandParser.parse("todo  buy milk"));
    }

    /** Verifies duplicate and unknown parameters are rejected. */
    @Test
    void parse_duplicateOrUnknownParameter_isRejected() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("deadline report "
                + "/by 2026-01-01 /by 2026-01-02"));
        assertThrows(ArrodesException.class, () -> CommandParser.parse("deadline report "
                + "/at 2026-01-01"));
        assertThrows(ArrodesException.class, () -> CommandParser.parse("bye /now please"));
    }

    /** Verifies equal event endpoints are rejected. */
    @Test
    void parse_eventWithEqualEndpoints_isRejected() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("event meeting "
                + "/from 2026-01-01T10:00 /to 2026-01-01T10:00"));
    }

    /** Verifies tags containing unsupported characters are rejected. */
    @Test
    void parse_tagWithUnsupportedCharacters_isRejected() {
        assertThrows(ArrodesException.class, () -> CommandParser.parse("tag 1 /with urgent!"));
    }
}
