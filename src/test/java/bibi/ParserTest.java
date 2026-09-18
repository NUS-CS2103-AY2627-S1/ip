package bibi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bibi.command.AddCommand;
import bibi.command.Command;
import bibi.command.DeleteCommand;
import bibi.command.ExitCommand;
import bibi.command.FindCommand;
import bibi.command.HelpCommand;
import bibi.command.ListCommand;
import bibi.command.MarkCommand;
import bibi.command.OnCommand;
import bibi.command.SocialCommand;
import bibi.command.UndoCommand;
import bibi.command.UnmarkCommand;
import bibi.task.TaskList;
import bibi.task.Todo;

/**
 * Tests that typed input is turned into the right command, and that malformed
 * input is refused before any command is built.
 */
public class ParserTest {

    @Test
    public void parse_wordOnlyCommands_matchedWithoutArguments() throws BibiException {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(HelpCommand.class, Parser.parse("help"));
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
        assertInstanceOf(UndoCommand.class, Parser.parse("undo"));
    }

    @Test
    public void parse_mixedCaseCommandWord_stillMatched() throws BibiException {
        assertInstanceOf(ListCommand.class, Parser.parse("LIST"));
        assertInstanceOf(ExitCommand.class, Parser.parse("Bye"));
        assertInstanceOf(AddCommand.class, Parser.parse("ToDo read book"));
    }

    @Test
    public void parse_surroundingSpaces_ignored() throws BibiException {
        assertInstanceOf(ListCommand.class, Parser.parse("   list   "));
    }

    @Test
    public void parse_exitCommand_isExitTrue() throws BibiException {
        assertTrue(Parser.parse("bye").isExit());
    }

    @Test
    public void parse_otherCommands_isExitFalse() throws BibiException {
        assertFalse(Parser.parse("list").isExit());
        assertFalse(Parser.parse("todo read book").isExit());
    }

