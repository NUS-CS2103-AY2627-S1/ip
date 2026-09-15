package duke.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import duke.command.ByeCommand;
import duke.command.Command;
import duke.command.DeadlineCommand;
import duke.command.DeleteCommand;
import duke.command.EventCommand;
import duke.command.FindCommand;
import duke.command.ListCommand;
import duke.command.MarkCommand;
import duke.command.TodoCommand;
import duke.command.UndoCommand;
import duke.command.UnknownCommand;
import duke.exception.GaryException;

public class ParserTest {

    private final Parser parser = new Parser();

    @Test
    public void parse_bye_returnsByeCommand() {
        Command command = parser.parse("bye");
        assertInstanceOf(ByeCommand.class, command);
    }

    @Test
    public void parse_list_returnsListCommand() {
        Command command = parser.parse("list");
        assertInstanceOf(ListCommand.class, command);
    }

    @Test
    public void parse_mark_returnsMarkCommand() {
        Command command = parser.parse("mark 2");
        assertInstanceOf(MarkCommand.class, command);
    }

    @Test
    public void parse_unmark_returnsMarkCommand() {
        Command command = parser.parse("unmark 2");
        assertInstanceOf(MarkCommand.class, command);
    }

    @Test
    public void parse_todo_returnsTodoCommand() {
        Command command = parser.parse("todo read book");
        assertInstanceOf(TodoCommand.class, command);
    }

    @Test
    public void parse_deadline_returnsDeadlineCommand() {
        Command command = parser.parse("deadline return book /by 2026-08-25");
        assertInstanceOf(DeadlineCommand.class, command);
    }

    @Test
    public void parse_event_returnsEventCommand() {
        Command command = parser.parse("event project meeting /from 2026-08-25 /to 2026-08-26");
        assertInstanceOf(EventCommand.class, command);
    }

    @Test
    public void parse_delete_returnsDeleteCommand() {
        Command command = parser.parse("delete 1");
        assertInstanceOf(DeleteCommand.class, command);
    }

    @Test
    public void parse_find_returnsFindCommand() {
        Command command = parser.parse("find book");
        assertInstanceOf(FindCommand.class, command);
    }

    @Test
    public void parse_undo_returnsUndoCommand() {
        Command command = parser.parse("undo");
        assertInstanceOf(UndoCommand.class, command);
    }

    @Test
    public void parse_undoIsCaseInsensitive_returnsUndoCommand() {
        Command command = parser.parse("UnDo");
        assertInstanceOf(UndoCommand.class, command);
    }

    @Test
    public void parse_unknown_returnsUnknownCommand() {
        Command command = parser.parse("what is this");
        assertInstanceOf(UnknownCommand.class, command);
    }

    @Test
    public void parse_ignoresLeadingAndTrailingWhitespace() {
        Command command = parser.parse("   list   ");
        assertInstanceOf(ListCommand.class, command);
    }

    @Test
    public void parse_multipleWhitespaceBetweenArguments_returnsCommand() {
        Command command = parser.parse("  event   project   meeting   /from   2026-08-25   /to   2026-08-26  ");
        assertInstanceOf(EventCommand.class, command);
    }

    @Test
    public void parse_isCaseInsensitive() {
        Command command = parser.parse("LiSt");
        assertInstanceOf(ListCommand.class, command);
    }

    @Test
    public void parse_underTurkishLocale_remainsCaseInsensitive() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));

            assertInstanceOf(ListCommand.class, parser.parse("list"));
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    public void parse_deadlineWithoutByIndicator_throwsGaryException() {
        GaryException exception = assertThrows(GaryException.class, () -> parser.parse("deadline return book"));

        assertEquals("Use: deadline <description> /by <YYYY-MM-DD>.", exception.getMessage());
    }

    @Test
    public void parse_eventWithoutToIndicator_throwsGaryException() {
        GaryException exception = assertThrows(GaryException.class, () ->
                parser.parse("event project meeting /from 2026-08-25"));

        assertEquals("Use: event <description> /from <YYYY-MM-DD> /to <YYYY-MM-DD>.", exception.getMessage());
    }

    @Test
    public void parse_deadlineWithRepeatedByIndicator_throwsGaryException() {
        assertThrows(GaryException.class, () ->
                parser.parse("deadline return book /by 2026-08-25 /by 2026-08-26"));
    }

    @Test
    public void parse_eventWithRepeatedFromIndicator_throwsGaryException() {
        assertThrows(GaryException.class, () -> parser.parse(
                "event meeting /from 2026-08-25 /from 2026-08-26 /to 2026-08-27"));
    }

    @Test
    public void parse_listWithArguments_throwsGaryException() {
        GaryException exception = assertThrows(GaryException.class, () -> parser.parse("list now"));

        assertEquals("The list command does not take any arguments.", exception.getMessage());
    }

    @Test
    public void parse_byeWithArguments_throwsGaryException() {
        GaryException exception = assertThrows(GaryException.class, () -> parser.parse("bye now"));

        assertEquals("The bye command does not take any arguments.", exception.getMessage());
    }

    @Test
    public void parse_blankInput_throwsGaryException() {
        GaryException exception = assertThrows(GaryException.class, () -> parser.parse("   "));

        assertEquals("Please enter a command.", exception.getMessage());
    }

    @Test
    public void parse_nullInput_throwsGaryException() {
        GaryException exception = assertThrows(GaryException.class, () -> parser.parse(null));

        assertEquals("Please enter a command.", exception.getMessage());
    }

    @Test
    public void parse_commandAtLengthLimit_returnsCommand() {
        Command command = parser.parse("todo " + "a".repeat(495));

        assertInstanceOf(TodoCommand.class, command);
    }

    @Test
    public void parse_deadlineMarkerWithoutDate_throwsGaryException() {
        assertThrows(GaryException.class, () -> parser.parse("deadline return book /by"));
    }

    @Test
    public void parse_eventWithRepeatedToIndicator_throwsGaryException() {
        assertThrows(GaryException.class, () -> parser.parse(
                "event meeting /from 2026-08-25 /to 2026-08-26 /to 2026-08-27"));
    }

    @Test
    public void parse_eventWithMarkersInWrongOrder_throwsGaryException() {
        assertThrows(GaryException.class, () ->
                parser.parse("event meeting /to 2026-08-26 /from 2026-08-25"));
    }

    @Test
    public void parse_commandLongerThanLimit_throwsGaryException() {
        GaryException exception = assertThrows(GaryException.class, () -> parser.parse("a".repeat(501)));

        assertEquals("Commands cannot exceed 500 characters.", exception.getMessage());
    }
}
