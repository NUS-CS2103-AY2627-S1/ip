package chione;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import chione.command.AddCommand;
import chione.command.CommandType;
import chione.command.DeleteCommand;
import chione.command.ExitCommand;
import chione.command.FindCommand;
import chione.command.ListCommand;
import chione.command.MarkCommand;
import chione.command.OnCommand;
import chione.command.UnmarkCommand;

/**
 * Tests {@link Parser}, which is the only part of Chione that sees raw input.
 *
 * <p>Two things are checked throughout: that a well-formed line produces the
 * right command, and that a malformed one is refused with the particular message
 * that says what to type instead. A parser that accepted bad input quietly would
 * push the problem into code that has no idea what the user meant.
 */
public class ParserTest {
    private static final String DEADLINE_EXAMPLE = "Try: deadline return book /by 2019-10-15";
    private static final String EVENT_EXAMPLE =
            "Try: event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600";

    // ---- parse: which command a line produces ----

    @Test
    public void parse_todoLine_addCommandProduced() throws ChioneException {
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
    }

    @Test
    public void parse_listLine_listCommandProduced() throws ChioneException {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
    }

    @Test
    public void parse_onLine_onCommandProduced() throws ChioneException {
        assertInstanceOf(OnCommand.class, Parser.parse("on 2019-10-15"));
    }

    @Test
    public void parse_markLine_markCommandProduced() throws ChioneException {
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
    }

    @Test
    public void parse_unmarkLine_unmarkCommandProduced() throws ChioneException {
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1"));
    }

    @Test
    public void parse_deleteLine_deleteCommandProduced() throws ChioneException {
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
    }

