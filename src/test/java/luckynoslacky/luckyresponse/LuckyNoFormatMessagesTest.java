package luckynoslacky.luckyresponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import luckynoslacky.luckyparser.LuckyNoParser;

/** Tests command syntax descriptions and invalid-format messages. */
class LuckyNoFormatMessagesTest {
    /** Verifies default formats are selected correctly for commands. */
    @Test
    void invalidFormatMessage_knownCommands_returnsFormattedMessages() {
        assertEquals(
                "Eh HELLO you know how to type command one anot? \n"
                        + "Lai lai let me teach you: `deadline "
                        + "<description> /by <date/time>.`",
                LuckyNoFormatMessages.invalidFormatMessage(
                        LuckyNoParser.CommandName.DEADLINE));
        assertEquals(
                "Eh HELLO you know how to type command one anot? \n"
                        + "Lai lai let me teach you: `event "
                        + "<description> /from <start date/time> /to <end date/time>.`",
                LuckyNoFormatMessages.invalidFormatMessage(
                        LuckyNoParser.CommandName.EVENT));
        assertEquals(
                "Eh HELLO you know how to type command one anot? \n"
                        + "Lai lai let me teach you: `find [<description>] [/on <date>]`",
                LuckyNoFormatMessages.invalidFormatMessage(
                        LuckyNoParser.CommandName.FIND));
        assertEquals(
                "Eh HELLO you know how to type command one anot? \n"
                        + "Lai lai let me teach you: `snooze "
                        + "<taskNumber> [/by <duration>]` or `snooze "
                        + "<taskNumber> [/to <end date/time>]`",
                LuckyNoFormatMessages.invalidFormatMessage(
                        LuckyNoParser.CommandName.SNOOZE));
        assertEquals(
                "Eh HELLO you know how to type command one anot? \n"
                        + "Lai lai let me teach you: `resched "
                        + "<taskNumber> /from <start date/time> /to <end date/time>`",
                LuckyNoFormatMessages.invalidFormatMessage(
                        LuckyNoParser.CommandName.RESCHED));
    }

    /** Verifies multiple valid formats are included in the error message. */
    @Test
    void invalidFormatMessage_multipleValidFormats_returnsAllFormats() {
        assertEquals(
                "Eh HELLO you know how to type command one anot? \n"
                        + "Lai lai let me teach you: `find <description> "
                        + "or /on <date>`",
                LuckyNoFormatMessages.invalidFormatMessage(
                        LuckyNoParser.CommandName.FIND,
                        "<description>",
                        "/on <date>"));
    }

    /** Verifies unsupported command formats trigger the internal assertion. */
    @Test
    void invalidFormatMessage_unsupportedOrNullCommand_throwsExpectedExceptions() {
        assertThrows(AssertionError.class, () ->
                LuckyNoFormatMessages.invalidFormatMessage(
                        LuckyNoParser.CommandName.LIST));
        assertThrows(IllegalArgumentException.class, () ->
                LuckyNoFormatMessages.invalidFormatMessage(null));
    }
}
