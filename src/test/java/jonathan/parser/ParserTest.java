package jonathan.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import jonathan.JonathanException;
import jonathan.command.AddCommand;
import jonathan.command.CheckCommand;
import jonathan.command.ExitCommand;
import jonathan.command.FilterPriorityCommand;
import jonathan.command.FindCommand;
import jonathan.command.ListCommand;
import jonathan.command.PriorityCommand;
import jonathan.command.SortCommand;

public class ParserTest {
    @Test
    public void isValidDate_correctFormat_returnsTrue() {
        assertTrue(Parser.isValidDate("2026-08-27"));
    }

    @Test
    public void isValidDate_wrongFormat_returnsFalse() {
        assertFalse(Parser.isValidDate("27-08-2026"));
        assertFalse(Parser.isValidDate("2026/08/27"));
        assertFalse(Parser.isValidDate("not a date"));
    }

    @Test
    public void parse_emptyTodoDescription_exceptionThrown() {
        assertThrows(JonathanException.class, () -> {
            Parser.parse("todo ");
        });
    }

    @Test
    public void parse_unknownCommand_exceptionThrown() {
        assertThrows(JonathanException.class, () -> {
            Parser.parse("blahblah");
        });
    }

    @Test
    public void parse_priorityCommand_returnsPriorityCommand() throws JonathanException {
        assertTrue(Parser.parse("priority 1 high") instanceof PriorityCommand);
    }

    @Test
    public void parse_filterPriorityCommand_returnsFilterCommand()
            throws JonathanException {
        assertInstanceOf(FilterPriorityCommand.class,
                Parser.parse("filter priority high"));
    }

    @Test
    public void parse_sortPriorityCommand_returnsSortCommand()
            throws JonathanException {
        assertInstanceOf(SortCommand.class, Parser.parse("sort priority"));
    }

    @Test
    public void parse_invalidPriorityCommands_throwsException() {
        assertThrows(JonathanException.class, () -> {
            Parser.parse("filter priority urgent");
        });
        assertThrows(JonathanException.class, () -> {
            Parser.parse("sort date");
        });
    }

    @Test
    public void parse_missingTaskNumber_throwsException() {
        assertThrows(JonathanException.class, () -> {
            Parser.parse("delete");
        });
    }

    @Test
    public void parse_invalidTaskNumber_throwsException() {
        assertThrows(JonathanException.class, () -> {
            Parser.parse("mark abc");
        });
    }

    @Test
    public void parse_missingFindKeyword_throwsException() {
        assertThrows(JonathanException.class, () -> {
            Parser.parse("find");
        });
    }

    @Test
    public void parse_invalidDate_throwsException() {
        assertThrows(JonathanException.class, () -> {
            Parser.parse("deadline report /by 2026-02-30");
        });
    }

    @Test
    public void parse_validCommands_returnsCorrectCommandTypes()
            throws JonathanException {
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class,
                Parser.parse("deadline report /by 2026-09-20"));
        assertInstanceOf(AddCommand.class,
                Parser.parse("event meeting /from 2026-09-20 /to 2026-09-21"));
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
        assertInstanceOf(CheckCommand.class, Parser.parse("check 2026-09-20"));
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
    }

}
