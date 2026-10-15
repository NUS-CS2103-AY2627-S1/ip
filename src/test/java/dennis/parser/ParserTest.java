package dennis.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import dennis.DennisException;
import dennis.command.AddCommand;
import dennis.command.Command;
import dennis.command.DeleteCommand;
import dennis.command.ExitCommand;
import dennis.command.FindCommand;
import dennis.command.ListCommand;
import dennis.command.MarkCommand;
import dennis.command.OnCommand;
import dennis.command.UnmarkCommand;

/**
 * Tests for {@link Parser#parse(String)}, the single entry point that turns a
 * raw command line into the {@link Command} object to run.
 *
 * <p>Two things are checked: that a well-formed line produces the right
 * {@code Command} subclass, and that a malformed line fails with the exact
 * user-facing message. The private helpers ({@code parseTaskNumber},
 * {@code parseDeadline}, {@code parseEvent}, {@code parseOnDate}) are covered
 * through {@code parse}, since that is the only way they are reached.</p>
 *
 * <p>The command objects' internal fields (task number, wrapped task) are
 * private and {@code execute} writes to disk, so these tests assert on the
 * returned type and on exceptions rather than on execution.</p>
 */
public class ParserTest {

    // --- well-formed commands: correct Command subclass ------------------

    @Test
    public void parse_bye_returnsExitCommand() throws DennisException {
        Command c = Parser.parse("bye");
        assertInstanceOf(ExitCommand.class, c);
        assertTrue(c.isExit());
    }

    @Test
    public void parse_list_returnsListCommand() throws DennisException {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
    }

    @Test
    public void parse_mark_returnsMarkCommand() throws DennisException {
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 2"));
    }

    @Test
    public void parse_unmark_returnsUnmarkCommand() throws DennisException {
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 2"));
    }

    @Test
    public void parse_delete_returnsDeleteCommand() throws DennisException {
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 2"));
    }

    @Test
    public void parse_todo_returnsAddCommand() throws DennisException {
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
    }

    @Test
    public void parse_deadline_returnsAddCommand() throws DennisException {
        assertInstanceOf(AddCommand.class,
                Parser.parse("deadline return book /by 2019-12-01"));
    }

    @Test
    public void parse_event_returnsAddCommand() throws DennisException {
        assertInstanceOf(AddCommand.class,
                Parser.parse("event fair /from 2019-12-02 /to 2019-12-05"));
    }

    @Test
    public void parse_on_returnsOnCommand() throws DennisException {
        assertInstanceOf(OnCommand.class, Parser.parse("on 2019-12-01"));
    }

    @Test
    public void parse_within_returnsAddCommand() throws DennisException {
        assertInstanceOf(AddCommand.class, Parser.parse(
                "within collect certificate /from 2019-01-15 /to 2019-01-25"));
    }