    @Test
    public void parse_findLine_findCommandProduced() throws ChioneException {
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    @Test
    public void parse_byeLine_commandEndsTheConversation() throws ChioneException {
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
        assertTrue(Parser.parse("bye").isExit());
    }

    @Test
    public void parse_anyOtherCommand_doesNotEndTheConversation() throws ChioneException {
        assertEquals(false, Parser.parse("list").isExit());
    }

    @Test
    public void parse_unknownWord_exceptionListsEveryKeyword() {
        ChioneException e = assertThrows(ChioneException.class, () -> Parser.parse("blah"));
        assertEquals("I don't know what \"blah\" means. "
                + "I understand: todo, deadline, event, list, on, find, mark, unmark, delete, bye.",
                e.getMessage());
    }

    @Test
    public void parse_keywordAsPrefixOfAWord_notTreatedAsThatCommand() {
        // "listing" starts with "list", but it is a different word, so it is not
        // the list command with a stray argument.
        assertThrows(ChioneException.class, () -> Parser.parse("listing"));
    }

    // ---- parse: spacing and empty lines ----

    @Test
    public void parse_spacesAroundTheLine_stillUnderstood() throws ChioneException {
        assertInstanceOf(ListCommand.class, Parser.parse("   list   "));
    }

    @Test
    public void parse_severalSpacesAfterTheKeyword_stillUnderstood() throws ChioneException {
        assertInstanceOf(MarkCommand.class, Parser.parse("mark     2"));
    }

    @Test
    public void parse_tabInsteadOfSpace_stillUnderstood() throws ChioneException {
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete\t2"));
    }

    @Test
    public void parse_emptyLine_exceptionSaysWhatToType() {
        ChioneException e = assertThrows(ChioneException.class, () -> Parser.parse(""));
        assertEquals("You haven't typed anything yet. Try: list", e.getMessage());
    }

    @Test
    public void parse_onlySpaces_treatedAsAnEmptyLine() {
        ChioneException e = assertThrows(ChioneException.class, () -> Parser.parse("     "));
        assertEquals("You haven't typed anything yet. Try: list", e.getMessage());
    }

    @Test
    public void normalizeSpacing_runsOfSpacesInside_collapsedToOne() {
        assertEquals("todo read book", Parser.normalizeSpacing("todo    read   book"));
    }

    @Test
    public void normalizeSpacing_spacesAtBothEnds_removed() {
        assertEquals("list", Parser.normalizeSpacing("  list  "));
    }

    @Test
    public void normalizeSpacing_tabsAndSpacesMixed_allBecomeOneSpace() {
        assertEquals("deadline x /by y", Parser.normalizeSpacing("deadline \t x\t/by  y"));
    }

    @Test
    public void normalizeSpacing_alreadyTidy_unchanged() {
        assertEquals("todo read book", Parser.normalizeSpacing("todo read book"));
    }

    // ---- find ----

    @Test
    public void parseKeyword_word_returnedUnchanged() throws ChioneException {
        assertEquals("book", Parser.parseKeyword("book"));
    }

    @Test
    public void parseKeyword_severalWords_allKept() throws ChioneException {
        // The whole of what was typed is the search text, spaces included.
        assertEquals("read book", Parser.parseKeyword("read book"));
    }

    @Test
    public void parseKeyword_nothingTyped_exceptionThrown() {
        // An empty search would match everything, which is what "list" is for.
        ChioneException e = assertThrows(ChioneException.class, () -> Parser.parseKeyword(""));
        assertEquals("Tell me what to look for. Try: find book", e.getMessage());
    }

    // ---- todo ----

    @Test
    public void parseTodo_description_todoCreated() throws ChioneException {
        assertEquals("[T][ ] read book", Parser.parseTodo("read book").toString());
    }

    @Test
    public void parseTodo_noDescription_exceptionThrown() {
        ChioneException e = assertThrows(ChioneException.class, () -> Parser.parseTodo(""));
        assertEquals("A todo needs a description. Try: todo borrow book", e.getMessage());
    }

    @Test
    public void parseTodo_barInDescription_exceptionThrown() {
        // A bar is what keeps the save file's columns apart, so a description
        // holding one would be read back as too many columns.
        ChioneException e = assertThrows(ChioneException.class, () -> Parser.parseTodo("read | write"));
        assertEquals("A description can't contain \"|\": that is what keeps my save file in columns. "
                + "Leave it out and try again.", e.getMessage());
    }

    @Test
    public void parseTodo_otherPunctuation_accepted() throws ChioneException {
        assertEquals("[T][ ] fix bug #12 (again!)", Parser.parseTodo("fix bug #12 (again!)").toString());
    }

    // ---- deadline ----

    @Test
    public void parseDeadline_descriptionAndDate_deadlineCreated() throws ChioneException {
        assertEquals("[D][ ] return book (by: Oct 15 2019, 6:00pm)",
                Parser.parseDeadline("return book /by 2019-10-15 1800").toString());
    }

    @Test
    public void parseDeadline_dateWithoutTime_deadlineShownWithoutTime() throws ChioneException {
        assertEquals("[D][ ] return book (by: Oct 15 2019)",
                Parser.parseDeadline("return book /by 2019-10-15").toString());
    }

    @Test
    public void parseDeadline_noBySeparator_exceptionThrown() {
        ChioneException e = assertThrows(ChioneException.class, () ->
                Parser.parseDeadline("return book"));
        assertEquals("A deadline needs a due date after /by. " + DEADLINE_EXAMPLE, e.getMessage());
    }

    @Test
    public void parseDeadline_nothingBeforeBy_exceptionNamesTheDescription() {
        ChioneException e = assertThrows(ChioneException.class, () ->
                Parser.parseDeadline("/by 2019-10-15"));
        assertEquals("A deadline needs a description before /by. " + DEADLINE_EXAMPLE, e.getMessage());
    }

    @Test
    public void parseDeadline_nothingAfterBy_exceptionNamesTheDate() {
        ChioneException e = assertThrows(ChioneException.class, () ->
                Parser.parseDeadline("return book /by "));
        assertEquals("Tell me when it is due after /by. " + DEADLINE_EXAMPLE, e.getMessage());
    }

    @Test
    public void parseDeadline_unreadableDate_exceptionThrown() {
        assertThrows(ChioneException.class, () -> Parser.parseDeadline("return book /by Sunday"));
    }

    @Test
    public void parseDeadline_bySpecifiedTwice_exceptionThrown() {
        // The split stops at the first " /by ", so the second one lands in the
        // date, where it is recognized and reported rather than left to fail as
        // an unreadable date.
        ChioneException e = assertThrows(ChioneException.class, () ->
                Parser.parseDeadline("return book /by 2019-10-15 /by 2019-10-16"));
        assertEquals("Give /by only once. " + DEADLINE_EXAMPLE, e.getMessage());
    }

    @Test
    public void parseDeadline_barInDescription_exceptionThrown() {
        assertThrows(ChioneException.class, () -> Parser.parseDeadline("a | b /by 2019-10-15"));
    }

    // ---- event ----

    @Test
    public void parseEvent_descriptionAndBothTimes_eventCreated() throws ChioneException {
        assertEquals("[E][ ] meeting (from: Oct 15 2019, 2:00pm to: Oct 17 2019, 4:00pm)",
                Parser.parseEvent("meeting /from 2019-10-15 1400 /to 2019-10-17 1600").toString());
    }

    @Test
    public void parseEvent_noFromSeparator_exceptionThrown() {
        ChioneException e = assertThrows(ChioneException.class, () ->
                Parser.parseEvent("meeting /to 2019-10-17 1600"));
        assertEquals("An event needs a start time after /from. " + EVENT_EXAMPLE, e.getMessage());
    }

    @Test
    public void parseEvent_noToSeparator_exceptionThrown() {
        ChioneException e = assertThrows(ChioneException.class, () ->
                Parser.parseEvent("meeting /from 2019-10-15 1400"));
        assertEquals("An event needs an end time after /to. " + EVENT_EXAMPLE, e.getMessage());
    }

    @Test
    public void parseEvent_emptyDescription_exceptionThrown() {
        ChioneException e = assertThrows(ChioneException.class, () ->
                Parser.parseEvent("/from 2019-10-15 1400 /to 2019-10-17 1600"));
        assertEquals("An event needs a description, a start and an end. " + EVENT_EXAMPLE, e.getMessage());
    }

    @Test
    public void parseEvent_toBeforeFrom_exceptionSaysWhichOrder() {
        // Both parts are present, just the wrong way round; that deserves a
        // better answer than "the end time is missing".
        ChioneException e = assertThrows(ChioneException.class, () ->
                Parser.parseEvent("meeting /to 2019-10-17 1600 /from 2019-10-15 1400"));
        assertEquals("Put /from before /to. " + EVENT_EXAMPLE, e.getMessage());
    }

    @Test
    public void parseEvent_fromSpecifiedTwice_exceptionThrown() {
        ChioneException e = assertThrows(ChioneException.class, () ->
                Parser.parseEvent("meeting /from 2019-10-15 /from 2019-10-16 /to 2019-10-17"));
        assertEquals("Give /from and /to once each. " + EVENT_EXAMPLE, e.getMessage());
    }

    @Test
    public void parseEvent_toSpecifiedTwice_exceptionThrown() {
        ChioneException e = assertThrows(ChioneException.class, () ->
                Parser.parseEvent("meeting /from 2019-10-15 /to 2019-10-16 /to 2019-10-17"));
        assertEquals("Give /from and /to once each. " + EVENT_EXAMPLE, e.getMessage());
    }

    @Test
    public void parseEvent_endBeforeStart_exceptionQuotesBothMoments() {
        ChioneException e = assertThrows(ChioneException.class, () ->
                Parser.parseEvent("meeting /from 2019-10-15 1600 /to 2019-10-15 1400"));
        assertEquals("An event has to end after it starts. "
                + "This one starts at Oct 15 2019, 4:00pm and ends at Oct 15 2019, 2:00pm.", e.getMessage());
    }

    @Test
    public void parseEvent_endSameAsStart_exceptionThrown() {
        // Ending at the very moment it starts is no better than ending before.
        assertThrows(ChioneException.class, () ->
                Parser.parseEvent("meeting /from 2019-10-15 1400 /to 2019-10-15 1400"));
    }

    @Test
    public void parseEvent_endOneMinuteAfterStart_accepted() throws ChioneException {
        assertEquals("[E][ ] blink (from: Oct 15 2019, 2:00pm to: Oct 15 2019, 2:01pm)",
                Parser.parseEvent("blink /from 2019-10-15 1400 /to 2019-10-15 1401").toString());
    }

    @Test
    public void parseEvent_unreadableStart_exceptionThrown() {
        assertThrows(ChioneException.class, () ->
                Parser.parseEvent("meeting /from Monday /to 2019-10-15 1600"));
    }

    @Test
    public void parseEvent_barInDescription_exceptionThrown() {
        assertThrows(ChioneException.class, () ->
                Parser.parseEvent("a | b /from 2019-10-15 /to 2019-10-16"));
    }

    // ---- task numbers ----

    @Test
    public void parseTaskNumber_number_convertedToAZeroBasedIndex() throws ChioneException {
        // The user counts from 1, the list counts from 0.
        assertEquals(1, Parser.parseTaskNumber("2", CommandType.MARK));
    }

    @Test
    public void parseTaskNumber_nothingTyped_exceptionNamesTheCommand() {
        ChioneException e = assertThrows(ChioneException.class, () ->
                Parser.parseTaskNumber("", CommandType.DELETE));
        assertEquals("Tell me which task to delete. Try: delete 2", e.getMessage());
    }

    @Test
    public void parseTaskNumber_notANumber_exceptionQuotesWhatWasTyped() {
        ChioneException e = assertThrows(ChioneException.class, () ->
                Parser.parseTaskNumber("abc", CommandType.MARK));
        assertEquals("\"abc\" is not a task number. Try: mark 2", e.getMessage());
    }

    @Test
    public void parseTaskNumber_decimal_exceptionThrown() {
        assertThrows(ChioneException.class, () -> Parser.parseTaskNumber("1.5", CommandType.MARK));
    }

    @Test
    public void parseTaskNumber_zero_exceptionThrown() {
        // No list of any length has a task 0, so this is refused before the
        // list is even asked.
        ChioneException e = assertThrows(ChioneException.class, () ->
                Parser.parseTaskNumber("0", CommandType.MARK));
        assertEquals("Task numbers start at 1. Try: mark 2", e.getMessage());
    }

    @Test
    public void parseTaskNumber_negative_exceptionThrown() {
        ChioneException e = assertThrows(ChioneException.class, () ->
                Parser.parseTaskNumber("-3", CommandType.DELETE));
        assertEquals("Task numbers start at 1. Try: delete 2", e.getMessage());
    }

    @Test
    public void parseTaskNumber_beyondAnyList_acceptedHereAndRefusedByTheList() throws ChioneException {
        // Whether a number points at a task is the task list's business, so a
        // number that is merely too large is not refused at this stage.
        assertEquals(999, Parser.parseTaskNumber("1000", CommandType.MARK));
    }

    // ---- on ----

    @Test
    public void parseDate_day_returnsThatDay() throws ChioneException {
        assertEquals("Oct 15 2019", DateTimes.formatDate(Parser.parseDate("2019-10-15")));
    }

    @Test
    public void parseDate_nothingTyped_exceptionThrown() {
        ChioneException e = assertThrows(ChioneException.class, () -> Parser.parseDate(""));
        assertEquals("Tell me which day to look at. Try: on 2019-10-15", e.getMessage());
    }
}
