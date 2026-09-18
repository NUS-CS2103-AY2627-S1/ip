package hermes.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import hermes.HermesException;
import hermes.command.AddCommand;
import hermes.command.ByeCommand;
import hermes.command.DeleteCommand;
import hermes.command.DueCommand;
import hermes.command.FindCommand;
import hermes.command.ListCommand;
import hermes.command.MarkCommand;
import hermes.command.SortCommand;
import hermes.command.UndoCommand;
import hermes.command.UnknownCommand;
import hermes.command.UnmarkCommand;
import hermes.task.LogBook;
import hermes.task.Storage;

/**
 * Tests turning a line of input into a command.
 *
 * <p>A command keeps what it was given to itself, so where the value read from
 * the input matters, the command is run against a LogBook backed by a throwaway
 * file and the result is checked instead.
 */
public class ParserTest {

    private static final String SEPARATOR_MESSAGE = "Forgive me, but a task may not contain '|', "
            + "for I use that mark to divide the fields of thy scroll when I save it.";

    @TempDir
    Path tempDir;

    private final Parser parser = new Parser();
    private LogBook logBook;

    @BeforeEach
    public void setUp() {
        logBook = new LogBook(new Storage(tempDir.resolve("Hermes.txt").toString()));
    }

    /** Parses and runs each line in turn, returning what the last one replied. */
    private String run(String... inputs) throws HermesException {
        String reply = "";
        for (String input : inputs) {
            reply = parser.parse(input).execute(logBook);
        }
        return reply;
    }

    private String errorFrom(String input) {
        return assertThrows(HermesException.class, () -> parser.parse(input)).getMessage();
    }

    // ---- choosing the command ----

    @Test
    public void parse_eachCommandWord_buildsMatchingCommand() throws HermesException {
        assertInstanceOf(ByeCommand.class, parser.parse("bye"));
        assertInstanceOf(ListCommand.class, parser.parse("list"));
        assertInstanceOf(MarkCommand.class, parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, parser.parse("delete 1"));
        assertInstanceOf(AddCommand.class, parser.parse("todo read"));
        assertInstanceOf(AddCommand.class, parser.parse("deadline essay /by 27 Aug 2026 1500"));
        assertInstanceOf(AddCommand.class,
                parser.parse("event talk /from 27 Aug 2026 1500 /to 27 Aug 2026 1600"));
        assertInstanceOf(DueCommand.class, parser.parse("due /by 27 Aug 2026 1500"));
        assertInstanceOf(SortCommand.class, parser.parse("sort"));
        assertInstanceOf(FindCommand.class, parser.parse("find book"));
        assertInstanceOf(UndoCommand.class, parser.parse("undo"));
    }

    @Test
    public void parse_unrecognisedWord_buildsUnknownCommandQuotingIt() throws HermesException {
        assertInstanceOf(UnknownCommand.class, parser.parse(""));
        assertEquals("Alas, 'dance' is a word unknown even upon Olympus. I cannot act upon it.",
                run("dance wildly"));
        assertEquals("Alas, 'BYE' is a word unknown even upon Olympus. I cannot act upon it.", run("BYE"));
    }

    @Test
    public void parse_bye_onlyCommandThatEndsTheConversation() throws HermesException {
        assertTrue(parser.parse("bye").isExit());
        assertFalse(parser.parse("list").isExit());
        assertFalse(parser.parse("dance").isExit());
    }

    // ---- task numbers ----

    @Test
    public void parse_taskNumber_countsFromOne() throws HermesException {
        run("todo read", "todo write", "mark 2");

        assertEquals("1. [T][ ] read\n2. [T][X] write\n", logBook.listTasks());
    }

    @Test
    public void parse_severalTaskNumbers_eachNamedTaskAffected() throws HermesException {
        run("todo read", "todo write", "todo rest", "delete 3 1");

        assertEquals("1. [T][ ] write\n", logBook.listTasks());
    }

    @Test
    public void parse_sameTaskNumberInTwoSpellings_countedOnce() throws HermesException {
        // Repeats are dropped once converted, so 1 and 01 name the same single task.
        run("todo read", "todo write", "delete 1 01");

        assertEquals("1. [T][ ] write\n", logBook.listTasks());
    }

    @Test
    public void parse_markOrUnmarkWithoutNumber_asksWhichTask() {
        assertEquals("Please name which task, for instance: " + Keyword.MARK.getExample(), errorFrom("mark"));
        assertEquals("Please name which task, for instance: " + Keyword.UNMARK.getExample(),
                errorFrom("unmark"));
    }

