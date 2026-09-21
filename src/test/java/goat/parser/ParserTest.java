package goat.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import goat.GoatException;
import goat.command.AddCommand;
import goat.command.Command;
import goat.command.DeleteCommand;
import goat.command.ExitCommand;
import goat.command.FindCommand;
import goat.command.ListCommand;
import goat.command.MarkCommand;
import goat.command.SortCommand;
import goat.storage.Storage;
import goat.task.TaskList;
import goat.ui.Ui;

/**
 * Tests the step that turns a typed line into the command it asks for.
 *
 * This is the widest method in the program -- every line a user types reaches
 * it -- so the cases below cover each keyword, each way an argument can be
 * missing or malformed, and the wording of the complaints, since those are
 * what a user actually sees.
 *
 * A few tests run the command they parsed. That is the only way to check the
 * command was built around the right values, since a Command deliberately
 * keeps what it holds to itself.
 */
public class ParserTest {

    /**
     * Runs a command against a throwaway list and save file, and returns the
     * list so the effect can be checked. Output is swallowed, because Ui
     * writes to System.out and a test has no use for it.
     */
    private static TaskList execute(Command command, Path folder) throws GoatException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(folder.resolve("goat.txt").toString());
        PrintStream original = System.out;
        try {
            System.setOut(new PrintStream(new ByteArrayOutputStream()));
            command.execute(tasks, new Ui(), storage);
        } finally {
            System.setOut(original);
        }
        return tasks;
    }

    @Test
    public void parse_bye_returnsExitCommand() throws GoatException {
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
    }

    @Test
    public void parse_bye_isExitTrue() throws GoatException {
        assertTrue(Parser.parse("bye").isExit());
    }

    @Test
    public void parse_anythingElse_isExitFalse() throws GoatException {
        assertFalse(Parser.parse("list").isExit());
        assertFalse(Parser.parse("todo read book").isExit());
    }

    @Test
    public void parse_list_returnsListCommand() throws GoatException {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
    }

    @Test
    public void parse_todo_returnsAddCommand() throws GoatException {
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
    }

    @Test
    public void parse_deadline_returnsAddCommand() throws GoatException {
        assertInstanceOf(AddCommand.class, Parser.parse("deadline return book /by 2019-12-02"));
    }

    @Test
    public void parse_event_returnsAddCommand() throws GoatException {
        assertInstanceOf(AddCommand.class,
                Parser.parse("event meeting /from 2019-08-06 1400 /to 2019-08-06 1600"));
    }

    @Test
    public void parse_mark_returnsMarkCommand() throws GoatException {
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
    }

    @Test
    public void parse_unmark_returnsMarkCommand() throws GoatException {
        assertInstanceOf(MarkCommand.class, Parser.parse("unmark 1"));
    }

    @Test
    public void parse_delete_returnsDeleteCommand() throws GoatException {
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
    }

    @Test
    public void parse_find_returnsFindCommand() throws GoatException {
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    @Test
    public void parse_findWithNoKeyword_exceptionThrown() {
        // A search for nothing would match every task, which is not what
        // anyone typing "find" on its own means.
        GoatException e = assertThrows(GoatException.class, () -> Parser.parse("find"));
        assertTrue(e.getMessage().contains("Try: find book"), e.getMessage());
    }

    @Test
    public void parse_findWithMultiWordKeyword_accepted() throws GoatException {
        // The keyword is everything after the command word, spaces included,
        // so a phrase is not truncated at the first space.
        assertInstanceOf(FindCommand.class, Parser.parse("find read book"));
    }

    @Test
    public void parse_sortByDate_returnsSortCommand() throws GoatException {
        assertInstanceOf(SortCommand.class, Parser.parse("sort date"));
    }

    @Test
    public void parse_sortByName_returnsSortCommand() throws GoatException {
        assertInstanceOf(SortCommand.class, Parser.parse("sort name"));
    }

    @Test
    public void parse_sortWithNoOrder_exceptionSuggestsBoth() {
        GoatException e = assertThrows(GoatException.class, () -> Parser.parse("sort"));
        assertTrue(e.getMessage().contains("sort date"), e.getMessage());
        assertTrue(e.getMessage().contains("sort name"), e.getMessage());
    }

    @Test
    public void parse_sortByUnknownOrder_exceptionThrown() {
        assertThrows(GoatException.class, () -> Parser.parse("sort priority"));
    }

    @Test
    public void parse_unknownKeyword_exceptionThrown() {
        assertThrows(GoatException.class, () -> Parser.parse("blah"));
    }

    @Test
    public void parse_emptyLine_exceptionThrown() {
        assertThrows(GoatException.class, () -> Parser.parse(""));
    }

    @Test
    public void parse_keywordWithWrongCase_exceptionThrown() {
        // Commands are matched exactly; "Bye" is not "bye".
        assertThrows(GoatException.class, () -> Parser.parse("BYE"));
    }

    @Test
    public void parse_keywordAsAPrefixOfTheTypedWord_exceptionThrown() {
        // "listen" must not be taken for "list" with an argument.
        assertThrows(GoatException.class, () -> Parser.parse("listen"));
    }

    @Test
    public void parse_todoWithNoDescription_exceptionThrown() {
        // A bare "todo" is the todo command missing its description, not an
        // unknown command.
        GoatException e = assertThrows(GoatException.class, () -> Parser.parse("todo"));
        assertTrue(e.getMessage().contains("Try: todo read book"), e.getMessage());
    }

    @Test
    public void parse_todoWithOnlySpaces_exceptionThrown() {
        assertThrows(GoatException.class, () -> Parser.parse("todo    "));
    }

    @Test
    public void parse_deadlineWithNoBy_exceptionThrown() {
        assertThrows(GoatException.class, () -> Parser.parse("deadline return book"));
    }

    @Test
    public void parse_deadlineWithNoDescription_exceptionThrown() {
        assertThrows(GoatException.class, () -> Parser.parse("deadline /by 2019-12-02"));
    }

    @Test
    public void parse_deadlineWithNothingAfterBy_exceptionThrown() {
        assertThrows(GoatException.class, () -> Parser.parse("deadline return book /by"));
    }

    @Test
    public void parse_deadlineWithUnreadableDate_exceptionThrown() {
        GoatException e = assertThrows(GoatException.class, () ->
                Parser.parse("deadline pay fees /by next Friday"));
        assertTrue(e.getMessage().contains("next Friday"), e.getMessage());
    }

    @Test
    public void parse_eventWithNoFrom_exceptionThrown() {
        assertThrows(GoatException.class, () -> Parser.parse("event meeting /to 2019-08-06 1600"));
    }

    @Test
    public void parse_eventWithNoTo_exceptionThrown() {
        assertThrows(GoatException.class, () -> Parser.parse("event meeting /from 2019-08-06 1400"));
    }

    @Test
    public void parse_eventWithNoDescription_exceptionThrown() {
        assertThrows(GoatException.class, () ->
                Parser.parse("event /from 2019-08-06 1400 /to 2019-08-06 1600"));
    }

    @Test
    public void parse_markWithNoNumber_messageMentionsMarking() {
        GoatException e = assertThrows(GoatException.class, () -> Parser.parse("mark"));
        assertEquals("Which one? Try: mark 1", e.getMessage());
    }

    @Test
    public void parse_unmarkWithNoNumber_messageMentionsMarking() {
        GoatException e = assertThrows(GoatException.class, () -> Parser.parse("unmark"));
        assertEquals("Which one? Try: mark 1", e.getMessage());
    }

    @Test
    public void parse_deleteWithNoNumber_messageMentionsDeleting() {
        // The wording differs from mark's, which is why the check knows the
        // command it came from.
        GoatException e = assertThrows(GoatException.class, () -> Parser.parse("delete"));
        assertEquals("Which one? Try: delete 1", e.getMessage());
    }

    @Test
    public void parse_markWithNonNumber_exceptionQuotesWhatWasTyped() {
        GoatException e = assertThrows(GoatException.class, () -> Parser.parse("mark abc"));
        assertTrue(e.getMessage().contains("'abc'"), e.getMessage());
    }

    @Test
    public void parse_markWithNumberOutOfRange_acceptedHereAndRejectedOnExecute(
            @TempDir Path folder) throws GoatException {
        // Parsing only reads the number; whether a task 99 exists is the
        // list's business, so this must not throw until the command runs.
        Command command = Parser.parse("mark 99");
        assertThrows(GoatException.class, () -> execute(command, folder));
    }

    @Test
    public void parse_todo_addCommandCarriesTheDescription(@TempDir Path folder)
            throws GoatException {
        TaskList tasks = execute(Parser.parse("todo read book"), folder);

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(1).toString());
    }

    @Test
    public void parse_deadline_addCommandCarriesDescriptionAndDate(@TempDir Path folder)
            throws GoatException {
        TaskList tasks = execute(Parser.parse("deadline return book /by 2019-12-02 1800"), folder);

        assertEquals("[D][ ] return book (by: Dec 02 2019, 6:00pm)", tasks.get(1).toString());
    }

    @Test
    public void parse_event_addCommandCarriesBothDatesInOrder(@TempDir Path folder)
            throws GoatException {
        TaskList tasks = execute(
                Parser.parse("event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600"),
                folder);

        assertEquals("[E][ ] project meeting (from: Aug 06 2019, 2:00pm to: Aug 06 2019, 4:00pm)",
                tasks.get(1).toString());
    }

    @Test
    public void parse_descriptionWithExtraSpaces_trimmed(@TempDir Path folder)
            throws GoatException {
        TaskList tasks = execute(Parser.parse("todo    read book   "), folder);
        assertEquals("[T][ ] read book", tasks.get(1).toString());
    }

    @Test
    public void parse_deadlineDescriptionWithSpacesBeforeBy_trimmed(@TempDir Path folder)
            throws GoatException {
        TaskList tasks = execute(Parser.parse("deadline return book   /by   2019-12-02"), folder);
        assertEquals("[D][ ] return book (by: Dec 02 2019)", tasks.get(1).toString());
    }
}
