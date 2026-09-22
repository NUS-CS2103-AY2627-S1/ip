package lebron.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import lebron.exception.LebronException;

/**
 * Tests for {@link Parser#parse(String)} -- the single entry point that
 * turns a raw command line into a {@link ParsedCommand} and rejects
 * anything malformed with a {@link LebronException}. This is the most
 * branch-heavy method in the codebase, so the cases below walk every
 * command keyword plus its failure modes.
 */
public class ParserTest {

    // ---- simple commands ------------------------------------------------

    @Test
    public void parse_list_returnsListCommand() throws LebronException {
        assertEquals(ParsedCommand.Type.LIST, Parser.parse("list").getType());
    }

    @Test
    public void parse_bye_returnsByeCommand() throws LebronException {
        assertEquals(ParsedCommand.Type.BYE, Parser.parse("bye").getType());
    }

    @Test
    public void parse_surroundingWhitespace_ignored() throws LebronException {
        assertEquals(ParsedCommand.Type.LIST, Parser.parse("   list  ").getType());
    }

    @Test
    public void parse_unknownKeyword_throws() {
        assertThrows(LebronException.class, () -> Parser.parse("sing a song"));
    }

    @Test
    public void parse_emptyInput_throws() {
        assertThrows(LebronException.class, () -> Parser.parse(""));
    }

    // ---- todo --------------------------------------------------------

    @Test
    public void parse_todo_returnsTodoWithTrimmedDescription() throws LebronException {
        ParsedCommand command = Parser.parse("todo   read book  ");
        assertEquals(ParsedCommand.Type.TODO, command.getType());
        assertEquals("read book", command.getDescription());
    }

    @Test
    public void parse_todoWithoutDescription_throws() {
        assertThrows(LebronException.class, () -> Parser.parse("todo"));
    }

    @Test
    public void parse_todoWithPipeCharacter_throws() {
        // '|' is the data-file field separator; letting it through would
        // silently corrupt this task on the next save/load round trip.
        assertThrows(LebronException.class, () -> Parser.parse("todo buy milk | eggs"));
    }

    // ---- deadline --------------------------------------------------

    @Test
    public void parse_deadline_returnsDeadlineTask() throws LebronException {
        ParsedCommand command = Parser.parse("deadline return book /by 2019-12-02");
        assertEquals(ParsedCommand.Type.DEADLINE, command.getType());
        assertEquals("[D][ ] return book (by: Dec 02 2019)", command.getTask().toString());
    }

    @Test
    public void parse_deadlineWithTime_parsesTime() throws LebronException {
        ParsedCommand command = Parser.parse("deadline submit /by 2/12/2019 1800");
        assertEquals("[D][ ] submit (by: Dec 02 2019 6:00pm)", command.getTask().toString());
    }

    @Test
    public void parse_deadlineWithoutBy_throws() {
        assertThrows(LebronException.class, () -> Parser.parse("deadline return book"));
    }

    @Test
    public void parse_deadlineWithoutDescription_throws() {
        assertThrows(LebronException.class, () -> Parser.parse("deadline /by 2019-12-02"));
    }

    @Test
    public void parse_deadlineWithoutDate_throws() {
        assertThrows(LebronException.class, () -> Parser.parse("deadline return book /by"));
    }

    @Test
    public void parse_deadlineUnparseableDate_throws() {
        assertThrows(LebronException.class, () -> Parser.parse("deadline return book /by someday"));
    }

    @Test
    public void parse_deadlineWithEvery_returnsRecurringDeadline() throws LebronException {
        ParsedCommand command = Parser.parse("deadline pay rent /by 2025-01-01 /every month");
        assertEquals("[D][ ] pay rent (by: Jan 01 2025) (every: month)", command.getTask().toString());
    }

    @Test
    public void parse_deadlineEveryUnrecognisedPeriod_throws() {
        assertThrows(LebronException.class, () ->
                Parser.parse("deadline pay rent /by 2025-01-01 /every fortnight"));
    }

    @Test
    public void parse_deadlineEveryWithoutValue_throws() {
        assertThrows(LebronException.class, () -> Parser.parse("deadline pay rent /by 2025-01-01 /every"));
    }

    @Test
    public void parse_deadlineEveryBeforeBy_throws() {
        assertThrows(LebronException.class, () -> Parser.parse("deadline pay rent /every month /by 2025-01-01"));
    }

    @Test
    public void parse_deadlineWithPipeCharacter_throws() {
        assertThrows(LebronException.class, () -> Parser.parse("deadline pay | rent /by 2025-01-01"));
    }

    @Test
    public void parse_deadlineByGivenTwice_throws() {
        assertThrows(LebronException.class, () ->
                Parser.parse("deadline pay rent /by 2025-01-01 /by 2025-02-01"));
    }