    @Test
    public void parse_deleteWithoutNumber_asksWhichTaskToRemove() {
        assertEquals("Please name which task thou wouldst have removed, for instance: "
                + Keyword.DELETE.getExample(), errorFrom("delete"));
    }

    @Test
    public void parse_taskNumberNotANumber_quotesWhatWasGiven() {
        assertEquals("'abc' is not a task number. I carry messages, not riddles.", errorFrom("mark abc"));
        assertEquals("'1 x' is not a task number. I carry messages, not riddles.", errorFrom("delete 1 x"));
        assertEquals("'99999999999' is not a task number. I carry messages, not riddles.",
                errorFrom("unmark 99999999999"));
    }

    // ---- todo ----

    @Test
    public void parse_todo_addsTrimmedDescription() throws HermesException {
        run("todo   read the book  ");

        assertEquals("1. [T][ ] read the book\n", logBook.listTasks());
    }

    @Test
    public void parse_todoWithoutDescription_exceptionThrown() {
        String expected = "A todo must have a description, for instance: " + Keyword.TODO.getExample();

        assertEquals(expected, errorFrom("todo"));
        assertEquals(expected, errorFrom("todo    "));
    }

    @Test
    public void parse_todoWithSeparator_exceptionThrown() {
        assertEquals(SEPARATOR_MESSAGE, errorFrom("todo a | b"));
    }

    // ---- deadline ----

    @Test
    public void parse_deadline_addsDescriptionAndDate() throws HermesException {
        run("deadline essay   /by   27 Aug 2026 1500");

        assertEquals("1. [D][ ] essay (by: 27/08/2026 1500)\n", logBook.listTasks());
    }

    @Test
    public void parse_deadlineWithoutDescription_exceptionThrown() {
        assertEquals("A deadline must have a description, for instance: " + Keyword.DEADLINE.getExample(),
                errorFrom("deadline"));
    }

    @Test
    public void parse_deadlineWithoutByDate_exceptionThrown() {
        assertEquals("Every deadline needs its /by date, lest it be forgotten, for instance: "
                + Keyword.DEADLINE.getExample(), errorFrom("deadline clean toilet"));
    }

    @Test
    public void parse_deadlineWithEmptyPart_asksForBoth() {
        String expected = "A deadline must have both a description and a /by date, for instance: "
                + Keyword.DEADLINE.getExample();

        assertEquals(expected, errorFrom("deadline /by 27 Aug 2026 1500"));
        assertEquals(expected, errorFrom("deadline essay /by"));
    }

    @Test
    public void parse_deadlineWithByTwice_saysWhichParameterIsRepeated() {
        assertEquals("I see /by more than once. Name it but once, for instance: "
                + Keyword.DEADLINE.getExample(),
                errorFrom("deadline essay /by 27 Aug 2026 1500 /by 28 Aug 2026 1500"));
    }

    @Test
    public void parse_deadlineWithUnreadableDate_quotesTheDate() {
        assertEquals("'next week' is no date I can read. Write it thus, for instance: 17/08/2026 1500",
                errorFrom("deadline essay /by next week"));
    }

    @Test
    public void parse_deadlineWithSeparator_exceptionThrown() {
        assertEquals(SEPARATOR_MESSAGE, errorFrom("deadline a|b /by 27 Aug 2026 1500"));
    }

    // ---- event ----

    @Test
    public void parse_event_addsDescriptionStartAndEnd() throws HermesException {
        run("event talk /from 27 Aug 2026 1500 /to 27 Aug 2026 1630");

        assertEquals("1. [E][ ] talk (from: 27/08/2026 1500 to: 27/08/2026 1630)\n", logBook.listTasks());
    }

    @Test
    public void parse_eventWithoutDescription_exceptionThrown() {
        assertEquals("An event must have a description, for instance: " + Keyword.EVENT.getExample(),
                errorFrom("event"));
    }

    @Test
    public void parse_eventWithoutFromDate_exceptionThrown() {
        assertEquals("An event must have a /from time, for instance: " + Keyword.EVENT.getExample(),
                errorFrom("event toilet cleaning"));
    }

    @Test
    public void parse_eventWithoutToDate_exceptionThrown() {
        assertEquals("Every road must end somewhere; give thy event a /to time, for instance: "
                + Keyword.EVENT.getExample(), errorFrom("event talk /from 27 Aug 2026 1500"));
    }

