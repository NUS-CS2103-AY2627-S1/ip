package sallman;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import sallman.command.AddCommand;
import sallman.command.DeleteCommand;
import sallman.command.ExitCommand;
import sallman.command.FindCommand;
import sallman.command.ListCommand;
import sallman.command.MarkCommand;
import sallman.command.OnCommand;
import sallman.command.SortCommand;
import sallman.command.SortOrder;
import sallman.command.TagCommand;
import sallman.command.UndoCommand;
import sallman.task.Deadline;
import sallman.task.Event;
import sallman.task.Task;
import sallman.task.Todo;

/**
 * Tests that {@link Parser} reads well-formed input correctly, and names the
 * specific fault in input that is not.
 */
public class ParserTest {

    @Test
    public void parse_eachKeyword_correctCommandType() throws Exception {
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(OnCommand.class, Parser.parse("on 2019-10-15"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(MarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
        assertInstanceOf(TagCommand.class, Parser.parse("tag 1 fun"));
        assertInstanceOf(TagCommand.class, Parser.parse("untag 1 fun"));
        assertInstanceOf(SortCommand.class, Parser.parse("sort"));
    }

    @Test
    public void parse_unknownKeyword_exceptionQuotesIt() {
        SallmanException e = assertThrows(SallmanException.class, () -> Parser.parse("blah"));
        assertEquals("I'm sorry, but I don't know what \"blah\" means.", e.getMessage());
    }

    @Test
    public void parse_extraSpacesBetweenWords_stillUnderstood() throws Exception {
        // The command and its arguments are split on any run of whitespace, so
        // sloppy spacing should not look like a different command.
        assertInstanceOf(AddCommand.class, Parser.parse("todo    read book"));
    }

    @Test
    public void parseTodo_description_taskCreated() throws Exception {
        Todo todo = (Todo) Parser.parseTodo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void parseTodo_noDescription_exceptionThrown() {
        SallmanException e = assertThrows(SallmanException.class, () -> Parser.parseTodo(""));
        assertEquals("I'd love to help, but a todo needs a description.", e.getMessage());
    }

    @Test
    public void parseDeadline_descriptionAndDate_taskCreated() throws Exception {
        Deadline deadline = (Deadline) Parser.parseDeadline("return book /by 2019-10-15");
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void parseDeadline_missingBy_exceptionNamesTheDelimiter() {
        SallmanException e = assertThrows(SallmanException.class, () -> Parser.parseDeadline("submit report"));
        assertEquals("I'm sorry, but I couldn't find a /by in that deadline.", e.getMessage());
    }

    @Test
    public void parseDeadline_missingDescription_exceptionNamesTheDescription() {
        // Distinct from the missing-/by case: the delimiter is present, so the
        // user needs to be told which side of it is empty.
        SallmanException e = assertThrows(SallmanException.class, () -> Parser.parseDeadline("/by 2019-10-15"));
        assertEquals("I'd love to help, but that deadline has no description before the /by.", e.getMessage());
    }

    @Test
    public void parseDeadline_missingDate_exceptionNamesTheDate() {
        SallmanException e = assertThrows(SallmanException.class, () -> Parser.parseDeadline("return book /by"));
        assertEquals("I'd love to help, but that deadline has no due date after the /by.", e.getMessage());
    }

    @Test
    public void parseDeadline_unreadableDate_exceptionThrown() {
        assertThrows(SallmanException.class, () -> Parser.parseDeadline("return book /by Sunday"));
    }

    @Test
    public void parseEvent_descriptionAndBothDates_taskCreated() throws Exception {
        Event event = (Event) Parser.parseEvent("conference /from 2019-10-14 /to 2019-10-17");
        assertEquals("[E][ ] conference (from: Oct 14 2019 to: Oct 17 2019)", event.toString());
    }

    @Test
    public void parseEvent_sameStartAndEnd_taskCreated() throws Exception {
        // A one-day event is legitimate, so the start-before-end check must
        // allow the two dates to be equal.
        Event event = (Event) Parser.parseEvent("standup /from 2019-10-15 /to 2019-10-15");
        assertEquals("[E][ ] standup (from: Oct 15 2019 to: Oct 15 2019)", event.toString());
    }

    @Test
    public void parseEvent_endBeforeStart_exceptionThrown() {
        String backwards = "oops /from 2019-10-20 /to 2019-10-10";
        SallmanException e = assertThrows(SallmanException.class, () -> Parser.parseEvent(backwards));
        assertEquals("As a large language model, I can't schedule an event that ends before it starts.",
                e.getMessage());
    }

    @Test
    public void parseEvent_missingParts_eachNamedSeparately() {
        assertEquals("I'm sorry, but I couldn't find a /from in that event.", eventErrorFor("meeting"));
        assertEquals("I'm sorry, but I couldn't find a /to in that event.", eventErrorFor("meeting /from 2019-10-15"));
        assertEquals("I'd love to help, but that event has no description before the /from.",
                eventErrorFor("/from 2019-10-15 /to 2019-10-16"));
        assertEquals("I'd love to help, but that event has no start time after the /from.",
                eventErrorFor("meeting /from /to 2019-10-16"));
        assertEquals("I'd love to help, but that event has no end time after the /to.",
                eventErrorFor("meeting /from 2019-10-15 /to"));
    }

    /**
     * Returns the message from the exception thrown by parsing the given event
     * arguments, so each case above states only its input and its message.
     *
     * @param arguments the text following the {@code event} keyword
     * @return the message of the exception that parsing it throws
     */
    private String eventErrorFor(String arguments) {
        return assertThrows(SallmanException.class, () -> Parser.parseEvent(arguments)).getMessage();
    }

    @Test
    public void parseTaskNumber_validNumber_zeroBasedIndexReturned() throws Exception {
        // The user counts from 1, the list counts from 0.
        assertEquals(0, Parser.parseTaskNumber("mark", "1", 3));
        assertEquals(2, Parser.parseTaskNumber("mark", "3", 3));
    }

    @Test
    public void parseTaskNumber_missingNumber_exceptionNamesTheCommand() {
        // The message quotes whichever command was used, since mark, unmark and
        // delete all share this method.
        assertEquals("I'd love to help, but mark needs a task number.",
                assertThrows(SallmanException.class, () -> Parser.parseTaskNumber("mark", "", 3)).getMessage());
        assertEquals("I'd love to help, but delete needs a task number.",
                assertThrows(SallmanException.class, () -> Parser.parseTaskNumber("delete", "", 3)).getMessage());
    }

    @Test
    public void parseTaskNumber_notANumber_exceptionQuotesInput() {
        SallmanException e = assertThrows(SallmanException.class, () -> Parser.parseTaskNumber("mark", "abc", 3));
        assertEquals("I'm sorry, but \"abc\" doesn't look like a task number.", e.getMessage());
    }

    @Test
    public void parseTaskNumber_outsideList_exceptionThrown() {
        // Both ends of the range, plus a negative, since an off-by-one here
        // would reach past the list or before it.
        assertThrows(SallmanException.class, () -> Parser.parseTaskNumber("mark", "0", 3));
        assertThrows(SallmanException.class, () -> Parser.parseTaskNumber("mark", "4", 3));
        assertThrows(SallmanException.class, () -> Parser.parseTaskNumber("mark", "-1", 3));
    }

    @Test
    public void parseTaskNumber_emptyList_exceptionSaysListIsEmpty() {
        SallmanException e = assertThrows(SallmanException.class, () -> Parser.parseTaskNumber("mark", "1", 0));
        assertEquals("As a large language model, I must point out that your list is empty, so there is no task 1.",
                e.getMessage());
    }

    @Test
    public void parseTags_leadingHashOptional_strippedEitherWay() throws Exception {
        assertEquals(List.of("fun", "books"), Parser.parseTags("tag", "fun #books"));
    }

    @Test
    public void parseTags_noTagGiven_rejected() {
        assertThrows(SallmanException.class, () -> Parser.parseTags("tag", ""));
        assertThrows(SallmanException.class, () -> Parser.parseTags("tag", "#"));
    }

    @Test
    public void parseTags_tagWithPunctuation_rejected() {
        // A tag sharing the file's field separator, or containing spaces,
        // could not be written and read back reliably.
        assertThrows(SallmanException.class, () -> Parser.parseTags("tag", "bad!tag"));
        assertThrows(SallmanException.class, () -> Parser.parseTags("tag", "a|b"));
    }

    @Test
    public void parseTags_hyphensAndUnderscores_accepted() throws Exception {
        assertEquals(List.of("in-tray", "read_later"),
                Parser.parseTags("tag", "in-tray read_later"));
    }

    @Test
    public void splitTaskNumberAndTags_numberThenTags_separated() throws Exception {
        assertArrayEquals(new String[] {"2", "fun books"},
                Parser.splitTaskNumberAndTags("tag", "2 fun books"));
    }

    @Test
    public void splitTaskNumberAndTags_nothingGiven_rejected() {
        assertThrows(SallmanException.class, () -> Parser.splitTaskNumberAndTags("tag", ""));
    }

    @Test
    public void parseSortOrder_noOrderGiven_sortsByDate() throws Exception {
        assertEquals(SortOrder.DATE, Parser.parseSortOrder(""));
    }

    @Test
    public void parseSortOrder_namedOrder_thatOrderChosen() throws Exception {
        assertEquals(SortOrder.NAME, Parser.parseSortOrder("name"));
        assertEquals(SortOrder.STATUS, Parser.parseSortOrder("status"));
    }

    @Test
    public void parseSortOrder_unknownOrder_rejectedAndListsTheKnownOnes() {
        SallmanException e = assertThrows(SallmanException.class, () -> Parser.parseSortOrder("sideways"));

        assertEquals("I'm sorry, but I don't know how to sort by \"sideways\".", e.getMessage());
        assertEquals("date, name, status", SortOrder.keywords());
    }

    @Test
    public void parseSearchKeyword_keywordGiven_returnedUnchanged() throws Exception {
        assertEquals("read book", Parser.parseSearchKeyword("read book"));
    }

    @Test
    public void parseSearchKeyword_noKeyword_exceptionThrown() {
        SallmanException e = assertThrows(SallmanException.class, () -> Parser.parseSearchKeyword(""));
        assertEquals("I'd love to help, but find needs something to search for.", e.getMessage());
    }

    @Test
    public void parseOnDate_validDate_dateReturned() throws Exception {
        assertEquals(LocalDate.of(2019, 10, 15), Parser.parseOnDate("2019-10-15"));
    }

    @Test
    public void parseOnDate_missingDate_exceptionThrown() {
        SallmanException e = assertThrows(SallmanException.class, () -> Parser.parseOnDate(""));
        assertEquals("I'd love to help, but on needs a date.", e.getMessage());
    }

    @Test
    public void parseDeadline_byGivenTwice_exceptionNamesTheRepeat() {
        String arguments = "pay bill /by 2019-10-15 /by 2019-10-16";
        SallmanException e = assertThrows(SallmanException.class, () -> Parser.parseDeadline(arguments));

        assertEquals("I'm sorry, but that deadline has more than one /by.", e.getMessage());
    }

    @Test
    public void parseDeadline_byJoinedToAWord_exceptionAsksForSpaces() {
        // Reporting the /by as missing would contradict what the user typed.
        String arguments = "return book/by 2019-10-15";
        SallmanException e = assertThrows(SallmanException.class, () -> Parser.parseDeadline(arguments));

        assertEquals("I'm sorry, but I need a space before and after the /by in that deadline.", e.getMessage());
    }

    @Test
    public void parseEvent_toBeforeFrom_exceptionNamesTheOrder() {
        // Without this check the /to disappears into the description and the
        // user is told, wrongly, that there is no /to.
        String arguments = "trip /to 2019-10-16 /from 2019-10-15";
        SallmanException e = assertThrows(SallmanException.class, () -> Parser.parseEvent(arguments));

        assertEquals("I'm sorry, but the /from has to come before the /to in that event.", e.getMessage());
    }

    @Test
    public void parseEvent_fromGivenTwice_exceptionNamesTheRepeat() {
        String arguments = "trip /from 2019-10-15 /from 2019-10-16 /to 2019-10-17";
        SallmanException e = assertThrows(SallmanException.class, () -> Parser.parseEvent(arguments));

        assertEquals("I'm sorry, but that event has more than one /from.", e.getMessage());
    }

    @Test
    public void parseEvent_markerInsideAWord_notTreatedAsAMarker() throws Exception {
        // "/tomorrow" starts with "/to" but is a word of its own, not the marker.
        Task event = Parser.parseEvent("plan /tomorrow trip /from 2019-10-15 /to 2019-10-16");

        assertEquals("[E][ ] plan /tomorrow trip (from: Oct 15 2019 to: Oct 16 2019)", event.toString());
    }

    @Test
    public void parseTodo_repeatedSpaces_collapsedToOne() throws Exception {
        assertEquals("[T][ ] read the book", Parser.parseTodo("read    the   book").toString());
    }

    @Test
    public void parseSearchKeyword_repeatedSpaces_collapsedToOne() throws Exception {
        assertEquals("read book", Parser.parseSearchKeyword("read     book"));
    }

    @Test
    public void parseTaskNumber_tooManyDigitsForAnInt_reportedAsOutOfRange() {
        // It is a number, just not one in the list, so "not a number" would be wrong.
        String number = "99999999999";
        SallmanException e = assertThrows(SallmanException.class, () -> Parser.parseTaskNumber("mark", number, 3));

        assertEquals("As a large language model, I must point out that there is no task 99999999999.", e.getMessage());
    }

    @Test
    public void parseTaskNumber_severalNumbers_exceptionAsksForOne() {
        String number = "1 2";
        SallmanException e = assertThrows(SallmanException.class, () -> Parser.parseTaskNumber("mark", number, 3));

        assertEquals("I'm sorry, but mark takes one task number at a time.", e.getMessage());
    }

    @Test
    public void parse_eachAddingCommand_routedToAnAddCommand() throws Exception {
        assertInstanceOf(AddCommand.class, Parser.parse("deadline return book /by 2019-10-15"));
        assertInstanceOf(AddCommand.class, Parser.parse("event trip /from 2019-10-15 /to 2019-10-16"));
        assertInstanceOf(UndoCommand.class, Parser.parse("undo"));
    }

    @Test
    public void parse_longKeywordTwoTyposAway_stillSuggested() {
        // Longer keywords allow two slips, since they are less likely to match by accident.
        SallmanException e = assertThrows(SallmanException.class, () -> Parser.parse("dleete"));

        assertEquals("Did you mean delete?", e.toLines()[1]);
    }

    @Test
    public void parse_typoEquallyCloseToTwoCommands_suggestsTheOneListedFirst() {
        // "tndo" is one slip from both todo and undo; the suggestion must not vary between runs.
        SallmanException e = assertThrows(SallmanException.class, () -> Parser.parse("tndo"));

        assertEquals("Did you mean todo?", e.toLines()[1]);
    }

    @Test
    public void splitTaskNumberAndTags_numberOnly_tagsEmpty() throws Exception {
        assertArrayEquals(new String[] {"2", ""}, Parser.splitTaskNumberAndTags("tag", "2"));
    }

    @Test
    public void parseTaskNumber_listOfOne_hintSaysOnlyTaskOne() {
        String number = "5";
        SallmanException e = assertThrows(SallmanException.class, () -> Parser.parseTaskNumber("mark", number, 1));

        assertEquals("You only have task 1.", e.toLines()[1]);
    }

    @Test
    public void parse_extraWordsAfterACommandThatTakesNone_refused() {
        // Ignoring them would mislead: "undo twice" would undo only once.
        for (String input : new String[] {"list nonsense", "undo twice", "bye later"}) {
            SallmanException e = assertThrows(SallmanException.class, () -> Parser.parse(input));
            assertTrue(e.getMessage().endsWith("doesn't take anything after it."), input);
        }
    }

    @Test
    public void parse_keywordInAnotherCase_recognized() throws Exception {
        assertInstanceOf(ListCommand.class, Parser.parse("LIST"));
        assertInstanceOf(AddCommand.class, Parser.parse("Todo read book"));
    }

    @Test
    public void parse_keywordOneTypoAway_suggestsTheCommand() {
        SallmanException e = assertThrows(SallmanException.class, () -> Parser.parse("lst"));

        assertEquals("Did you mean list?", e.toLines()[1]);
    }

    @Test
    public void parse_keywordFarFromAnyCommand_listsEveryCommand() {
        // Guessing from a word this far off would suggest something unrelated.
        SallmanException e = assertThrows(SallmanException.class, () -> Parser.parse("blah"));

        assertTrue(e.toLines()[1].startsWith("I understand: todo, deadline"));
    }
}