    // ---- event -----------------------------------------------------

    @Test
    public void parse_event_returnsEventTask() throws LebronException {
        ParsedCommand command =
                Parser.parse("event meeting /from 2019-12-02 1400 /to 2019-12-02 1600");
        assertEquals(ParsedCommand.Type.EVENT, command.getType());
        assertEquals("[E][ ] meeting (from: Dec 02 2019 2:00pm to: Dec 02 2019 4:00pm)",
                command.getTask().toString());
    }

    @Test
    public void parse_eventWithoutFrom_throws() {
        assertThrows(LebronException.class, () -> Parser.parse("event meeting /to 2019-12-02"));
    }

    @Test
    public void parse_eventWithoutTo_throws() {
        assertThrows(LebronException.class, () -> Parser.parse("event meeting /from 2019-12-02"));
    }

    @Test
    public void parse_eventToBeforeFrom_throws() {
        assertThrows(LebronException.class, () ->
                Parser.parse("event meeting /to 2019-12-02 /from 2019-12-01"));
    }

    @Test
    public void parse_eventUnparseableDate_throws() {
        assertThrows(LebronException.class, () ->
                Parser.parse("event meeting /from someday /to 2019-12-02"));
    }

    @Test
    public void parse_eventWithEvery_returnsRecurringEvent() throws LebronException {
        ParsedCommand command =
                Parser.parse("event standup /from 2025-01-06 0900 /to 2025-01-06 0930 /every week");
        assertEquals("[E][ ] standup (from: Jan 06 2025 9:00am to: Jan 06 2025 9:30am) (every: week)",
                command.getTask().toString());
    }

    @Test
    public void parse_eventEveryBeforeTo_throws() {
        assertThrows(LebronException.class, () ->
                Parser.parse("event standup /every week /from 2025-01-06 0900 /to 2025-01-06 0930"));
    }

    @Test
    public void parse_eventWithPipeCharacter_throws() {
        assertThrows(LebronException.class, () ->
                Parser.parse("event stand | up /from 2025-01-06 0900 /to 2025-01-06 0930"));
    }

    @Test
    public void parse_eventFromGivenTwice_throws() {
        assertThrows(LebronException.class, () ->
                Parser.parse("event standup /from 2025-01-06 /from 2025-01-07 /to 2025-01-08"));
    }

    @Test
    public void parse_eventToNotAfterFrom_throws() {
        assertThrows(LebronException.class, () ->
                Parser.parse("event backwards /from 2025-01-10 /to 2025-01-01"));
    }

    @Test
    public void parse_eventToEqualsFrom_throws() {
        // A zero-duration event isn't valid: /to must be strictly after /from.
        assertThrows(LebronException.class, () ->
                Parser.parse("event instant /from 2025-01-01 0900 /to 2025-01-01 0900"));
    }

    // ---- mark / unmark / delete ----------------------------------

    @Test
    public void parse_mark_returnsMarkWithIndex() throws LebronException {
        ParsedCommand command = Parser.parse("mark 3");
        assertEquals(ParsedCommand.Type.MARK, command.getType());
        assertEquals(3, command.getIndex());
    }

    @Test
    public void parse_unmark_returnsUnmarkWithIndex() throws LebronException {
        ParsedCommand command = Parser.parse("unmark 2");
        assertEquals(ParsedCommand.Type.UNMARK, command.getType());
        assertEquals(2, command.getIndex());
    }

    @Test
    public void parse_delete_returnsDeleteWithIndex() throws LebronException {
        ParsedCommand command = Parser.parse("delete 1");
        assertEquals(ParsedCommand.Type.DELETE, command.getType());
        assertEquals(1, command.getIndex());
    }

    @Test
    public void parse_markWithoutNumber_throws() {
        assertThrows(LebronException.class, () -> Parser.parse("mark"));
    }

    @Test
    public void parse_markNonNumeric_throws() {
        assertThrows(LebronException.class, () -> Parser.parse("mark two"));
    }

    @Test
    public void parse_markOutOfRangeNumber_notCheckedByParser() throws LebronException {
        // Range checking is TaskList's job; the parser only needs a number.
        assertEquals(999, Parser.parse("mark 999").getIndex());
    }

    // ---- find ----------------------------------------------------------

    @Test
    public void parse_find_returnsFindWithKeyword() throws LebronException {
        ParsedCommand command = Parser.parse("find read book");
        assertEquals(ParsedCommand.Type.FIND, command.getType());
        assertEquals("read book", command.getDescription());
    }

    @Test
    public void parse_findWithoutKeyword_throws() {
        assertThrows(LebronException.class, () -> Parser.parse("find"));
    }
}
