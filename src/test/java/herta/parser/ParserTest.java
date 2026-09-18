package herta.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import herta.command.ArchiveCommand;
import herta.command.ArchivedCommand;
import herta.command.Command;
import herta.command.DeadlineCommand;
import herta.command.DeleteCommand;
import herta.command.EventCommand;
import herta.command.ExitCommand;
import herta.command.FilterCommand;
import herta.command.FindCommand;
import herta.command.ListCommand;
import herta.command.MarkCommand;
import herta.command.RestoreCommand;
import herta.command.SortCommand;
import herta.command.TodoCommand;
import herta.command.UnknownCommand;
import herta.command.UnmarkCommand;
import herta.command.UpcomingCommand;
import herta.exception.HertaException;

/**
 * Tests command recognition and routing in the public parser facade.
 */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parse_supportedCommands_returnsCorrespondingCommandObjects() throws HertaException {
        assertCommandType("todo read book", TodoCommand.class);
        assertCommandType("deadline submit report /by 2019-10-15", DeadlineCommand.class);
        assertCommandType("event meeting /from 2019-10-15 /to 2019-10-16", EventCommand.class);
        assertCommandType("list", ListCommand.class);
        assertCommandType("find book", FindCommand.class);
        assertCommandType("filter /on 2019-10-15", FilterCommand.class);
        assertCommandType("upcoming 7", UpcomingCommand.class);
        assertCommandType("sort date", SortCommand.class);
        assertCommandType("mark 1", MarkCommand.class);
        assertCommandType("unmark 1", UnmarkCommand.class);
        assertCommandType("delete 1", DeleteCommand.class);
        assertCommandType("archive 1-2", ArchiveCommand.class);
        assertCommandType("archived", ArchivedCommand.class);
        assertCommandType("restore 1", RestoreCommand.class);
        assertCommandType("bye", ExitCommand.class);
        assertCommandType("unknown command", UnknownCommand.class);
    }

    @Test
    void parseCommandType_delegatesCommandRecognition() {
        assertEquals(CommandType.SORT, parser.parseCommandType("sort date"));
        assertEquals(CommandType.SORT, parser.parseCommandType("sort time"));
    }

    @Test
    void parseNoArgumentCommands_extraArgumentsAndWrongCase_returnPreciseGuidance() {
        for (String input : List.of("list extra", "bye extra", "archived extra")) {
            HertaException exception = assertThrows(HertaException.class, () -> parser.parse(input));
            String commandKeyword = input.split(" ")[0];
            assertEquals("Just use: " + commandKeyword + ". Nothing else is required.",
                    exception.getMessage());
        }
        HertaException wrongCase = assertThrows(HertaException.class, () -> parser.parse("LIST"));

        assertEquals("Lowercase only. Try: list.", wrongCase.getMessage());
    }

    @Test
    void parse_nullInputOrType_reportsParsingError() {
        HertaException nullInput = assertThrows(HertaException.class, () ->
                parser.parse(null, CommandType.LIST));
        HertaException nullType = assertThrows(HertaException.class, () ->
                parser.parse("list", null));

        assertEquals("Command parsing requires input and a command type.", nullInput.getMessage());
        assertEquals("Command parsing requires input and a command type.", nullType.getMessage());
    }

    private void assertCommandType(String input, Class<? extends Command> commandClass)
            throws HertaException {
        assertInstanceOf(commandClass, parser.parse(input));
    }
}
