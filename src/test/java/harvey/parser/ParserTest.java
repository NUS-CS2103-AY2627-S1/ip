package harvey.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import harvey.HarveyException;
import harvey.command.AddCommand;
import harvey.command.Command;
import harvey.command.DeleteCommand;
import harvey.command.ExitCommand;
import harvey.command.FindCommand;
import harvey.command.ListCommand;
import harvey.command.MarkCommand;
import harvey.command.UnmarkCommand;
import harvey.storage.Storage;
import harvey.task.TaskList;
import harvey.ui.Ui;

/**
 * Tests {@link Parser}, whose whole job is choosing which {@link Command} a line means.
 * The tests therefore check the type that comes back rather than what it does; what each
 * command does is the business of that command's own tests.
 */
public class ParserTest {
    /** An empty folder for the tests that run the parsed command. */
    @TempDir
    private Path tempDir;

    @Test
    public void parse_bye_returnsExitCommandThatEndsTheSession() throws HarveyException {
        Command command = Parser.parse("bye");
        assertInstanceOf(ExitCommand.class, command);
        assertTrue(command.isExit(), "bye must be the command that stops Harvey");
    }

    @Test
    public void parse_list_returnsListCommandThatDoesNotEndTheSession() throws HarveyException {
        Command command = Parser.parse("list");
        assertInstanceOf(ListCommand.class, command);
        assertTrue(!command.isExit());
    }

    @Test
    public void parse_todo_returnsAddCommand() throws HarveyException {
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
    }

    @Test
    public void parse_deadline_returnsAddCommand() throws HarveyException {
        assertInstanceOf(AddCommand.class, Parser.parse("deadline return book /by 2019-10-15"));
    }

    @Test
    public void parse_event_returnsAddCommand() throws HarveyException {
        assertInstanceOf(AddCommand.class, Parser.parse("event meeting /from 2pm /to 4pm"));
    }

    @Test
    public void parse_mark_returnsMarkCommand() throws HarveyException {
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 2"));
    }

    @Test
    public void parse_unmark_returnsUnmarkCommand() throws HarveyException {
        // mark and unmark share a prefix, so this guards against the keyword being
        // matched by startsWith rather than by equality.
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 2"));
    }

    @Test
    public void parse_delete_returnsDeleteCommand() throws HarveyException {
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 3"));
    }

    @Test
    public void parse_commandWithoutArguments_stillReturnsItsCommand() throws HarveyException {
        // A missing argument is the command's problem to report when it runs, not a
        // reason for the parser to refuse the line.
        assertInstanceOf(AddCommand.class, Parser.parse("todo"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark"));
    }

    @Test
    public void parse_unknownCommand_exceptionThrown() {
        assertThrows(HarveyException.class, () -> Parser.parse("blah blah"));
    }

    @Test
    public void parse_emptyLine_exceptionThrown() {
        assertThrows(HarveyException.class, () -> Parser.parse(""));
    }

    @Test
    public void parse_keywordFollowedByExtraSpaces_stillRecognized() throws HarveyException {
        // Spaces between the keyword and the argument are the parser's to cope with.
        assertInstanceOf(AddCommand.class, Parser.parse("todo    read book"));
    }

    @Test
    public void parse_find_returnsFindCommand() throws HarveyException {
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    @Test
    public void parse_findWithoutKeyword_stillReturnsFindCommand() throws HarveyException {
        // A missing keyword is FindCommand's to report when it runs.
        assertInstanceOf(FindCommand.class, Parser.parse("find"));
    }

    @Test
    public void parse_leadingAndTrailingSpaces_stillRecognized() throws HarveyException {
        // The window passes the line untrimmed, so the parser must trim it itself.
        assertInstanceOf(ListCommand.class, Parser.parse("   list   "));
    }

    @Test
    public void parse_tabAfterKeyword_stillRecognized() throws HarveyException {
        assertInstanceOf(AddCommand.class, Parser.parse("todo\tread book"));
    }

    @Test
    public void parse_upperCaseKeyword_stillRecognized() throws HarveyException {
        assertInstanceOf(ListCommand.class, Parser.parse("LIST"));
    }

    @Test
    public void parse_repeatedSpacesInDescription_collapsedToOne() throws HarveyException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.toString(), "t.txt");
        Parser.parse("todo  read \t  book").execute(tasks, new Ui(), storage);
        assertEquals("[T][ ] read book", tasks.get(1).toString());
    }

    @Test
    public void parse_byeWithExtraWords_exceptionThrown() {
        // Quitting on a line the user may not have meant as a goodbye would lose their place.
        assertThrows(HarveyException.class, () -> Parser.parse("bye now"));
    }

    @Test
    public void parse_listWithExtraWords_exceptionThrown() {
        assertThrows(HarveyException.class, () -> Parser.parse("list everything"));
    }
}
