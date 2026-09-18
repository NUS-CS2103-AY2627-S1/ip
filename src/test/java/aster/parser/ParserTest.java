package aster.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import aster.command.AddCommand;
import aster.command.FindCommand;
import aster.command.StatsCommand;
import aster.exception.AsterException;

/**
 * Tests how {@link Parser#parse(String)} reads a {@code find} or {@code stats} command,
 * and the exact messages it gives when a deadline or event marker is missing or repeated.
 *
 * <p>The marker messages are compared in full, so that restructuring how they are built
 * cannot change what the user reads without a test noticing.
 *
 * <p>{@code find} is one of the commands whose wording the parser checks in full, so
 * these cases cover both halves of that promise: a line carrying a keyword produces a
 * command, and a line carrying none is refused before any command object exists.
 *
 * <p>Only the type of the command is asserted, never the keyword it holds, because the
 * keyword is private and nothing in the production code is made more visible for a
 * test. What the command does with that keyword is covered by {@code FindCommandTest}.
 */
class ParserTest {

    @Test
    void parse_findWithKeyword_returnsFindCommand() throws AsterException {
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    @Test
    void parse_findWithMultiWordKeyword_returnsFindCommand() throws AsterException {
        assertInstanceOf(FindCommand.class, Parser.parse("find project meeting"));
    }

    @Test
    void parse_findWithExtraSpaces_returnsFindCommand() throws AsterException {
        assertInstanceOf(FindCommand.class, Parser.parse("find    book"));
    }

    @Test
    void parse_findWithoutKeyword_throwsAsterException() {
        assertThrows(AsterException.class, () -> Parser.parse("find"));
    }

    @Test
    void parse_findWithBlankKeyword_throwsAsterException() {
        // The user interface trims the line, so a keyword of only spaces is the one way
        // an empty argument can still reach the parser.
        assertThrows(AsterException.class, () -> Parser.parse("find   "));
    }

    @Test
    void parse_unknownCommand_messageListsFindKeyword() {
        AsterException thrown =
                assertThrows(AsterException.class, () -> Parser.parse("bicycle"));

        assertTrue(thrown.getMessage().contains("find"),
                "the list of understood commands must name find: " + thrown.getMessage());
    }

    @Test
    void parse_stats_returnsStatsCommand() throws AsterException {
        assertInstanceOf(StatsCommand.class, Parser.parse("stats"));
    }

    @Test
    void parse_statsWithArguments_throwsNoArgumentsMessage() {
        AsterException thrown = assertThrows(AsterException.class, () -> Parser.parse("stats extra"));

        assertEquals("The stats command takes nothing after it. Type stats on its own.",
                thrown.getMessage());
    }

    @Test
    void parse_deadlineWithoutBy_throwsMissingMarkerMessage() {
        String input = "deadline return book";

        AsterException thrown = assertThrows(AsterException.class, () -> Parser.parse(input));

        assertEquals("A deadline needs a /by part. Try: deadline return book /by 2019-12-02",
                thrown.getMessage());
    }

    @Test
    void parse_deadlineWithTwoBy_throwsRepeatedMarkerMessage() {
        String input = "deadline return book /by 2019-06-06 /by 2019-06-07";

        AsterException thrown = assertThrows(AsterException.class, () -> Parser.parse(input));

        assertEquals("A deadline can have only one /by part. Try: deadline return book /by 2019-12-02",
                thrown.getMessage());
    }

    @Test
    void parse_eventWithoutFrom_throwsMissingMarkerMessage() {
        String input = "event project meeting /to 2019-08-08";

        AsterException thrown = assertThrows(AsterException.class, () -> Parser.parse(input));

        assertEquals("An event needs a /from part. "
                + "Try: event project meeting /from 2019-12-02 /to 2019-12-03", thrown.getMessage());
    }

    @Test
    void parse_eventWithTwoTo_throwsRepeatedMarkerMessage() {
        String input = "event project meeting /from 2019-08-06 /to 2019-08-07 /to 2019-08-08";

        AsterException thrown = assertThrows(AsterException.class, () -> Parser.parse(input));

        assertEquals("An event can have only one /to part. "
                + "Try: event project meeting /from 2019-12-02 /to 2019-12-03", thrown.getMessage());
    }

    @Test
    void parse_eventEndingBeforeItStarts_throwsBackwardsEventMessage() {
        String input = "event project meeting /from 2019-08-08 /to 2019-08-06";

        AsterException thrown = assertThrows(AsterException.class, () -> Parser.parse(input));

        assertEquals("An event needs its /to date on or after its /from date. "
                + "Try: event project meeting /from 2019-12-02 /to 2019-12-03", thrown.getMessage());
    }

    @Test
    void parse_eventEndingOnTheDayItStarts_returnsAddCommand() throws AsterException {
        // A whole-day event is written with the same date twice, so it must stay accepted.
        assertInstanceOf(AddCommand.class,
                Parser.parse("event project meeting /from 2019-08-06 /to 2019-08-06"));
    }

    @Test
    void parse_eventEndingAfterItStarts_returnsAddCommand() throws AsterException {
        assertInstanceOf(AddCommand.class,
                Parser.parse("event project meeting /from 2019-08-06 /to 2019-08-08"));
    }

    @Test
    void parse_eventEndingBeforeItStartsWithUnreadableDate_throwsDateMessage() {
        // The dates are read before their order is judged, so wording that is not a date
        // at all is still answered with the date message.
        String input = "event project meeting /from 2019-08-08 /to tomorrow";

        AsterException thrown = assertThrows(AsterException.class, () -> Parser.parse(input));

        assertEquals("I couldn't read \"tomorrow\" as a date. "
                + "Dates go in the form yyyy-MM-dd, for example 2019-12-02.", thrown.getMessage());
    }
}