    @Test
    public void parse_find_returnsFindCommand() throws DennisException {
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    // --- unrecognised input --------------------------------------------

    @Test
    public void parse_unknownWord_throwsWithDontUnderstandMessage() {
        DennisException e = assertThrows(DennisException.class, () ->
                Parser.parse("sing"));
        assertEquals("Meow? I have no idea what that means.", e.getMessage());
    }

    @Test
    public void parse_emptyLine_throwsWithDontUnderstandMessage() {
        DennisException e = assertThrows(DennisException.class, () ->
                Parser.parse(""));
        assertEquals("Meow? I have no idea what that means.", e.getMessage());
    }

    @Test
    public void parse_keywordAsPrefixOnly_throwsDennisException() {
        // "byebye" is not "bye".
        assertThrows(DennisException.class, () -> Parser.parse("byebye"));
    }

    // --- bare commands must not carry trailing text --------------------

    @Test
    public void parse_byeWithTrailingText_throwsDennisException() {
        assertThrows(DennisException.class, () -> Parser.parse("bye now"));
    }

    @Test
    public void parse_listWithTrailingText_throwsDennisException() {
        assertThrows(DennisException.class, () -> Parser.parse("list all"));
    }

    // --- surrounding whitespace on the whole line is ignored -----------

    @Test
    public void parse_byeWithTrailingSpace_returnsExitCommand() throws DennisException {
        assertInstanceOf(ExitCommand.class, Parser.parse("bye "));
    }

    @Test
    public void parse_listWithLeadingSpace_returnsListCommand() throws DennisException {
        assertInstanceOf(ListCommand.class, Parser.parse(" list"));
    }

    @Test
    public void parse_leadingSpaceBeforeTodo_stillRequiresADescription() {
        // Regression test: before fullCommand was trimmed, " todo" (leading
        // space) had its description extracted one character short of where
        // "todo" actually ends, silently producing a bogus one-letter
        // description instead of correctly rejecting the missing one.
        DennisException e = assertThrows(DennisException.class, () ->
                Parser.parse(" todo"));
        assertEquals("I'm sorry, todo must contain a task.", e.getMessage());
    }

    @Test
    public void parse_leadingSpaceBeforeMark_stillRequiresANumber() {
        // Same regression, for the keyword.length()-based extraction in
        // parseTaskNumber: a leading space used to leave "k" (the tail of
        // "mark") as the argument instead of the empty string.
        DennisException e = assertThrows(DennisException.class, () ->
                Parser.parse(" mark"));
        assertEquals("Please enter a task number.", e.getMessage());
    }

    // --- mark / unmark / delete argument errors -----------------------

    @Test
    public void parse_markWithNoNumber_throwsAskingForATaskNumber() {
        DennisException e = assertThrows(DennisException.class, () ->
                Parser.parse("mark"));
        assertEquals("Please enter a task number.", e.getMessage());
    }

    @Test
    public void parse_markWithNonInteger_throwsAskingForAnInteger() {
        DennisException e = assertThrows(DennisException.class, () ->
                Parser.parse("mark two"));
        assertEquals("The task number must be an integer.", e.getMessage());
    }

    @Test
    public void parse_markWithZero_returnsCommandBecauseRangeIsCheckedLater() {
        // Parser only checks "is it an integer"; whether the task exists is
        // TaskCommand.checkInRange's job, at execute time.
        assertDoesNotThrowInstanceOf(MarkCommand.class, "mark 0");
    }

    @Test
    public void parse_deleteWithNegativeNumber_returnsCommand() {
        assertDoesNotThrowInstanceOf(DeleteCommand.class, "delete -3");
    }

    // --- deadline / event structure errors --------------------------

    @Test
    public void parse_deadlineWithoutBy_tellsUserToUseBy() {
        DennisException e = assertThrows(DennisException.class, () ->
                Parser.parse("deadline return book"));
        assertEquals("Use /by to specify the deadline.", e.getMessage());
    }

    @Test
    public void parse_deadlineWithBadDate_propagatesDateError() {
        assertThrows(DennisException.class, () ->
                Parser.parse("deadline return book /by next friday"));
    }

    @Test
    public void parse_deadlineWithByUsedTwice_tellsUserToUseItOnlyOnce() {
        DennisException e = assertThrows(DennisException.class, () ->
                Parser.parse("deadline return book /by 2019-12-01 /by 2019-12-05"));
        assertEquals("Use /by only once to specify the deadline.", e.getMessage());
    }

    @Test
    public void parse_eventWithoutMarkers_tellsUserToUseFromAndTo() {
        DennisException e = assertThrows(DennisException.class, () ->
                Parser.parse("event fair"));
        assertEquals("Use /from and /to to specify the duration of the event.",
                e.getMessage());
    }

    @Test
    public void parse_eventWithToBeforeFrom_tellsUserToUseFromAndTo() {
        DennisException e = assertThrows(DennisException.class, () ->
                Parser.parse("event fair /to 2019-12-05 /from 2019-12-02"));
        assertEquals("Use /from and /to to specify the duration of the event.",
                e.getMessage());
    }

    @Test
    public void parse_eventWithFromUsedTwice_tellsUserToUseEachOnlyOnce() {
        DennisException e = assertThrows(DennisException.class, () -> Parser.parse(
                "event fair /from 2019-12-02 /to 2019-12-05 /from 2019-12-06"));
        assertEquals("Use /from and /to only once each to specify "
                + "the duration of the event.", e.getMessage());
    }

    @Test
    public void parse_eventWithToUsedTwice_tellsUserToUseEachOnlyOnce() {
        DennisException e = assertThrows(DennisException.class, () -> Parser.parse(
                "event fair /from 2019-12-02 /to 2019-12-05 /to 2019-12-06"));
        assertEquals("Use /from and /to only once each to specify "
                + "the duration of the event.", e.getMessage());
    }

    // --- within structure errors -------------------------------------

    @Test
    public void parse_withinWithoutMarkers_tellsUserToUseFromAndTo() {
        DennisException e = assertThrows(DennisException.class, () ->
                Parser.parse("within collect certificate"));
        assertEquals("Use /from and /to to specify the period for this task.",
                e.getMessage());
    }

    @Test
    public void parse_withinWithToBeforeFrom_tellsUserToUseFromAndTo() {
        DennisException e = assertThrows(DennisException.class, () ->
                Parser.parse(
                        "within collect certificate /to 2019-01-25 /from 2019-01-15"));
        assertEquals("Use /from and /to to specify the period for this task.",
                e.getMessage());
    }

    @Test
    public void parse_withinWithFromUsedTwice_tellsUserToUseEachOnlyOnce() {
        DennisException e = assertThrows(DennisException.class, () -> Parser.parse(
                "within collect certificate /from 2019-01-15 /to 2019-01-25 "
                        + "/from 2019-01-16"));
        assertEquals("Use /from and /to only once each to specify "
                + "the period for this task.", e.getMessage());
    }

    @Test
    public void parse_withinWithStartAfterEnd_propagatesOrderError() {
        DennisException e = assertThrows(DennisException.class, () ->
                Parser.parse(
                        "within collect certificate /from 2019-01-25 /to 2019-01-15"));
        assertEquals("The period's start date must not be after its end date.",
                e.getMessage());
    }

    // --- todo / on argument errors ---------------------------------

    @Test
    public void parse_todoWithNoDescription_propagatesTodoError() {
        DennisException e = assertThrows(DennisException.class, () ->
                Parser.parse("todo"));
        assertEquals("I'm sorry, todo must contain a task.", e.getMessage());
    }

    @Test
    public void parse_onWithNoDate_asksForADate() {
        DennisException e = assertThrows(DennisException.class, () ->
                Parser.parse("on"));
        assertEquals("Please enter a date, e.g. on 2019-12-01.", e.getMessage());
    }

    @Test
    public void parse_findWithNoKeyword_asksForAKeyword() {
        DennisException e = assertThrows(DennisException.class, () ->
                Parser.parse("find"));
        assertEquals("Please enter a keyword to search for.", e.getMessage());
    }

    @Test
    public void parse_findWithWhitespaceOnlyKeyword_asksForAKeyword() {
        assertThrows(DennisException.class, () -> Parser.parse("find    "));
    }

    @Test
    public void parse_onWithBadDate_propagatesDateError() {
        assertThrows(DennisException.class, () -> Parser.parse("on 2019-13-40"));
    }

    /** Parses {@code input} and asserts it returns an instance of {@code type} without throwing. */
    private static void assertDoesNotThrowInstanceOf(Class<?> type, String input) {
        try {
            assertInstanceOf(type, Parser.parse(input));
        } catch (DennisException e) {
            throw new AssertionError(
                    "expected no exception for \"" + input + "\"", e);
        }
    }
}
