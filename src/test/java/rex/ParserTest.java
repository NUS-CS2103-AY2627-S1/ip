package rex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import rex.command.AddCommand;
import rex.command.Command;
import rex.command.CommandType;
import rex.command.DeleteCommand;
import rex.command.ExitCommand;
import rex.command.FindCommand;
import rex.command.ListCommand;
import rex.command.MarkCommand;
import rex.command.OnCommand;
import rex.command.UnknownCommand;
import rex.command.UnmarkCommand;
import rex.task.Deadline;
import rex.task.Event;
import rex.task.WithinPeriod;

/**
 * Tests reading the arguments of the commands that take more than a word.
 *
 * A parsed task is checked through its save format rather than field by field,
 * since that one string carries the description, the done flag and every date
 * the task holds, and comparing it catches a value landing in the wrong place
 * as readily as a wrong value.
 *
 * Test methods are deliberately not public, for the reason given in
 * TaskDateTimeTest.
 */
public class ParserTest {
    @Test
    void parseDeadline_descriptionAndDate_returnsDeadline() throws RexException {
        Deadline deadline = Parser.parseDeadline("return book /by 2019-10-15");

        assertEquals("D | 0 | return book | 2019-10-15", deadline.toSaveFormat());
    }

    @Test
    void parseDeadline_dateCarryingATime_timeKept() throws RexException {
        Deadline deadline = Parser.parseDeadline("submit report /by 2019-10-15 1800");

        assertEquals("D | 0 | submit report | 2019-10-15 1800", deadline.toSaveFormat());
    }

    @Test
    void parseDeadline_surroundingWhitespace_trimmed() throws RexException {
        Deadline deadline = Parser.parseDeadline("  return book  /by 2019-10-15 ");

        assertEquals("D | 0 | return book | 2019-10-15", deadline.toSaveFormat());
    }

    @Test
    void parseDeadline_missingBy_askedForABy() {
        RexException thrown = assertThrows(RexException.class, () -> Parser.parseDeadline("return book"));

        assertTrue(thrown.getMessage().contains("/by"), thrown.getMessage());
    }

    @Test
    void parseDeadline_byWithNothingAfterIt_askedForABy() {
        // The message matters as much as the refusal here. A "/by" with nothing
        // after it splits into an empty date, which would otherwise be reported
        // as an unreadable date rather than as a missing one.
        RexException thrown = assertThrows(RexException.class, () -> Parser.parseDeadline("return book /by "));

        assertTrue(thrown.getMessage().contains("/by"), thrown.getMessage());
    }

    @Test
    void parseDeadline_emptyDescription_exceptionThrown() {
        assertThrows(RexException.class, () -> Parser.parseDeadline("  /by 2019-10-15"));
    }

    @Test
    void parseDeadline_unreadableDate_exceptionThrown() {
        // A date the user got wrong has to arrive as a RexException. Left as the
        // IllegalArgumentException that TaskDateTime throws, it would end the
        // session instead of being answered with a message.
        assertThrows(RexException.class, () -> Parser.parseDeadline("return book /by tomorrow"));
    }

    @Test
    void parseTodo_descriptionHoldingTheSeparator_exceptionThrown() {
        // A task like this would save without complaint and then be skipped as
        // unreadable on the next start, so it has to be refused up front.
        RexException thrown = assertThrows(RexException.class, () -> Parser.parseTodo("buy milk | eggs"));

        assertTrue(thrown.getMessage().contains("|"), thrown.getMessage());
    }

    @Test
    void parseTodo_separatorWithoutSpaces_stillRefused() {
        assertThrows(RexException.class, () -> Parser.parseTodo("either|or"));
    }

    @Test
    void parseDeadline_descriptionHoldingTheSeparator_exceptionThrown() {
        // The check is shared by every kind of task, so one dated kind is
        // enough to show it is not only the todo that refuses.
        assertThrows(RexException.class, () -> Parser.parseDeadline("pay | file taxes /by 2019-10-15"));
    }

    @Test
    void parseEvent_descriptionAndBothTimes_returnsEvent() throws RexException {
        Event event = Parser.parseEvent(
                "project meeting /from 2019-10-15 1400 /to 2019-10-15 1600");

        assertEquals("E | 0 | project meeting | 2019-10-15 1400 | 2019-10-15 1600",
                event.toSaveFormat());
    }

    @Test
    void parseEvent_datesWithoutTimes_returnsEvent() throws RexException {
        Event event = Parser.parseEvent("company retreat /from 2019-10-18 /to 2019-10-20");

        assertEquals("E | 0 | company retreat | 2019-10-18 | 2019-10-20", event.toSaveFormat());
    }

    @Test
    void parseEvent_missingFrom_exceptionThrown() {
        assertThrows(RexException.class, () -> Parser.parseEvent("project meeting /to 2019-10-15 1600"));
    }