    @Test
    public void parse_addCommands_allThreeTaskTypesBecomeAddCommand() throws BibiException {
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class, Parser.parse("deadline return book /by 2019-10-15"));
        assertInstanceOf(AddCommand.class,
                Parser.parse("event camp /from 2019-08-10 /to 2019-08-12"));
    }

    @Test
    public void parse_numberedCommands_matchedWithTheirNumber() throws BibiException {
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 2"));
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 2"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("remove 2"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 2"));
    }

    @Test
    public void parse_findCommand_matched() throws BibiException {
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    @Test
    public void parse_findKeywordWithSpaces_keptWhole(@TempDir Path tempDir) throws BibiException {
        TaskList tasks = new TaskList(new Todo("join sports club"), new Todo("book club"));
        Ui ui = new Ui();
        ui.startCapture();

        Parser.parse("find sports club").execute(tasks, ui, new Storage(tempDir.resolve("bibi.txt")));
        String said = ui.takeCapturedReply().text();

        // The whole phrase is the keyword, so a task sharing only one of its
        // words must not match.
        assertTrue(said.contains("1. [T][ ] join sports club"));
        assertFalse(said.contains("book club"));
    }

    @Test
    public void parse_findWithoutKeyword_exceptionThrown() {
        BibiException thrown = assertThrows(BibiException.class, () -> Parser.parse("find"));
        assertEquals("Use find followed by a keyword, for example: find book",
                thrown.getMessage());
        assertThrows(BibiException.class, () -> Parser.parse("find    "));
    }

    @Test
    public void parse_onCommand_matched() throws BibiException {
        assertInstanceOf(OnCommand.class, Parser.parse("on 2019-08-11"));
    }

    @Test
    public void parse_emptyInput_exceptionThrown() {
        BibiException thrown = assertThrows(BibiException.class, () -> Parser.parse(""));
        assertEquals("Please enter a command.", thrown.getMessage());
        assertThrows(BibiException.class, () -> Parser.parse("    "));
    }

    @Test
    public void parse_unknownCommand_exceptionThrown() {
        BibiException thrown = assertThrows(BibiException.class, () -> Parser.parse("remind me"));
        // The message now names the word it could not place, which is the part
        // that helps a user who simply mistyped.
        assertTrue(thrown.getMessage().contains("I don't recognize 'remind'"));
    }

    @Test
    public void parse_commandWordAsPrefixOfAnotherWord_notMatched() {
        // "listen" starts with "list" but is not the list command.
        assertThrows(BibiException.class, () -> Parser.parse("listen"));
    }

    @Test
    public void parse_todoWithoutDescription_exceptionThrown() {
        BibiException thrown = assertThrows(BibiException.class, () -> Parser.parse("todo"));
        assertTrue(thrown.getMessage().contains("A task needs a description. Use todo <description>"));
    }

    @Test
    public void parse_deadlineWithoutByMarker_exceptionThrown() {
        BibiException thrown = assertThrows(BibiException.class, () ->
                Parser.parse("deadline return book"));
        assertTrue(thrown.getMessage().startsWith("I need a due date marked with /by. "
                + "Use deadline <description> /by <time>"));
    }

    @Test
    public void parse_deadlineWithUnreadableDate_exceptionThrown() {
        assertThrows(BibiException.class, () -> Parser.parse("deadline return book /by someday"));
    }

    @Test
    public void parse_eventMissingToMarker_exceptionThrown() {
        BibiException thrown = assertThrows(BibiException.class, () ->
                Parser.parse("event camp /from 2019-08-10"));
        assertTrue(thrown.getMessage()
                .startsWith("I need both /from and /to dates for an event. "
                + "Use event <description> /from <start> /to <end>"));
    }

    @Test
    public void parse_eventEndingBeforeItStarts_exceptionThrown() {
        BibiException thrown = assertThrows(BibiException.class, () ->
                Parser.parse("event camp /from 2019-08-12 /to 2019-08-10"));
        assertEquals("An event cannot end before it starts. "
                + "Use event <description> /from <start> /to <end>, for example: "
                + "event study group /from 21/12/2026 1800 /to 21/12/2026 2000.", thrown.getMessage());
    }

    @Test
    public void parse_taskNumberNotANumber_exceptionThrown() {
        BibiException thrown = assertThrows(BibiException.class, () -> Parser.parse("mark two"));
        assertTrue(thrown.getMessage().contains("'two' is not a task number"));
    }

    @Test
    public void parse_taskNumberMissing_exceptionNamesTheWordTyped() {
        // remove and delete are one command, so the message has to echo the word
        // the user actually typed. Naming the other one points them at a command
        // they did not use, which is worse than saying nothing.
        BibiException afterRemove = assertThrows(BibiException.class, () -> Parser.parse("remove"));
        assertTrue(afterRemove.getMessage().contains("Use remove followed by a task number"));
        assertFalse(afterRemove.getMessage().contains("delete"));

        BibiException afterDelete = assertThrows(BibiException.class, () -> Parser.parse("delete"));
        assertTrue(afterDelete.getMessage().contains("Use delete followed by a task number"));
        assertFalse(afterDelete.getMessage().contains("remove"));
    }

    @Test
    public void parse_taskNumberUnreadable_exceptionNamesTheWordTyped() {
        // A different branch of parseTaskNumber from the test above, worded
        // "one number" rather than "a task number", so the two do not overlap.
        BibiException thrown = assertThrows(BibiException.class, () -> Parser.parse("delete two"));
        assertTrue(thrown.getMessage().contains("Use delete followed by one number"));
    }

    @Test
    public void parse_onWithUnreadableDate_exceptionThrown() {
        assertThrows(BibiException.class, () -> Parser.parse("on someday"));
    }

    @Test
    public void parse_onWithoutADate_exceptionNamesTheCommandForm() {
        // The date parser alone would say only that a date is missing, which
        // leaves out the one thing the user needs: how to ask the question.
        BibiException thrown = assertThrows(BibiException.class, () -> Parser.parse("on"));
        assertEquals("I need a date to look up. Use on <date>, for example: on 21/12/2026.",
                thrown.getMessage());
        assertThrows(BibiException.class, () -> Parser.parse("on    "));
    }

    @Test
    public void parse_addCommand_taskIsBuiltFromTheInput(@TempDir Path tempDir)
            throws BibiException {
        // The parser is responsible for building the task, so executing the
        // command should add exactly what was typed.
        TaskList tasks = new TaskList();
        Command command = Parser.parse("deadline return book /by 2/12/2019 1800");
        command.execute(tasks, new Ui(), new Storage(tempDir.resolve("bibi.txt")));

        assertEquals(1, tasks.size());
        assertEquals("[D][ ] return book (by: 02 Dec 2019 6:00PM)", tasks.get(1).toString());
    }

    @Test
    public void parse_tabBetweenCommandAndArgument_accepted() throws BibiException {
        // Any run of whitespace separates the command word from its argument,
        // so a tab is read as the user meant it rather than as an unknown word.
        assertInstanceOf(AddCommand.class, Parser.parse("todo\tread book"));
    }

    @Test
    public void parse_repeatedInnerSpaces_collapsedInDescription(@TempDir Path tempDir)
            throws BibiException {
        TaskList tasks = new TaskList();
        Parser.parse("todo   read    book")
                .execute(tasks, new Ui(), new Storage(tempDir.resolve("bibi.txt")));

        assertEquals("[T][ ] read book", tasks.get(1).toString());
    }

    @Test
    public void parse_byGivenTwice_exceptionThrown() {
        BibiException thrown = assertThrows(BibiException.class, () ->
                Parser.parse("deadline a /by 2019-10-15 /by 2019-11-11"));
        assertTrue(thrown.getMessage().contains("You used /by 2 times"));
    }

    @Test
    public void parse_toGivenTwice_exceptionThrown() {
        BibiException thrown = assertThrows(BibiException.class, () ->
                Parser.parse("event a /from 2019-08-06 /to 2019-08-07 /to 2019-08-08"));
        assertTrue(thrown.getMessage().contains("You used /to 2 times"));
    }

    @Test
    public void parse_toBeforeFrom_exceptionNamesTheOrder() {
        BibiException thrown = assertThrows(BibiException.class, () ->
                Parser.parse("event a /to 2019-08-07 /from 2019-08-06"));
        assertTrue(thrown.getMessage().contains("/to came before the /from"));
    }

    @Test
    public void parse_argumentAfterArgumentlessCommand_exceptionThrown() {
        for (String input : new String[] {"list extra", "sort now", "undo now", "bye now"}) {
            BibiException thrown = assertThrows(BibiException.class, () -> Parser.parse(input));
            assertTrue(thrown.getMessage().contains("does not take anything after it"),
                    "no complaint for: " + input);
        }
    }

    @Test
    public void parse_helpExamplesFlag_accepted() throws BibiException {
        // The flag is the one optional argument Bibi has, so it is matched the
        // way every other command word is: without regard to case.
        assertInstanceOf(HelpCommand.class, Parser.parse("help --examples"));
        assertInstanceOf(HelpCommand.class, Parser.parse("help --EXAMPLES"));
        assertInstanceOf(HelpCommand.class, Parser.parse("help   --examples"));
    }

    @Test
    public void parse_helpWithAnUnknownArgument_exceptionNamesTheFlag() {
        // Showing the short help anyway would look like the flag had worked.
        for (String input : new String[] {"help me", "help --example", "help examples", "help -x"}) {
            BibiException thrown = assertThrows(BibiException.class, () -> Parser.parse(input));
            assertTrue(thrown.getMessage().contains("help takes nothing or --examples"),
                    "no complaint for: " + input);
        }
    }

    @Test
    public void parse_taskNumberTooLargeForAnInt_exceptionSaysItIsOutOfRange() {
        // The digits are a number, just not one a task can have, so the reply
        // must not claim they are not a number at all.
        BibiException thrown = assertThrows(BibiException.class, () -> Parser.parse("mark 99999999999"));
        assertTrue(thrown.getMessage().contains("Task numbers do not go as high as 99999999999"));
        assertFalse(thrown.getMessage().contains("is not a task number"));
    }

    @Test
    public void parse_negativeTaskNumberTooLargeForAnInt_exceptionExplainsNumbering() {
        BibiException thrown = assertThrows(BibiException.class, () -> Parser.parse("mark -99999999999"));
        assertTrue(thrown.getMessage().contains("Task numbers start at 1, so -99999999999"));
    }

    @Test
    public void parse_taskNumberThatIsNotDigits_exceptionSaysSo() {
        for (String input : new String[] {"mark two", "mark 1.5", "mark 1a", "mark --"}) {
            BibiException thrown = assertThrows(BibiException.class, () -> Parser.parse(input));
            assertTrue(thrown.getMessage().contains("is not a task number"), "no complaint for: " + input);
        }
    }

    @Test
    public void parse_taskNumberBelowOne_exceptionExplainsNumbering() {
        for (String input : new String[] {"mark 0", "unmark -1", "remove -7"}) {
            BibiException thrown = assertThrows(BibiException.class, () -> Parser.parse(input));
            assertTrue(thrown.getMessage().contains("Task numbers start at 1"),
                    "no complaint for: " + input);
        }
    }

    @Test
    public void parse_markersInAnyCase_accepted(@TempDir Path tempDir) throws BibiException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("bibi.txt"));

        Parser.parse("deadline return book /BY 2019-10-15").execute(tasks, new Ui(), storage);
        Parser.parse("event camp /From 2019-08-10 /TO 2019-08-12").execute(tasks, new Ui(), storage);

        assertEquals("[D][ ] return book (by: 15 Oct 2019)", tasks.get(1).toString());
        assertEquals("[E][ ] camp (from: 10 Aug 2019 to: 12 Aug 2019)", tasks.get(2).toString());
    }

    @Test
    public void parse_tabsAroundMarker_accepted(@TempDir Path tempDir) throws BibiException {
        // A tab is whitespace just as a space is, so it sets the marker apart
        // equally well.
        TaskList tasks = new TaskList();
        Parser.parse("deadline return book\t/by\t2019-10-15")
                .execute(tasks, new Ui(), new Storage(tempDir.resolve("bibi.txt")));

        assertEquals("[D][ ] return book (by: 15 Oct 2019)", tasks.get(1).toString());
    }

    @Test
    public void parse_dottedCapitalInDescription_datesReadCorrectly(@TempDir Path tempDir)
            throws BibiException {
        // A dotted capital I lowercases to two characters, so positions found in
        // a lowercased copy of the input would cut the original in the wrong place.
        // It is built from its code point so the test does not depend on the
        // encoding this source file is read in.
        String place = (char) 0x130 + "zmir";
        TaskList tasks = new TaskList();
        Parser.parse("event " + place + " trip /from 2019-08-06 /to 2019-08-07")
                .execute(tasks, new Ui(), new Storage(tempDir.resolve("bibi.txt")));

        assertEquals("[E][ ] " + place + " trip (from: 06 Aug 2019 to: 07 Aug 2019)",
                tasks.get(1).toString());
    }

    @Test
    public void parse_standaloneSocialWords_matchesCaseAndPunctuation() throws BibiException {
        for (String input : new String[] {"hi", "HELLO!", "  Hey?!  ", "Thanks...", "hello !"}) {
            assertInstanceOf(SocialCommand.class, Parser.parse(input));
            assertFalse(Parser.parse(input).isExit());
        }
    }

    @Test
    public void parse_socialWordWithOtherText_notConversation() throws BibiException {
        assertInstanceOf(AddCommand.class, Parser.parse("todo say hello and thanks!"));
        for (String input : new String[] {"hi there", "thanks for helping", "hey todo book", "highlight"}) {
            assertThrows(BibiException.class, () -> Parser.parse(input));
        }
    }

    @Test
    public void parse_compactMarkers_buildsTheSameDatedTasks(@TempDir Path tempDir) throws BibiException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("bibi.txt"));

        Parser.parse("DEADLINE  return book /BY21/12/2026").execute(tasks, new Ui(), storage);
        Parser.parse("event camp /From21/12/2026 1800 /TO22/12/2026 0800")
                .execute(tasks, new Ui(), storage);

        assertEquals("[D][ ] return book (by: 21 Dec 2026)", tasks.get(1).toString());
        assertEquals("[E][ ] camp (from: 21 Dec 2026 6:00PM to: 22 Dec 2026 8:00AM)",
                tasks.get(2).toString());
    }

    @Test
    public void parse_compactMarkerRepeated_rejectedBeforeChoosingADate() {
        BibiException deadlineError = assertThrows(BibiException.class, () ->
                Parser.parse("deadline book /by21/12/2026 /BY 22/12/2026"));
        BibiException fromError = assertThrows(BibiException.class, () ->
                Parser.parse("event camp /from21/12/2026 /from22/12/2026 /to23/12/2026"));
        BibiException toError = assertThrows(BibiException.class, () ->
                Parser.parse("event camp /from21/12/2026 /to22/12/2026 /TO23/12/2026"));

        assertTrue(deadlineError.getMessage().contains("You used /by 2 times"));
        assertTrue(fromError.getMessage().contains("You used /from 2 times"));
        assertTrue(toError.getMessage().contains("You used /to 2 times"));
    }

    @Test
    public void parse_markerPrefixInDescription_keepsTheWholeWord(@TempDir Path tempDir) throws BibiException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("bibi.txt"));

        Parser.parse("deadline check /bypass /by2pass route /by21/12/2026").execute(tasks, new Ui(), storage);
        Parser.parse("event check /fromage and /tomato /from21/12/2026 /to22/12/2026")
                .execute(tasks, new Ui(), storage);

        assertEquals("[D][ ] check /bypass /by2pass route (by: 21 Dec 2026)", tasks.get(1).toString());
        assertEquals("[E][ ] check /fromage and /tomato (from: 21 Dec 2026 to: 22 Dec 2026)",
                tasks.get(2).toString());
    }

    @Test
    public void parse_dateMarkerWithoutDescription_namesMissingDescription() {
        for (String input : new String[] {"deadline /by21/12/2026", "event /from21/12/2026 /to22/12/2026"}) {
            BibiException thrown = assertThrows(BibiException.class, () -> Parser.parse(input));

            assertTrue(thrown.getMessage().startsWith("A task needs a description."));
            assertTrue(thrown.getMessage().contains("for example:"));
        }
    }

    @Test
    public void parse_dateMarkerWithoutValue_namesMissingValue() {
        BibiException deadlineError = assertThrows(BibiException.class, () ->
                Parser.parse("deadline return book /by"));
        BibiException fromError = assertThrows(BibiException.class, () ->
                Parser.parse("event camp /from /to21/12/2026"));
        BibiException toError = assertThrows(BibiException.class, () ->
                Parser.parse("event camp /from21/12/2026 /to"));

        assertEquals("I need a due date after /by. Use deadline <description> /by <time>, "
                + "for example: deadline return book /by 21/12/2026.", deadlineError.getMessage());
        assertTrue(fromError.getMessage().startsWith("I need a date after /from."));
        assertTrue(toError.getMessage().startsWith("I need a date after /to."));
    }

    @Test
    public void parse_normalizedDescription_keepsCaseAndDuplicateMeaning(@TempDir Path tempDir)
            throws BibiException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("bibi.txt"));
        Parser.parse("todo  Read\t  Book!").execute(tasks, new Ui(), storage);

        assertEquals("[T][ ] Read Book!", tasks.get(1).toString());
        assertThrows(BibiException.class, () ->
                Parser.parse("todo read book!").execute(tasks, new Ui(), storage));
        assertEquals(1, tasks.size());

        Ui ui = new Ui();
        ui.startCapture();
        Parser.parse("FiNd  READ\t   BOOK").execute(tasks, ui, storage);

        assertTrue(ui.takeCapturedReply().text().contains("1. [T][ ] Read Book!"));
    }
}