    @Test
    public void parse_eventWithAnyPartEmpty_asksForAllThree() {
        String expected = "An event must have a description, a /from time and a /to time, for instance: "
                + Keyword.EVENT.getExample();

        assertEquals(expected, errorFrom("event /from 27 Aug 2026 1500 /to 27 Aug 2026 1630"));
        assertEquals(expected, errorFrom("event talk /from /to 27 Aug 2026 1630"));
        assertEquals(expected, errorFrom("event talk /from 27 Aug 2026 1500 /to"));
    }

    @Test
    public void parse_eventWithFromOrToTwice_saysWhichParameterIsRepeated() {
        String from = "I see /from more than once. Name it but once, for instance: "
                + Keyword.EVENT.getExample();
        String to = "I see /to more than once. Name it but once, for instance: "
                + Keyword.EVENT.getExample();

        assertEquals(from, errorFrom("event talk /from 27 Aug 2026 1500 /from 27 Aug 2026 1600 "
                + "/to 27 Aug 2026 1700"));
        assertEquals(to, errorFrom("event talk /from 27 Aug 2026 1500 /to 27 Aug 2026 1600 "
                + "/to 27 Aug 2026 1700"));
    }

    @Test
    public void parse_eventWithUnreadableEnd_quotesTheDate() {
        assertEquals("'soon' is no date I can read. Write it thus, for instance: 17/08/2026 1500",
                errorFrom("event talk /from 27 Aug 2026 1500 /to soon"));
    }

    @Test
    public void parse_eventEndingBeforeItBegins_refusedQuotingBothTimes() {
        assertEquals("An event must end after it begins, yet thou hast set its end "
                + "to '27 Aug 2026 1500' and its start to '27 Aug 2026 1600'.",
                errorFrom("event talk /from 27 Aug 2026 1600 /to 27 Aug 2026 1500"));
    }

    @Test
    public void parse_eventEndingWhenItBegins_refused() {
        assertEquals("An event must end after it begins, yet thou hast set its end "
                + "to '27 Aug 2026 1500' and its start to '27 Aug 2026 1500'.",
                errorFrom("event talk /from 27 Aug 2026 1500 /to 27 Aug 2026 1500"));
    }

    @Test
    public void parse_eventEndingAfterItBegins_accepted() throws HermesException {
        run("event talk /from 27 Aug 2026 1500 /to 27 Aug 2026 1501");

        assertEquals("1. [E][ ] talk (from: 27/08/2026 1500 to: 27/08/2026 1501)\n",
                logBook.listTasks());
    }

    @Test
    public void parse_eventWithSeparator_exceptionThrown() {
        assertEquals(SEPARATOR_MESSAGE, errorFrom("event a|b /from 27 Aug 2026 1500 /to 27 Aug 2026 1630"));
    }

    // ---- due ----

    @Test
    public void parse_due_measuresAgainstTheGivenDate() throws HermesException {
        run("deadline essay /by 26 Aug 2026 0900", "deadline later /by 28 Aug 2026 0900");

        assertEquals("[D][ ] essay (by: 26/08/2026 0900)", run("due /by 27 Aug 2026 1500"));
    }

    @Test
    public void parse_dueWithoutDate_asksForOne() {
        String expected = "Name the hour to reckon by with /by, for instance: " + Keyword.DUE.getExample();

        assertEquals(expected, errorFrom("due"));
        assertEquals(expected, errorFrom("due /by"));
    }

    @Test
    public void parse_dueWithByTwice_saysWhichParameterIsRepeated() {
        assertEquals("I see /by more than once. Name it but once, for instance: "
                + Keyword.DUE.getExample(),
                errorFrom("due /by 27 Aug 2026 1500 /by 28 Aug 2026 1500"));
    }

    @Test
    public void parse_dueWithUnreadableDate_quotesTheDate() {
        assertEquals("'never' is no date I can read. Write it thus, for instance: 17/08/2026 1500",
                errorFrom("due /by never"));
    }

    // ---- find ----

    @Test
    public void parse_find_searchesIgnoringCaseOfTheKeyword() throws HermesException {
        run("todo borrow book", "todo write");

        assertEquals("[T][ ] borrow book", run("find BOOK"));
    }

    @Test
    public void parse_findWithSeveralWords_asksForOne() {
        assertEquals("Pardon me, but I seek only one word at a time, for instance: "
                + Keyword.FIND.getExample(), errorFrom("find two words"));
    }

    @Test
    public void parse_findWithoutWord_asksForOne() {
        assertEquals("Pardon me, but I cannot seek what thou hast not named, for instance: "
                + Keyword.FIND.getExample(), errorFrom("find"));
    }
}
