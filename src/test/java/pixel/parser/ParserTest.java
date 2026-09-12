package pixel.parser;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import pixel.command.AddCommand;
import pixel.command.Command;
import pixel.command.DateCommand;
import pixel.command.DeleteCommand;
import pixel.command.ExitCommand;
import pixel.command.FindCommand;
import pixel.command.ListCommand;
import pixel.command.MarkCommand;
import pixel.command.UnknownCommand;
import pixel.command.UnmarkCommand;
import pixel.command.UpdateCommand;

/** Tests command classification and validation of user input. */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parse_supportedCommands_returnsCorrespondingCommandTypes() {
        assertInstanceOf(ExitCommand.class, parser.parse("bye"));
        assertInstanceOf(ListCommand.class, parser.parse("list"));
        assertInstanceOf(DateCommand.class, parser.parse("date 2019-10-15"));
        assertInstanceOf(FindCommand.class, parser.parse("find book"));
        assertInstanceOf(MarkCommand.class, parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, parser.parse("unmark 1"));
        assertInstanceOf(AddCommand.class, parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class, parser.parse("deadline submit report /by 2019-10-15"));
        assertInstanceOf(AddCommand.class, parser.parse("event meeting /from 2pm /to 4pm"));
        assertInstanceOf(DeleteCommand.class, parser.parse("delete 1"));
        assertInstanceOf(UpdateCommand.class, parser.parse("update 1 /description revised task"));
        assertInstanceOf(UnknownCommand.class, parser.parse("blah"));
    }

    @Test
    void parse_leadingAndTrailingWhitespace_preservesCommandBoundaryRules() {
        assertInstanceOf(AddCommand.class, parser.parse(" todo read book"));
        assertInstanceOf(AddCommand.class, parser.parse("todo read book "));
        assertInstanceOf(UnknownCommand.class, parser.parse("todoist read book"));
    }

    @Test
    void parse_missingOrMalformedTaskArguments_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse("todo"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("deadline report"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("deadline report /by not-a-date"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("event meeting"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("event meeting /from 2pm"));
        assertThrows(IllegalArgumentException.class, () ->
                parser.parse("deadline report /by 2019-10-15 /by 2019-10-16"));
        assertThrows(IllegalArgumentException.class, () ->
                parser.parse("event meeting /from 2pm /from 3pm /to 4pm"));
        assertThrows(IllegalArgumentException.class, () ->
                parser.parse("event meeting /to 4pm /from 2pm"));
        assertThrows(IllegalArgumentException.class, () ->
                parser.parse("event meeting /from 2019-10-16 /to 2019-10-15"));
    }

    @Test
    void parse_missingOrMalformedQueryArguments_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse("date"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("date 2019-02-30"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("find"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("mark"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("mark zero"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("mark 0"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("mark 1 extra"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("delete"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("update"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("update one /description revised"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("update 1 description revised"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("update 1 /unknown revised"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("update 1 /to"));
        assertThrows(IllegalArgumentException.class, () ->
                parser.parse("update 1 /description revised /to 5pm"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse(null));
    }

    @Test
    void parse_validTaskNumbers_acceptsOneBasedIndexes() {
        Command mark = parser.parse("mark 1");
        Command delete = parser.parse("delete 100");
        Command update = parser.parse("update 2 /to 5pm");

        assertInstanceOf(MarkCommand.class, mark);
        assertInstanceOf(DeleteCommand.class, delete);
        assertInstanceOf(UpdateCommand.class, update);
    }

    @Test
    void parse_validUpdateFields_returnUpdateCommands() {
        assertInstanceOf(UpdateCommand.class, parser.parse("update 1 /description revised"));
        assertInstanceOf(UpdateCommand.class, parser.parse("update 1 /by 2026-09-12"));
        assertInstanceOf(UpdateCommand.class, parser.parse("update 1 /from 2pm"));
        assertInstanceOf(UpdateCommand.class, parser.parse("update 1 /to 4pm"));
    }
}