    @Test
    void parseEvent_missingTo_exceptionThrown() {
        assertThrows(RexException.class, () -> Parser.parseEvent("project meeting /from 2019-10-15 1400"));
    }

    @Test
    void parseEvent_emptyDescription_exceptionThrown() {
        assertThrows(RexException.class, () -> Parser.parseEvent(" /from 2019-10-15 1400 /to 2019-10-15 1600"));
    }

    @Test
    void parseEvent_unreadableStartDate_exceptionThrown() {
        assertThrows(RexException.class, () -> Parser.parseEvent("meeting /from someday /to 2019-10-15 1600"));
    }

    @Test
    void parseEvent_endsBeforeItStarts_exceptionThrown() {
        RexException thrown = assertThrows(
                RexException.class, () -> Parser.parseEvent("retreat /from 2019-10-18 /to 2019-10-15"));

        assertTrue(thrown.getMessage().contains("end before it starts"), thrown.getMessage());
    }

    @Test
    void parseEvent_endTimeBeforeStartTimeOnTheSameDay_exceptionThrown() {
        String reversedTimes = "meeting /from 2019-10-15 1600 /to 2019-10-15 1400";

        assertThrows(RexException.class, () -> Parser.parseEvent(reversedTimes));
    }

    @Test
    void parseEvent_startsAndEndsAtTheSameMoment_accepted() throws RexException {
        Event event = Parser.parseEvent("call /from 2019-10-15 1400 /to 2019-10-15 1400");

        assertEquals("E | 0 | call | 2019-10-15 1400 | 2019-10-15 1400", event.toSaveFormat());
    }

    @Test
    void parseEvent_timedStartEndingOnAPlainDateThatSameDay_accepted() throws RexException {
        // "/to 2019-10-15" means the whole of that day, not its midnight, so an
        // event starting at 2pm that day does not end before it starts.
        Event event = Parser.parseEvent("workshop /from 2019-10-15 1400 /to 2019-10-15");

        assertEquals("E | 0 | workshop | 2019-10-15 1400 | 2019-10-15", event.toSaveFormat());
    }

    @Test
    void parseWithin_endsBeforeItStarts_exceptionThrown() {
        String reversedDates = "collect certificate /from 2026-01-25 /to 2026-01-15";

        RexException thrown = assertThrows(RexException.class, () -> Parser.parseWithin(reversedDates));

        assertTrue(thrown.getMessage().contains("end before it starts"), thrown.getMessage());
    }

    @Test
    void parseWithin_descriptionAndBothDates_returnsWithinPeriod() throws RexException {
        WithinPeriod within = Parser.parseWithin("collect certificate /from 2026-01-15 /to 2026-01-25");

        assertEquals("W | 0 | collect certificate | 2026-01-15 | 2026-01-25", within.toSaveFormat());
    }

    @Test
    void parseWithin_datesCarryingTimes_timesKept() throws RexException {
        WithinPeriod within = Parser.parseWithin("vote /from 2026-01-15 1800 /to 2026-01-16 0900");

        assertEquals("W | 0 | vote | 2026-01-15 1800 | 2026-01-16 0900", within.toSaveFormat());
    }

    @Test
    void parseWithin_missingFrom_exceptionThrown() {
        String argument = "collect certificate";
        RexException thrown = assertThrows(RexException.class, () -> Parser.parseWithin(argument));

        assertTrue(thrown.getMessage().contains("/from"), thrown.getMessage());
    }

    @Test
    void parseWithin_missingTo_exceptionThrown() {
        String argument = "collect certificate /from 2026-01-15";
        RexException thrown = assertThrows(RexException.class, () -> Parser.parseWithin(argument));

        assertTrue(thrown.getMessage().contains("/to"), thrown.getMessage());
    }

    @Test
    void parseWithin_emptyDescription_exceptionThrown() {
        assertThrows(RexException.class, () -> Parser.parseWithin(" /from 2026-01-15 /to 2026-01-25"));
    }

    @Test
    void parseWithin_unreadableDate_exceptionThrown() {
        String argument = "collect certificate /from someday /to 2026-01-25";
        assertThrows(RexException.class, () -> Parser.parseWithin(argument));
    }

    @Test
    void parseTaskNumber_plainNumber_returnsNumber() throws RexException {
        assertEquals(3, Parser.parseTaskNumber("3"));
    }

    @Test
    void parseTaskNumber_surroundingWhitespace_ignored() throws RexException {
        assertEquals(3, Parser.parseTaskNumber("  3  "));
    }

    @Test
    void parseTaskNumber_outsideAnyList_stillReturned() {
        // Whether a number names a task depends on how many tasks there are,
        // which the task list knows and the parser does not, so a number that
        // no task could ever carry is still read rather than refused here.
        assertEquals(0, assertDoesNotThrowNumber("0"));
        assertEquals(-1, assertDoesNotThrowNumber("-1"));
    }

    @Test
    void parseTaskNumber_word_exceptionThrown() {
        assertThrows(RexException.class, () -> Parser.parseTaskNumber("three"));
    }

