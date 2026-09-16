package brucli.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import brucli.command.Command;
import brucli.command.FindCommand;
import brucli.ui.ResponseType;

public class ParserTest {

    @Test
    public void parse_findWithKeyword_returnsFindCommand() {
        Parser parser = new Parser();

        Command command = parser.parse("find library book");

        assertInstanceOf(FindCommand.class, command);
    }

    @Test
    public void parse_findWithoutKeyword_exceptionThrown() {
        Parser parser = new Parser();

        assertThrows(IllegalArgumentException.class, () -> parser.parse("find"));
    }

    @Test
    public void parse_taskCreationCommand_returnsCreationResponseType() {
        Parser parser = new Parser();

        Command command = parser.parse("todo train footwork");

        assertEquals(ResponseType.CREATION, command.getResponseType());
    }

    @Test
    public void parse_viewCommand_returnsViewResponseType() {
        Parser parser = new Parser();

        Command command = parser.parse("list");

        assertEquals(ResponseType.VIEW, command.getResponseType());
    }

    @Test
    public void parse_unknownCommand_returnsErrorResponseType() {
        Parser parser = new Parser();

        Command command = parser.parse("spin kick");

        assertEquals(ResponseType.ERROR, command.getResponseType());
    }

    @Test
    public void parse_gameCommand_returnsJokeResponseType() {
        Parser parser = new Parser();

        Command command = parser.parse("game");

        assertEquals(ResponseType.JOKE, command.getResponseType());
    }
}
