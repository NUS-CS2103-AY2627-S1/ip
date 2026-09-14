package sine.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import sine.command.AddCommand;
import sine.command.DeleteCommand;
import sine.command.ExitCommand;
import sine.command.FindCommand;
import sine.command.ListCommand;
import sine.command.MarkCommand;
import sine.command.UndoCommand;
import sine.command.UnknownCommand;
import sine.command.UnmarkCommand;
import sine.exception.SineException;

/**
 * Tests command recognition and argument validation by the parser.
 */
public class ParserTest {
    private final Parser parser = new Parser();

    /** Tests recognition of every supported command category. */
    @Test
    public void parse_supportedCommands_returnsMatchingCommandTypes() throws SineException {
        assertInstanceOf(ExitCommand.class, parser.parse("bye", 0));
        assertInstanceOf(ListCommand.class, parser.parse("list", 0));
        assertInstanceOf(FindCommand.class, parser.parse("find book", 0));
        assertInstanceOf(DeleteCommand.class, parser.parse("delete 1", 1));
        assertInstanceOf(MarkCommand.class, parser.parse("mark 1", 1));
        assertInstanceOf(UnmarkCommand.class, parser.parse("unmark 1", 1));
        assertInstanceOf(AddCommand.class, parser.parse("todo read book", 0));
        assertInstanceOf(AddCommand.class,
                parser.parse("deadline return book /by 2026-08-30", 0));
        assertInstanceOf(AddCommand.class,
                parser.parse("event meeting /from 2pm /to 4pm", 0));
        assertInstanceOf(UnknownCommand.class, parser.parse("something else", 0));
    }

    /** Tests rejection of missing and non-numeric task numbers. */
    @Test
    public void parse_findKeywordIsMissing_throwsHelpfulException() {
        SineException exception = assertThrows(SineException.class,
                () -> parser.parse("find", 0));

        assertEquals("The search keyword cannot be empty.", exception.getMessage());
    }

    @Test
    public void parse_taskNumberIsMissingOrNotNumeric_throwsHelpfulException() {
        for (String command : new String[]{"delete", "mark abc", "unmark 1.5"}) {
            SineException exception = assertThrows(SineException.class,
                    () -> parser.parse(command, 3));
            assertEquals("Please enter a valid task number.", exception.getMessage());
        }
    }

    /** Tests rejection of task numbers outside the current list. */
    @Test
    public void parse_taskNumberDoesNotExist_throwsHelpfulException() {
        for (String command : new String[]{"delete 0", "mark -1", "unmark 4"}) {
            SineException exception = assertThrows(SineException.class,
                    () -> parser.parse(command, 3));
            assertEquals("That task number does not exist.", exception.getMessage());
        }
    }

    /** Tests rejection of a todo with no description. */
    @Test
    public void parse_todoDescriptionIsEmpty_throwsHelpfulException() {
        SineException exception = assertThrows(SineException.class,
                () -> parser.parse("todo", 0));

        assertEquals("The description of a todo cannot be empty.", exception.getMessage());
    }

    /** Tests rejection of missing and invalid deadline dates. */
    @Test
    public void parse_deadlineFieldIsMissingOrInvalid_throwsHelpfulException() {
        SineException missingDate = assertThrows(SineException.class,
                () -> parser.parse("deadline return book", 0));
        SineException invalidDate = assertThrows(SineException.class,
                () -> parser.parse("deadline return book /by 2026-02-30", 0));

        assertEquals("The deadline of a deadline cannot be empty.", missingDate.getMessage());
        assertEquals("Please enter the deadline as yyyy-MM-dd.", invalidDate.getMessage());
    }

    /** Tests rejection of events with missing time fields. */
    @Test
    public void parse_eventFieldIsMissing_throwsHelpfulException() {
        SineException missingStart = assertThrows(SineException.class,
                () -> parser.parse("event meeting /to 4pm", 0));
        SineException missingEnd = assertThrows(SineException.class,
                () -> parser.parse("event meeting /from 2pm", 0));

        assertEquals("The start time of an event cannot be empty.", missingStart.getMessage());
        assertEquals("The start time of an event cannot be empty.", missingEnd.getMessage());
    }

    /** Tests exact spellings, malformed undo words, and unrelated unknown words. */
    @Test
    public void parse_undoSyntax_acceptsOnlyExactSpellings() throws SineException {
        assertInstanceOf(UndoCommand.class, parser.parse("undo", 0));
        assertInstanceOf(UndoCommand.class, parser.parse("UNDO", 0));
        for (String input : new String[]{"Undo", "uNdO", "undo 2", "UNDO now", " undo ", "undo\t2"}) {
            SineException exception = assertThrows(SineException.class, () -> parser.parse(input, 0));
            assertEquals("Usage: undo or UNDO", exception.getMessage());
        }
        assertInstanceOf(UnknownCommand.class, parser.parse("undoable", 0));
        assertInstanceOf(UnknownCommand.class, parser.parse("undo!", 0));
        assertInstanceOf(UnknownCommand.class, parser.parse("redo", 0));
    }
    @Test
    public void parse_whitespaceAroundCommands_acceptsCommands() throws SineException {
        assertInstanceOf(ListCommand.class, parser.parse("  list  ", 0));
        assertInstanceOf(MarkCommand.class, parser.parse(" \tmark\t 1 ", 1));
        assertInstanceOf(AddCommand.class, parser.parse("todo\tread  book", 0));
        assertInstanceOf(AddCommand.class,
                parser.parse("event\tmeeting /from\t2pm /to   4pm", 0));
    }

    @Test
    public void parse_blankOrUnexpectedArguments_throwsHelpfulException() {
        for (String input : new String[]{null, "", " \t "}) {
            assertEquals("Please enter a command.",
                    assertThrows(SineException.class, () -> parser.parse(input, 0)).getMessage());
        }
        for (String input : new String[]{"list extra", "bye\tsoon"}) {
            String word = input.split("\\s+", 2)[0];
            assertEquals("Usage: " + word,
                    assertThrows(SineException.class, () -> parser.parse(input, 0)).getMessage());
        }
    }

    @Test
    public void parse_repeatedParameters_rejectsAmbiguousFields() {
        String[] inputs = {
            "deadline book /by 2026-08-30 /by 2026-09-01",
            "event meeting /from 2pm /from 3pm /to 4pm",
            "event meeting /from 2pm /to 4pm /to 5pm"
        };
        String[] parameters = {"/by", "/from", "/to"};
        for (int i = 0; i < inputs.length; i++) {
            String input = inputs[i];
            assertEquals("Specify " + parameters[i] + " only once.",
                    assertThrows(SineException.class, () -> parser.parse(input, 0)).getMessage());
        }
    }

    @Test
    public void parse_reversedEventParameters_throwsHelpfulException() {
        assertEquals("Place /from before /to.", assertThrows(SineException.class,
                () -> parser.parse("event meeting /to 4pm /from 2pm", 0)).getMessage());
    }

    @Test
    public void parse_parameterLikeDescription_acceptsLiteralText() throws SineException {
        assertInstanceOf(AddCommand.class,
                parser.parse("deadline read /bylaws /by 2026-08-30", 0));
        assertInstanceOf(AddCommand.class,
                parser.parse("event visit /fromage /from 2pm /to 4pm", 0));
        assertEquals("The deadline of a deadline cannot be empty.", assertThrows(SineException.class,
                () -> parser.parse("deadline book /by2026-08-30", 0)).getMessage());
    }

}
