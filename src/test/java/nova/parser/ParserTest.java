package nova.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import nova.command.AddCommand;
import nova.command.DeleteCommand;
import nova.command.EditCommand;
import nova.command.ExitCommand;
import nova.command.FindCommand;
import nova.command.FindDateCommand;
import nova.command.ListCommand;
import nova.command.MarkCommand;
import nova.command.UnmarkCommand;
import nova.exception.NovaException;

/**
 * Tests command recognition and argument validation performed by {@link Parser}.
 */
public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    public void parse_validCommands_returnsMatchingCommandTypes() throws NovaException {
        assertInstanceOf(AddCommand.class, parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class,
                parser.parse("deadline return book /by 2019-12-02 1800"));
        assertInstanceOf(AddCommand.class,
                parser.parse("event meeting /from 2019-12-02 1400 /to 2019-12-02 1600"));
        assertInstanceOf(FindCommand.class, parser.parse("find book"));
        assertInstanceOf(FindDateCommand.class, parser.parse("on 2019-12-02"));
        assertInstanceOf(ListCommand.class, parser.parse("list"));
        assertInstanceOf(MarkCommand.class, parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, parser.parse("delete 1"));
        assertInstanceOf(EditCommand.class, parser.parse("edit 1 /description read novel"));
        assertInstanceOf(ExitCommand.class, parser.parse("bye"));
    }

    @Test
    public void parse_blankCommand_throwsActionableException() {
        NovaException exception = assertThrows(NovaException.class, () -> parser.parse(""));

        assertEquals("You entered a blank command. Try todo, deadline, event, find, on, list, "
                + "mark, unmark, delete, edit, or bye.", exception.getMessage());
    }

    @Test
    public void parse_keywordWithInvalidSuffix_throwsUnknownCommandException() {
        NovaException exception = assertThrows(NovaException.class, () ->
                parser.parse("list everything"));

        assertEquals("I don't recognize that command. Start with todo, deadline, event, find, on, "
                + "list, mark, unmark, delete, edit, or bye.", exception.getMessage());
    }

    @Test
    public void parse_todoWithoutDescription_throwsActionableException() {
        NovaException exception = assertThrows(NovaException.class, () -> parser.parse("todo"));

        assertEquals("A todo needs a description. Try: todo <description>.", exception.getMessage());
    }

    @Test
    public void parse_deadlineMarkerInsideWord_throwsMissingMarkerException() {
        NovaException exception = assertThrows(NovaException.class, () ->
                parser.parse("deadline check /bypass 2019-12-02"));

        assertEquals("A deadline needs a /by date or time. "
                + "Try: deadline check /bypass 2019-12-02 /by <date or time>.",
                exception.getMessage());
    }

    @Test
    public void parse_eventMarkersInWrongOrder_throwsActionableException() {
        NovaException exception = assertThrows(NovaException.class, () ->
                parser.parse("event meeting /to 2019-12-02 /from 2019-12-01"));

        assertEquals("Put /from before /to. "
                + "Try: event <description> /from <start> /to <end>.", exception.getMessage());
    }

    @Test
    public void parse_eventEndingBeforeStart_throwsActionableException() {
        NovaException exception = assertThrows(NovaException.class, () ->
                parser.parse("event meeting /from 2019-12-03 /to 2019-12-02"));

        assertEquals("An event's /to date/time cannot be before its /from date/time.",
                exception.getMessage());
    }

    @Test
    public void parse_taskNumberWithExtraText_throwsWholeNumberException() {
        NovaException exception = assertThrows(NovaException.class, () ->
                parser.parse("mark 1 now"));

        assertEquals("The task number after mark must be a whole number, for example: mark 1.",
                exception.getMessage());
    }

    @Test
    public void parse_editWithoutField_throwsActionableException() {
        NovaException exception = assertThrows(NovaException.class, () -> parser.parse("edit 2"));

        assertEquals("Tell me which field to edit in task 2. "
                + "Use /description, /by, /from, or /to.", exception.getMessage());
    }

    @Test
    public void parse_editWithUnknownField_throwsActionableException() {
        NovaException exception = assertThrows(NovaException.class, () ->
                parser.parse("edit 2 /when tomorrow"));

        assertEquals("I don't recognize that edit field. "
                + "Use /description, /by, /from, or /to.", exception.getMessage());
    }

    @Test
    public void parse_editWithEmptyValue_throwsActionableException() {
        NovaException exception = assertThrows(NovaException.class, () ->
                parser.parse("edit 2 /to"));

        assertEquals("The /to field cannot be empty. Add a new value after /to.",
                exception.getMessage());
    }

    @Test
    public void parse_editWithMultipleFields_throwsActionableException() {
        NovaException exception = assertThrows(NovaException.class, () ->
                parser.parse("edit 2 /from 2019-12-02 /to 2019-12-03"));

        assertEquals("Edit one field at a time. Use a separate edit command for each field.",
                exception.getMessage());
    }

    @Test
    public void parse_searchWithoutDate_throwsActionableException() {
        NovaException exception = assertThrows(NovaException.class, () -> parser.parse("on"));

        assertEquals("Tell me which date to search. Try: on 2019-12-02.", exception.getMessage());
    }

    @Test
    public void parse_findWithoutKeyword_throwsActionableException() {
        NovaException exception = assertThrows(NovaException.class, () -> parser.parse("find"));

        assertEquals("Tell me what to find. Try: find <keyword>.", exception.getMessage());
    }
}