    @Test
    void parseTaskNumber_decimal_exceptionThrown() {
        assertThrows(RexException.class, () -> Parser.parseTaskNumber("1.5"));
    }

    @Test
    void parseTaskNumber_nothingGiven_exceptionThrown() {
        assertThrows(RexException.class, () -> Parser.parseTaskNumber(""));
    }

    @Test
    void parseKeyword_singleWord_returnsKeyword() throws RexException {
        assertEquals("book", Parser.parseKeyword("book"));
    }

    @Test
    void parseKeyword_severalWords_keptAsOnePhrase() throws RexException {
        // Splitting here would turn a search for one phrase into a search for
        // any of its words, which finds far more than was asked for.
        assertEquals("return book", Parser.parseKeyword("  return book  "));
    }

    @Test
    void parseKeyword_nothingGiven_exceptionThrown() {
        assertThrows(RexException.class, () -> Parser.parseKeyword("   "));
    }

    @Test
    void parseCommandType_everyCommandWord_ownType() {
        assertEquals(CommandType.LIST, Parser.parseCommandType("list"));
        assertEquals(CommandType.MARK, Parser.parseCommandType("mark 1"));
        assertEquals(CommandType.UNMARK, Parser.parseCommandType("unmark 1"));
        assertEquals(CommandType.DELETE, Parser.parseCommandType("delete 1"));
        assertEquals(CommandType.TODO, Parser.parseCommandType("todo read book"));
        assertEquals(CommandType.DEADLINE, Parser.parseCommandType("deadline x /by 2019-10-15"));
        assertEquals(CommandType.EVENT, Parser.parseCommandType("event x /from a /to b"));
        assertEquals(CommandType.WITHIN, Parser.parseCommandType("within x /from a /to b"));
        assertEquals(CommandType.ON, Parser.parseCommandType("on 2019-10-15"));
        assertEquals(CommandType.FIND, Parser.parseCommandType("find book"));
        assertEquals(CommandType.BYE, Parser.parseCommandType("bye"));
    }

    @Test
    void parseCommandType_differentCase_stillRecognized() {
        assertEquals(CommandType.LIST, Parser.parseCommandType("LIST"));
        assertEquals(CommandType.TODO, Parser.parseCommandType("ToDo read book"));
    }

    @Test
    void parseCommandType_wordNamingNoCommand_unknown() {
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("blah"));
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType(""));
    }

    @Test
    void parseCommandType_commandWordAsPartOfAnother_unknown() {
        // Only the whole first word names a command, so a longer word starting
        // with one is not that command.
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("listen to music"));
    }

    @Test
    void parseArgument_wordsAfterTheCommand_returnedWhole() {
        assertEquals("read book", Parser.parseArgument("todo read book"));
        assertEquals("x /by 2019-10-15", Parser.parseArgument("deadline x /by 2019-10-15"));
    }

    @Test
    void parseArgument_commandWordAlone_empty() {
        assertEquals("", Parser.parseArgument("list"));
    }

    @Test
    void parse_eachCommandWord_buildsThatCommand() throws RexException {
        // The parser's job here is to choose a command, so each is checked by
        // the kind of object it hands back rather than by what it later does.
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
        assertInstanceOf(OnCommand.class, Parser.parse("on 2019-10-15"));
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
        assertInstanceOf(UnknownCommand.class, Parser.parse("blah"));
    }

    @Test
    void parse_everyKindOfTask_buildsAnAddCommand() throws RexException {
        // All four kinds are added by the same command, so what tells them
        // apart is the task inside it, not the command around it.
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class, Parser.parse("deadline return book /by 2019-10-15"));
        assertInstanceOf(AddCommand.class,
                Parser.parse("event meeting /from 2019-10-15 /to 2019-10-16"));
        assertInstanceOf(AddCommand.class,
                Parser.parse("within collect /from 2026-01-15 /to 2026-01-25"));
    }

    @Test
    void parse_unknownWord_refusedWhenRunRatherThanWhenParsed() throws RexException {
        // Refusing is itself a command, so parsing an unrecognized word
        // succeeds and the complaint comes only once it is carried out.
        Command command = Parser.parse("blah");

        assertInstanceOf(UnknownCommand.class, command);
    }

    @Test
    void parse_commandNamedButDescribedWrongly_exceptionThrown() {
        assertThrows(RexException.class, () -> Parser.parse("todo"));
        assertThrows(RexException.class, () -> Parser.parse("deadline return book"));
        assertThrows(RexException.class, () -> Parser.parse("mark abc"));
        assertThrows(RexException.class, () -> Parser.parse("on someday"));
    }

    /** Reads a task number, turning a refusal into a test failure. */
    private static int assertDoesNotThrowNumber(String argument) {
        try {
            return Parser.parseTaskNumber(argument);
        } catch (RexException e) {
            throw new AssertionError("Expected " + argument + " to be read as a number", e);
        }
    }
}
